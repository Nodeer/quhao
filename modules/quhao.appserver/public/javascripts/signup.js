Signup = {};

Signup.validate = function() {
	var userName = Quhao.trim($("#userName_su").val());
	var userPwd1 = Quhao.trim($("#userPwd1_su").val());
	var userPwd2 = Quhao.trim($("#userPwd2_su").val());
	if (userName == null || userName == "") {
		$("#error_tip_su").text("请输入手机号或邮箱！").css("visibility", "visible");
		return false;
	}
	if (!(Common.email(userName) || Common.mobile(userName))) {
		$("#error_tip_su").text("请输入正确的手机号或邮箱！").css("visibility", "visible");
		return false;
	}
	if (userPwd1 == null || userPwd1 == "") {
		$("#error_tip_su").text("请输入密码！").css("visibility", "visible");
		return false;
	}
	if (userPwd1.length < 6) {
		$("#error_tip_su").text("密码长度不小于6位！").css("visibility", "visible");
		return false;
	}
	if (userPwd2 == null || userPwd2 == "") {
		$("#error_tip_su").text("请再次输入密码！").css("visibility", "visible");
		return false;
	}
	if (userPwd2.length < 6) {
		$("#error_tip_su").text("密码长度不小于6位！").css("visibility", "visible");
		return false;
	}
	
	if (userPwd1 != userPwd2){
		$("#error_tip_su").text("两次密码不一致！").css("visibility", "visible");
		return false;
	}
	$("#error_tip_su").css("visibility", "hidden");
	return true;
}

Signup.signup = function() {
	if (Signup.validate()) {
		
//		$("#su_form").submit();
		
		$.ajax({
			type : "POST",
			url : "/b/self/AccountController/signup",
			data : $("#su_form").serialize(),
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null) {
					if(data.error != ""){
						$("#error_tip_su").text(data.error).css("visibility", "visible");
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