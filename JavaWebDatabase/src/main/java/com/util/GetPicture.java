package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTable;

@WebServlet("/GetPicture")
public class GetPicture extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get method
		// 透過id來回傳一張圖片。
		// 使用 query string 來傳送id值。
		// 接著開始從 query string 取出要抓的圖片編號。
		String indexStr = "";
		int index = 0;
		indexStr = request.getParameter("id");

		// System.out.println("indexStr = " + indexStr);

		if (indexStr == null) {
			// 如果沒有 query string 就結束這段程式。
			System.out.println("如果沒有 query string 就結束這段程式。");
			return;
		}

		try {
			index = Integer.parseInt(indexStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// 如果不是合法數字就結束這段程式。
			System.out.println("如果不是合法數字就結束這段程式。");
			return;
		}

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();
		PictureTable pictureTable = pictureDao.getPictureWithBlobById(index);

		if (pictureTable == null) {
			// 查無資料，結束程式。
			System.out.println("查無資料，結束程式。");
			return;
		}

		String pictureName = pictureTable.getPictureName();
		InputStream inputStream = null;
		try {
			inputStream = pictureTable.getFile2().getBinaryStream();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		OutputStream outputStream = null;

		String mimeType = this.getServletContext().getMimeType(pictureName);
		// System.out.println("mimeType : " + mimeType);
		response.setContentType(mimeType);
		outputStream = response.getOutputStream();
		byte[] byteBuffer = new byte[8192];
		int length = 0;
		while ((length = inputStream.read(byteBuffer)) != -1) {
			outputStream.write(byteBuffer, 0, length);
		}

		if (outputStream != null) {
			outputStream.close();
			outputStream = null;
		}
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
	}// end of doGet method
}