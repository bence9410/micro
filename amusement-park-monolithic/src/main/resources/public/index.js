var links
var pages
var jses
var username
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
        	setUsernameAndAuthorityAndSpendingMoney(data)
			getAmusementParkPage()
			$("#photo").attr("src", data.photo)
        },
        error: function (data) {
        	$("#content").show()
        }
    })
}

function setUsernameAndAuthorityAndSpendingMoney(data){
	 username = data.username
	 $("#username").html(username)

	 isAdmin = data.authority === "ROLE_ADMIN"
     
     $("#spendingMoney").html(data.spendingMoney)
     $("#header").show()
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