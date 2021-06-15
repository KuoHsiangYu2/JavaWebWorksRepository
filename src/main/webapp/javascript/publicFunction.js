"use strict";

/* 檢查瀏覽器是否支援 window.localStorage */
function supportLocalStorage() {
    try {
        return eval("window.localStorage !== undefined");
    } catch (e) {
        console.log("catch e");
        console.log(e);
        return false;
    }
}
if (!supportLocalStorage()) {
    console.log("Browser didn't support window.localStorage");
}

function check(inputObj, flag, type) {
    if (null == inputObj) {
        throw new TypeError("null type " + type);
    }
    if (flag instanceof RegExp) {
        throw new TypeError("RegExp type " + type);
    }
    return inputObj + "";
}

if (!String.prototype.repeat) {
    /* for support Internet Explorer browser */
    String.prototype.repeat = function (count) {
        var thisObj = check(this, null, "repeat");
        if (0 > count || 1342177279 < count) {
            throw new RangeError("l");
        }
        count |= 0;
        for (var result = ""; count;) {
            if ((count & 1) && (result = result + thisObj), (count = count >> 1)) {
                thisObj = thisObj + thisObj;
            }
        }
        return result;
    }
}

if (!String.prototype.padStart) {
    /* for support Internet Explorer browser */
    String.prototype.padStart = function (targetLength, padString) {
        var thisObj = check(this, null, "padStart");
        targetLength = targetLength - thisObj.length;
        if (void (0) !== padString) {
            padString = String(padString);
        } else {
            padString = " ";
        }
        if (0 < targetLength && padString) {
            return padString.repeat(Math.ceil(targetLength / padString.length)).substring(0, targetLength) + thisObj;
        } else {
            return "" + thisObj;
        }
    }
}

if (!Array.prototype.find) {
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/find
    /* for support Internet Explorer browser */
    Object.defineProperty(Array.prototype, 'find', {
        value: function (predicate) {
            if (this == null) {
                throw new TypeError('"this" is null or not defined');
            }
            var thisObj = Object(this);
            var len = thisObj.length >>> 0;
            if (typeof predicate !== 'function') {
                throw new TypeError('predicate must be a function');
            }
            var thisArg = arguments[1];
            var index = 0;
            while (index < len) {
                var kValue = thisObj[index];
                if (predicate.call(thisArg, kValue, index, thisObj)) {
                    return kValue;
                }
                index++;
            }
            return undefined;
        }
    });
}

if (!String.prototype.includes) {
    /* for support Internet Explorer browser */
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
    /* 這個函數用來檢查瀏覽器是否支援 Template literals */
    /* IE瀏覽器 不支援 Template literals */
    try {
        return eval("'' === ``");
    } catch (e) {
        console.log("catch e");
        console.log(e);
        return false;
    }
}
if (!supportsLiterals()) {
    console.log("Browser didn't support String Template literals");
}

if (!Number.parseInt) {
    /* for support Internet Explorer browser */
    console.log("Start loading Number.parseInt()")
    Number.parseInt = window.parseInt;
}

if (!Number.parseFloat) {
    /* for support Internet Explorer browser */
    console.log("Start loading Number.parseFloat()")
    Number.parseFloat = window.parseFloat;
}

if (!Number.isInteger) {
    /* for support Internet Explorer browser */
    console.log("Start loading Number.isInteger()");
    Number.isInteger = function isInteger(input) {
        if (typeof (input) !== "number") {
            return false;
        }

        var inputStr = String(input).toString();
        if (Number.parseInt(inputStr, 10) === Number.parseFloat(inputStr)) {
            return true;
        } else {
            return false;
        }
    }
}