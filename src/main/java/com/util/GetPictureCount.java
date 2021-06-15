package com.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;

@WebServlet("/GetPictureCount")
public class GetPictureCount extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* 回傳總共有幾筆資料 */
        IPictureTableDao pictureDao = new PictureTableMSSQLDao();
        int result = 0;
        try {
            result = pictureDao.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("讀取資料總筆數失敗");
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.print(String.valueOf(result));
    }// end of doGet() method
}