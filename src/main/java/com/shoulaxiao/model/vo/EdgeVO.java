package com.shoulaxiao.model.vo;

import javax.xml.soap.Node;

/**
 * @description: 边
 * @author: shoulaxiao
 * @create: 2020-05-09 18:58
 **/
public class EdgeVO {

    private Long id;

    private NodeVO startNode;

    private NodeVO endNode;

    private Double cosValue;

    private Integer belongGraph;

    private Boolean visited=false;


    /**
     * 社区内边=1;社区间边为0
     */
    private Double classification;


    public EdgeVO(NodeVO startNode, NodeVO endNode,int belongGraph) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.belongGraph=belongGraph;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NodeVO getStartNode() {
        return startNode;
    }

    public void setStartNode(NodeVO startNode) {
        this.startNode = startNode;
    }

    public NodeVO getEndNode() {
        return endNode;
    }

    public void setEndNode(NodeVO endNode) {
        this.endNode = endNode;
    }

    public Double getCosValue() {
        return cosValue;
    }

    public void setCosValue(Double cosValue) {
        this.cosValue = cosValue;
    }

    public Integer getBelongGraph() {
        return belongGraph;
    }

    public void setBelongGraph(Integer belongGraph) {
        this.belongGraph = belongGraph;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    public Double getClassification() {
        return classification;
    }

    public void setClassification(Double classification) {
        this.classification = classification;
    }
}
