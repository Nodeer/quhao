Login = {};

/**
 * when click "登录", will switch with hidden and show
 */
Login.exchange = function() {
	if ($("#login_div").is(":hidden")) {
		$("#login_div").show();
	} else {
		$("#login_div").hide();
	}
}

/**
 * validate the login form
 */
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
	if (userPwd.length < 6) {
		$("#error_tip").text("密码长度不小于6位！").css("visibility", "visible");
		return false;
	}
	$("#error_tip").text("请输入手机号或邮箱！").css("visibility", "hidden");
	return true;
}

/**
 * core login function
 */
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
					
					window.location.href = "/b/w?uid="+data.uid;
				}
			},
			error : function() {
				$("#error_tip").text("服务器维护，请稍后尝试。或者联系管理员admin@quhao.la").css("visibility", "visible");
			}
		});
	}
}

Login.entered = function(e){
	if(Quhao.getEntryKey(e) == 1){
		Login.login();
	}
}

Login.forgetPassword = function(){
	
	if(!(Common.email($("#resetEmail").val()))){
		$("#error_tip1").text("请输入正确的邮箱！").css("visibility", "visible");
		return;
	}
	
	$.ajax({
			type : "POST",
			url : "/b/self/AccountController/forget",
			data : $("#reset_form").serialize(),
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null) {
					$("#error_tip1").text(data.value).css("visibility", "visible");
				}
			},
			error : function() {
				$("#error_tip1").text("服务器维护，请稍后尝试。或者联系管理员admin@quhao.la").css("visibility", "visible");
			}
	});
}

Login.resetSubmit = function(){
	var p = $("#password").val();
	var pR = $("#passwordR").val();
	if(p == null || p == ""){
		$("#tips").html("密码不能为空").css("visibility","visible");
		return false;
	}
	
	if(p.length < 6 || p.length > 20){
		$("#tips").html("密码长度6-20").css("visibility","visible");
		return false;
	}
	if(p!=pR){
		$("#tips").html("两次密码输入不一致，请修改").css("visibility","visible");
		return false;
	}
	
	$.ajax({
			type : "POST",
			url : "/b/self/AccountController/resetPassword",
			data : $("#passwordForm").serialize(),
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null) {
					$("#tips").html(data.value).css("visibility", "visible");
				}
			},
			error : function() {
				$("#tips").html("服务器维护，请稍后尝试。或者联系管理员admin@quhao.la").css("visibility", "visible");
			}
	});
	
}