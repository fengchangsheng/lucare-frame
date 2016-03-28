package com.lucare.invoke.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
public abstract class AbstractHttpServlet extends HttpServlet {

    public AbstractHttpServlet(){

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        this.handleRequest(request, response);
    }

    protected abstract void handleRequest(HttpServletRequest var1, HttpServletResponse var2) throws IOException, ServletException;


}
