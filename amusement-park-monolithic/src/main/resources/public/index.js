var links
var pages
var jses
var email
var isAdmin

function init() {
	$.ajax({
		url: "/links",
		success: function (data) {
			links = {}
			$.each(data, function (i, e) {
				links[e.rel] = e.href
			})
			getUserData()
			$("#logout").attr("action", links.logout)
		}
	}) 
	pages = {
		amusementPark: "/pages/amusement-park.html"
	}
	jses = {
		amusementPark: "js/amusement-park.js"
	}
}

function getUserData(){
	$.ajax({
        url: links.me,
        success: function (data) {
        	setEmailAndAuthorityAndSpendingMoney(data)
			getAmusementParkPage()
			$("#photo").attr("src", data.photo)
        },
        error: function (data) {
        	$("#content").show()
        }
    })
}

function setEmailAndAuthorityAndSpendingMoney(data){
	 email = data.email
	 $("#email").html(email)

	 isAdmin = data.authority === "ROLE_ADMIN"
     
     $("#spendingMoney").html(data.spendingMoney)
     $("#header").show()
}

function uploadMoney(){
	var money = $("#money").val()
	$.ajax({
		url : links.uploadMoney,
		method : "POST",
		contentType : "application/json",
		data : money,
		success : function(){
			$("#moneyUploadResult").html("success")
			var spendingMoney = $("#spendingMoney")
			spendingMoney.html(parseInt(spendingMoney.html()) + parseInt(money))
		},
		error : function(){
			$("#moneyUploadResult").html("error")
		}
	})
}

function getAmusementParkPage() {
	$.ajax({
		url : pages.amusementPark,
		success : function(data) {
			$("#content").html("<script src=\"" + jses.amusementPark + "\"></script>" + data)
			$("#content").show()
		}
	})
}