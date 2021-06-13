package com.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableTwo;
import com.util.GlobalService;

//MultipartConfig的屬性說明:
//location: 上傳之表單資料與檔案暫時存放在Server端之路徑，此路徑必須存在，否則Web Container將丟出例外。
//
//fileSizeThreshold: 上傳檔案的大小臨界值，超過此臨界值，上傳檔案會用存放在硬碟，否則存放在主記憶體。
//
//maxFileSize: 上傳單一檔案之長度限制，如果超過此數值，Web Container會丟出例外
//
//maxRequestSize: 上傳所有檔案之總長度限制，如果超過此數值，Web Container會丟出例外
@MultipartConfig(location = "",
        fileSizeThreshold = 25 * 1024 * 1024,
        maxFileSize = 25 * 1024 * 1024,
        maxRequestSize = 25 * 1024 * 1024)
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
        String typeName = "";
        String pictureName = "";
        long sizeInBytes = 0L;
        InputStream inputStream = null;
        Collection<Part> parts = request.getParts();

        // 由 parts != null 來判斷此上傳資料是否為HTTP multipart request
        // 如果這是一個上傳資料的表單
        if (parts != null) {
            Iterator<Part> iterator = parts.iterator();
            Part part = null;
            while (true == iterator.hasNext()) {
                part = iterator.next();
                String partName = part.getName();
                String value = request.getParameter(partName);

                // 1. 讀取使用者輸入資料
                if (part.getContentType() == null) {
                    if (partName.equals("title")) {
                        title = value;
                    } else if (partName.equals("typeName")) {
                        typeName = value;
                    } else {
                        // do nothing
                    }
                } else {
                    pictureName = GlobalService.getFileName(part);
                    System.out.println("pictureName = " + pictureName);
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
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("uploadFile.jsp");
            requestDispatcher.forward(request, response);
            return;
        }

        // 修改標題字串的長度
        title = GlobalService.adjustTitleName(title, 200);

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
        pictureName = GlobalService.adjustFileName(pictureName, 200);

        // 修改分類字串的長度
        typeName = GlobalService.adjustTitleName(typeName, 50);

        PictureTableTwo pictureTable = new PictureTableTwo();
        pictureTable.setTitle(title);
        pictureTable.setPictureName(pictureName);
        pictureTable.setTypeName(typeName);

        IPictureTableDao pictureDao = new PictureTableMSSQLDao();

        try {
            pictureDao.savePicture(pictureTable);
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg.put("all", "資料庫存取失敗。");
        }

        // String rootDirectory = this.getServletContext().getRealPath("/");
        File imageFolder = new File("C:/imageData/");
        if (false == imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            byte[] byteBuffer = new byte[(int) sizeInBytes];
            inputStream.read(byteBuffer);
            String imageFile = "C:/imageData/" + pictureName;
            fileOutputStream = new FileOutputStream(new File(imageFile));
            fileOutputStream.write(byteBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorMsg.put("all", "檔案存取失敗");
        } catch (IOException e) {
            e.printStackTrace();
            errorMsg.put("all", "檔案存取失敗");
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

        if (errorMsg.size() != 0) {
            // 進行資料庫存取失敗。
            // 返回首頁
            System.out.println("資料庫存取失敗 或 檔案IO失敗。");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("uploadFile.jsp");
            requestDispatcher.forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/uploadFile.jsp");
    }

}
