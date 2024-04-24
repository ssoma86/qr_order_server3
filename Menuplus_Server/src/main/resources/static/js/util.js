/*<![CDATA[*/
var util = {
	/* 전화번호 */
	inputTel: function (val) {
		val = "" + val;
		return val.replace(/[^0-9+-\s]/g, "");
	},
	/* 전화번호 */
	inputTelNew: function (val) {
		val.target.value = "" + val.target.value;
		return val.target.value.replace(/[^0-9+-\s]/g, "");
	},
	inputBusiNum: function (val) {
		val = "" + val;
		return val.replace(/[^0-9-\s]/g, "");
	},
	inputBusiNumNew: function (val) {
		val.target.value = "" + val.target.value;
		return val.target.value.replace(/[^0-9-\s]/g, "");
	},
	/* 수자 */
	inputNum: function (val) {
		val = "" + val; // 텍스트 입력 폼이지만 수자만 있을 시 스크립트 변수는 Num으로 들어올수 있음
		return val.replace(/[^0-9]/g, "");
	},
	/* 이미지 2 Base64 */
	imgToBase64: function(url, outputFormat, callback) {
		var canvas = document.createElement('CANVAS'),
		ctx = canvas.getContext('2d'),
		img = new Image();
		img.onload = function() {
			canvas.height = img.height;
			canvas.width = img.width;
			ctx.drawImage(img,0,0);
			var dataURL = canvas.toDataURL(outputFormat || 'image/png');
			callback(dataURL, img);
			canvas = null;
		};
		img.src = url;
	},
	copyObj: function (obj) {
		return JSON.parse(JSON.stringify(obj));
	},
	getNow: function (fmt) {
		let now = new Date();
		let o = {
				"M+" : now.getMonth()+1,		//월
			    "d+" : now.getDate(),			//일   
			    "H+" : now.getHours(),			//시   
			    "m+" : now.getMinutes(),		//분   
			    "s+" : now.getSeconds(),		//초      
			    "S"  : now.getMilliseconds()	//밀리새턴드   
		};   
		
		// 년
		if (/(y+)/.test(fmt)) {
	        fmt = fmt.replace(RegExp.$1, (now.getFullYear()+"").substr(4 - RegExp.$1.length));           
	    }
		
		for (let k in o) {
			if(new RegExp("("+ k +")").test(fmt)) {
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
			}
		}
		
		return fmt;
	},
	getDate: function (fmt, date) {
		let now = date;
		let o = {
				"M+" : now.getMonth()+1,		//월
			    "d+" : now.getDate(),			//일   
			    "H+" : now.getHours(),			//시   
			    "m+" : now.getMinutes(),		//분   
			    "s+" : now.getSeconds(),		//초      
			    "S"  : now.getMilliseconds()	//밀리새턴드   
		};   
		
		// 년
		if (/(y+)/.test(fmt)) {
	        fmt = fmt.replace(RegExp.$1, (now.getFullYear()+"").substr(4 - RegExp.$1.length));           
	    }
		
		for (let k in o) {
			if(new RegExp("("+ k +")").test(fmt)) {
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
			}
		}
		
		return fmt;
	},
	// 날자 계산
	dateAdd: function (interval, number, date) {
		if (this.isEmpty(date)) {
			date = new Date();
		}
		
		try {
		    switch (interval) {
		    case "y ":
		        date.setFullYear(date.getFullYear() + number);
		        return date;
		        break;
		    case "q ":
		        date.setMonth(date.getMonth() + number * 3);
		        return date;
		        break;
		    case "m ":
		        date.setMonth(date.getMonth() + number);
		        return date;
		        break;
		    case "w ":
		        date.setDate(date.getDate() + number * 7);
		        return date;
		        break;
		    case "d ":
		        date.setDate(date.getDate() + number);
		        return date;
		        break;
		    case "h ":
		        date.setHours(date.getHours() + number);
		        return date;
		        break;
		    case "M ":
		        date.setMinutes(date.getMinutes() + number);
		        return date;
		        break;
		    case "s ":
		        date.setSeconds(date.getSeconds() + number);
		        return date;
		        break;
		    default:
		        date.setDate(date.getDate() + number);
		        return date;
		        break;
		    }
		} catch(e) {
			console.log(e);
		}
	},
    isEmpty: function (v) {
        if (null == v || typeof(v) == 'undefined' || 'undefined' == v || '' == v) {
            return true;
        } else {
            return false;
        }
    },
    isArray: function (v) {
    	if (v instanceof Array) {
    		return true;
        } else {
            return false;
        }
    },
    formatMoney: function (money) {
    	let m = 0;
    	
    	if (!this.isEmpty(money)) {
    		m = money.toFixed(1).replace(/(\d)(?=(\d{3})+\.)/g, '$1,').replace('.0', '');
    	}
    	
    	return m;
    },
    setCookie: function (c_name , value, expiredays) {
    	this.delCookie(c_name)
		
		var exdate = new Date()
		exdate.setDate(exdate.getDate() + expiredays)
		document.cookie = c_name + "=" + escape(value) + ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString()) + ";path=/;"
    },
	getCookie: function (c_name) {
		if (document.cookie.length > 0) {
			var c_start = document.cookie.indexOf(c_name + "=")
			
			if (c_start != -1) { 
				c_start = c_start + c_name.length + 1
				var c_end = document.cookie.indexOf(";", c_start)
				
				if (c_end == -1) c_end = document.cookie.length
				
				return unescape(document.cookie.substring(c_start,c_end))
			}
		}
		
		return ""
	},
	/* Cookie 삭제 */
	delCookie: function (c_name) {
		var exdate = new Date()
		exdate.setTime(exdate.getTime() - 1)
		var value = this.getCookie(c_name)
		if(value != null)
			document.cookie = c_name + "=" + escape(value) + ";expires=" + exdate.toGMTString() + ";path=/;"
	}
	
}
/*]]>*/