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
		if(userPwd1.length < 6 || userPwd1.length > 12  ){
			$("#error_tip_su").text("输入密码长度6-12位").css("visibility","visible");
			return false;
		}
		if(userPwd2 == "" || userPwd2 == null){
			$("#error_tip_su").text("请输入确认密码").css("visibility","visible");
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

/**
 * 验证商家提交信息的表单
 * @return {Boolean}
 */
Signup.validateSubmitInfo = function(){
	var companyName = $("#companyName").val();
	var peopleName = $("#peopleName").val();
	var peopleContact = $("#peopleContact").val();
	var peopleEmail = $("#peopleEmail").val();
	var captchaCode = $("#captchaCode").val();
	
	var warningIcon = "<span style='margin-right:10px;' class='glyphicon glyphicon-exclamation-sign'></span>"
	
	if(Common.isEmpty(companyName)){
		$("#submitinfotips").html(warningIcon+"请输入公司/餐厅名称").show();
		return false;
	}
	if(Common.isEmpty(peopleName)){
		$("#submitinfotips").html(warningIcon+"请输入你的姓名").show();
		return false;
	}
	if(Common.isEmpty(peopleContact)){
		$("#submitinfotips").html(warningIcon+"请输入你的联系方式").show();
		return false;
	}
	if(!Common.number(peopleContact)){
		$("#submitinfotips").html(warningIcon+"请输入正确的联系方式").show();
		return false;
	}
	
	if(Common.isEmpty(peopleEmail)){
		$("#submitinfotips").html(warningIcon+"请输入你的邮件地址").show();
		return false;
	}
	
	if(!Common.email(peopleEmail)){
		$("#submitinfotips").html(warningIcon+"请输入正确的邮件地址").show();
		return false;
	}
	
	if(Common.isEmpty(captchaCode)){
		$("#submitinfotips").html(warningIcon+"请输入验证码").show();
		return false;
	}
	
	return true;
}

/**
 * 提交商家的信息
 */
Signup.submitinfo = function(){
	var flag = Signup.validateSubmitInfo();
	if(flag){
		$.ajax({
			type : "POST",
			url : "/b/self/AccountController/submitinfo",
			data : $("#businessInfoForm").serialize(),
			dataType : "json",
			async : false,
			success : function(data) {
				if(data.errorKey == "true"){
					var successIcon = "<span style='margin-right:10px;' class='glyphicon glyphicon-ok'></span>"
					$("#submitinfotips").html(successIcon+"成功！2个工作日内会联系你。").show();
					setTimeout('location.reload()', 2000);
				} else {
					var warningIcon = "<span style='margin-right:10px;' class='glyphicon glyphicon-exclamation-sign'></span>"
					$("#submitinfotips").html(warningIcon+data.errorText).show();
//					$("#submitinfotips").html(warningIcon+"提交失败！请联系管理员").show();
				}
			},
			error : function() {
				alert("请联系管理员service@quhao.la");
				window.location.href = "/business";
			}
		});
	}
}
