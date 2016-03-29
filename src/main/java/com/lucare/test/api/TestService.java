package com.lucare.test.api;

import com.lucare.invoke.annotation.LoginState;
import com.lucare.invoke.annotation.LoginStateType;

/**
 * Created by Lucare.Feng on 2016/3/29.
 */
public interface TestService {
    @LoginState(stateType = LoginStateType.ALLOW_UN_LOGIN)
    public void doTest(String param);
}
