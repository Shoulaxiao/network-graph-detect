package com.shoulaxiao.util.mapper;

import com.shoulaxiao.model.EdgeDO;
import com.shoulaxiao.model.vo.EdgeVO;
import com.shoulaxiao.util.converter.NodeStringConverter;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-13 12:17
 **/
@Mapper(componentModel = "spring",uses = {NodeStringConverter.class})
public interface EdgeMapper {

    List<EdgeDO> vo2dos(List<EdgeVO> edgeVOS);

    List<EdgeVO> do2vos(List<EdgeDO> edgeDOList);

    EdgeDO vo2do(EdgeVO edgeVO);
}
