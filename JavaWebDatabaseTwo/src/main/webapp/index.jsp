<!-- https://sites.google.com/site/yutingnote/sql/mssqlqudedinbiziliao -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Cache-Control" content="no-store">
<meta http-equiv="Expires" content="0">
<title>index</title>
<style type="text/css">
.fontStyle {
	/* 標楷體 */
	font-family: DFKai-sb;
}

.show {
	opacity: 1;
}

.hide {
	opacity: 0;
}

img {
	width: 500px;
	transition: 3s;
}
</style>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/mainTheme.css">
<script type="text/javascript"
	src="${pageContext.request.contextPath}/javascript/publicFunction.js"></script>

<!-- 設定 favicon.ico 圖示 -->
<link rel="icon" href="favicon.ico" type="image/x-icon">

</head>
<body>
	<div align="center" class="allpage">
		<%-- <jsp:include page="/includeJSPFile/header.jsp" /> --%>
		<%@ include file="/includeJSPFile/header.jsp"%>
		<div style="clear: both;"></div>
		<div id="show"></div>
		<br />
		<p>
			<font class="fontStyle" size="+4">歡迎使用圖片庫存管理系統</font>
		</p>
		<br /> <br /> <img id="showPicture" />
	</div>

	<script type="text/javascript">
		"use strict";

		var showObj = document.getElementById("show");
		var showPictureObj = document.getElementById("showPicture");

		var xmlHttpObj = new XMLHttpRequest();
		xmlHttpObj.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var count = this.responseText;
				showObj.innerText = "目前已上傳 " + count + " 張圖片。";
			}
		}
		xmlHttpObj.open("get", "GetPictureCount", true);
		xmlHttpObj.setRequestHeader("If-Modified-Since", "0");
		xmlHttpObj.send();

		function playPictureWall() {
			// 播放圖片輪播牆
			var picListLength = picList.length;
			var picId = 0;
			showPictureObj.setAttribute("src", "/imageData/" + picList[0]);
			var flag = true;

			window.setInterval(function() {
				if (true === flag) {
					picId = picId + 1;
					if (picId >= picListLength) {
						picId = 0;
					}
					showPictureObj.classList.add("hide");
					window.setTimeout(function() {
						showPictureObj.setAttribute("src", "/imageData/" + picList[picId]);
					}, 3000);
				} else {
					showPictureObj.classList.remove("hide");
				}
				flag = !flag;
			}, 3000);
		}

		var picList = [];
		var xmlHttpObj2 = new XMLHttpRequest();
		xmlHttpObj2.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var result = this.responseText;
				result = JSON.parse(result);
				for (var i = 0, len = result.length; i < len; i++) {
					picList.push(result[i].pictureName);
				}
				if (result.length != 0) {
					// 如果資料庫裡面有圖片資料才執行輪播牆。
					playPictureWall();
				}
			}
		}
		xmlHttpObj2.open("get", "GetPictureTableList", true);
		xmlHttpObj2.setRequestHeader("If-Modified-Since", "0");
		xmlHttpObj2.send();
	</script>
</body>
</html>