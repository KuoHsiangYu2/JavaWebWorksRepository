package com.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.dao.IClassTypeTableDao;
import com.dao.impl.ClassTypeTableMSSQLDao;

@WebServlet("/GetTypeNameList")
public class GetTypeNameList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetTypeNameList() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		/* Setting the encoding of the output data to the web page */
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		IClassTypeTableDao classTypeDao = new ClassTypeTableMSSQLDao();

		// 從資料庫撈出 <圖片分類> 清單
		List<String> classTypeList = classTypeDao.getClassTypeStringList();

		JSONArray jsonArray = new JSONArray();
		for (int i = 0, len = classTypeList.size(); i < len; i++) {
			jsonArray.put(classTypeList.get(i));
		}

		// 一定要加這行程式碼印出來。才可以讓AJAX把動態產生的下拉式選單製作出來。
		response.getWriter().print(jsonArray.toString());
	}
}