package com.iscas.yf.IntelliPipeline.common.graph;

public class Edge<Vertex,Edge_Type> {
    private Vertex source;
    private Vertex target;
    private Edge_Type type;
    public Edge(Vertex sourceVertex, Vertex targetVertex, Edge_Type type) {
        this.source=sourceVertex;
        this.target=targetVertex;
        this.type=type;
    }

    // 连线的两端, source和target, 有先后顺序
    public Vertex getSource() {
        return source;
    }
    public void setSource(Vertex source) {
        this.source = source;
    }
    public Vertex getTarget() {
        return target;
    }
    public void setTarget(Vertex target) {
        this.target = target;
    }
    public Edge_Type getType() {
        return type;
    }
    public void setType(Edge_Type type) {
        this.type = type;
    }
}
