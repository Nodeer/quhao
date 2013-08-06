Login = {};

Login.show = function(){
	$("#login_div").show();
}

Login.hide = function(){
	$("#login_div").hide();
}

Login.exchange = function(){
	if($("#login_div").is(":hidden")){
		$("#login_div").show('bounce',0);
	} else {
		$("#login_div").hide('bounce',0);
	}
}