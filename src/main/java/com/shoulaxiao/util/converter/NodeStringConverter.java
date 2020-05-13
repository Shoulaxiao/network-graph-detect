package com.shoulaxiao.util.converter;

import com.shoulaxiao.model.vo.NodeVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-13 12:22
 **/
@Component
public class NodeStringConverter {

    private static Logger logger= LoggerFactory.getLogger(NodeStringConverter.class);

    public static NodeVO String2Node(String code){
        if (StringUtils.isEmpty(code)){
            logger.warn("发现节点没有编号");
        }
        return new NodeVO(code);
    }


    public static String Node2String(NodeVO nodeVO){
        if (null==nodeVO){
            logger.warn("节点转化发现节点没有编号");
            return StringUtils.EMPTY;
        }
        return nodeVO.getCode();
    }
}
