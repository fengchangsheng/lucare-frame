package com.lucare.invoke.servlet;

import com.alibaba.fastjson.JSON;
import com.lucare.common.exception.MessageException;
import com.lucare.common.utils.CommonUtils;
import com.lucare.invoke.*;
import com.lucare.invoke.annotation.LoginStateType;
import com.lucare.invoke.utils.CookieUtils;
import com.lucare.tsession.TSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
public class JSONInvokeServlet extends AbstractJSONHttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(JSONInvokeServlet.class);
    private String invokerName = null;
    protected String serviceName = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String serviceName = config.getInitParameter("service");
        this.invokerName = config.getServletName();
        if(CommonUtils.isEmpty(serviceName)){
            serviceName = invokerName.replace("Invoke","Service");
        }
        this.serviceName = serviceName;
    }

    @Override
    protected void handleJSONRequest(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
        String subtime = request.getParameter("subtime");
        if(subtime == null){
            throw new IllegalArgumentException("please add subtime");
        }else{
            String methodName = request.getParameter("method");
            if(methodName == null){
                throw new IllegalArgumentException("please add methodName");
            }else{
                HttpInvokeHolder invokeHolder = new HttpInvokeHolder(subtime,request,response);
                this.doInvoke(methodName,invokeHolder);
                if (response.getStatus() == 200){
                    HashMap resMap = new HashMap(8, 1.0F);
                    resMap.put("subtime",subtime);
                    resMap.put("method", methodName);
                    resMap.put("status", Integer.valueOf(invokeHolder.getStatus()));
                    resMap.put("desc",invokeHolder.getDesc());
                    String output = JSON.toJSONString(resMap);
                    response.getWriter().write(output);
                    response.flushBuffer();
                }
            }

        }

    }

    protected void doInvoke(String methodName, HttpInvokeHolder invokeHolder) {
        long start = System.currentTimeMillis();
        boolean invokeResult = false;
        boolean isMessage = false;
        MethodHolder methodHolder = null;
        Object[] invokeArgs = null;

        try {
            ServiceHolder t = InvokeManager.foundServiceHolder(this.serviceName);
            if (t == null) {
                throw new InvokeNotFoundException("\"unfound method=[\" + methodName + \"] from service=[\" + this.serviceName + \"] @ \" + this.invokerName");
            }

            methodHolder = t.getMethodHolder(methodName);
            if (methodHolder == null) {
                throw new InvokeNotFoundException("unfound method=[" + methodName + "] from service=[" + this.serviceName);
            }

            this.checkState(methodHolder,invokeHolder);
            if (!invokeHolder.isOk()) {
                return;
            }

            invokeArgs = HttpInvokeHelper.extractInvokeParameters(methodHolder.getArgsNames(), methodHolder.getArgsMarkers(), invokeHolder.getHttpRequest());
            InvokeManager.registerInvokeHolder(invokeHolder);
            methodHolder.getMethod().invoke(t.getService(), invokeArgs);
        } catch (Throwable throwable) {
            Throwable cause = CommonUtils.foundRealThrowable(throwable);
            String reson = CommonUtils.formatThrowable(cause);
            isMessage = cause instanceof MessageException;
            if (!isMessage){
                Map desc = invokeHolder.getHttpRequest().getParameterMap();
                HashMap parameters = new HashMap();
                if (desc != null) {
                    Iterator iterator = desc.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        parameters.put(key, Arrays.toString((Object[]) desc.get(key)));
                    }
                }

                logger.error("invoke service={}, method={}, userid={}, args={}, parameters={} with:{}", new Object[]{this.serviceName, methodName, "", invokeArgs, parameters, reson});
            }

            if (cause instanceof MustLoginException) {
                redirectLogin(invokeHolder);
            }

            if (cause instanceof InvokeNotFoundException) {
                redirectNotFound(invokeHolder);
            } else {
                String desc = "系统繁忙,请稍后重试";
                if (invokeHolder.isDebug()) {
                    desc = CommonUtils.formatThrowableForHtml(cause);
                } else if(isMessage) {
                    desc = cause.getMessage();
                }

                invokeHolder.setDesc(desc);
            }
        }finally {
            InvokeManager.releaseInvoke();
        }

    }

    protected void checkState(MethodHolder mHolder, HttpInvokeHolder invokeHolder) {
        if (!invokeHolder.isDebug()) {
            boolean isLogin = TSessionManager.get(CookieUtils.getCookie("sid", invokeHolder.getHttpRequest())) != null;
            if (mHolder.getLoginState() == LoginStateType.MUST_LOGIN && !isLogin){
                redirectLogin(invokeHolder);
            }else if (mHolder.getLoginState() == LoginStateType.FORBIN_LOGIN && isLogin){
                forbiddenLogin(invokeHolder);
            }
        }
    }

    private static void forbiddenLogin(InvokeHolder invokeHolder) {
        invokeHolder.setResult("/account.html");
        invokeHolder.setDesc(302, (String)null);
    }

    private static void redirectNotFound(InvokeHolder invokeHolder) {
        invokeHolder.setResult("/404.html");
        invokeHolder.setDesc(404, (String)null);
    }

    private static void redirectLogin(InvokeHolder invokeHolder) {
        invokeHolder.setResult("/user-login.html");
        invokeHolder.setDesc(302, (String)null);
    }
}



















