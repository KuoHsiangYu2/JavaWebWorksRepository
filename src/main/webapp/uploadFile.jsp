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
<!-- <meta http-equiv="Cache-Control" content="no-store"> -->
<meta http-equiv="Expires" content="0">
<title>uploadFile</title>
<style type="text/css">
fieldset {
	width: 450px;
	border: 1px solid #acd6ff;
	margin: 15px;
	border-radius: 15px;
}

.st1 {
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
		<div id="show"></div>
		<br />
		<form action="${pageContext.request.contextPath}/SavePicture"
			method="post" enctype="multipart/form-data">
			<fieldset>
				<legend>上傳圖片表單</legend>
				<div class="st1">
					<label class="t1" for="title">標題：</label>
					<!-- autofocus 自動對焦 -->
					<!-- autocomplete="off" 阻止瀏覽器自動記憶選單內容 -->
					<!-- required 必輸 -->
					<div align="left">
						<input type="text" id="title" name="title" autofocus
							autocomplete="off" required />
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<font color="red">${errorMsg.title}</font>
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
						<input type="file" id="file2" name="file2" required /> <font
							color="red">${errorMsg.file2}</font>
					</div>
				</div>
				<div class="st1">
					<font color="red">${errorMsg.all}</font> <img id="previewImg"
						width="0px" />
				</div>
				<div class="sub">
					<input type="submit" value="提交" /> &nbsp;&nbsp;
					<button type="button" id="reset">重設</button>
					&nbsp;&nbsp;
					<button type="button" id="fillTitleName">用檔名填入標題</button>
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
				console.log("typeName result");
				console.log(result);
				var showResult = "";
				for (var i = 0, len = result.length; i < len; i++) {
					showResult = "result[" + i + "] = " + result[i];
					console.log(showResult);
					typeNameObj.add(new Option(result[i], result[i]));
				}
			}
		}
		xmlHttpObj.open("get", "GetTypeNameList", true);
		xmlHttpObj.setRequestHeader("If-Modified-Since", "0");
		xmlHttpObj.send();

		var showObj = document.getElementById("show");
		var xmlHttpObj2 = new XMLHttpRequest();
		xmlHttpObj2.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var count = this.responseText;
				showObj.innerText = "目前已上傳 " + count + " 張圖片。";
			}
		}
		xmlHttpObj2.open("get", "GetPictureCount", true);
		xmlHttpObj2.setRequestHeader("If-Modified-Since", "0");
		xmlHttpObj2.send();

		var file2Obj = document.getElementById("file2");
		var previewImgObj = document.getElementById("previewImg");
		var pictureName = "";

		file2Obj.addEventListener("change", function() {
			/* 當使用者上傳圖片檔案時， */
			/* 把圖片讀進來，顯示在預覽上。 */
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

		var resetObj = document.getElementById("reset");
		var titleObj = document.getElementById("title");
		resetObj.addEventListener("click", function() {
			/* 當 reset按鈕 被按下時觸發此事件， */
			/* 清空欄位，並且讓瀏覽器再發送一次請求重新整理頁面。 */
			previewImgObj.removeAttribute("src");
			previewImgObj.setAttribute("width", "0px");
			pictureName = "";
			titleObj.setAttribute("value", "");
			// history.go(0); /* 重新刷新頁面 */
			window.location.href = "${pageContext.request.contextPath}/uploadFile.jsp"; /* 重新刷新頁面 */
		});

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
			titleObj.value = fileName;// IE
		});

		// 參考資料
		// https://progressbar.tw/posts/47
		// https://stackoverflow.com/questions/4459379/preview-an-image-before-it-is-uploaded
		// https://www.tenlong.com.tw/products/9789864344130
	</script>
</body>
</html>