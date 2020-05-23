package com.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.impl.ClassTypeTableMSSQLDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableSearchType;
import com.model.PictureTableTwo;

@WebServlet("/GetAllPicturePath")
public class GetAllPicturePath extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get method
		// https://sites.google.com/site/yutingnote/sql/mssqlqudedinbiziliao

		String typeName = request.getParameter("typeName");

		String searchString = request.getParameter("searchString");

		String pageNoStr = request.getParameter("pageNo");
		int pageNo = 1;

		if (pageNoStr == null || pageNoStr.trim().length() == 0) {
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

		int totalPages = 0;

		List<PictureTableTwo> pictureTableList = null;
		PictureTableMSSQLDao pictureDao = new PictureTableMSSQLDao();

		try {
			if (searchString != null && searchString.trim().length() != 0) {
				// 進入查詢模式
				// 依使用者的搜尋字把資料取出來
				request.setAttribute("searchString", searchString);// 搜尋字串
				request.setAttribute("typeName", null);// 分類
				pictureTableList = pictureDao.getPagePicture(pageNo, searchString, PictureTableSearchType.searchString);
				totalPages = pictureDao.getTotalPages(searchString, PictureTableSearchType.searchString);
			} else if (typeName != null && typeName.trim().length() != 0 && !typeName.equals("全部")) {
				// 依據圖片分類來撈資料
				request.setAttribute("searchString", null);// 搜尋字串
				request.setAttribute("typeName", typeName);// 分類
				pictureTableList = pictureDao.getPagePicture(pageNo, typeName, PictureTableSearchType.typeName);
				totalPages = pictureDao.getTotalPages(typeName, PictureTableSearchType.typeName);
			} else {
				// 撈出全部資料。
				request.setAttribute("searchString", null);// 搜尋字串
				request.setAttribute("typeName", null);// 分類

				pictureTableList = pictureDao.getPagePicture(pageNo);
				totalPages = pictureDao.getTotalPages();
			}
		} catch (Exception e) {
			System.out.println("資料庫存取失敗");
			e.printStackTrace();
		}

		ClassTypeTableMSSQLDao classTypeDao = new ClassTypeTableMSSQLDao();
		List<String> classTypeList = classTypeDao.getClassTypeStringList();

		request.setAttribute("classTypeList", classTypeList);// 分類清單
		request.setAttribute("pictureTableList", pictureTableList);// 一頁五筆的圖片清單
		request.setAttribute("pageNo", pageNo);// 頁面編號
		request.setAttribute("totalPages", totalPages);// 總共有幾頁

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
		requestDispatcher.forward(request, response);
		// System.out.println("request.getContextPath() : " + request.getContextPath());
		// response.sendRedirect(request.getContextPath() + "/viewAllPicture.jsp");
	}// end of doGet() method
}
