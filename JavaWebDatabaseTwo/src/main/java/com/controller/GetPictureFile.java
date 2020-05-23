package com.controller;

import static com.util.GlobalService.stringIncludes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableTwo;
import com.util.GlobalService;

@WebServlet("/GetPictureFile")
public class GetPictureFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		String indexStr = "";
		int index = 0;
		indexStr = request.getParameter("id");

		// System.out.println("indexStr = " + indexStr);

		if (indexStr == null) {
			// 如果沒有 query string 就結束這段程式。
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
			requestDispatcher.forward(request, response);
			return;
		}

		try {
			index = Integer.parseInt(indexStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// 如果不是合法數字就結束這段程式。
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
			requestDispatcher.forward(request, response);
			return;
		}

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();
		PictureTableTwo pictureTable = pictureDao.getFullPictureDataById(index);

		if (pictureTable == null) {
			// 查無資料，結束程式。
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
			requestDispatcher.forward(request, response);
			return;
		}

		String pictureName = pictureTable.getPictureName();

		// String mimeType = this.getServletContext().getMimeType(pictureName);
		// System.out.println("mimeType = " + mimeType);

		// 原文網址：https://kknews.cc/code/n5xn4yq.html
		String userAgent = request.getHeader("User-agent");
		userAgent = userAgent.toLowerCase();
		System.out.println("userAgent -> " + userAgent);

		boolean isInternetExplorer = false;
		if (stringIncludes(userAgent, "MSIE") || stringIncludes(userAgent, "trident")
				|| stringIncludes(userAgent, "wow64") || stringIncludes(userAgent, "edge")
				|| stringIncludes(userAgent, "edg")) {
			System.out.println("Yes. It's Internet Explorer or Microsoft Edge Browser.");
			isInternetExplorer = true;
		} else {
			isInternetExplorer = false;
		}

		System.out.println("1 pictureName -> " + pictureName);

		InputStream inputStream = null;
		long fileLength = 0L;
		// Java IO 從硬碟把圖片檔案讀取出來。
		File imageFile = new File("C:/imageData/" + pictureName);
		inputStream = new FileInputStream(imageFile);
		fileLength = imageFile.length();

		// 去除掉時間戳記，還原成原始的檔案名稱。
		pictureName = GlobalService.revertFileName(pictureName);

		if (false == isInternetExplorer) {
			// Google, Firefox, Opera瀏覽器
			pictureName = new String(pictureName.getBytes(), "ISO8859-1");
		} else {
			// Internet Explorer, Microsoft Edge瀏覽器
			pictureName = URLEncoder.encode(pictureName, "UTF-8");
			pictureName = pictureName.replace("+", "%20");
		}

		System.out.println("2 pictureName -> " + pictureName);

		// 清空response
		response.reset();
		// 設定response的Header
		/* FireFox瀏覽器 必須在 fileName前後加上 \" 才能避免瀏覽器擷取到空格就停止擷取檔案名稱。 */
		response.addHeader("Content-Disposition", "attachment;filename=\"" + pictureName + "\"");
		response.addHeader("Content-Length", String.valueOf(fileLength));
		response.setContentType("application/octet-stream");

		OutputStream outputStream = response.getOutputStream();
		byte[] byteBuffer = new byte[(int) fileLength];
		int length = 0;
		try {
			length = inputStream.read(byteBuffer);
			outputStream.write(byteBuffer, 0, length);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}
	}// end of doGet() method
}