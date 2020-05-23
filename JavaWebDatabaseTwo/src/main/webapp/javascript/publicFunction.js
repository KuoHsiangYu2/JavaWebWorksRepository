"use strict";

// 檢查瀏覽器是否支援 window.localStorage
// Internet Explorer瀏覽器【以下簡稱 IE瀏覽器】、Edge瀏覽器 不支援 window.localStorage
function supportLocalStorage() {
	try {
		return eval("window.localStorage !== undefined");
	} catch (e) {
		console.log("catch e");
		console.log(e);
		return false;
	}
}
if (false === supportLocalStorage()) {
	console.log("Browser didn't support window.localStorage");
}

if (Number.parseInt === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading Number.parseInt()")
	Number.parseInt = window.parseInt;
}

if (String.prototype.padStart === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading String.prototype.padStart()");
	String.prototype.padStart = function padStart(targetLength, padString) {

		// truncate if number or convert non-number to 0;
		targetLength = targetLength >> 0;

		// console.log("targetLength -> " + targetLength);
		// console.log("this.length -> " + this.length);
		padString = String((typeof padString !== "undefined" ? padString : " "));
		if (this.length >= targetLength) {
			// console.log("第一個if條件判斷式。");
			return String(this);
		} else {
			// console.log("第二個if條件判斷式。");
			var thisObj = String(this);
			var thisArray = thisObj.split("");
			// console.log("thisArray obj");
			// console.log(thisArray);

			while (thisArray.length < targetLength) {
				// 從左邊開始插入padString
				thisArray.unshift(padString);
			}

			// 最後用 ""字串 隔開組合成一個完整的 String 回傳回去。
			return thisArray.join("");
		}
	}
}

if (String.prototype.includes === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading String.prototype.includes()");
	String.prototype.includes = function includes(searchString) {
		if (this.match(searchString) === null) {
			return false;
		} else {
			return true;
		}
	}
}

function supportsLiterals() {
	// 這個函數用來檢查瀏覽器是否支援 Template literals
	// IE瀏覽器 不支援 Template literals
	try {
		return eval("'' === ``");
	} catch (e) {
		console.log("catch e");
		console.log(e);
		return false;
	}
}
if (false === supportsLiterals()) {
	console.log("Browser didn't support String Template literals");
}