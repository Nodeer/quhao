Login = {};

Login.show = function(){
	$("#login_div").show();
}

Login.hide = function(){
	$("#login_div").hide();
}

Login.exchange = function(){
	if($("#login_div").is(":hidden")){
		$("#login_div").show();
	} else {
		$("#login_div").hide();
	}
}