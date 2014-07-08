Yuding = {};

Yuding.tongyi = function(yid){
	$.ajax({
		type : "GET",
		url : "/b/w/yuding/tongyi",
		dataType : "JSON",
		data : {
			"yid" : yid
		},
		success : function(data) {
			console.log(data);
			if(data){
				window.location.reload();
			} else {
				alert("操作失败，请重试");
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}

Yuding.finish = function(yid){
	$.ajax({
		type : "GET",
		url : "/b/w/yuding/finish",
		dataType : "JSON",
		data : {
			"yid" : yid
		},
		success : function(data) {
			console.log(data);
			if(data){
				window.location.reload();
			} else {
				alert("操作失败，请重试");
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}

Yuding.expire = function(yid){
	$.ajax({
		type : "GET",
		url : "/b/w/yuding/expire",
		dataType : "JSON",
		data : {
			"yid" : yid
		},
		success : function(data) {
			console.log(data);
			if(data){
				window.location.reload();
			} else {
				alert("操作失败，请重试");
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}

Yuding.cancelTemp = function(yid){
	$.ajax({
		type : "GET",
		url : "/b/w/yuding/cancelTemp",
		dataType : "JSON",
		data : {
			"yid" : yid
		},
		success : function(data) {
			console.log(data);
			if(data){
				window.location.reload();
			} else {
				alert("操作失败，请重试");
			}
		},
		error : function() {
			alert("服务器维护中，马上就好。");
		}
	});
}