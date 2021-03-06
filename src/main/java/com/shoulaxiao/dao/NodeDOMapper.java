package com.shoulaxiao.dao;

import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.NodeDOExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface NodeDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int countByExample(NodeDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int deleteByExample(NodeDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int insert(NodeDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int insertSelective(NodeDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    List<NodeDO> selectByExample(NodeDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    NodeDO selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int updateByExampleSelective(@Param("record") NodeDO record, @Param("example") NodeDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int updateByExample(@Param("record") NodeDO record, @Param("example") NodeDOExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int updateByPrimaryKeySelective(NodeDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table data_node
     *
     * @mbggenerated Thu May 14 15:18:03 CST 2020
     */
    int updateByPrimaryKey(NodeDO record);

    /**
     * 批量插入
     * @param record
     * @return
     */
    int insertByBatch(List<NodeDO> record);

    /**
     * 根据节点code进行查询
     * @param map
     * @return
     */
    List<NodeDO> selectByNodeCodes(Map<String, Object> map);


    List<NodeDO> selectByGraph(Integer graph);
}