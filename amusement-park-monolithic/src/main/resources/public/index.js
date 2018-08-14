var links
var pages
var jses
var username
var authorities

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
        url: links.user,
        success: function (data) {
        	setUsernameAndAuthiritiesAndSpendingMoney(data)
			getAmusementParkPage()
        },
        error: function (data) {
        	$("#content").show()
        }
    })
}

function setUsernameAndAuthiritiesAndSpendingMoney(data){
	 username = data.name
	 $("#username").html(username)

     var auth = []
     $.each(data.authorities, function (i, e) {
         auth.push(e.authority)
     })
     authorities = auth.join(",")
     $("#authorities").html(authorities)
     
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