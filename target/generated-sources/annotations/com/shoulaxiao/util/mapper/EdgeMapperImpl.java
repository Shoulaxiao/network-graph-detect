package com.shoulaxiao.util.mapper;

import com.shoulaxiao.model.EdgeDO;
import com.shoulaxiao.model.vo.EdgeVO;
import com.shoulaxiao.util.converter.NodeStringConverter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-05-13T14:29:12+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_241 (Oracle Corporation)"
)
@Component
public class EdgeMapperImpl implements EdgeMapper {

    @Override
    public List<EdgeDO> vo2dos(List<EdgeVO> edgeVOS) {
        if ( edgeVOS == null ) {
            return null;
        }

        List<EdgeDO> list = new ArrayList<EdgeDO>( edgeVOS.size() );
        for ( EdgeVO edgeVO : edgeVOS ) {
            list.add( edgeVOToEdgeDO( edgeVO ) );
        }

        return list;
    }

    protected EdgeDO edgeVOToEdgeDO(EdgeVO edgeVO) {
        if ( edgeVO == null ) {
            return null;
        }

        EdgeDO edgeDO = new EdgeDO();

        edgeDO.setId( edgeVO.getId() );
        edgeDO.setStartNode( NodeStringConverter.Node2String( edgeVO.getStartNode() ) );
        edgeDO.setEndNode( NodeStringConverter.Node2String( edgeVO.getEndNode() ) );
        edgeDO.setCosValue( edgeVO.getCosValue() );
        if ( edgeVO.getClassification() != null ) {
            edgeDO.setClassification( String.valueOf( edgeVO.getClassification() ) );
        }
        edgeDO.setBelongGraph( edgeVO.getBelongGraph() );

        return edgeDO;
    }
}
