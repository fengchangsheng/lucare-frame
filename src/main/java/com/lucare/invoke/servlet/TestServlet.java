package com.lucare.invoke.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
@WebServlet(name = "TestServlet")
public class TestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String res = upload("/upload/files/",request);
        response.getWriter().write(res);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    public static String upload( String urlPath, HttpServletRequest request){
        //存入数据库文件的路径
        String modelpath = urlPath;
        String storeName = null;
        //1.判断文件上传的是否没有子文件
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart) {
            // 2得到存放文件的真实路径
            String realpath = request.getSession().getServletContext().getRealPath(urlPath);
            System.out.println(realpath);
            File dir = new File(realpath);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("utf-8");
            try {
                List<FileItem> items = upload.parseRequest(request);
                for(FileItem item : items) {
                    if(item.isFormField()) {
                        String name = item.getFieldName();
                        String value = item.getString("utf-8");
                        System.out.println(name + "=" + value);
                    }
                    else {
                        //上传文件重命名再存储
                        storeName = rename(item.getName().toString());
                        item.write(new File(dir,storeName));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("该文件不能上传");
        }
        modelpath += storeName;//存入数据库文件后面拼接文件名
        return modelpath;
    }

    public static String rename(String name) {
        Long now = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        Long random = (long) (Math.random() * now);
        String fileName = now + "" + random;
        if (name.indexOf(".") != -1) {
            fileName += name.substring(name.lastIndexOf("."));
        }
        return fileName;
    }
}
