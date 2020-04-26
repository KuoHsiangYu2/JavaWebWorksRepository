<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>editPicturePage</title>
<style type="text/css">
fieldset {
	width: 550px;
	border: 1px solid #acd6ff;
	margin: 15px;
	border-radius: 15px;
}

.st1 {
	width: 500px;
	border-bottom: 1px solid #e0e0e0;
	margin: 20px;
	padding-bottom: 10px;
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
	/* border:1px solid red; */
	padding-right: 3px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/publicFunction.js"></script>

<!-- 設定 favicon.ico 圖示 -->
<link rel="icon" href="favicon.ico" type="image/x-icon">

</head>
<body>
	<div align="center">
		<h1>editPicturePage</h1>
		<h2>修改頁面</h2>
		<form action="${pageContext.request.contextPath}/EditPictureAndSave?id=${pictureTable.id}" method="post" enctype="multipart/form-data">
			<fieldset>
				<legend>上傳圖片表單</legend>
				<div class="st1">
					<label class="t1" for="title">標題：</label>
					<!-- autofocus 自動對焦 -->
					<!-- autocomplete="off" 阻止瀏覽器自動記憶選單內容 -->
					<!-- required 必須輸入 -->
					<input type="text" id="title" name="title" autofocus autocomplete="off" value="${pictureTable.title}" />
				</div>
				<!-- 圖片名稱： -->
				<input type="hidden" id="pictureName" name="pictureName" value="" />
				<!-- 圖片id編號 -->
				<input type="hidden" id="pictureId" name="pictureId" value="${pictureTable.id}" />
				<!-- 頁數編號 -->
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
				<div class="st1">
					<label class="t1" for="file2">上傳檔案：</label>
					<input type="file" id="file2" name="file2" />
				</div>
				<div class="st1">
					<img id="previewImg" width="450px" src="${pageContext.request.contextPath}/GetPicture?id=${pictureTable.id}" />
				</div>
				<div class="sub">
					<input type="submit" value="提交" />
					&nbsp;&nbsp;
					<input type="button" id="fillTitleName" value="用檔名填入標題" />
				</div>
			</fieldset>
		</form>
	</div>

	<script type="text/javascript">
		"use strict";

		var file2Obj = document.getElementById("file2");
		var pictureNameObj = document.getElementById("pictureName");
		var previewImgObj = document.getElementById("previewImg");

		file2Obj.addEventListener("change", function() {
			// print("file2Obj.addEventListener");
			if (this.files && this.files[0]) {
				var fileReader = new FileReader();
				fileReader.onload = function(e) {
					previewImgObj.setAttribute("src", e.target.result);
					previewImgObj.setAttribute("width", "450px");
				}
				fileReader.readAsDataURL(this.files[0]);
				pictureNameObj.setAttribute("value", this.files[0].name);
			} else {
				previewImgObj.removeAttribute("src");
				previewImgObj.setAttribute("width", "0px");
				pictureNameObj.setAttribute("value", "");
			}
		});

		var titleObj = document.getElementById("title");

		var fillTitleNameObj = document.getElementById("fillTitleName");
		fillTitleNameObj.addEventListener("click", function() {
			var fileName = pictureNameObj.getAttribute("value");
			var pointIndex = fileName.lastIndexOf(".");
			fileName = fileName.substring(0, pointIndex);// 把 [.] 跟 [副檔名] 去除掉。
			titleObj.setAttribute("value", fileName);
		});

		// https://progressbar.tw/posts/47
		// https://stackoverflow.com/questions/4459379/preview-an-image-before-it-is-uploaded
		// https://www.tenlong.com.tw/products/9789864344130
	</script>
</body>
</html>