package com.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

//https://www.facebook.com/groups/748837991920629/permalink/1600114716792948/
// http://stackoverflow.com/questions/913626/what-should-a-multipart-http-request-with-multiple-files-look-like
//啟動檔案上傳的功能：
//1. <form>標籤的 method屬性必須是"POST", 而且
//  enctype屬性必須是"multipart/form-data"
//  注意：enctype屬性的預設值為"application/x-www-form-urlencoded"
//2. 定義可以挑選上傳檔案的表單欄位：
// <input type='file' name='user-defined_name' />
//
//所謂 HTTP multipart request是指由Http客戶端(如瀏覽器)所建構的ㄧ種請求，
//用來上傳一般的表單資料(form data)與檔案。
//參考網頁：http://stackoverflow.com/questions/913626/what-should-a-multipart-http-request-with-multiple-files-look-like
//
//Servlet規格書一直到Servlet 3.0才提出標準API將檔案上傳的功能標準化。
//
//在Servlet 3.0中，若要能夠處理瀏覽器送來的HTTP multipart request, 
//我們撰寫的Servlet程式必須以註釋
// 『javax.servlet.annotation.MultipartConfig』來加以說明。
//
//MultipartConfig的屬性說明:
//location: 上傳之表單資料與檔案暫時存放在Server端之路徑，此路徑必須存在，否則Web Container將丟出例外。
//
//fileSizeThreshold: 上傳檔案的大小臨界值，超過此臨界值，上傳檔案會用存放在硬碟，
//                 否則存放在主記憶體。
//
//maxFileSize: 上傳單一檔案之長度限制，如果超過此數值，Web Container會丟出例外
//
//maxRequestSize: 上傳所有檔案之總長度限制，如果超過此數值，Web Container會丟出例外
@MultipartConfig(location = "", fileSizeThreshold = 5 * 1024 * 1024, maxFileSize = 1024 * 1024
		* 500, maxRequestSize = 1024 * 1024 * 500 * 5)
@WebServlet("/SavePicture")
public class SavePicture extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 把使用者上傳的資料儲存起來。
		// 接收 post請求
		// 取出HTTP multipart request內所有的parts

		// 準備存放錯誤訊息的Map物件
		Map<String, String> errorMsg = new HashMap<String, String>();

		request.setAttribute("errorMsg", errorMsg);

		String title = "";
		String pictureName = "";
		long sizeInBytes = 0L;
		InputStream inputStream = null;
		Collection<Part> parts = request.getParts();

		// 由 parts != null 來判斷此上傳資料是否為HTTP multipart request
		// 如果這是一個上傳資料的表單
		String partName = "";
		String value = "";
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
					} else if (partName.equals("pictureName")) {
						pictureName = value;
					} else {
						// do nothing
					}
				} else {
					if (pictureName != null && pictureName.trim().length() > 0) {
						sizeInBytes = part.getSize();
						inputStream = part.getInputStream();
					} else {
						System.out.println("error. 必須挑選圖片檔");
						errorMsg.put("file2", "必須挑選圖片檔");
					}
				}
			} // end of while-loop

		} else {
			System.out.println("error. 此表單不是上傳檔案的表單");
			return;
		}

		if (title.trim().length() == 0 || title == null) {
			errorMsg.put("title", "必須輸入標題");
		}

		if (pictureName.trim().length() == 0 || pictureName == null) {
			errorMsg.put("file2", "必須挑選圖片檔");
		}

		if (errorMsg.size() != 0) {
			// 上傳表單資料有問題，
			// 結束這支Servlet程式，
			// 就不執行資料庫存檔的程式，
			// 同時返回原始頁面。
			System.out.println("使用者輸入資料錯誤。");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
			requestDispatcher.forward(request, response);
			return;
		}

		// 修改標題字串的長度
		title = adjustTitleName(title, 50);

		// 修改檔案名稱字串的長度
		pictureName = adjustFileName(pictureName, 50);

		PictureTable pictureTable = new PictureTable();
		pictureTable.setTitle(title);
		pictureTable.setPictureName(pictureName);

		boolean isBlobDataSuccess = true;
		// InputStream 轉 Blob-start
		try {
			byte[] byteBuffer = new byte[(int) sizeInBytes];
			SerialBlob serialBlob = null;
			inputStream.read(byteBuffer);
			serialBlob = new SerialBlob(byteBuffer);
			pictureTable.setFile2(serialBlob);
		} catch (SerialException e1) {
			e1.printStackTrace();
			errorMsg.put("all", "檔案轉換失敗");
			isBlobDataSuccess = false;
		} catch (IOException e1) {
			e1.printStackTrace();
			errorMsg.put("all", "檔案轉換失敗");
			isBlobDataSuccess = false;
		} catch (SQLException e1) {
			e1.printStackTrace();
			errorMsg.put("all", "檔案轉換失敗");
			isBlobDataSuccess = false;
		}
		// InputStream 轉 Blob-end

		if (false == isBlobDataSuccess) {
			System.out.println("圖片檔案轉換失敗。");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
			requestDispatcher.forward(request, response);
			return;
		}

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();

		try {
			pictureDao.savePicture(pictureTable);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.put("all", "資料庫存取失敗。");
		}

		if (errorMsg.size() != 0) {
			// 進行資料庫存取失敗。
			// 返回首頁
			System.out.println("資料庫存取失敗。");
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
			requestDispatcher.forward(request, response);
			return;
		}

		// RequestDispatcher requestDispatcher =
		// request.getRequestDispatcher("index.jsp");
		// requestDispatcher.forward(request, response);
		// System.out.println("request.getContextPath() = " + request.getContextPath());
		response.sendRedirect(request.getContextPath() + "/index.jsp");
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
