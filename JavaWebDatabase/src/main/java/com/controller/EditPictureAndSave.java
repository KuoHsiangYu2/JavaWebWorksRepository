package com.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTable;

@MultipartConfig(location = "", fileSizeThreshold = 5 * 1024 * 1024, maxFileSize = 1024 * 1024
		* 500, maxRequestSize = 1024 * 1024 * 500 * 5)
@WebServlet("/EditPictureAndSave")
public class EditPictureAndSave extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// editPicturePage.jsp 發送 post請求呼叫這隻 Servlet，
		// 開始把使用者輸入資料更新進資料庫裡面。
		String pictureId = "";
		String pageNoStr = "";
		String title = "";
		String pictureName = "";
		long sizeInBytes = 0L;
		InputStream inputStream = null;
		Collection<Part> parts = request.getParts();

		// 由 parts != null 來判斷此上傳資料是否為HTTP multipart request
		// 如果這是一個上傳資料的表單
		String partName = "";
		String value = "";
		boolean needSaveFile = true;
		if (parts != null) {
			Iterator<Part> iterator = parts.iterator();
			Part part = null;
			while (true == iterator.hasNext()) {
				part = iterator.next();
				partName = part.getName();
				value = request.getParameter(partName);

				// 1. 讀取使用者輸入資料
				if (part.getContentType() == null) {
					if (partName.equals("title")) {
						title = value;
						// System.out.println("title = " + title);
					} else if (partName.equals("pictureName")) {
						pictureName = value;
						// System.out.println("pictureName = " + pictureName);
					} else if (partName.equals("pictureId")) {
						pictureId = value;
						// System.out.println("pictureId = " + pictureId);
					} else if (partName.equals("pageNo")) {
						pageNoStr = value;
					} else {
						// do nothing
					}
				} else {
					if (pictureName != null && pictureName.trim().length() > 0) {
						sizeInBytes = part.getSize();
						inputStream = part.getInputStream();
					} else {
						needSaveFile = false;
						System.out.println("使用者沒有上傳圖片檔");
					}
				}
			} // end of while-loop

		} else {
			System.out.println("error. 此表單不是上傳檔案的表單");
			return;
		}

		if (title == null) {
			title = "";
		}
		if (pictureName == null) {
			pictureName = "";
		}
		int id = 0;
		if (pictureId == null || pictureId.trim().length() == 0) {
			// 如果是空字串或null，
			// 代表沒有接到圖片id的值。
			pictureId = "error";
		}
		try {
			id = Integer.parseInt(pictureId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("解析id失敗。");
			return;
		}

		// 修改標題字串的長度
		title = adjustTitleName(title, 50);

		// 修改檔案名稱字串的長度
		pictureName = adjustFileName(pictureName, 50);

		PictureTable pictureTable = new PictureTable();
		pictureTable.setId(id);
		pictureTable.setTitle(title);
		pictureTable.setPictureName(pictureName);

		if (true == needSaveFile) {
			// 如果使用者有上傳圖片才執行這段程式。
			try {
				byte[] byteBuffer = new byte[(int) sizeInBytes];
				SerialBlob serialBlob = null;
				inputStream.read(byteBuffer);
				serialBlob = new SerialBlob(byteBuffer);
				pictureTable.setFile2(serialBlob);
			} catch (SerialException e1) {
				e1.printStackTrace();
				System.out.println("檔案轉換失敗");
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("檔案轉換失敗");
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("檔案轉換失敗");
			}
		} else {
			// 使用者沒有上傳圖片
			pictureTable.setFile2(null);
		}

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();

		try {
			pictureDao.saveAndUpdatePictureById(id, pictureTable, needSaveFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("資料庫更新資料失敗。");
		}

		int totalPages = 1;
		int pageNo = 1;

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

		List<PictureTable> pictureTableList = null;
		try {
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
	}// end of doPost() method

	private String adjustFileName(String fileName, int maxLength) {
		// 修改含有副檔名的檔案名稱
		int length = fileName.length();
		if (length <= maxLength) {
			return fileName;
		}
		int pointIndex = fileName.lastIndexOf(".");
		int endSubLength = fileName.length() - pointIndex - 1;
		fileName = fileName.substring(0, maxLength - 1 - endSubLength) + "." + fileName.substring(pointIndex + 1);
		return fileName;
	}// end of adjustFileName() method

	private String adjustTitleName(String titleName, int maxLength) {
		// 修改標題長度
		int length = titleName.length();
		if (length <= maxLength) {
			return titleName;
		}
		titleName = titleName.substring(0, maxLength);
		return titleName;
	}// end of adjustTitleName() method

}
