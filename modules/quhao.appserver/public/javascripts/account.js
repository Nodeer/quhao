Account = {};

Account.validate = function(){
	var oPwd = $("#oPwd").val();
	var nPwd = $("#nPwd").val();
	var nPwdR = $("#nPwdR").val();
	
	if(Common.isEmpty(oPwd)){
		alert("请输入原始密码");
		return false;
	}
	if(Common.isEmpty(nPwd)){
		alert("请输入新密码");
		return false;
	}
	if(Common.isEmpty(nPwdR)){
		alert("请再次输入新密码");
		return false;
	}
	if(nPwd != nPwdR){
		alert("两次新密码不一致，请重新输入");
		return false;
	}
	return true;
}

Account.updatePwd = function(){
	var uid = $("#uid").val();
	var oPwd = $("#oPwd").val();
	var nPwd = $("#nPwd").val();
	var nPwdR = $("#nPwdR").val();
	if(Account.validate()){
		$.ajax({
			type:"POST",
			url:"/b/a/updatePwd",
			dataType:"json",
			data:{"uid":uid, "oPwd":oPwd, "nPwd":nPwd ,"nPwdR":nPwdR},
			success:function(data){
				if(data.success){
					alert("密码修改成功");
				}else{
					alert(data.value);
				}
			},
			error:function(){
				alert("服务器维护中，马上就好。");
			}
		});
	}
}

