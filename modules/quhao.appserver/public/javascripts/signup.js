/**
 * add by cross
 */
var Signup = new function(){
	// default signup is by email
	this.isFb = false;
	
	this.pwdFocus = function(obj) {
		if (obj.id == "signpassword") {
			$(this).hide();
			$("#signshowpassword").show();
			$("#signshowpassword").focus();
		}
		if (obj.id == "signpasswordrep") {
			$(this).hide();
			$("#signshowpasswordrep").show();
			$("#signshowpasswordrep").focus();
		}

	}
	
	this.pwdBlur = function(obj, def) {
		if (obj.value == '') {
			$("#" + obj.id).hide();
			if (obj.id == "signshowpassword") {
				$("#signpassword").val(def);
				$("#signpassword").show();
			}
			if (obj.id == "signshowpasswordrep") {
				$("#signpasswordrep").val(def);
				$("#signpasswordrep").show();
			}
		}
	}
	
	this.hideErrorTipForFBSignup = function(){
		$("#errorTipForUsername").hide();
		$("#errorTipForBirthday").hide();
		$("#errorTipForRole").hide();
		$("#errorTipForEmail").hide();
		$("#errorTipForPassword").hide();
		$("#errorTipForPasswordrep").hide();
		$("#errorTipForApplication").hide();
		$("#errorTipForLocation").hide();
	}
	
	this.hideErrorBoxSignup = function(){
		$("#signfirstname").removeClass("errorBox");
		$("#signlastname").removeClass("errorBox");
		$("#signemail").removeClass("errorBox");
		$("#signpassword").removeClass("errorBox");
	}
	
	this.init = function(isFb){
		
		// hide errortips
		if(isFb){
			this.hideErrorTipForFBSignup();
		}
		
		// initial form
		$("#errorTip").hide();
		$("#signshowpassword").hide();
		$("#signshowpasswordrep").hide();
		$("#signfirstname").val("");
		$("#signlastname").val("");
		$("#month").val("0");
		$("#day").val("0");
		$("#year").val("0");
		$("#role").val("student");
		$("#signemail").val("");
		$("#signpassword").val("");
		$("#signapplication").val("0");
		
		// initial day select
		for(var i=0; i<=31; i++){
			var option = document.createElement("option");
			option.value = i;
			option.innerHTML = i;
			if(i == 0){
				option.innerHTML = "Birth Day";
			}
			$("#day").append(option.outerHTML);
		}
		
		// initial year select
		for(var i=1939; i<=2000; i++){
			var option = document.createElement("option");
			option.value = i;
			option.innerHTML = i;
			if(i == 1939){
				option.value = 0;
				option.innerHTML = "Birth Year";
			}
			$("#year").append(option.outerHTML);
		}
	}
	
	this.validate = function(isFb){
		var message = "Please complete the form.";
		if($("#signfirstname").val() == null || $("#signfirstname").val() == ""){
			$("#signfirstname").addClass("errorBox");
			message = "Please enter First Name";
			$("#errorTip").html(message);
			$("#errorTip").show();
			this.hideErrorTipForFBSignup();
			$("#errorTipForUsername").show();
			$("#errorWordForUsername").text(message);
			
			$("#formButton").addClass("lgoinBtn1");
			$("#formButton").addClass("lgoinBtn1_red");
			$("#formButton").val("Please Complete the Fields Correctly");
			
			return false;
		}else{
			$("#signfirstname").removeClass("errorBox");
		}
		
		if($("#signlastname").val() == null || $("#signlastname").val() == ""){
			message = "Please enter Last Name";
			$("#errorTip").html(message);
			$("#errorTip").show();
			$("#signlastname").addClass("errorBox");
			$("#errorWordForUsername").text(message);
			
			$("#formButton").addClass("lgoinBtn1");
			$("#formButton").addClass("lgoinBtn1_red");
			$("#formButton").val("Please Complete the Fields Correctly");
			
			return false;
		}else{
			$("#signlastname").removeClass("errorBox");
		}
		
		if($("#month").val() == 0){
			message = "Please select Birth Month.";
			$("#errorTip").html(message);
			$("#errorTip").show();
			$("#monthspan").addClass("errorBox");
			this.hideErrorTipForFBSignup();
			$("#errorTipForBirthday").show();
			$("#errorWordForBirthday").text(message);
			
			$("#formButton").addClass("lgoinBtn1");
			$("#formButton").addClass("lgoinBtn1_red");
			$("#formButton").val("Please Complete the Fields Correctly");
			
			return false;
		}else{
			$("#monthspan").removeClass("errorBox");
		}
		if($("#day").val() == 0){
			message = "Please select Birth Day.";
			$("#errorTip").html(message);
			$("#errorTip").show();
			$("#dayspan").addClass("errorBox");
			this.hideErrorTipForFBSignup();
			$("#errorTipForBirthday").show();
			$("#errorWordForBirthday").text(message);
			
			$("#formButton").addClass("lgoinBtn1");
			$("#formButton").addClass("lgoinBtn1_red");
			$("#formButton").val("Please Complete the Fields Correctly");
			
			return false;
		}else{
			$("#dayspan").removeClass("errorBox");
		}
		if($("#year").val() == 0){
			message = "Please select Birth Year.";
			$("#errorTip").html(message);
			$("#errorTip").show();
			$("#yearspan").addClass("errorBox");
			this.hideErrorTipForFBSignup();
			$("#errorTipForBirthday").show();
			$("#errorWordForBirthday").text(message);
			
			$("#formButton").addClass("lgoinBtn1");
			$("#formButton").addClass("lgoinBtn1_red");
			$("#formButton").val("Please Complete the Fields Correctly");
			
			return false;
		}else{
			$("#yearspan").removeClass("errorBox");
		}
		
		if($("#signemail").val() == null || $("#signemail").val() == ""){
			message = "Please enter email.";
			$("#errorTip").html(message);
			$("#errorTip").show();
			$("#signemail").addClass("errorBox");
			this.hideErrorTipForFBSignup();
			$("#errorTipForEmail").show();
			$("#errorWordForEmail").text(message);
			
			$("#formButton").addClass("lgoinBtn1");
			$("#formButton").addClass("lgoinBtn1_red");
			$("#formButton").val("Please Complete the Fields Correctly");
			
			return false;
		}else{
			$("#signemail").removeClass("errorBox");
		}
		
		$("#formButton").removeClass("lgoinBtn1_red");
		$("#formButton").addClass("lgoinBtn1");
		$("#formButton").val("Request an Invite");
			
		return true;
	};
	
	//newLogin
	this.newLogin=function(){
		if($("#new_login").is(":hidden")){
			$("#new_login").show();
		} else{
			$("#new_login").hide()
		}
	}
	
	//automaticsliding
	this.automaticsliding=function(){
		var click_picture1=$("#click_picture1");
		var click_picture2=$("#click_picture2");
		var click_picture3=$("#click_picture3");
		var click_picture4=$("#click_picture4");
		if(click_picture1.attr('class')=="point_hover"){
			click_picture1.attr('class','point_link');
			click_picture2.attr('class','point_hover');
			click_picture3.attr('class','point_link');
			click_picture4.attr('class','point_link');
			$("#div_photp1").hide();
			$("#div_photp2").show();
			$("#div_photp3").hide();
			$("#div_photp4").hide();
		}else if(click_picture2.attr('class')=="point_hover"){
			click_picture1.attr('class','point_link');
			click_picture2.attr('class','point_link');
			click_picture3.attr('class','point_hover');
			click_picture4.attr('class','point_link');
			$("#div_photp1").hide();
			$("#div_photp2").hide();
			$("#div_photp3").show();
			$("#div_photp4").hide();
		}else if(click_picture3.attr('class')=="point_hover"){
			click_picture1.attr('class','point_link');
			click_picture2.attr('class','point_link');
			click_picture3.attr('class','point_link');
			click_picture4.attr('class','point_hover');
			$("#div_photp1").hide();
			$("#div_photp2").hide();
			$("#div_photp3").hide();
			$("#div_photp4").show();
		}else if(click_picture4.attr('class')=="point_hover"){
			click_picture1.attr('class','point_hover');
			click_picture2.attr('class','point_link');
			click_picture3.attr('class','point_link');
			click_picture4.attr('class','point_link');
			$("#div_photp1").show();
			$("#div_photp2").hide();
			$("#div_photp3").hide();
			$("#div_photp4").hide();
		}
	}
	
	//Carousel Picture
	this.carouselPicture=function(obj){
		var id=$(obj).attr('id');
		if(id=="click_picture1"){
			$("#click_picture1").attr('class',"point_hover");
			$("#div_photp1").show();
			$("#click_picture2").attr('class',"point_link");
			$("#div_photp2").hide();
			$("#click_picture3").attr('class',"point_link");
			$("#div_photp3").hide();
			$("#click_picture4").attr('class',"point_link");
			$("#div_photp4").hide();
		}else if(id=="click_picture2"){
			$("#click_picture1").attr('class',"point_link");
			$("#div_photp1").hide();
			$("#click_picture2").attr('class',"point_hover");
			$("#div_photp2").show();
			$("#click_picture3").attr('class',"point_link");
			$("#div_photp3").hide();
			$("#click_picture4").attr('class',"point_link");
			$("#div_photp4").hide();
		}else if(id=="click_picture3"){
			$("#click_picture1").attr('class',"point_link");
			$("#div_photp1").hide();
			$("#click_picture2").attr('class',"point_link");
			$("#div_photp2").hide();
			$("#click_picture3").attr('class',"point_hover");
			$("#div_photp3").show();
			$("#click_picture4").attr('class',"point_link");
			$("#div_photp4").hide();
		}else if(id=="click_picture4"){
			$("#click_picture1").attr('class',"point_link");
			$("#div_photp1").hide();
			$("#click_picture2").attr('class',"point_link");
			$("#div_photp2").hide();
			$("#click_picture3").attr('class',"point_link");
			$("#div_photp3").hide();
			$("#click_picture4").attr('class',"point_hover");
			$("#div_photp4").show();
		}
	}
	
	//login key up
	this.keyUp=function(text){
		if(text.value==null||text.value=="")
			$("#but_go").hide();
		else
		$("#but_go").show();
	}
	
	//newSubmit
	this.newSubmit=function(){
		if($("#signemail").val() == null || $("#signemail").val() == ""){
			$("#div_login").attr("class","login_email2");
		}else{
			$("#signemail").removeClass("login_email");
			var url = "/accountcontroller/signup";
			$.post(url,"account.email="+$("#signemail").val(),function(spVO){
				if(spVO.result == "success"){
					top.window.location.href = "/accountcontroller/signupresult?email="+spVO.email
				}else{
				if(spVO.result.indexOf("E-mail") != -1){
					$("#div_login").attr("class","login_email2");
				}
				}
			},"json");
		}
	}
	
	// submit
	this.submitByFB = function(isFb){
		this.isFb = isFb;
		if(this.validate(isFb)){
			var url = "/accountcontroller/signup";
			$.post(url,$("#signForm").serialize(),function(spVO){
				if(spVO.result == "success"){
					top.window.location.href = "/accountcontroller/signupresult?email="+spVO.email
				}else{
					if(Signup.isFb){
						if(spVO.result.indexOf("First Name") != -1 || spVO.result.indexOf("Last Name") != -1){
							Signup.hideErrorTipForFBSignup();
							$("#errorTipForUsername").show();
							$("#errorWordForUsername").text(spVO.result);
						}
						if(spVO.result.indexOf("E-mail") != -1){
							$("#signemail").addClass("errorBox");
							Signup.hideErrorTipForFBSignup();
							$("#errorTipForEmail").show();
							$("#errorWordForEmail").text(spVO.result);
						}
						if(spVO.result.indexOf("Passowrd") != -1){
							$("#signpassword").addClass("errorBox");
							Signup.hideErrorTipForFBSignup();
							$("#errorTipForPassword").show();
							$("#errorWordForPassword").text(spVO.result);
						}
					}else{
						Signup.hideErrorBoxSignup();
						if(spVO.result.indexOf("First Name") != -1){
						
							$("#formButton").addClass("lgoinBtn1");
							$("#formButton").addClass("lgoinBtn1_red");
							$("#formButton").val("Please Complete the Fields Correctly");
							
							$("#signfirstname").addClass("errorBox");
						}
						if(spVO.result.indexOf("Last Name") != -1){
						
							$("#formButton").addClass("lgoinBtn1");
							$("#formButton").addClass("lgoinBtn1_red");
							$("#formButton").val("Please Complete the Fields Correctly");
						
							$("#signlastname").addClass("errorBox");
						}
						if(spVO.result.indexOf("E-mail") != -1){
						
							$("#formButton").addClass("lgoinBtn1");
							$("#formButton").addClass("lgoinBtn1_red");
							$("#formButton").val("Please Complete the Fields Correctly");
	
							$("#signemail").addClass("errorBox");
						}
						if(spVO.result.indexOf("Passowrd") != -1){
							$("#signpassword").addClass("errorBox");
							$("#signshowpassword").addClass("errorBox");
						}
						$("#errorTip").text(spVO.result);
						$("#errorTip").show();
					}
				}
			},"json");
		}
	};
	
	this.popupSignup = function(){
		var height = $("#signupDiv").attr("height");
		var width = $("#signupDiv").attr("width");
		popup("signupDiv", height, width, "fatherContainer",null);
	};
	
	this.resentEmail = function(){
		var url = "/accountcontroller/resentEmail"
		$.post(url,$("#resentForm").serialize(),function(spVO){
			if(spVO.result == "success"){
				alert("Resent successfully!");
			}else{
				alert("Resent failed");
			}
		},"json");
	}
};

/**
 * add by cross
 */
function popup(newDivID, width, height, container, callbackName) {

	var newMaskID = "mask";
	var newMaskWidth = document.documentElement.clientWidth;
	var newMaskHeight = document.documentElement.clientHeight;

	// mask
	var newMask = document.createElement("div");
	newMask.id = newMaskID;
	newMask.style.position = "absolute";
	newMask.style.zIndex = "1000";
	newMask.style.width = newMaskWidth + "px";
	newMask.style.height = newMaskHeight + "px";
	newMask.style.top = "0px";
	newMask.style.left = "0px";
	newMask.style.background = "black";
	newMask.style.filter = "alpha(opacity=40)";
	newMask.style.opacity = "0.40";
	document.getElementById(container).appendChild(newMask);

	// popup div
	var newDivWidth = parseInt($("#"+newDivID).css("width"));
	var newDivHeight = parseInt($("#"+newDivID).css("height"));
	var newDivtop = (newMaskHeight / 2 - newDivHeight / 2);
	var newDivleft = (newMaskWidth / 2 - newDivWidth / 2);

	document.getElementById(newDivID).style.position = "absolute";
	document.getElementById(newDivID).style.zIndex = "1001";
	
	if(newDivtop < 0 || newDivtop == 0){
		document.getElementById(newDivID).style.top = 10 + "px";
	}else{
		document.getElementById(newDivID).style.top = newDivtop + "px";
	}
	
	$("#"+newDivID).css({"left":newDivleft+"px"});
	document.getElementById(newDivID).style.display = "";
	document.getElementById(container).appendChild(document.getElementById(newDivID));
	$("#"+container).bind("keydown",function(e){
		
		if(Applyful.getEntryKey(e) == 1){
			return;
		}
		
		
		if(window.event){
		    if(e.keyCode == 27){
		    	removePopup(newMask, newDivID);
		    }
		}else if(e.which){
		    if(e.which == 27){
		    	removePopup(newMask, newDivID);
		    }
		}
		return;
	});
}

function removePopup(newMask, newDivID){
	$(newMask).remove();
	$("#"+newDivID).hide();
//	location.reload(true);
//	location.replace(location.href);
//	window.location.reload();
//	window.top.location.reload();
}