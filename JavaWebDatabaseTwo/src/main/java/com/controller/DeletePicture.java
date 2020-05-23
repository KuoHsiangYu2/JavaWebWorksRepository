package com.controller;

import java.io.File;
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
import com.model.PictureTableTwo;

@WebServlet("/DeletePicture")
public class DeletePicture extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// System.out.println("DeletePicture doGet");
		String idStr = request.getParameter("id");
		int id = 0;

		String pageNoStr = request.getParameter("pageNo");
		int pageNo = 1;
		int totalPages = 1;

		if (idStr == null || idStr.trim().length() == 0) {
			System.out.println("沒有收到id值，停止DeletePicture程式。");
			return;
		}

		try {
			id = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("不合法的id字串。");
			return;
		}

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();

		// 把硬碟上的圖片檔案刪除。
		PictureTableTwo pictureTable = pictureDao.getFullPictureDataById(id);
		String filename = pictureTable.getPictureName();
		File pictureFile = new File("C:/imageData/" + filename);
		boolean isDelete = pictureFile.delete();
		System.out.println("pictureFile.delete()：" + isDelete);

		try {
			pictureDao.deletePictureById(id);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("資料刪除失敗");
		}

		if (pageNoStr == null || pageNoStr.trim().length() == 0) {
			pageNoStr = "1";
		}
		try {
			pageNo = Integer.parseInt(pageNoStr);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			System.out.println("頁數解析失敗。");
			pageNo = 1;
		}

		try {
			// 取得總頁數。
			totalPages = pictureDao.getTotalPages();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (pageNo > totalPages) {
			// 如果刪除資料後，
			// 當前頁數比總頁數大，
			// 重新修正，把當前頁數設定為總頁數。
			pageNo = totalPages;
		}

		List<PictureTableTwo> pictureTableList = null;
		try {
			pictureTableList = pictureDao.getPagePicture(pageNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("pictureTableList", pictureTableList);// 一頁五筆的圖片清單
		request.setAttribute("pageNo", pageNo);// 頁面編號
		request.setAttribute("totalPages", totalPages);// 總共有幾頁

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
		requestDispatcher.forward(request, response);
	}// end of doGet() method
}