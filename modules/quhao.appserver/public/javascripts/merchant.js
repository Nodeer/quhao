Merchant = {};

/**
 * reset the merchant infomation form
 */
Merchant.reset = function(){
	
	$("#description").val("");
	$("#merchantImage").val("");
	$("#address").val("");
	$("#tel").val("");
	$("#cateType").val("");
	$("#openTime").val("");
	$("#closeTime").val("");
}

/**
 * update merchant information
 */
Merchant.update = function(){
	if(Merchant.validate()){
		$("#merchantForm").submit();
	}
}

/**
 * validate form
 */
Merchant.validate = function(){
	var description = $("#description").val();
	var merchantImage = $("#merchantImage").val();
	var address = $("#address").val();
	var tel = $("#tel").val();
	var cateType = $("#cateType").val();
	var openTime = $("#openTime").val();
	var closeTime = $("#closeTime").val();
	
	if(Common.isEmpty(description)){
		alert("请输入商家描述");
		return false;
	}
	if(Common.isEmpty(address)){
		alert("请输入商家详细地址");
		return false;
	}
	if(Common.isEmpty(tel)){
		alert("请输入联系方式");
		return false;
	}
	if(Common.isEmpty(cateType)){
		alert("请选择商家菜系");
		return false;
	}
	if(Common.isEmpty(openTime)){
		alert("请选择营业时间（开始）");
		return false;
	}
	if(Common.isEmpty(closeTime)){
		alert("请选择营业时间（结束）");
		return false;
	}
	return true;
}

Merchant.goPaiduiPage = function(mid){
	window.location.href="/b/w/goPaiduiPage?mid="+mid;
}

Merchant.goPersonalPage = function(aid){
	window.location.href="/b/w/goPersonalPage?aid="+aid;
}

Merchant.autoRefresh = function(mid){
	window.setInterval(refresh,1000 * 60,mid);
}

function refresh(mid){
	$.ajax({
		type:"POST",
		url:"/b/w/paiduiPageAutoRefresh",
		dataType:"HTML",
		data:{"mid":mid},
		success:function(data){
			$("#dataDetailsDiv").html(data);
		},
		error:function(){
			alert("服务器维护中，马上就好。");
		}
	});
}

//<!--
function hello(_name){
       alert("hello,"+_name);
}
//*=============================================================
//*   功能： 修改 window.setInterval ，使之可以传递参数和对象参数   
//*   方法： setInterval (回调函数,时间,参数1,,参数n)  参数可为对象:如数组等
//*============================================================= 

var __sto = setInterval;    
window.setInterval = function(callback,timeout,param){
    var args = Array.prototype.slice.call(arguments,2);    
    var _cb = function(){    
        callback.apply(null,args);    
    }
    __sto(_cb,timeout);    
}

//-->