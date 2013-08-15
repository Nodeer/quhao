/**
 * Sign up function
 */

Signup = {};

/**
 * validate the sign up form,
 * the parameter type is "email" or "mobile"
 */
Signup.validate = function(type) {
	if(type == "email"){
		var userName=Quhao.trim($("#userName_su").val());
		var userPwd1=Quhao.trim($("#userPwd1_su").val());
		var userPwd2=Quhao.trim($("#userPwd2_su").val());
		if(userName == "" || userName == null){
			$("#error_tip_su").text("请输入邮箱").css("visibility","visible");
			return false;
		}
		if(!Common.email(userName)){
			$("#error_tip_su").text("请输入正确的邮箱").css("visibility","visible");
			return false;
		}
		if(userPwd1 == "" || userPwd1 == null){
			$("#error_tip_su").text("请输入密码").css("visibility","visible");
			return false;
		}
		if(userPwd2 == "" || userPwd2 == null){
			$("#error_tip_su").text("请输入确认密码").css("visibility","visible");
			return false;
		}
		if(userPwd1.length < 6 || userPwd1.length > 20  ){
			$("#error_tip_su").text("输入密码长度6-20位").css("visibility","visible");
			return false;
		}
		if(userPwd1 != userPwd2){
			$("#error_tip_su").text("两次密码输入不一致").css("visibility","visible");
			return false;
		}
		$("#error_tip_su").css("visibility", "hidden");
		return true;	
	}
}


/**
 * Sign up core function
 * the parameter type is "email" or "mobile"
 */
Signup.signup = function(type) {
	var formName = type+"_form";
	if (Signup.validate(type)) {
		$.ajax({
			type : "POST",
			//
			url : "/b/self/AccountController/signup",
			data : $("#"+formName).serialize(),
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

/**
 * switch sign up between by email and mobile 
 */
Signup.changeType = function(){
	var type = $("#signup_type").val();
	if(type == "mobile"){
		$("#email_form").hide();
		$("#mobile_form").show();
	}
	if(type == "email"){
		$("#email_form").show();
		$("#mobile_form").hide();
	}
}