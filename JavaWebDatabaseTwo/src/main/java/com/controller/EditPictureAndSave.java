package com.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.dao.IClassTypeTableDao;
import com.dao.IPictureTableDao;
import com.dao.impl.ClassTypeTableMSSQLDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableTwo;
import com.util.GlobalService;

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
		String backBlockPicture = "";
		long sizeInBytes = 0L;
		InputStream inputStream = null;
		Collection<Part> parts = request.getParts();

		// 由 parts != null 來判斷此上傳資料是否為HTTP multipart request
		// 如果這是一個上傳資料的表單
		String partName = "";
		String value = "";
		String typeName = "";
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
					} else if (partName.equals("pictureId")) {
						pictureId = value;
					} else if (partName.equals("pageNo")) {
						pageNoStr = value;
					} else if (partName.equals("typeName")) {
						typeName = value;
					} else if (partName.equals("backBlockPicture")) {
						backBlockPicture = value;
					} else {
						// do nothing
					}
				} else {
					pictureName = GlobalService.getFileName(part);
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
			return;
		}

		// 修改標題字串的長度
		title = GlobalService.adjustTitleName(title, 50);

		PictureTableTwo pictureTable = new PictureTableTwo();
		pictureTable.setId(id);
		pictureTable.setTitle(title);
		pictureTable.setTypeName(typeName);

		IPictureTableDao pictureDao = new PictureTableMSSQLDao();

		if (true == needSaveFile) {
			// 如果使用者有上傳圖片才執行這段程式。

			System.out.println("origin pictureName : " + pictureName);
			if (pictureName.indexOf(":\\") != -1) {
				// Internet Explorer 跟 Microsoft Edge 上傳檔案時，
				// 檔案名稱包含檔案的絕對路徑在內，因此必須預先處理把絕對路徑拿掉。
				// 只保留檔案名稱
				int index = pictureName.lastIndexOf("\\");

				// 進行字串切割，把絕對路徑切掉，只保留檔案名稱。
				pictureName = pictureName.substring(index + 1, pictureName.length());
				System.out.println("substring pictureName : " + pictureName);
			}

			String fileId = GlobalService.getTimeStampStr();
			pictureName = fileId + "_" + pictureName;
			pictureName = pictureName.replace(':', '\uFF1A');

			// 修改檔案名稱字串的長度
			pictureName = GlobalService.adjustFileName(pictureName, 50);

			pictureTable.setPictureName(pictureName);

			File imageFolder = new File("C:/imageData/");
			if (false == imageFolder.exists()) {
				imageFolder.mkdirs();
			}

			// 把硬碟上舊的圖片檔案刪除。
			PictureTableTwo pictureTable2 = pictureDao.getFullPictureDataById(id);
			String filename = pictureTable2.getPictureName();
			File pictureFile = new File("C:/imageData/" + filename);
			boolean isDelete = pictureFile.delete();
			System.out.println("pictureFile.delete()：" + isDelete);

			// String rootDirectory = this.getServletContext().getRealPath("/");
			FileOutputStream fileOutputStream = null;
			try {
				byte[] byteBuffer = new byte[(int) sizeInBytes];
				inputStream.read(byteBuffer);
				String imageFile = "C:/imageData/" + pictureName;
				fileOutputStream = new FileOutputStream(new File(imageFile));
				fileOutputStream.write(byteBuffer);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null) {
					fileOutputStream.close();
					fileOutputStream = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			}
		} else {
			// 使用者沒有上傳圖片
			pictureTable.setPictureName(null);
		}

		try {
			pictureDao.saveAndUpdatePictureById(id, pictureTable);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("資料庫更新資料失敗。");
		}

		int totalPages = 0;
		int pageNo = 1;

		if (pageNoStr == null || pageNoStr.trim().length() == 0) {
			pageNoStr = "1";
		}
		try {
			pageNo = Integer.parseInt(pageNoStr);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			pageNo = 1;
		}

		List<PictureTableTwo> pictureTableList = null;
		try {
			pictureTableList = pictureDao.getPagePicture(pageNo);
			totalPages = pictureDao.getTotalPages();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (backBlockPicture != null && backBlockPicture.trim().length() != 0) {
			// 回到圖片磚畫面。
			response.sendRedirect(request.getContextPath() + "/viewAllBlockPicture.jsp");
			return;
		}

		IClassTypeTableDao classTypeDao = new ClassTypeTableMSSQLDao();
		List<String> classTypeList = classTypeDao.getClassTypeStringList();

		request.setAttribute("classTypeList", classTypeList);// 分類清單
		request.setAttribute("pictureTableList", pictureTableList);// 一頁五筆的圖片清單
		request.setAttribute("pageNo", pageNo);// 頁面編號
		request.setAttribute("totalPages", totalPages);// 總共有幾頁

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("viewAllPicture.jsp");
		requestDispatcher.forward(request, response);
	}// end of doPost() method
}
