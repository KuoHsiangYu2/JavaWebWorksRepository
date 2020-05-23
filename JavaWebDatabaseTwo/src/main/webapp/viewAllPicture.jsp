<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.net.URLEncoder"%>
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

.fontVariableSize {
	color: black;
	text-align: left;
	font-size: 18px;
}

article {
	width: 80%;
	float: right;
}

aside {
	width: 20%;
	float: left;
	text-align: left;
}

#outerDiv2 {
	clear: both;
}

.horizontalGroup {
	/* 讓裡面的div元素一個一個水平排列在一起。 */
	display: flex;
	width: 100%;
}

.searchDiv {
	float: left;
}

#innerDiv {
	width: 100%;
}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainTheme.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/publicFunction.js"></script>

<!-- 設定 favicon.ico 圖示 -->
<link rel="icon" href="favicon.ico" type="image/x-icon">

</head>
<body>
	<jsp:useBean id="viewStatusData" class="com.model.ViewStatus" scope="page" />

	<div align="center" class="allpage">
		<header>
			<%@ include file="/includeJSPFile/header.jsp"%>
			<div style="clear: both;"></div>
			<div class="searchDiv">
				<form action="${pageContext.request.contextPath}/GetAllPicturePath" method="get" enctype="application/x-www-form-urlencoded">
					標題：
					<input type="text" id="searchString" name="searchString" />
					<input type="submit" value="搜尋" />
					<br />
					<br />
				</form>
			</div>
		</header>

		<!-- 把 float CSS 設定清除掉 -->
		<div style="clear: both;"></div>

		<div id="innerDiv">
			<!-- align="left" -->
			<aside>
				<h2>分類清單</h2>
				<input id="radio0" type="radio" name="typeNameList" value="全部" />
				<label for="radio0">
					<font size="+2">全部</font>
				</label>
				<br />
				<br />
				<c:forEach items="${classTypeList}" var="classType" varStatus="varStatus">
					<input id="radio${varStatus.count}" type="radio" name="typeNameList" value="${classType}" />
					<label for="radio${varStatus.count}">
						<font size="+2">${classType}</font>
					</label>
					<br />
					<br />
				</c:forEach>
			</aside>
			<article>
				<c:if test="${not empty pictureTableList}">
					<!-- 如果 userDataList != null，userDataList物件裡面有資料，就依依把 標題、圖片資料秀出來。 -->
					<!-- true 代表裡面有資料 -->
					<jsp:setProperty name="viewStatusData" property="viewEmpty" value="true" />
					<button id="decreaseFontSize">
						<font size="+2">縮小</font>
					</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button id="resetFontSize">
						<font size="+2">重設大小</font>
					</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button id="increaseFontSize">
						<font size="+2">放大</font>
					</button>
					<br />
					<br />
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
								<font size="+2">分類</font>
							</td>
							<td>
								<font size="+2">編輯</font>
							</td>
							<td>
								<font size="+2">刪除</font>
							</td>
							<td>
								<font size="+2">下載</font>
							</td>
						</tr>
						<c:forEach var="picture" items="${pictureTableList}">
							<tr>
								<td width="120px">
									<font class="fontVariableSize">${picture.id}</font>
								</td>
								<td width="300px">
									<font class="fontVariableSize">${picture.title}</font>
								</td>
								<td width="400px">
									<img src="/imageData/${picture.pictureName}" width="400px" />
								</td>
								<td align="center" width="100px">
									<font size="+2">${picture.typeName}</font>
								</td>
								<td align="center" width="60px">
									<a href="${pageContext.request.contextPath}/EditPicture?id=${picture.id}&pageNo=${pageNo}">
										<font size="+2">編輯</font>
									</a>
								</td>
								<td align="center" width="60px">
									<p class="delete" hrefName="${pageContext.request.contextPath}/DeletePicture?id=${picture.id}&pageNo=${pageNo}">
										<font size="+2">刪除</font>
									</p>
								</td>
								<td align="center" width="60px">
									<a href="${pageContext.request.contextPath}/GetPictureFile?id=${picture.id}">
										<font size="+2">下載</font>
									</a>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
				<c:if test="${empty pictureTableList}">
					<!-- 如果 pictureTableList 是 null 或是 空集合 就執行這段程式 -->
					<!-- false 代表裡面沒有資料 -->
					<jsp:setProperty name="viewStatusData" property="viewEmpty" value="false" />
					<div class="horizontalGroup">
						<div style="width: 340px;"></div>
						<div>
							<!-- <h2>您目前沒上傳任何圖片。</h2> -->
						</div>
					</div>
				</c:if>
			</article>
		</div>
		<br />
	</div>

	<!-- 把 float CSS 設定清除掉 -->
	<div style="clear: both;"></div>

	<div id="outerDiv2" align="center" class="allpage">
		<div style="height: 20px"></div>
		<footer>
			<c:if test="${not empty pictureTableList}">
				<!-- 以下為控制 第一頁、前一頁、下一頁、最末頁 等超連結-->
				<%
					// 進行中文字網址編碼UTF-8的工作
				String typeName = (String) request.getAttribute("typeName");
				if (typeName != null && typeName.trim().length() != 0) {
					typeName = URLEncoder.encode(typeName, "UTF-8");
					typeName = typeName.replace("+", "%20");
					request.setAttribute("typeName", typeName);
				}

				String searchString = (String) request.getAttribute("searchString");
				if (searchString != null && searchString.trim().length() != 0) {
					searchString = URLEncoder.encode(searchString, "UTF-8");
					searchString = searchString.replace("+", "%20");
					request.setAttribute("searchString", searchString);
				}
				%>
				<table border="1">
					<tr>
						<td width="90px" align="center">
							<c:choose>
								<c:when test="${pageNo > 1}">
									<font color="blue">
										<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=1&typeName=${typeName}&searchString=${searchString}">第一頁</a>
									</font>
								</c:when>
								<c:otherwise>
									第一頁
								</c:otherwise>
							</c:choose>
						</td>
						<td width="90px" align="center">
							<c:choose>
								<c:when test="${pageNo > 1}">
									<font color="blue">
										<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=${pageNo - 1}&typeName=${typeName}&searchString=${searchString}">上一頁</a>
									</font>
								</c:when>
								<c:otherwise>
									上一頁
								</c:otherwise>
							</c:choose>
						</td>
						<td width="110px" align="center">
							<select id="pageId">
							</select>
						</td>
						<td width="90px" align="center">
							<c:choose>
								<c:when test="${pageNo != totalPages}">
									<font color="blue">
										<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=${pageNo + 1}&typeName=${typeName}&searchString=${searchString}">下一頁</a>
									</font>
								</c:when>
								<c:otherwise>
									下一頁
								</c:otherwise>
							</c:choose>
						</td>
						<td width="90px" align="center">
							<c:choose>
								<c:when test="${pageNo != totalPages}">
									<font color="blue">
										<a href="${pageContext.request.contextPath}/GetAllPicturePath?pageNo=${totalPages}&typeName=${typeName}&searchString=${searchString}">最末頁</a>
									</font>
								</c:when>
								<c:otherwise>
									最末頁
								</c:otherwise>
							</c:choose>
						</td>
						<td width="180px" align="center">第 ${pageNo} 頁 / 共 ${totalPages} 頁</td>
					</tr>
				</table>
			</c:if>
		</footer>
	</div>

	<script type="text/javascript">
		"use strict";

		var viewEmptyData = eval("<jsp:getProperty name='viewStatusData' property='viewEmpty' />");
		console.log("viewEmptyData -> " + viewEmptyData);

		// 讓分類清單依據當前狀態決定被勾選的選項。
		var typeNameData = "${typeName}";
		typeNameData = window.decodeURI(typeNameData);// 把UTF-8編碼轉回中文字
		if ("" === typeNameData) {
			var targetRadioButton = document.querySelector("aside > input[value='全部']");
			targetRadioButton.setAttribute("checked", "checked");
		} else {
			var cssSelectorString = "aside > input[value='" + typeNameData + "']";
			var targetRadioButton = document.querySelector(cssSelectorString);
			targetRadioButton.setAttribute("checked", "checked");
		}

		var radioButtonArray = document.querySelectorAll("aside > input");
		for (var i = 0, len = radioButtonArray.length; i < len; i++) {
			radioButtonArray[i].addEventListener("click", function() {
				var typeNameString = this.value;
				typeNameString = window.encodeURI(typeNameString);
				var jumpHref = "${pageContext.request.contextPath}/GetAllPicturePath?&typeName=" + typeNameString;
				window.location.href = jumpHref;
			});
		}

		if (true === viewEmptyData) {
			// Javascript調整字體大小
			// https://blog.xuite.net/jon6773/blog/29539743-Javascript%E8%AA%BF%E6%95%B4%E5%AD%97%E9%AB%94%E5%A4%A7%E5%B0%8F

			var pageIdObj = document.getElementById("pageId");
			var totalPage = 1;
			totalPage = Number.parseInt("${totalPages}");
			var optionText = "";
			for (var i = 1; i <= totalPage; i++) {
				optionText = "第 " + i + " 頁";
				pageIdObj.add(new Option(optionText, i));
			}
			var pageNo = "${pageNo}";// 取得現在第幾頁
			pageIdObj.value = pageNo;// 設定 focus 下拉選單在第幾頁

			pageIdObj.addEventListener("change", function() {
				// 當下拉式選單的值變化時，觸發此事件，跳轉到指定的頁面。
				// console.log("pageIdObj.addEventListener");
				// console.log("pageIdObj.value = " + pageIdObj.value);
				var jumpHref = "${pageContext.request.contextPath}/GetAllPicturePath?pageNo=" + pageIdObj.value + "&typeName=${typeName}&searchString=${searchString}";
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

			var fontVarSizeColl = document.getElementsByClassName("fontVariableSize");
			var fontCollLength = fontVarSizeColl.length;
			function initialFontSize() {
				for (var i = 0; i < fontCollLength; i++) {
					// 替每個顯示文字設置大小初始值。
					fontVarSizeColl[i].style.fontSize = "18px";
				}
			}
			initialFontSize();

			// 重設字型大小
			var resetFontObj = document.getElementById("resetFontSize");
			resetFontObj.addEventListener("click", function() {
				initialFontSize();
			});

			// 縮小字型
			var decreaseSizeObj = document.getElementById("decreaseFontSize");
			decreaseSizeObj.addEventListener("click", function() {
				if (fontVarSizeColl[0].style.fontSize === "") {
					fontVarSizeColl[0].style.fontSize = "18px";
				}
				var tempSize = Number.parseInt(fontVarSizeColl[0].style.fontSize.replace("px", ""));
				tempSize = tempSize - 1;
				for (var i = 0; i < fontCollLength; i++) {
					fontVarSizeColl[i].style.fontSize = tempSize + "px";
				}
			});

			// 放大字型
			var increaseSizeObj = document.getElementById("increaseFontSize");
			increaseSizeObj.addEventListener("click", function() {
				if (fontVarSizeColl[0].style.fontSize === "") {
					fontVarSizeColl[0].style.fontSize = "18px";
				}
				var tempSize = Number.parseInt(fontVarSizeColl[0].style.fontSize.replace("px", ""));
				tempSize = tempSize + 1;
				for (var i = 0; i < fontCollLength; i++) {
					fontVarSizeColl[i].style.fontSize = tempSize + "px";
				}
			});
		}
	</script>
</body>
</html>