package com.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableTwo;

@WebServlet("/EditPicture")
public class EditPicture extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 當使用者按下[編輯]連結時，先由這隻Servlet進行預處理。
		// 修改圖片

		String backBlockPicture = request.getParameter("backBlockPicture");

		int index = 0;
		String indexStr = "";

		int pageNo = 1;
		String pageNoStr = "";

		indexStr = request.getParameter("id");
		// System.out.println("indexStr = [" + indexStr + "]");
		if (indexStr == null || indexStr.trim().length() == 0) {
			indexStr = "0";
		}
		try {
			index = Integer.parseInt(indexStr);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			index = 0;
		}

		pageNoStr = request.getParameter("pageNo");
		if (pageNoStr == null || pageNoStr.trim().length() == 0) {
			pageNoStr = "1";
		}
		try {
			pageNo = Integer.parseInt(pageNoStr);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			pageNo = 1;
		}

		PictureTableTwo pictureTable = new PictureTableTwo();

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();

		try {
			pictureTable = pictureDao.getFullPictureDataById(index);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("資料庫存取失敗。");
		}

		request.setAttribute("pictureTable", pictureTable);// 要修改的那筆資料。
		request.setAttribute("pageNo", pageNo);// 原本使用者瀏覽的頁數。
		request.setAttribute("backBlockPicture", backBlockPicture);// 判斷是否要返回[viewAllBlockPicture.jsp]

		// 轉往 editPicturePage.jsp 修改頁面。
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("editPicturePage.jsp");
		requestDispatcher.forward(request, response);
	}// end of doGet() method
}