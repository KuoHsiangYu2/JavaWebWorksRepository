<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>viewAllPicture</title>
<style type="text/css">
a {
	color: blue;
}

font {
	/* 設定微軟正黑體 */
	font-family: Microsoft JhengHei;
}

.delete {
	/* color: blue 藍色的文字看起來像超連結 */
	color: blue;
	/* cursor: pointer 滑鼠移到這個項目變成手指圖標。 */
	cursor: pointer;
	/* 	text-decoration: underline 加上底線 */
	text-decoration: underline;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/publicFunction.js"></script>

<!-- 設定 favicon.ico 圖示 -->
<link rel="icon" href="favicon.ico" type="image/x-icon">

</head>
<body>
	<div align="center">
		<h1>viewAllPicture</h1>

		<c:if test="${not empty pictureTableList}">
			<!-- 如果 pictureTableList != null 或 userDataList物件不為空集合，代表 pictureTableList 裡面有資料，依依把 標題、圖片資料秀出來。 -->
			<table border="1">
				<tr align="center">
					<td>
						<font size="+2">編號</font>
					</td>
					<td>
						<font size="+2">標題</font>
					</td>
					<td>
						<font size="+2">圖</font>
					</td>
					<td>
						<font size="+2">編輯</font>
					</td>
					<td>
						<font size="+2">刪除</font>
					</td>
				</tr>
				<c:forEach var="picture" items="${pictureTableList}">
					<tr>
						<td>${picture.id}</td>
						<td>${picture.title}</td>
						<td>
							<img src="${pageContext.request.contextPath}/GetPicture?id=${picture.id}" width="400px" />
						</td>
						<td align="center">
							<a href="${pageContext.request.contextPath}/EditPicture?id=${picture.id}&pageNo=${pageNo}">編輯</a>
						</td>
						<td align="center">
							<p class="delete" hrefName="${pageContext.request.contextPath}/DeletePicture?id=${picture.id}&pageNo=${pageNo}">刪除</p>
						</td>
					</tr>
				</c:forEach>
			</table>

			<br />

			<!-- 以下為控制 第一頁、前一頁、下一頁、最末頁 等超連結-->
			<table border="1">
				<tr>
					<td width="90" align="center">
						<c:choose>
							<c:when test="${pageNo > 1}">
								<font color="blue">
									<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=1">第一頁</a>
								</font>
							</c:when>
							<c:otherwise>
								第一頁
							</c:otherwise>
						</c:choose>
					</td>
					<td width="90" align="center">
						<c:choose>
							<c:when test="${pageNo > 1}">
								<font color="blue">
									<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=${pageNo - 1}">上一頁</a>
								</font>
							</c:when>
							<c:otherwise>
								上一頁
							</c:otherwise>
						</c:choose>
					</td>
					<td width="110" align="center">
						<select id="pageId">
						</select>
					</td>
					<td width="90" align="center">
						<c:choose>
							<c:when test="${pageNo != totalPages}">
								<font color="blue">
									<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=${pageNo + 1}">下一頁</a>
								</font>
							</c:when>
							<c:otherwise>
								下一頁
							</c:otherwise>
						</c:choose>
					</td>
					<td width="90" align="center">
						<c:choose>
							<c:when test="${pageNo != totalPages}">
								<font color="blue">
									<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=${totalPages}">最末頁</a>
								</font>
							</c:when>
							<c:otherwise>
								最末頁
							</c:otherwise>
						</c:choose>
					</td>
					<td width="180" align="center">第 ${pageNo} 頁 / 共 ${totalPages} 頁</td>
				</tr>
			</table>
		</c:if>
		<c:if test="${empty pictureTableList}">
			<!-- 如果 pictureTableList 是 null 或 空集合 就執行這段程式 -->
			<p>您目前沒上傳任何圖片。</p>
		</c:if>

		<br />
		<a href="${pageContext.request.contextPath}/index.jsp">
			<font size="+3" color="blue">回首頁</font>
		</a>
		<br />
		<br />
	</div>
	<script type="text/javascript">
		"use strict";

		var pageIdObj = document.getElementById("pageId");
		var totalPage = 1;
		var xmlHttpObj = new XMLHttpRequest();
		xmlHttpObj.onreadystatechange = function() {
			// 使用AJAX取得總頁數資料
			if (this.readyState == 4 && this.status == 200) {
				totalPage = Number.parseInt(this.responseText);
				var optionText = "";
				for (var i = 1; i <= totalPage; i++) {
					optionText = "第 " + i + " 頁";
					pageIdObj.add(new Option(optionText, i));
				}

				var pageNo = "${pageNo}";// 取得現在第幾頁
				pageIdObj.value = pageNo;// 設定 focus 下拉選單在第幾頁
			}
		}
		xmlHttpObj.open("get", "GetPictureTotalPage", true);// 第三個參數設定 true，代表開啟非同步模式。
		xmlHttpObj.send();

		pageIdObj.addEventListener("change", function() {
			// 當下拉式選單的值變化時，觸發此事件，跳轉到指定的頁面。
			// console.log("pageIdObj.addEventListener");
			// console.log("pageIdObj.value = " + pageIdObj.value);
			var jumpHref = "${pageContext.request.contextPath}/GetAllPicturePath?pageNo=" + pageIdObj.value;
			window.location.href = jumpHref;// 跳轉到指定的頁面。
		});

		// 以「delete class」名稱來抓取 元素。
		var delHrefColl = document.getElementsByClassName("delete");
		var length = delHrefColl.length;
		for (var i = 0; i < length; i++) {
			delHrefColl[i].addEventListener("click", function() {
				// 彈出確認視窗，詢問使用者是否確定要刪除？
				var isConfirm = confirm("你確定要刪除嗎？");
				if (false === isConfirm) {
					// do nothing
				} else {
					// 使用者按下「確定」就發送get請求，
					// 呼叫後端Servlet執行刪除資料的程式。
					var hrefName = this.getAttribute("hrefName");
					window.location.href = hrefName;
				}
			});
		}
	</script>
</body>
</html>
