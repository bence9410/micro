var links
var pages
var email
var isAdmin

function init() {
	$.ajax({
		url : "/links",
		success : function(data) {
			links = {}
			$.each(data, function(i, e) {
				links[e.rel] = e.href
			})
			getUserData()
			$("#logout").attr("action", links.logout)
		}
	})
	pages = {
		loginAndSignUp : "/pages/login-and-sign-up.html",
		amusementPark : "/pages/amusement-park.html",
		machine : "/pages/machine.html"
	}
}

function getUserData() {
	$.ajax({
		url : links.me,
		success : function(data) {
			initHeader(data)
			getAmusementParkPage()
		},
		error : function(data) {
			$.ajax({
				url : pages.loginAndSignUp,
				success : function(data) {
					$("#content").html(data)
					$("#content").show()
				}
			})
		}
	})
}

function initHeader(data) {
	email = data.email
	$("#email").html(email)
	
	$("#photo").attr("src", data.photo)

	isAdmin = data.authority === "ROLE_ADMIN"

	$("#spendingMoney").html(data.spendingMoney)
	$("#header").show()
}

function uploadMoney() {
	var money = $("#money").val()
	if (isNaN(money)) {
		var moneyUploadResult = $("#moneyUploadResult")
		moneyUploadResult.removeClass()
		moneyUploadResult.addClass("text-danger")
		moneyUploadResult.html("Error: '"+ money  + "' is not a number.")
	} else {
		$.ajax({
			url : links.uploadMoney,
			method : "POST",
			contentType : "application/json",
			data : money,
			success : function() {
				var moneyUploadResult = $("#moneyUploadResult")
				moneyUploadResult.removeClass()
				moneyUploadResult.addClass("text-success")
				moneyUploadResult.html("success")
	
				var spendingMoney = $("#spendingMoney")
				spendingMoney
						.html(parseInt(spendingMoney.html()) + parseInt(money))
			},
			error : function(data) {		
				var moneyUploadResult = $("#moneyUploadResult")
				moneyUploadResult.removeClass()
				moneyUploadResult.addClass("text-danger")
				moneyUploadResult.html("Error: " + data.responseText)
			}
		})
	}
}

function clearUploadMoneyPopup() {
	var moneyUploadResult = $("#moneyUploadResult")
	moneyUploadResult.removeClass()
	moneyUploadResult.html("")
	
	$("#money").val("")
 }


function getAmusementParkPage() {
	$.ajax({
		url : pages.amusementPark,
		success : function(data) {
			$("#content").html(data)
			$("#content").show()
		}
	})
}