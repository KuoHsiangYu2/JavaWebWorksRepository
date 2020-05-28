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

if (String.prototype.padStart === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading String.prototype.padStart()");
	String.prototype.padStart = function padStart(targetLength, padString) {

		// truncate if number or convert non-number to 0;
		targetLength = targetLength >> 0;

		padString = String((typeof padString !== "undefined" ? padString : " "));
		if (this.length >= targetLength) {
			return String(this);
		} else {
			var thisObj = String(this);
			var thisArray = thisObj.split("");

			while (thisArray.length < targetLength) {
				// 從左邊開始插入padString
				thisArray.unshift(padString);
			}

			// 最後用 ""字串 隔開組合成一個完整的 String 回傳回去。
			return thisArray.join("");
		}
	}
}

if (String.prototype.padEnd === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading String.prototype.padEnd()");
	String.prototype.padEnd = function padEnd(targetLength, padString) {

		targetLength = targetLength >> 0;

		padString = String((typeof padString !== "undefined" ? padString : " "));
		if (this.length >= targetLength) {
			return String(this);
		} else {
			var thisObj = String(this);
			var thisArray = thisObj.split("");

			while (thisArray.length < targetLength) {
				// 從右邊開始插入padString
				thisArray.push(padString);
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

if (Number.parseInt === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading Number.parseInt()")
	Number.parseInt = window.parseInt;
}

if (Number.parseFloat === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading Number.parseFloat()")
	Number.parseFloat = window.parseFloat;
}

if (Number.isInteger === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log("Start loading Number.isInteger()");
	Number.isInteger = function isInteger(input) {
		if (typeof (input) !== "number") {
			return false;
		}

		var inputStr = String(input);
		if (Number.parseInt(inputStr) === Number.parseFloat(inputStr)) {
			return true;
		} else {
			return false;
		}
	}
}