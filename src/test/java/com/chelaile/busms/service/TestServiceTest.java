package com.chelaile.busms.service;

import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/11 09:44
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestServiceTest {
    @Autowired
    private TestService testService;

    @Test
    public void test() {
        String str = testService.test();
        Assert.assertThat(str, equalTo("hello world"));
    }

}