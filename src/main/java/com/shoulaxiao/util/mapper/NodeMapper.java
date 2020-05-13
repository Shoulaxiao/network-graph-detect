package com.shoulaxiao.util.mapper;

import com.shoulaxiao.model.NodeDO;
import com.shoulaxiao.model.vo.NodeVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-13 12:19
 **/
@Mapper(componentModel = "spring")
public interface NodeMapper {

    NodeDO vo2do(NodeVO nodeVO);

    NodeVO do2vo(NodeDO nodeDO);

    List<NodeDO> vo2dos(List<NodeVO> nodeVOS);
}
