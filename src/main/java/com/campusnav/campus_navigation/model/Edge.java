
package com.campusnav.campus_navigation.model;

public class Edge {
    private String from;
    private String to;
    private double weight;

    public Edge() {}
    public Edge(String from, String to, double weight){
        this.from = from; this.to = to; this.weight = weight;
    }

    // getters & setters
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
