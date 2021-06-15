<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<!-- <meta http-equiv="Cache-Control" content="no-store"> -->
<meta http-equiv="Expires" content="0">
<title>editPicturePage</title>
<style type="text/css">
fieldset {
	width: 450px;
	border: 1px solid #acd6ff;
	margin: 15px;
	border-radius: 15px;
}

.st1 {
	/* 	width: 400px; */
	width: 450px;
	border-bottom: 1px solid #e0e0e0;
	margin: 20px;
	padding-bottom: 10px;
	/* 	border: 1px solid red; */
}

.sub {
	width: 450px;
	margin: 20px;
	text-align: center;
}

.t1 {
	width: 100px;
	float: left;
	text-align: right;
	/* 	border: 1px solid red; */
	padding-right: 3px;
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
		<%@ include file="/includeJSPFile/header.jsp"%>
		<div style="clear: both;"></div>
		<p>修改頁面</p>
		<form
			action="${pageContext.request.contextPath}/EditPictureAndSave?id=${pictureTable.id}"
			method="post" enctype="multipart/form-data">
			<fieldset>
				<legend>上傳圖片表單</legend>
				<!-- 圖片id編號 -->
				<input type="hidden" id="pictureId" name="pictureId"
					value="${pictureTable.id}" />
				<!-- 頁數編號 -->
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
				<!-- 是否要返回[viewAllBlockPicture.jsp] -->
				<input type="hidden" id="backBlockPicture" name="backBlockPicture"
					value="${backBlockPicture}" />
				<div class="st1">
					<label class="t1" for="title">標題：</label>
					<!-- autofocus 自動對焦 -->
					<!-- autocomplete="off" 阻止瀏覽器自動記憶選單內容 -->
					<!-- required 必須輸入 -->
					<div align="left">
						<input type="text" id="title" name="title" autofocus
							autocomplete="off" value="${pictureTable.title}" />
					</div>
				</div>
				<div class="st1">
					<label class="t1" for="typeName">分類：</label>
					<div align="left">
						<select id="typeName" name="typeName">
						</select>
					</div>
				</div>
				<div class="st1">
					<label class="t1" for="file2">上傳檔案：</label>
					<div align="left">
						<input type="file" id="file2" name="file2" />
					</div>
				</div>
				<div class="st1">
					<img id="previewImg" width="450px"
						src="/imageData/${pictureTable.pictureName}" />
				</div>
				<div class="sub">
					<input type="submit" value="提交" /> &nbsp;&nbsp; <input
						type="button" id="fillTitleName" value="用檔名填入標題" />
				</div>
			</fieldset>
		</form>
	</div>

	<script type="text/javascript">
		"use strict";

		var typeNameObj = document.getElementById("typeName");
		var xmlHttpObj = new XMLHttpRequest();
		xmlHttpObj.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var result = JSON.parse(this.responseText);
				var showResult = "";
				for (var i = 0, len = result.length; i < len; i++) {
					showResult = "result[" + i + "] = " + result[i];
					typeNameObj.add(new Option(result[i], result[i]));
				}

				typeNameObj.value = "${pictureTable.typeName}"; /* 自動 focus 到原本圖片的分類選項 */
			}
		}
		xmlHttpObj.open("get", "GetTypeNameList", true); /* 第三個參數設定 true，代表開啟非同步模式。 */
		xmlHttpObj.setRequestHeader("If-Modified-Since", "0");
		xmlHttpObj.send();

		var file2Obj = document.getElementById("file2");
		var previewImgObj = document.getElementById("previewImg");
		var pictureName = "";

		file2Obj.addEventListener("change", function() {
			if (this.files && this.files[0]) {
				var fileReader = new FileReader();
				fileReader.onload = function(e) {
					previewImgObj.setAttribute("src", e.target.result);
					previewImgObj.setAttribute("width", "400px");
				}
				fileReader.readAsDataURL(this.files[0]);
				pictureName = this.files[0].name;
			} else {
				previewImgObj.removeAttribute("src");
				previewImgObj.setAttribute("width", "0px");
				pictureName = "";
			}
		});

		var titleObj = document.getElementById("title");

		/* 這段程式取出圖片檔名，把[.] 跟 [副檔名] 去除掉， */
		/* 接著把修改過的檔名填入標題欄位。 */
		var fillTitleNameObj = document.getElementById("fillTitleName");
		fillTitleNameObj.addEventListener("click", function() {
			console.log("fillTitleNameObj has been click.");
			var fileName = pictureName;
			var pointIndex = fileName.lastIndexOf(".");
			console.log("pointIndex");
			console.log(pointIndex);
			fileName = fileName.substring(0, pointIndex); /* 把 [.] 跟 [副檔名] 去除掉。 */
			console.log("fileName = [" + fileName + "]");
			titleObj.setAttribute("value", fileName);
			titleObj.value = fileName;
		});
	</script>
</body>
</html>