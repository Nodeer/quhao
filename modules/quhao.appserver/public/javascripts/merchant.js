Merchant = {};

Merchant.ALL = null;

Merchant.ajaxSearch = function(keyword,type){
	$.ajax({
		type:"GET",
		url:"/search",
		dataType:"JSON",
		data:{"name":keyword},
		success:function(data){
			if(data != null && data.length > 0){
				
				var availableNames = [];
				for(var i=0; i< data.length; i++){
					console.log(data[i].name);
					var item = {};
					item["label"] = data[i].name + ", " + data[i].address;
					item["value"] = data[i].name;
					item["key"] = data[i].id;
					availableNames.push(item);
				}
				$("#merchantName").autocomplete({
					source: availableNames,
					select: function( event, ui ) {
						alert(ui.item.label);
						alert(ui.item.value);
						alert(ui.item.key);
						
						$.ajax({
							type:"GET",
							url:"/merchant",
							dataType:"JSON",
							data:{"id":ui.item.key},
							success:function(data){
								console.log(data);
								$("#description").val(data.description);
								
								// add merchant image here
								$("#address").val(data.address);
								$("#tel").val(data.telephone);
							},
							error:function(){
								alert("服务器维护中，马上就好。");
							}
						});
					}
				});
			}
			
			if(type == "search"){
				$('#createMerchantDescription').html('没有搜索到相关记录，确定使用<font style=\"color:red;\">'+ keyword +'</font>作为商家名称吗?');
				$('#createMerchant').modal();
			}
		},
		error:function(){
			alert("服务器维护中，马上就好。");
		}
	});
}

Merchant.findMerchant = function(mNameObj){
	var keyword = Quhao.trim($(mNameObj).val());
	Merchant.ajaxSearch(keyword,'keyup');
}

Merchant.search = function(){
	var keyword = Quhao.trim($("#merchantName").val());
	if(keyword == null || keyword == ""){
		$("#tips").html("请输入商家名称或者相关关键字").show();
		return;
	}
	
	Merchant.ajaxSearch(keyword,'search');
}

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

Merchant.enableEdit = function(){
	if(window.confirm("更改桌位类型将清空当前排队号，请谨慎操作！")){
		$("#description").removeAttr("disabled");
		$("#address").removeAttr("disabled");
		$("#merchantImage").removeAttr("disabled");
		$("#tel").removeAttr("disabled");
		$("#cateType").removeAttr("disabled");
		$("#openTime").removeAttr("disabled");
		$("#closeTime").removeAttr("disabled");
		$("input[name=seatType]").removeAttr("disabled");
		$("#btnupdate").show();
	}else{
	}
}

Merchant.create = function(){
	$("#description").removeAttr("disabled");
	$("#address").removeAttr("disabled");
	$("#merchantImage").removeAttr("disabled");
	$("#tel").removeAttr("disabled");
	$("#cateType").removeAttr("disabled");
	$("#openTime").removeAttr("disabled");
	$("#closeTime").removeAttr("disabled");
	$("input[name=seatType]").removeAttr("disabled");
	$("#btnli").removeAttr("hidden");
	$('#createMerchant').modal('hide');
	$("#btnupdate").show();
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
	
	// check if at least one checkbox is selected
	var i = 1;
	for(;i<=20; i++){
		var obj = $("#seat"+i);
		if(obj.attr("checked")=="checked"){
			break;
		}
	}
	
	if(Common.isEmpty(description)){
		$("#tips").html("请输入商家描述").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200);   
		return false;
	}
	if(Common.isEmpty(address)){
		$("#tips").html("请输入商家详细地址").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200); 
		return false;
	}
	
	// validate telephone
	if(Common.isEmpty(tel)){
		$("#tips").html("请输入联系方式").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200); 
		return false;
	}
	if(!Common.number(tel)){
		$("#tips").html("请输入正确的联系方式，格式02183004700").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200);
		return false;
	}
	
	
	if(Common.isEmpty(cateType)){
		$("#tips").html("请选择商家菜系").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200); 
		return false;
	}
	if(Common.isEmpty(openTime)){
		$("#tips").html("请选择营业时间（开始）").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200); 
		return false;
	}
	if(Common.isEmpty(closeTime)){
		$("#tips").html("请选择营业时间（结束）").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200); 
		return false;
	}
	if(i == 21){
		$("#tips").html("请至少选择一个桌位类型").show();
		$("html,body").animate({scrollTop: $("#body").offset().top}, 200); 
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
Merchant.goStatisticPage = function(mid){
	window.location.href="/b/w/goStatisticPage?mid="+mid;
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

/**
 * finish one reservation
 */
Merchant.finish = function(seatNumber, currentNumber, mid){
	$.ajax({
		type:"POST",
		url:"/b/w/finishByMerchant",
		dataType:"HTML",
		data:{"currentNumber":currentNumber,"seatNumber":seatNumber,"mid":mid},
		success:function(data){
			if(data == true){
				alert("success");
			}else{
				alert("操作失败");
			}
		},
		error:function(){
			alert("服务器维护中，马上就好。");
		}
	});
}

/**
 * expire one reservation
 *
 */
// TODO add expire function here
Merchant.expired = function(seatNumber, currentNumber, mid){
	
}

Merchant.logout = function(aid){
	$.ajax({
		type:"POST",
		url:"/b/a/logout",
		dataType:"json",
		data:{"aid":aid},
		success:function(data){
			if(data.error == ""){
				window.location.href="/b/m";
			}
		},
		error:function(){
			alert("服务器维护中，马上就好。");
		}
	});
}