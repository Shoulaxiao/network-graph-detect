package com.shoulaxiao.controller;

import com.shoulaxiao.service.NetworkLearnService;
import com.shoulaxiao.util.SingleResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-12 18:15
 **/

@Controller
@RequestMapping(value = "/api/data")
public class NetWorkHandleController {

    @Resource
    private NetworkLearnService networkLearnService;

    @RequestMapping(value = "/launch",method = RequestMethod.POST)
    @ResponseBody
    public SingleResult acquireFileData(@RequestParam("files") CommonsMultipartFile[] files,@RequestParam("networkGraph") Integer networkGraph){

        SingleResult response=new SingleResult();

        if (files.length==0){
            return new SingleResult(null,"","数据文件不能为空");
        }
        try {
            networkLearnService.networkLearn(files,networkGraph);
            response.setSuccess(true);
        }catch (Exception e){
            return new SingleResult(null,"",e.getMessage());
        }
        return response;

    }
}
