package com.shoulaxiao.model;

import java.util.Date;

public class NodeDO {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.id
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.node_code
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private String nodeCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.density_value
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private Double densityValue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.vector_value
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private String vectorValue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.belong_graph
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private Integer belongGraph;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.create_time
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column data_node.update_time
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.id
     *
     * @return the value of data_node.id
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.id
     *
     * @param id the value for data_node.id
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.node_code
     *
     * @return the value of data_node.node_code
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public String getNodeCode() {
        return nodeCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.node_code
     *
     * @param nodeCode the value for data_node.node_code
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode == null ? null : nodeCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.density_value
     *
     * @return the value of data_node.density_value
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public Double getDensityValue() {
        return densityValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.density_value
     *
     * @param densityValue the value for data_node.density_value
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setDensityValue(Double densityValue) {
        this.densityValue = densityValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.vector_value
     *
     * @return the value of data_node.vector_value
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public String getVectorValue() {
        return vectorValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.vector_value
     *
     * @param vectorValue the value for data_node.vector_value
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setVectorValue(String vectorValue) {
        this.vectorValue = vectorValue == null ? null : vectorValue.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.belong_graph
     *
     * @return the value of data_node.belong_graph
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public Integer getBelongGraph() {
        return belongGraph;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.belong_graph
     *
     * @param belongGraph the value for data_node.belong_graph
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setBelongGraph(Integer belongGraph) {
        this.belongGraph = belongGraph;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.create_time
     *
     * @return the value of data_node.create_time
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.create_time
     *
     * @param createTime the value for data_node.create_time
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column data_node.update_time
     *
     * @return the value of data_node.update_time
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column data_node.update_time
     *
     * @param updateTime the value for data_node.update_time
     *
     * @mbggenerated Wed May 13 14:15:32 CST 2020
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}