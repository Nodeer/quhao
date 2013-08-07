Common = {};

Common.REGEX_TELEPHONE = /^(0[0-9]{2,3}\/-)?([2-9][0-9]{6,7})+(\/-[0-9]{1,4})?$/;
Common.REGEX_MOBILE = /(^0?[1][358][0-9]{9}$)/;
//Common.REGEX_EMAIL = /^[a-zA-Z0-9_\.\-]+\/@([a-zA-Z0-9\-]+\.)+[a-zA-Z0-9]{2,4}$/;
Common.REGEX_EMAIL = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
Common.REGEX_DATE = /^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$/;
Common.REGEX_IP = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\/.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
Common.REGEX_URL = /^[a-zA-z]://///\/[^s]$/;
Common.REGEX_QQ = /^[1-9]\/d{4,9}$/;
Common.REGEX_ONLY_NUMBER = /^[0-9]+$/;
Common.REGEX_ZIPCODE = /^[1-9]\/d{5}$/;
Common.REGEX_ONLY_LETTER = /^[a-zA-Z]+$/;
Common.REGEX_NO_SPECILAL_CHARS = /^[0-9a-zA-Z]+$/;

Common.mobile = function(tel){
	if(Common.REGEX_MOBILE.test(tel)){
		return true;
	}else{
		return false;
	}
}

Common.email = function(email){
	if(Common.REGEX_EMAIL.test(email)){
		return true;
	}else{
		return false;
	}
}

Common.number = function(num){
	if(Common.REGEX_ONLY_NUMBER.test(num)){
		return true;
	}else{
		return false;
	}
}