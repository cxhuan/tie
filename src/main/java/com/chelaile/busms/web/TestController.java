package com.chelaile.busms.web;

import com.chelaile.busms.service.impl.TestServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping(value = "/test")
public class TestController {
	@Resource
	private TestServiceImpl testServiceImpl;


	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
	public void test(@RequestParam(required = true) String userId) {


		testServiceImpl.test();
	}
}
