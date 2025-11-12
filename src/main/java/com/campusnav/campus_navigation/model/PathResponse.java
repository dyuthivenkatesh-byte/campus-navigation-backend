
package com.campusnav.campus_navigation.model;

import java.util.List;

public class PathResponse {
    private List<Node> pathNodes;
    private double totalDistanceMeters;

    public PathResponse() {}
    public PathResponse(List<Node> pathNodes, double totalDistanceMeters){
        this.pathNodes = pathNodes; this.totalDistanceMeters = totalDistanceMeters;
    }

    public List<Node> getPathNodes() { return pathNodes; }
    public void setPathNodes(List<Node> pathNodes) { this.pathNodes = pathNodes; }
    public double getTotalDistanceMeters() { return totalDistanceMeters; }
    public void setTotalDistanceMeters(double totalDistanceMeters) { this.totalDistanceMeters = totalDistanceMeters; }
}
