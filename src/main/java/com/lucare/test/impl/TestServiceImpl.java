package com.lucare.test.impl;

import com.lucare.invoke.HttpInvokeHelper;
import com.lucare.test.api.TestService;

/**
 * Created by Lucare.Feng on 2016/3/29.
 */
public class TestServiceImpl implements TestService {
    public void doTest(String param) {
        System.out.println("this param is "+param);
        System.out.println("hahhhah");

        HttpInvokeHelper.setInvokeResult("ok");
    }
}
