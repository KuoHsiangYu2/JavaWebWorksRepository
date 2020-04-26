<!-- https://sites.google.com/site/yutingnote/sql/mssqlqudedinbiziliao -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>index</title>
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
	<h1>index</h1>
	<div id="show"></div>
	<br />
	<form action="${pageContext.request.contextPath}/SavePicture" method="post" enctype="multipart/form-data">
		<fieldset>
			<legend>上傳圖片表單</legend>
			<div class="st1">
				<label class="t1" for="title">標題：</label>
				<!-- autofocus 自動對焦 -->
				<!-- autocomplete="off" 阻止瀏覽器自動記憶選單內容 -->
				<!-- required 必輸 -->
				<input type="text" id="title" name="title" autofocus autocomplete="off" required />
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<font color="red">${errorMsg.title}</font>
			</div>
			<!-- 圖片名稱： -->
			<input type="hidden" id="pictureName" name="pictureName" value="" />
			<div class="st1">
				<label class="t1" for="file2">上傳檔案：</label>
				<input type="file" id="file2" name="file2" required />
				<font color="red">${errorMsg.file2}</font>
			</div>
			<div class="st1">
				<font color="red">${errorMsg.all}</font>
				<img id="previewImg" width="0px" />
			</div>
			<div class="sub">
				<input type="submit" value="提交" />
				&nbsp;&nbsp;
				<input type="button" id="reset" value="重設" />
				&nbsp;&nbsp;
				<input type="button" id="fillTitleName" value="用檔名填入標題" />
			</div>
		</fieldset>
	</form>

	<br />

	<a href="${pageContext.request.contextPath}/GetAllPicturePath">瀏覽所有圖片</a>

	<script type="text/javascript">
		"use strict";
		
		var showObj = document.getElementById("show");

		var xmlHttpObj = new XMLHttpRequest();
		xmlHttpObj.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var count = this.responseText;
				showObj.innerHTML = "目前已上傳 " + count + " 張圖片。";
			}
		}
		xmlHttpObj.open("get", "GetPictureCount", true);// 第三個參數設定 true，代表開啟非同步模式。
		xmlHttpObj.send();

		var file2Obj = document.getElementById("file2");
		var pictureNameObj = document.getElementById("pictureName");
		var previewImgObj = document.getElementById("previewImg");

		file2Obj.addEventListener("change", function() {
			// print("file2Obj.addEventListener");
			// 當使用者上傳圖片檔案時，
			// 把圖片讀進來，顯示在預覽上。
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

		var resetObj = document.getElementById("reset");
		var titleObj = document.getElementById("title");
		resetObj.addEventListener("click", function() {
			// 當 reset按鈕 被按下時觸發此事件，
			// 清空欄位，並且讓瀏覽器再發送一次請求重新整理頁面。
			previewImgObj.removeAttribute("src");
			previewImgObj.setAttribute("width", "0px");
			pictureNameObj.setAttribute("value", "");
			titleObj.setAttribute("value", "");
			//history.go(0);// 重新刷新頁面
			window.location.href = "${pageContext.request.contextPath}/index.jsp";// 重新刷新頁面
		});

		// 這段程式取出圖片檔名，把[.] 跟 [副檔名] 去除掉，
		// 接著把修改過的檔名填入標題欄位。
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