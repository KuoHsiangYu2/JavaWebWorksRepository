<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>viewAllBlockPicture</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainTheme.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/publicFunction.js"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.12.4.min.js"></script>

<!-- 設定 favicon.ico 圖示 -->
<link rel="icon" href="favicon.ico" type="image/x-icon">

<style type="text/css">
.back-to-top {
	/* 默認是隱藏的，這樣在第一屏才不顯示 */
	display: none;
	/* 位置是固定的 */
	position: fixed;
	/* 顯示在頁面底部 */
	bottom: 20px;
	/* 顯示在頁面的右邊 */
	right: 30px;
	/* 確保不被其他功能覆蓋 */
	z-index: 99;
	/* 顯示邊框 */
	border: 1px solid #5cb85c;
	/* 不顯示外框 */
	outline: none;
	/* 設置背景背景顏色 */
	background-color: #fff;
	/* 設置文本顏色 */
	color: #5cb85c;
	/* 滑鼠移到按鈕上顯示手型 */
	cursor: pointer;
	/* 增加一些內邊距 */
	padding: 10px 15px 15px 15px;
	/* 增加圓角 */
	border-radius: 10px;
}

.back-to-top:hover {
	/* 滑鼠移上去時，反轉顏色 */
	background-color: #5cb85c;
	color: #fff;
}
</style>
</head>
<body>
	<div align="center" class="allpage">
		<%@ include file="/includeJSPFile/header.jsp"%>
		<div style="clear: both;"></div>
		<br />
		<br />
		<div align="left" id="showPictures" style="width: 1500px;"></div>
		<button class="js-back-to-top back-to-top" title="Top">Top</button>
	</div>

	<script type="text/javascript">
		"use strict";

		var showPicturesObj = document.getElementById("showPictures");
		var picList = [];// 圖片名稱清單
		var picId = [];// 圖片id編號

		function clickImg(img) {
			var jumpHref = img.getAttribute("jumpHref");
			window.location.href = jumpHref;
		}

		function addPictureToDiv() {
			var imgObj = null;
			for (var i = 0, length = picList.length; i < length; i++) {
				imgObj = document.createElement("img");
				imgObj.setAttribute("width", "500px");
				imgObj.setAttribute("src", "/imageData/" + picList[i]);
				imgObj.setAttribute("jumpHref", "${pageContext.request.contextPath}/EditPicture?id=" + picId[i] + "&backBlockPicture=true");
				imgObj.setAttribute("onclick", "clickImg(this)");
				showPicturesObj.appendChild(imgObj);
				imgObj = null;
			}
		}

		var xmlHttpObj = new XMLHttpRequest();
		xmlHttpObj.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var result = this.responseText;
				result = JSON.parse(result);
				for (var i = 0, len = result.length; i < len; i++) {
					picList.push(result[i].pictureName);
					picId.push(result[i].id);
				}
				addPictureToDiv();
			}
		}
		xmlHttpObj.open("get", "GetPictureTableList", true);
		xmlHttpObj.send();

		// https://kknews.cc/zh-tw/news/k8o5arb.html
		// https://medium.com/%E4%B8%80%E5%80%8B%E4%BA%BA%E7%9A%84%E6%96%87%E8%97%9D%E5%BE%A9%E8%88%88/%E8%AE%93%E7%80%8F%E8%A6%BD%E5%99%A8%E8%87%AA%E5%8B%95%E6%BB%BE%E7%B6%B2%E9%A0%81-javascript-4a8c24f94f04

		var winObj = $(window);
		var backToTopObj = $(".js-back-to-top");

		// 當用戶滾動到離頂部100像素時，展示回到頂部按鈕
		winObj.scroll(function() {
			if (winObj.scrollTop() > 100) {
				backToTopObj.show();
			} else {
				backToTopObj.hide();
			}
		});

		// 當用戶點擊按鈕時，通過動畫效果返回頭部
		backToTopObj.click(function() {
			$("html, body").animate({
				scrollTop : 0
			}, 200);
		});
	</script>
</body>
</html>