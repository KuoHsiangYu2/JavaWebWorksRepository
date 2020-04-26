package com.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTable;

@WebServlet("/GetAllPicturePath")
public class GetAllPicturePath extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get method
		// https://sites.google.com/site/yutingnote/sql/mssqlqudedinbiziliao

		String pageNoStr = request.getParameter("pageNo");
		int pageNo = 1;

		if (pageNoStr == null) {
			pageNoStr = "1";
		}
		try {
			pageNo = Integer.parseInt(pageNoStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pageNo = 1;
		}

		if (pageNo < 1) {
			pageNo = 1;
		}

		int totalPages = 1;

		List<PictureTable> pictureTableList = null;
		try {
			IPictureTableDao pictureDao = new PictureTableMSSQLDao();
			pictureTableList = pictureDao.getPagePictureNoBlob(pageNo);
			totalPages = pictureDao.getTotalPages();
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("pictureTableList", pictureTableList);// 一頁五筆的圖片清單
		request.setAttribute("pageNo", pageNo);// 頁面編號
		request.setAttribute("totalPages", totalPages);// 總共有幾頁

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
		requestDispatcher.forward(request, response);
		// System.out.println("request.getContextPath() : " + request.getContextPath());
		// response.sendRedirect(request.getContextPath() + "/viewAllPicture.jsp");
	}
}
