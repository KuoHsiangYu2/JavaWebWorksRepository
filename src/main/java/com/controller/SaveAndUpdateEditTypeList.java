package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.IClassTypeTableDao;
import com.dao.IPictureTableDao;
import com.dao.impl.ClassTypeTableMSSQLDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableTwo;

@WebServlet("/SaveAndUpdateEditTypeList")
public class SaveAndUpdateEditTypeList extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        IClassTypeTableDao classTypeDao = new ClassTypeTableMSSQLDao();
        IPictureTableDao pictureTableDao = new PictureTableMSSQLDao();

        /* 從資料庫撈出 <圖片分類> 清單 */
        List<String> oldClassTypeList = classTypeDao.getClassTypeStringList();

        /* 使用者傳進來的 <圖片分類> 清單 */
        List<String> newClassTypeList = new ArrayList<String>();

        String[] typeList = request.getParameterValues("typeList");
        int length = typeList.length;

        for (int i = 0; i < length; i++) {
            if (typeList[i] != null && typeList[i].trim().length() != 0) {
                newClassTypeList.add(typeList[i]);
            }
        }

        System.out.println("newClassTypeList.get(0) : " + newClassTypeList.get(0));
        if (false == newClassTypeList.get(0).equals("未分類")) {
            System.out.println("error [未分類]欄位 必須存在");
            response.sendRedirect(request.getContextPath() + "/editTypeNameList.jsp");
            return;
        }

        if (true == oldClassTypeList.equals(newClassTypeList)) {
            /* 資料庫舊清單 與 使用者修改的清單一模一樣，不需要再改資料庫資料。 */
            /* 返回首頁。 */
            System.out.println("old equals new == true");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        } else {
            System.out.println("old equals new == false");

            /* 先刪除 ClassTypeTable 裡的所有資料 */
            classTypeDao.deleteAll();

            /* 接著插入新的分類資料 */
            classTypeDao.insertAll(newClassTypeList);

            /* 然後取出所有圖片資料裡的分類進行比對 */
            List<PictureTableTwo> pictureTable = pictureTableDao.getAllPicture();
            if (pictureTable.size() == 0) {
                /* 如果圖片庫目前沒資料，就先回去主畫面。 */
                /* 後面的操作可以省略。 */
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }

            List<PictureTableTwo> updateTypeNameList = new ArrayList<PictureTableTwo>();

            int length2 = pictureTable.size();
            String typeName = "";
            for (int i = 0; i < length2; i++) {
                typeName = pictureTable.get(i).getTypeName();
                if (false == newClassTypeList.contains(typeName)) {
                    /* 如果圖片裡的分類項目沒有包含在 新版的分類項目名單裡的話。 */
                    /* 進行記錄 */
                    PictureTableTwo ptObj = new PictureTableTwo();
                    ptObj.setId(pictureTable.get(i).getId());// 設定id值
                    updateTypeNameList.add(ptObj);
                    ptObj = null;
                }
            }

            /* 把戳記的圖片分類資料重新改成 [未分類] */
            pictureTableDao.resetTypeNameList(updateTypeNameList);

            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

    }/* end of doPost() method */
}
