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
        	setUsernameAndAuthirities(data)
        	setSpendingMoneyOrShowSingUpDiv(data.spendingMoney)
        }
    })
}

function setUsernameAndAuthirities(data){
	 username = data.name
     $("#username").html(username)

     var auth = []
     $.each(data.authorities, function (i, e) {
         auth.push(e.authority)
     })
     authorities = auth.join(",")
     $("#authorities").html(authorities)
}

function setSpendingMoneyOrShowSingUpDiv(spendingMoney){
	if (spendingMoney === undefined) {
		$("#signUp").show()
	} else {
		$("#spendingMoney").html(spendingMoney)
		$("#spendingMoneyDiv").show()
		getAmusementParkPage()
	}
}

function signUp() {
	$.ajax({
		url : links.visitorSignUp,
		method : "POST",
		contentType : "application/json",
		data : JSON.stringify(collectData()),
		success : function(data) {
			$("#signUp").hide()
			$("#spendingMoney").html(data.spendingMoney)
			$("#spendingMoneyDiv").show()
			getAmusementParkPage()
		}
	})
}

function collectData() {
	var visitor = {}
	visitor.name = $("#name").val()
	visitor.dateOfBirth = $("#dateOfBirth").val()
	visitor.spendingMoney = $("#signUpSpendingMoney").val()
	return visitor
}

function fillWithSampleData() {
	$("#name").val("Németh Bence")
	$("#dateOfBirth").val("1994-10-22")
	$("#signUpSpendingMoney").val("1000")
}

function getAmusementParkPage() {
	$.ajax({
		url : pages.amusementPark,
		success : function(data) {
			$("#content").html("<script src=\"" + jses.amusementPark + "\"></script>" + data)
		}
	})
}