package com.lucare.invoke.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
public abstract class AbstractJSONHttpServlet extends AbstractHttpServlet {

    public AbstractJSONHttpServlet() {
    }

    @Override
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        this.handleJSONRequest(request, response);
    }

    protected abstract void handleJSONRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
