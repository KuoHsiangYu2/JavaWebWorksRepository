package com.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.dao.IPictureTableDao;
import com.dao.impl.PictureTableMSSQLDao;
import com.model.PictureTableTwo;

@WebServlet("/GetPictureTableList")
public class GetPictureTableList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		/* Setting the encoding of the output data to the web page */
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		IPictureTableDao pictureTableDao = new PictureTableMSSQLDao();
		List<PictureTableTwo> pictureTableList = pictureTableDao.getAllPicture();

		Map<String, String> pictureTableObj = null;
		JSONArray jsonArray = new JSONArray();
		for (int i = 0, len = pictureTableList.size(); i < len; i++) {
			pictureTableObj = new HashMap<String, String>();
			pictureTableObj.put("id", String.valueOf(pictureTableList.get(i).getId()));
			pictureTableObj.put("pictureName", pictureTableList.get(i).getPictureName());
			jsonArray.put(pictureTableObj);
			pictureTableObj = null;
		}
		
		response.getWriter().print(jsonArray.toString());
	}
}