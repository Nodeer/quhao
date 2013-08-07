Quhao = {};

Quhao.log = function(msg) {
	window.console && console.log(msg);
}

Quhao.trim = function(str) {
	if(str == null || str == ""){
		return "";
	}
	return str.replace(/(^\s*)|(\s*$)/g, "");
};
Quhao.ltrim = function(str) {
	if(str == null || str == ""){
		return "";
	}
	return str.replace(/(^\s*)/g, "");
};
Quhao.rtrim = function(str) {
	if(str == null || str == ""){
		return "";
	}
	return str.replace(/(\s*$)/g, "");
};

Quhao.getEntryKey = function(e) {
	var enterkey = 0;
	if (window.event) {
		if (e.keyCode == 13) {
			enterkey = 1;
		}
	} else if (e.which) {
		if (e.which == 13) {
			enterkey = 1;
		}
	}
	return enterkey;
};

Quhao.encodeURIComponent = function(url) {
	url = encodeURIComponent(url);
	return url;
}

Quhao.truncate = function(st, len) {
	if (st == null || st == "") {
		st = "";
	}

	st = st.toString();
	var origst = st;
	st = removeDoubleQuotes(st);

	if (st.length > len) {
		st = st.substring(0, (len - 3)) + "...";
		st = "<span title='" + origst + "'>" + st + "</span>";
	}
	return st;
};
