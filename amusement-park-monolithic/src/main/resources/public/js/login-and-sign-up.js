$(document).ready(function() {
	$("#loginDiv").keyup(loginWithEnter);
})

function loginWithEnter(event) {
	if (event.keyCode === 13) {
		event.preventDefault();
	   	$("#login").click();
	}
}

function ajaxLogin() {
	$.ajax({
		url : links.login,
		method : "POST",
		data : "email="+$("#loginEmail").val() + "&password=" + $("#password").val() + "&remember-me=true",
		success : function (data) {
			$("#content").hide()
			initHeader(data)
			getAmusementParkPage()
		},
		error : function (data) {
			$("#loginError").html(data.responseText)
		}
	})
}
		
function showSignUp() {
	$("#loginDiv").hide()
	$("#signUpDiv").show()
}

function drawImage(){
	var reader = new FileReader()
	reader.onload = e => $('#img').attr('src', e.target.result)
	reader.readAsDataURL($("#file")[0].files[0])
}
		
function backToLogin(){
	$("#signUpDiv").hide()
	$("#loginDiv").show()			
}
		
function signUp() {
	$("#signUp").prop("disabled", true)
	$.ajax({
		url : links.signUp + "?remember-me=true",
		method : "POST",
		contentType : "application/json",
		data : JSON.stringify(collectSignUpData()),
		success : function (data) {
			$("#content").hide()
			initHeader(data)
			getAmusementParkPage()
		},
		error : function (data) {	
			$("#signUpError").html(data.responseText)
			$("#signUpError").show()
			$("#signUp").prop("disabled", false)
		}
	})
}
		
function collectSignUpData(){
	var user = {}
	user.email = $("#signUpLoginEmail").val()
	user.password = $("#signUpPassword").val()
	user.confirmPassword = $("#signUpConfirmPassword").val()
	user.dateOfBirth = $("#dateOfBirth").val()
	user.photo = $("#img").attr("src")
	return user
}