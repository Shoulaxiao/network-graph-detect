package com;

import com.shoulaxiao.service.NetworkLearnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * @description:
 * @author: shoulaxiao
 * @create: 2020-05-16 22:12
 **/
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml","classpath:spring/springmvc-servlet.xml"})
public class MainTest {

    @Resource
    private NetworkLearnService networkLearnService;


    @Test
    public void mainRun(){
        networkLearnService.networkModelTrain(1);
    }
}
