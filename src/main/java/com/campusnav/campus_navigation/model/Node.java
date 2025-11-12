
package com.campusnav.campus_navigation.model;

public class Node {
    private String id;
    private String name;
    private double lat;
    private double lng;

    public Node() {}
    public Node(String id, String name, double lat, double lng) {
        this.id = id; this.name = name; this.lat = lat; this.lng = lng;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}
