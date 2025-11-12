
package com.campusnav.campus_navigation.controller;

import com.campusnav.campus_navigation.model.Node;
import com.campusnav.campus_navigation.model.PathResponse;
import com.campusnav.campus_navigation.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin(origins = "*")
@RestController
public class GraphController {

    @Autowired
    private GraphService graphService;

    @GetMapping("/graph")
    public Collection<Node> allNodes() {
        return graphService.allNodes();
    }

    @GetMapping("/path")
    public PathResponse getPath(@RequestParam String src, @RequestParam String dst) {
        return graphService.shortestPath(src, dst);
    }

    @GetMapping("/nearest")
    public Node nearestNode(@RequestParam double lat, @RequestParam double lng) {
        return graphService.nearestNode(lat, lng);
    }
    
    @GetMapping("/routeFromLocation")
    public PathResponse routeFromLocation(@RequestParam double srcLat, @RequestParam double srcLng, @RequestParam String dst) {
        return graphService.shortestPathFromGPS(srcLat, srcLng, dst);
    }

}
