
package com.campusnav.campus_navigation.service;

import com.campusnav.campus_navigation.model.Node;
import com.campusnav.campus_navigation.model.Edge;
import com.campusnav.campus_navigation.model.PathResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class GraphService {

    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, List<Edge>> adj = new HashMap<>();

    @PostConstruct
    public void init() throws Exception {
        ObjectMapper om = new ObjectMapper();
        InputStream is = new ClassPathResource("graph.json").getInputStream();

        Map<String, Object> g = om.readValue(is, new TypeReference<Map<String,Object>>(){});
        // parse nodes
        List<Map<String,Object>> nlist = (List<Map<String,Object>>) g.get("nodes");
        for (Map<String,Object> nm : nlist){
            String id = nm.get("id").toString();
            String name = nm.get("name").toString();
            double lat = ((Number)nm.get("lat")).doubleValue();
            double lng = ((Number)nm.get("lng")).doubleValue();
            Node node = new Node(id, name, lat, lng);
            nodes.put(id, node);
            adj.put(id, new ArrayList<>());
        }
        // parse edges
        List<Map<String,Object>> elist = (List<Map<String,Object>>) g.get("edges");
        for (Map<String,Object> em : elist){
            String from = em.get("from").toString();
            String to = em.get("to").toString();
            double w = ((Number)em.get("weight")).doubleValue();
            adj.get(from).add(new Edge(from, to, w));
            adj.get(to).add(new Edge(to, from, w)); // undirected
        }
    }

    public Collection<Node> allNodes() {
        return nodes.values();
    }

    public PathResponse shortestPath(String srcId, String dstId){
        // Dijkstra
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        for (String id : nodes.keySet()) {
            dist.put(id, Double.POSITIVE_INFINITY);
        }
        dist.put(srcId, 0.0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(srcId);

        while(!pq.isEmpty()){
            String u = pq.poll();
            if (u.equals(dstId)) break;
            for(Edge e : adj.getOrDefault(u, List.of())) {
                String v = e.getTo();
                double alt = dist.get(u) + e.getWeight();
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        // reconstruct path
        List<Node> path = new LinkedList<>();
        String cur = dstId;
        if (!prev.containsKey(cur) && !cur.equals(srcId)) {
            return new PathResponse(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        while(cur != null) {
            path.add(0, nodes.get(cur));
            if (cur.equals(srcId)) break;
            cur = prev.get(cur);
        }
        double total = dist.getOrDefault(dstId, Double.POSITIVE_INFINITY);
        return new PathResponse(path, total);
    }

    // helper nearest node by lat/lng
    public Node nearestNode(double lat, double lng){
        double best = Double.POSITIVE_INFINITY;
        Node bestNode = null;
        for(Node n : nodes.values()){
            double d = haversineMeters(lat, lng, n.getLat(), n.getLng());
            if (d < best) { best = d; bestNode = n; }
        }
        return bestNode;
    }

    private double haversineMeters(double lat1, double lon1, double lat2, double lon2){
        double R = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
    
    public PathResponse shortestPathFromGPS(double srcLat, double srcLng, String dstId) {
        // 1. Find nearest node(s) to connect temporary node
        Node nearest = nearestNode(srcLat, srcLng);
        double distToNearest = haversineMeters(srcLat, srcLng, nearest.getLat(), nearest.getLng());

        // 2. Create a virtual "source" node
        Node virtualSrc = new Node("SRC", "Current Location", srcLat, srcLng);

        // 3. Temporarily add it to the graph
        adj.put("SRC", List.of(new Edge("SRC", nearest.getId(), distToNearest)));
        adj.get(nearest.getId()).add(new Edge(nearest.getId(), "SRC", distToNearest));

        nodes.put("SRC", virtualSrc);

        // 4. Run Dijkstra from virtual node
        PathResponse path = shortestPath("SRC", dstId);

        // 5. Clean up after running (optional)
        adj.remove("SRC");
        adj.get(nearest.getId()).removeIf(e -> e.getTo().equals("SRC"));
        nodes.remove("SRC");

        return path;
    }

}
