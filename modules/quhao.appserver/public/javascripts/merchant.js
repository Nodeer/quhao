Merchant = {};

Merchant.ALL = null;

Merchant.ajaxSearch = function(keyword, type) {

	if (type == "check") {
		url = "/check?type=check"
	} else {
		url = "/check?type=think"
	}

	$.ajax({
		type : "GET",
		url : url,
		dataType : "JSON",
		data : {
			"name" : keyword
		},
		success : function(data) {
			
			console.log("type:"+type);
			
			if (type == "think") {
				if (data != null && data.length > 0) {
					var availableNames = [];
					for (var i = 0; i < data.length; i++) {
						var item = {};
						item["label"] = data[i].name + ", " + data[i].address;
						item["value"] = data[i].name;
						item["key"] = data[i].id;
						availableNames.push(item);
					}
					$("#merchantName").autocomplete({
						minChars : 0,
						source : availableNames,
						delay: 1000,
						select : function(event, ui) {
							$.ajax({
										type : "GET",
										url : "/merchant",
										dataType : "JSON",
										data : {
											"id" : ui.item.key
										},
										success : function(data) {
											console.log(data);
											console.log(data.id);
											$("#mid").val(data.id);
											$("#description")
													.val(data.description);

											// add merchant image here
											$("#address").val(data.address);
											$("#x").val(data.x);
											$("#y").val(data.y);
											
											$("#tel").val(data.telephone);
											$("#cityCode").val(data.cityCode);
											Merchant.enableEdit();
										},
										error : function() {
											alert("服务器维护中，马上就好。");
										}
									});
						}
					});
				} 
			}
			
			if(type=="check"){ // 检查是否有同名商家
					console.log(data);
					if (data != null && data.length > 0) {
						$("#tips").html("已经有同名的商家了，请换个名称").show();
					} else {
						$('#createMerchantDescription')
								.html('没有搜索到相关记录，确定使用<font style=\"color:red;\">'
										+ keyword + '</font>作为商家名称吗?');
						$('#createMerchant').modal();
					}
				}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}

Merchant.findMerchant = function(mNameObj, e) {
	// 回车触发联想
	if(Quhao.getEntryKey(e) == 1){
		var keyword = Quhao.trim($(mNameObj).val());
		Merchant.ajaxSearch(keyword, 'think');
	}
}

Merchant.search = function() {
	var keyword = Quhao.trim($("#merchantName").val());
	if (keyword == null || keyword == "") {
		$("#tips").html("请输入商家名称或者相关关键字").show();
		return;
	}

	Merchant.ajaxSearch(keyword, 'check');
}

/**
 * reset the merchant infomation form
 */
Merchant.reset = function() {

	$("#description").val("");
	$("#merchantImage").val("");
	$("#address").val("");
	$("#tel").val("");
	$("#cateType").val("");
	$("#cateType1").val("");
	$("#openTime").val("");
	$("#closeTime").val("");
}

Merchant.enableEdit = function() {
	$("#description").removeAttr("disabled");
	$("#cityCode").removeAttr("disabled");
	$("#address").removeAttr("disabled");
	$("#merchantImage").removeAttr("disabled");
	$("#tel").removeAttr("disabled");
	$("#cateType").removeAttr("disabled");
	$("#cateType1").removeAttr("disabled");
	$("#openTime").removeAttr("disabled");
	$("#closeTime").removeAttr("disabled");
	$("input[name=seatType]").removeAttr("disabled");
	$('#updateMerchant').modal('hide');
	$("#btnupdate").show();
}

Merchant.create = function() {
	$("#description").removeAttr("disabled");
	$("#cityCode").removeAttr("disabled");
	$("#address").removeAttr("disabled");
	$("#merchantImage").removeAttr("disabled");
	$("#tel").removeAttr("disabled");
	$("#cateType").removeAttr("disabled");
	$("#cateType1").removeAttr("disabled");
	$("#openTime").removeAttr("disabled");
	$("#closeTime").removeAttr("disabled");
	$("input[name=seatType]").removeAttr("disabled");
	$('#createMerchant').modal('hide');
	$("#btnupdate").show();
}

/**
 * update merchant information
 */
Merchant.update = function() {
	if (Merchant.validate()) {
		$("#merchantForm").submit();
	}
}

/**
 * validate form
 */
Merchant.validate = function() {
	var description = $("#description").val();
	var merchantImage = $("#merchantImage").val();
	var address = $("#address").val();
	var tel = $("#tel").val();
	var cateType = $("#cateType").val();
	var openTime = $("#openTime").val();
	var closeTime = $("#closeTime").val();
	var cityCode = $("#cityCode").val();

	// check if at least one checkbox is selected
	var ii = 1;
	for (; ii <= 20; ii++) {
		var obj = $("#seat" + ii);
		if (obj.attr("checked") == "checked") {
			break;
		}
	}

	if (Common.isEmpty(description)) {
		$("#tips").html("请输入商家描述").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}
	if (Common.isEmpty(cityCode)) {
		$("#tips").html("请选择商家所在城市").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}
	if (Common.isEmpty(address)) {
		$("#tips").html("请输入商家详细地址").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}

	// validate telephone
	if (Common.isEmpty(tel)) {
		$("#tips").html("请输入联系方式").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}

	var tels = tel.split(",");
	var telsLength = tels.length;
	console.log(tels);
	console.log(telsLength);
	for (var i = 0; i < telsLength; i++) {
		if (!Common.tel(tels[i])) {
			$("#tips").html("请输入正确的联系方式，格式021-83004700").show();
			$("html,body").animate({
						scrollTop : $("#body").offset().top
					}, 200);
			return false;
		}
	}

	if (Common.isEmpty(cateType)) {
		$("#tips").html("请选择商家菜系").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}
	if (Common.isEmpty(openTime)) {
		$("#tips").html("请选择营业时间（开始）").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}
	if (Common.isEmpty(closeTime)) {
		$("#tips").html("请选择营业时间（结束）").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}

	var timeTest = closeTime.split(":")[0] - openTime.split(":")[0];
	if (timeTest <= 0) {
		$("#tips").html("营业时间（结束）必须晚于营业时间（开始）").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}

	console.log(ii+"=====");
	
	if (ii == 21) {
		$("#tips").html("请至少选择一个桌位类型").show();
		$("html,body").animate({
					scrollTop : $("#body").offset().top
				}, 200);
		return false;
	}
	return true;
}

Merchant.goPaiduiPage = function(mid) {
	window.location.href = "/b/w/goPaiduiPage?mid=" + mid;
}

Merchant.goPersonalPage = function(aid, mid) {
	window.location.href = "/b/w/goPersonalPage?aid=" + aid + "&mid=" + mid;
}
Merchant.goStatisticPage = function(mid) {
	window.location.href = "/b/w/goStatisticPage?mid=" + mid;
}
Merchant.goYouhuiPage = function(mid) {
	window.location.href = "/b/w/goYouhuiPage?mid=" + mid;
}

Merchant.autoRefresh = function(mid) {
	window.setInterval(refresh, 1000 * 60, mid);
}

function refresh(mid) {
	$.ajax({
				type : "POST",
				url : "/b/w/paiduiPageAutoRefresh",
				dataType : "HTML",
				data : {
					"mid" : mid
				},
				success : function(data) {
					$("#autoRefreshDiv").html(data);
				},
				error : function() {
					alert("服务器维护中，马上就好。");
				}
			});
}

// <!--
function hello(_name) {
	alert("hello," + _name);
}
// *=============================================================
// * 功能： 修改 window.setInterval ，使之可以传递参数和对象参数
// * 方法： setInterval (回调函数,时间,参数1,,参数n) 参数可为对象:如数组等
// *=============================================================

var __sto = setInterval;
window.setInterval = function(callback, timeout, param) {
	var args = Array.prototype.slice.call(arguments, 2);
	var _cb = function() {
		callback.apply(null, args);
	}
	__sto(_cb, timeout);
}

// -->

/**
 * about modal
 */
Merchant.about = function() {
	$("#about").remove();
	var modalHTML = ""
			+ "<div class=\"modal fade\" id=\"about\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">"
			+ "<div class=\"modal-dialog\">"
			+ "<div class=\"modal-content\">"
			+ "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>"
			+ "<h4 class=\"modal-title\" id=\"about_title\">关于</h4>"
			+ "</div>"
			+ "<div class=\"modal-body\" id=\"about_body\">排队不用等，从取号开始"
			+ "</div>"
			+ "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">关闭</button>"
			+ "</div>" + "</div>" + "</div>" + "</div>";

	$("body").append(modalHTML);
	$("#about").modal();
}

/**
 * about modal
 */
Merchant.contactus = function() {
	$("#contactus").remove();
	var modalHTML = ""
			+ "<div class=\"modal fade\" id=\"contactus\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">"
			+ "<div class=\"modal-dialog\">"
			+ "<div class=\"modal-content\">"
			+ "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>"
			+ "<h4 class=\"modal-title\" id=\"about_title\">联系我们</h4>"
			+ "</div>"
			+ "<div class=\"modal-body\" id=\"about_body\">邮箱 : support@quhao.la<br/><br/>电话 : 021-52990657-8003"
			+ "</div>"
			+ "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">关闭</button>"
			+ "</div>" + "</div>" + "</div>" + "</div>";

	$("body").append(modalHTML);
	$("#contactus").modal();
}

/**
 * finish one reservation confirmation
 */
Merchant.finishConfirm = function(seatNumber, currentNumber, mid) {
	$("#xiaofei_confirm").remove();
	var modalHTML = ""
			+ "<div class=\"modal fade\" id=\"xiaofei_confirm\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">"
			+ "<div class=\"modal-dialog\">"
			+ "<div class=\"modal-content\">"
			+ "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>"
			+ "<h4 class=\"modal-title\" id=\"xiaofei_confirm_title\"></h4>"
			+ "</div>"
			+ "<div class=\"modal-body\" id=\"xiaofei_confirm_body\">"
			+ "</div>"
			+ "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">取消</button>"
			+ "<button type=\"button\" class=\"btn btn-primary\" onclick=\"Merchant.finish('"
			+ seatNumber + "','" + currentNumber + "','" + mid
			+ "');\">确定</button>" + "</div>" + "</div>" + "</div>" + "</div>";

	$("#paiduiPageBody").append(modalHTML);
	$("#xiaofei_confirm_title").html("请确认");
	$("#xiaofei_confirm_body").html("确认第<font style='color: red;'>"
			+ currentNumber + "</font>号消费吗？");
	$("#xiaofei_confirm").modal();
}

/**
 * finish one reservation
 */
Merchant.finish = function(seatNumber, currentNumber, mid) {
	$("#xiaofei_confirm").modal("hide");
	$.ajax({
				type : "POST",
				url : "/b/w/finishByMerchant",
				dataType : "JSON",
				data : {
					"currentNumber" : currentNumber,
					"seatNumber" : seatNumber,
					"mid" : mid
				},
				success : function(data) {
					if (data == true) {
						window.location.reload();
					} else {
						alert("操作失败");
					}
				},
				error : function() {
					alert("服务器维护中，马上就好。");
				}
			});
}

/**
 * expire one reservation confirmation
 * 
 */
Merchant.expiredConfirm = function(seatNumber, currentNumber, mid) {
	$("#guoqi_confirm").remove();
	var modalHTML = ""
			+ "<div class=\"modal fade\" id=\"guoqi_confirm\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">"
			+ "<div class=\"modal-dialog\">"
			+ "<div class=\"modal-content\">"
			+ "<div class=\"modal-header\">"
			+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>"
			+ "<h4 class=\"modal-title\" id=\"guoqi_confirm_title\"></h4>"
			+ "</div>"
			+ "<div class=\"modal-body\" id=\"guoqi_confirm_body\">"
			+ "</div>"
			+ "<div class=\"modal-footer\">"
			+ "<button type=\"button\" class=\"btn btn-primary\" data-dismiss=\"modal\">取消</button>"
			+ "<button type=\"button\" class=\"btn btn-primary\" onclick=\"Merchant.expired('"
			+ seatNumber + "','" + currentNumber + "','" + mid
			+ "');\">确定</button>" + "</div>" + "</div>" + "</div>" + "</div>";

	$("#paiduiPageBody").append(modalHTML);
	$("#guoqi_confirm_title").html("请确认");
	$("#guoqi_confirm_body").html("<p>确认第<font style='color: red;'>"
			+ currentNumber + "</font>号过期吗？</p>");
	$("#guoqi_confirm").modal();
}

/**
 * expire one reservation
 * 
 */
Merchant.expired = function(seatNumber, currentNumber, mid) {
	$("#guoqi_confirm").modal("hide");
	$.ajax({
				type : "POST",
				url : "/b/w/expireByMerchant",
				dataType : "JSON",
				data : {
					"currentNumber" : currentNumber,
					"seatNumber" : seatNumber,
					"mid" : mid
				},
				success : function(data) {
					if (data == true) {
						window.location.reload();
					} else {
						alert("服务器维护中，马上就好。");
					}
				},
				error : function() {
					alert("服务器维护中，马上就好。");
				}
			});
}

Merchant.quhaoOnsiteConfirm = function(seatNumber, mid) {
	$("#xianchangquhao_confirm").remove();

	var divModalFade = document.createElement("div");
	$(divModalFade).addClass("modal fade").attr("id", "xianchangquhao_confirm")
			.attr("tabindex", "-1").attr("role", "dialog").attr(
					"aria-labelledby", "myModalLabel").attr("aria-hidden",
					"true");

	var divModalDialog = document.createElement("div");
	$(divModalDialog).addClass("modal-dialog");

	var divModalContent = document.createElement("div");
	$(divModalContent).addClass("modal-content");

	var divModalHeader = document.createElement("div");
	$(divModalHeader).addClass("modal-header");
	var btnClose = document.createElement("button");
	$(btnClose).attr("type", "button").addClass("close").attr("data-dismiss",
			"modal").attr("aria-hidden", "true").html("&times;");
	var modalTitle = document.createElement("h4");
	$(modalTitle).addClass("modal-title").attr("id",
			"xianchangquhao_confirm_title");
	divModalHeader.appendChild(btnClose);
	divModalHeader.appendChild(modalTitle);

	var modalBody = document.createElement("div");
	$(modalBody).addClass("modal-body").attr("id",
			"xianchangquhao_confirm_body");

	var modalFooter = document.createElement("div");
	$(modalFooter).addClass("modal-footer");
	var btnCancel = document.createElement("button");
	$(btnCancel).attr("type", "button").addClass("btn btn-primary").attr(
			"data-dismiss", "modal").text("取消");
	var btnOK = document.createElement("button");
	$(btnOK).attr("type", "button").addClass("btn btn-primary").text("确定");
	modalFooter.appendChild(btnCancel);
	modalFooter.appendChild(btnOK);

	divModalContent.appendChild(divModalHeader);
	divModalContent.appendChild(modalBody);
	divModalContent.appendChild(modalFooter);
	divModalDialog.appendChild(divModalContent);
	divModalFade.appendChild(divModalDialog);

	$("#paiduiPageBody").append(divModalFade);
	$("#xianchangquhao_confirm_title").html("现场取号");

	// body content
	var bodyContainer = document.createElement("div");
	$(bodyContainer).addClass("form-group");

	var inputDiv = document.createElement("div");
	$(inputDiv).addClass("col-sm-8");
	var inputElement = document.createElement("input");
	$(inputElement).attr("type", "telphone").addClass("form-control").attr(
			"maxlength", "11").attr("id", "inputTel").attr("placeholder",
			"输入手机号码");

	$(inputElement).keyup(function(event) {
				this.value = this.value.replace(/\D/g, '');
			});

	$(btnOK).bind("click", function() {
		var validate = Common.mobile($("#inputTel").val());
		if (!validate) {
			$("#errorTelMsg").remove();
			$("#xianchangquhao_confirm_title")
					.append("<font id=\"errorTelMsg\" color=\"red\" style=\"padding-left:20px;\">请输入正确的手机号码</font>");
			return;
		}
		Merchant.quhaoOnsite(seatNumber, mid, $(inputElement).val());
	});

	inputDiv.appendChild(inputElement);
	bodyContainer.appendChild(inputDiv);

	$("#xianchangquhao_confirm_body").html(bodyContainer);
	$("#xianchangquhao_confirm").modal();
}

function hideModal() {
	$("#xianchangquhao_confirm").modal("hide");
	window.location.reload();
}

/**
 * 现场取号
 * 
 * @param {}
 *            seatNumber
 * @param {}
 *            mid
 * @param {}
 *            tel
 */
Merchant.quhaoOnsite = function(seatNumber, mid, tel) {
	$.ajax({
		type : "POST",
		url : "/b/w/quhaoOnsite",
		dataType : "JSON",
		data : {
			"tel" : tel,
			"seatNumber" : seatNumber,
			"mid" : mid
		},
		success : function(data) {
			if (data.tipKey == true) {
				$("#errorTelMsg").remove();
				$("#xianchangquhao_confirm_title")
						.append("<font id=\"errorTelMsg\" color=\"red\" style=\"padding-left:20px;\">排队号已发送到手机，3秒后自动关闭此对话框</font>");
				setTimeout('hideModal()', 3000);
			} else {
				$("#errorTelMsg").remove();
				$("#xianchangquhao_confirm_title")
						.append("<font id=\"errorTelMsg\" color=\"red\" style=\"padding-left:20px;\">"
								+ data.tipValue + "</font>");
				setTimeout('hideModal()', 3000);
				alert("服务器维护中，马上就好。");
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}

/**
 * 退出系统
 * 
 * @param {}
 *            aid
 */
Merchant.logout = function(aid) {
	$.ajax({
				type : "POST",
				url : "/b/a/logout",
				dataType : "json",
				data : {
					"aid" : aid
				},
				success : function(data) {
					if (data.error == "") {
						window.location.href = "/business";
					}
				},
				error : function() {
					alert("服务器维护中，马上就好。");
				}
			});
}

/**
 * 添加优惠
 * @param {} mid
 */
Merchant.saveYouhui = function(mid) {
	if (Merchant.validateYouhui()) {
		var youhuiTitle = $("#youhuiTitle").val();
		var youhuiContent = $("#youhuiContent").val();
		$.ajax({
					type : "POST",
					url : "/b/w/saveYouhui",
					dataType : "json",
					data : {
						"mid" : mid,
						"title" : youhuiTitle,
						"content" : youhuiContent
					},
					success : function(data) {
						console.log(data);
						if (data) {
							$("#errorTip").html("添加优惠信息成功，3秒后刷新优惠列表！")
									.removeClass().addClass("text-success")
									.show();
							setTimeout('location.reload()', 3000);
						}
					},
					error : function() {
						alert("服务器维护中，马上就好。");
					}
				});
	}
}

/**
 * 取消优惠
 * @param {} mid
 */
Merchant.disableYouhui = function(mid, yid) {
	$.ajax({
		type : "POST",
		url : "/b/w/disableYouhui",
		dataType : "json",
		data : {
			"mid" : mid,
			"yid" : yid
		},
		success : function(data) {
			console.log(data);
			if (data) {
				$("#deleteErrorTip").html("删除优惠信息成功，3秒后刷新优惠列表！")
						.removeClass().addClass("text-success")
						.show();
				setTimeout('location.reload()', 3000);
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}

/**
 * 添加优惠表单检查
 * @param {} mid
 * @return {Boolean}
 */
Merchant.validateYouhui = function(mid) {
	var youhuiTitle = $("#youhuiTitle").val();
	var youhuiContent = $("#youhuiContent").val();

	if (Common.isEmpty(youhuiTitle)) {
		$("#errorTip").html("请输入优惠标题").addClass("text-danger").show();
		return false;
	}
	if (Common.isEmpty(youhuiContent)) {
		$("#errorTip").html("请输入优惠详细规则").addClass("text-danger").show();
		return false;
	}

	return true;
}

/**
 * 关闭排队/开放排队
 * @param {} mid
 * @param {} online
 */
Merchant.changeStatus = function(mid, online){
	if(Common.isEmpty(mid) || Common.isEmpty(online)){
		alert("请联系管理员");
		return;
	}
	
	$.ajax({
		type : "POST",
		url : "/b/w/changeStatus",
		dataType : "json",
		data : {
			"mid" : mid,
			"online" : online
		},
		success : function(data) {
			console.log(data);
			if (data) {
				if(online == 'false'){
					$("#paiduipageTip").html("关闭排队成功！注意：需要时可开放排队，3秒后刷新页面。").removeClass().addClass("text-danger");
				} else {
					$("#paiduipageTip").html("开放排队成功！3秒后刷新页面。").removeClass().addClass("text-success");
				}
				setTimeout('location.reload()', 3000);
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
	
}