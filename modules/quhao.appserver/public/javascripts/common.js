/**
 * All Common functions will be defined here
 */

Common = {};

Common.REGEX_TELEPHONE = /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
Common.REGEX_MOBILE = /(^0?[1][358][0-9]{9}$)/;
// Common.REGEX_EMAIL =
// /^[a-zA-Z0-9_\.\-]+\/@([a-zA-Z0-9\-]+\.)+[a-zA-Z0-9]{2,4}$/;
Common.REGEX_EMAIL = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
Common.REGEX_DATE = /^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$/;
Common.REGEX_IP = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\/.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
Common.REGEX_URL = /^[a-zA-z]://///\/[^s]$/;
Common.REGEX_QQ = /^[1-9]\/d{4,9}$/;
Common.REGEX_ONLY_NUMBER = /^[0-9]+$/;
Common.REGEX_ZIPCODE = /^[1-9]\/d{5}$/;
Common.REGEX_ONLY_LETTER = /^[a-zA-Z]+$/;
Common.REGEX_NO_SPECILAL_CHARS = /^[0-9a-zA-Z]+$/;

Common.tel = function(tel){
	if(Common.REGEX_TELEPHONE.test(tel)){
		return true;
	} else {
		return false;
	}
}

/**
 * Validate the mobile format
 * if tel matches mobile format return true, otherwise return false
 */
Common.mobile = function(mobile) {
	if (Common.REGEX_MOBILE.test(mobile)) {
		return true;
	} else {
		return false;
	}
}

/**
 * Validate the email format
 * if email matches mobile format return true, otherwise return false
 */
Common.email = function(email) {
	if (Common.REGEX_EMAIL.test(email)) {
		return true;
	} else {
		return false;
	}
}

/**
 * Validate the number format
 * if num matches number format return true, otherwise return false
 */
Common.number = function(num) {
	if (Common.REGEX_ONLY_NUMBER.test(num)) {
		return true;
	} else {
		return false;
	}
}

/**
 * Popup with a Black Transparent Texture Background
 */
Common.popup = function(newDivID, width, height, container, callbackName) {
	var newMaskID = "mask";
	var newMaskWidth = document.documentElement.clientWidth;
	var newMaskHeight = document.documentElement.clientHeight;

	// mask
	var newMask = document.createElement("div");
	newMask.id = newMaskID;
	newMask.style.position = "absolute";
	newMask.style.zIndex = "1000";
	newMask.style.width = newMaskWidth + "px";
	newMask.style.height = newMaskHeight + "px";
	newMask.style.top = "0px";
	newMask.style.left = "0px";
	newMask.style.background = "black";
	newMask.style.filter = "alpha(opacity=40)";
	newMask.style.opacity = "0.40";
	document.getElementById(container).appendChild(newMask);

	// popup div
	var newDivWidth = parseInt($("#" + newDivID).css("width"));
	var newDivHeight = parseInt($("#" + newDivID).css("height"));
	var newDivtop = (newMaskHeight / 2 - newDivHeight / 2);
	var newDivleft = (newMaskWidth / 2 - newDivWidth / 2);

	document.getElementById(newDivID).style.position = "absolute";
	document.getElementById(newDivID).style.zIndex = "1001";

	if (newDivtop < 0 || newDivtop == 0) {
		document.getElementById(newDivID).style.top = 10 + "px";
	} else {
		document.getElementById(newDivID).style.top = newDivtop + "px";
	}

	$("#" + newDivID).css({
		"left" : newDivleft + "px"
	});
	document.getElementById(newDivID).style.display = "";
	document.getElementById(container).appendChild(
			document.getElementById(newDivID));
	$("#" + container).bind("keydown", function(e) {

		if (Quhao.getEntryKey(e) == 1) {
			return;
		}

		if (window.event) {
			if (e.keyCode == 27) {
				Common.removePopup(newMask, newDivID);
			}
		} else if (e.which) {
			if (e.which == 27) {
				Common.removePopup(newMask, newDivID);
			}
		}
		return;
	});
}

/**
 * remove the popup
 */
Common.removePopup = function(newMask, newDivID) {
	$(newMask).remove();
	$("#" + newDivID).hide();
}

/**
 * check str is null or empty string
 */
Common.isEmpty = function(str){
	if(str == null){
		return true;
	}
	if(str == ""){
		return true;
	}
	if(Quhao.trim(str) == ""){
		return true;
	}
	
	return false;
}

