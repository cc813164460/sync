/**
 * Created by maodi on 2018/9/20.
 */

/**
 * 为排序拼接过后的url添加签名signature
 * @param url 排序拼接过后的url
 * @param secretKey hmacsha256加密的秘钥
 */
function addSignature(url, secretKey) {
    var secretKeyUrl = url + "&secret_key=" + secretKey;
    var md5 = MD5(secretKeyUrl).toUpperCase();
    var signature = CryptoJS.HmacSHA256(md5, secretKey).toString();
    url += "&signature=" + signature;
    return url;
}

/**
 * 排序url中的参数
 * @param url 组装好带参数的url请求
 * @returns {string} 排序过后的url请求
 */
function sortUrlParameter(url) {
    var questionMarkIndex = url.indexOf("?");
    var newUrl = url.substring(0, questionMarkIndex);
    url = url.substring(questionMarkIndex + 1, url.length);
    var parametersAndValues = url.split("&");
    var parameterAndValueMap = new Object();
    for (var i in parametersAndValues) {
        var parameterAndValue = parametersAndValues[i];
        var splits = parameterAndValue.split("=");
        var parameter = splits[0];
        var value = splits[1];
        parameterAndValueMap[parameter] = value;
    }
    parameterAndValueMap = objKeySort(parameterAndValueMap);
    var count = 0;
    for (var key in parameterAndValueMap) {
        if (count++ == 0) {
            newUrl += "?";
        } else {
            newUrl += "&";
        }
        newUrl += key + "=" + parameterAndValueMap[key];
    }
    return newUrl;
}

/**
 * 对Object中的key按照a-z排序值
 * @param obj 需要排序的Object
 * @returns {{}} 排序过后的Object
 */
function objKeySort(obj) {
    //先用Object内置类的keys方法获取要排序对象的属性名，再利用Array原型上的sort方法对获取的属性名进行排序，newKeys是一个数组
    var newKeys = Object.keys(obj).sort();
    //创建一个新的对象，用于存放排好序的键值对
    var newObj = {};
    //遍历newKeys数组
    for (var i = 0; i < newKeys.length; i++) {
        //向新创建的对象中按照排好的顺序依次增加键值对
        newObj[newKeys[i]] = obj[newKeys[i]];
    }
    //返回排好序的新对象
    return newObj;
}

/**
 * 扁平化object对象
 * @param obj object对象
 * @returns {{}}
 */
function flatten(obj) {
    var result = {};

    function recurse(cur, prop) {
        if (Object(cur) !== cur) {
            result[prop] = cur;
        } else if (Array.isArray(cur)) {
            for (var i = 0, l = cur.length; i < l; i++) {
                recurse(cur[i], prop + "[" + i + "]");
            }
            if (l == 0) {
                result[prop] = [];
            }
        } else {
            var isEmpty = true;
            for (var p in cur) {
                isEmpty = false;
                recurse(cur[p], prop ? prop + "." + p : p);
            }
            if (isEmpty && prop) {
                result[prop] = {};
            }
        }
    }

    recurse(obj, "");
    return result;
}

/**
 *  将扁平化数据对象化为object
 * @param obj
 * @returns {*}
 */
function unflatten(obj) {
    "use strict";
    if (Object(obj) !== obj || Array.isArray(obj))
        return obj;
    var regex = /\.?([^.\[\]]+)|\[(\d+)\]/g,
        resultHolder = {};
    for (var p in obj) {
        var cur = resultHolder,
            prop = "",
            m;
        while (m = regex.exec(p)) {
            cur = cur[prop] || (cur[prop] = (m[2] ? [] : {}));
            prop = m[2] || m[1];
        }
        cur[prop] = obj[p];
    }
    return resultHolder[""] || resultHolder;
}