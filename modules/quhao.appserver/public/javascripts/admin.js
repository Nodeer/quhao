Admin = {};

Admin.generateAccount = function(){
	$.ajax({
			type : "POST",
			url : "/admin/genaccount",
			data : $("#generateAccountForm").serialize(),
			dataType : "json",
			async : false,
			success : function(data) {
				console.log(data.error);
				console.log(data.result);
				if (data != null) {
					if(data.error != ""){
						$("#tip").attr("class","alert alert-danger").html(data.result).show();
						return;
					} else {
						$("#tip").attr("class","alert alert-success").html(data.result).show();
					}
				}
			},
			error : function() {
				alert("ajax error!");
			}
	});
}

