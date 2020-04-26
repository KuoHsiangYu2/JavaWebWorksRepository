"use strict";

if (Number.parseInt === undefined) {
	// 為了支援 IE瀏覽器 加的程式碼
	console.log('Start loading Number.parseInt() for IE')
	Number.parseInt = window.parseInt;
}

if (String.prototype.padStart === undefined) {
	console.log('Start loading padStart() for IE');
	String.prototype.padStart = function padStart(targetLength, padString) {

		// truncate if number or convert non-number to 0;
		targetLength = targetLength >> 0;

		// console.log('targetLength -> ' + targetLength);
		// console.log('this.length -> ' + this.length);
		padString = String((typeof padString !== 'undefined' ? padString : ' '));
		if (this.length >= targetLength) {
			// console.log('第一個if條件判斷式。');
			return String(this);
		} else {
			// console.log('第二個if條件判斷式。');
			var thisObj = String(this);
			var thisArray = thisObj.split('');
			// console.log('thisArray obj');
			// console.log(thisArray);

			while (thisArray.length < targetLength) {
				// 從左邊開始插入padString
				thisArray.unshift(padString);
			}

			// 最後用 ''字串 隔開組合成一個完整的 String 回傳回去。
			return thisArray.join('');
		}
	}
}
