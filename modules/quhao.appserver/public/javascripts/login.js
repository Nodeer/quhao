Login = {};

Login.exchange = function() {
	if ($("#login_div").is(":hidden")) {
		$("#login_div").show();
	} else {
		$("#login_div").hide();
	}
}

Login.validate = function() {
	var userName = Quhao.trim($("#userName").val());
	var userPwd = Quhao.trim($("#userPwd").val());
	if (userName == null || userName == "") {
		$("#error_tip").text("请输入手机号或邮箱！").css("visibility", "visible");
		return false;
	}
	if (!(Common.email(userName) || Common.mobile(userName))) {
		$("#error_tip").text("请输入正确的手机号或邮箱！").css("visibility", "visible");
		return false;
	}
	if (userPwd == null || userPwd == "") {
		$("#error_tip").text("请输入密码！").css("visibility", "visible");
		return false;
	}
	if (userPwd.length < 8) {
		$("#error_tip").text("密码长度不小于8位！").css("visibility", "visible");
		return false;
	}
	$("#error_tip").text("请输入手机号或邮箱！").css("visibility", "hidden");
	return true;
}

Login.login = function() {
	if (Login.validate()) {
		$.ajax({
			type : "POST",
			url : "/b/self/AccountController/login",
			data : $("#login_form").serialize(),
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null) {
					if(data.error != ""){
						$("#error_tip").text(data.error).css("visibility", "visible");
						return;
					}
					
					window.location.href = "/b/m/home?uid="+data.uid;
				}
			},
			error : function() {
				alert("ajax error!");
			}
		});
	}
}