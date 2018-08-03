var links
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
		}
	}) 
}

function getUserData(){
	$.ajax({
        url: links.user,
        success: function (data) {
        	setUsernameAndAuthirities(data)
        	getSpendingMoneyShowSignUpIfCanNot()
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

function getSpendingMoneyShowSignUpIfCanNot(){
	$.ajax({
		url : "/visitor/spending-money/",
		success : function(data) {
			$("#spendingMoney").html(data)
			$("#spendingMoneyDiv").show()
			getAmusementParkPage()
		},
		error : function(response) {
			if (response.status == 418) {
				$("#signUp").show()
			}
		}
	})

}

function signUp() {
	$.ajax({
		url : "/visitor",
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
		url : "/pages/amusement-park.html",
		success : function(data) {
			$("#content").html("<script src=\"js/amusement-park.js\"></script>" + data)
		}
	})
}