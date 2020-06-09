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
<title>editTypeNameList</title>
<style type="text/css">
.no {
	/* 標記編號的CSS */
	width: 30px;
}

input[readonly="readonly"] {
	background-color: #dddddd;
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
		<form
			action="${pageContext.request.contextPath}/SaveAndUpdateEditTypeList"
			method="post" enctype="application/x-www-form-urlencoded">
			<table id="showData">
			</table>
			<br />
			<button id="addRow" type="button">新增一列</button>
			<br /> <br /> <input type="submit" value="提交" />
		</form>
	</div>

	<script type="text/javascript">
		"use strict";

		var showDataObj = document.getElementById("showData");
		var addRowObj = document.getElementById("addRow");// 新增一列
		var countRow = 0;// 計算有幾列
		var editList = [];// 儲存分類清單的陣列

		function initialTable() {
			// 初始化整個分類清單
			var length = editList.length;
			for (countRow = 0; countRow < length; countRow++) {
				var newTrObj = document.createElement("tr");

				// 第1個 td
				var td1Obj = document.createElement("td");
				td1Obj.setAttribute("align", "center");
				td1Obj.setAttribute("class", "no");
				td1Obj.appendChild(document.createTextNode(String(countRow + 1)));
				newTrObj.appendChild(td1Obj);

				// 第2個 td
				var td2Obj = document.createElement("td");
				td2Obj.setAttribute("width", "160px");
				var inputObj = document.createElement("input");
				inputObj.setAttribute("type", "text");
				inputObj.setAttribute("name", "typeList");
				inputObj.setAttribute("value", editList[countRow]);
				if (countRow === 0) {
					// 第一列禁止修改編輯
					inputObj.setAttribute("readonly", "readonly");
				} else if (countRow === 1) {
					// 第二列自動聚焦
					inputObj.setAttribute("autofocus", "autofocus");
				} else {
					// do nothing
				}
				td2Obj.appendChild(inputObj);
				newTrObj.appendChild(td2Obj);

				// 第3個 td
				var td3Obj = document.createElement("td");
				var buttonObj = document.createElement("button");
				buttonObj.setAttribute("type", "button");
				buttonObj.setAttribute("onclick", "deleteRow(this)");
				buttonObj.innerText = "刪除";
				td3Obj.appendChild(buttonObj);
				newTrObj.appendChild(td3Obj);

				showDataObj.appendChild(newTrObj);
				// 從for迴圈出來後 countRow === length ，所以外面不需要再做 countRow++; 的動作。
			}
		}

		var xmlHttpObj = new XMLHttpRequest();
		xmlHttpObj.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var result = JSON.parse(this.responseText);

				for (var i = 0, len = result.length; i < len; i++) {
					editList.push(result[i]);
				}

				for (var i = 1; i <= 3; i++) {
					// 再加入3個預設的空白。
					editList.push("");
				}

				// 資料都到齊了，就可以開始初始化繪製表格。
				initialTable();
			}
		}
		xmlHttpObj.open("get", "GetTypeNameList", true);// 第三個參數設定 true，代表開啟非同步模式。
		xmlHttpObj.setRequestHeader("If-Modified-Since", "0");// 禁止瀏覽器快取
		xmlHttpObj.send();

		function renameTdNo() {
			var tdNoArray = document.getElementsByClassName("no");
			var length = tdNoArray.length;
			var n = 1;
			for (var i = 0; i < length; i++) {
				tdNoArray[i].innerText = String(n);
				n = n + 1;
			}
		}

		function deleteRow(buttonObj) {
			var isDelete = confirm("確定要刪除嗎？");
			if (true === isDelete) {
				if (countRow === 1) {
					// 只剩一列就不要再刪了。
					window.alert("分類名單不可為空！");
					return;
				}
				var trObj = buttonObj.parentElement.parentElement;
				var td1NoObj = trObj.childNodes[0];
				if (td1NoObj.childNodes[0].nodeValue === "1") {
					// 檢查是否為第一列的程式
					// 第一列 [未分類] 禁止使用者刪除
					window.alert("[未分類] 為不可刪除項目！");
					return;
				}

				var tbodyObj = buttonObj.parentElement.parentElement.parentElement;
				tbodyObj.removeChild(trObj);

				renameTdNo();
				countRow = countRow - 1;
			} else {
				// do nothing
			}
		}

		addRowObj.addEventListener("click", function() {
			var lastTrObj = document.querySelector("#showData tr:last-child");

			var newTrObj = document.createElement("tr");

			// 第1個 td
			var td1Obj = document.createElement("td");
			td1Obj.setAttribute("align", "center");
			td1Obj.setAttribute("class", "no");
			td1Obj.appendChild(document.createTextNode(String(countRow)));
			newTrObj.appendChild(td1Obj);

			// 第2個 td
			var td2Obj = document.createElement("td");
			td2Obj.setAttribute("width", "160px");
			var inputObj = document.createElement("input");
			inputObj.setAttribute("type", "text");
			inputObj.setAttribute("name", "typeList");
			inputObj.setAttribute("value", "");
			td2Obj.appendChild(inputObj);
			newTrObj.appendChild(td2Obj);

			// 第3個 td
			var td3Obj = document.createElement("td");
			var buttonObj = document.createElement("button");
			buttonObj.setAttribute("type", "button");
			buttonObj.setAttribute("onclick", "deleteRow(this)");
			buttonObj.innerText = "刪除";
			td3Obj.appendChild(buttonObj);
			newTrObj.appendChild(td3Obj);

			lastTrObj.insertAdjacentElement("afterend", newTrObj);

			renameTdNo();
			countRow = countRow + 1;
		});
	</script>
</body>
</html>