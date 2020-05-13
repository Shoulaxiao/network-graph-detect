package com.shoulaxiao.model.vo;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-09 18:59
 **/
public class NodeVO {

    private Long id;

    private String nodeCode;

    private Double densityValue;

    /**
     * 节点向量
     */
    private String vectorValue;

    private Integer belongGraph;

    public NodeVO(){

    }

    public NodeVO(String code) {
        this.nodeCode = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public Double getDensityValue() {
        return densityValue;
    }

    public void setDensityValue(Double densityValue) {
        this.densityValue = densityValue;
    }

    public String getVectorValue() {
        return vectorValue;
    }

    public void setVectorValue(String vectorValue) {
        this.vectorValue = vectorValue;
    }

    public Integer getBelongGraph() {
        return belongGraph;
    }

    public void setBelongGraph(Integer belongGraph) {
        this.belongGraph = belongGraph;
    }
}
