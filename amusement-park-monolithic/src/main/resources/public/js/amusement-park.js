$(document).ready(function() {

	if (isAdmin) {
		$("thead").children("tr").append("<th></th>")
	}

	getAmusementParks()

})

function toggleSaveDiv(){
	$("#saveDiv").toggle()
}
function toggleSearchDiv(){
	$("#searcheDiv").toggle()
}

function save() {
	$("#save").attr("disabled", true)
	$.ajax({
		url : links.amusementPark,
		method : "POST",
		contentType : "application/json",
		data : JSON.stringify(parkCollectData()),
		success : function(response) {
			var result = $("#result")
			result.html("success")
			result.addClass("text-success")
			result.show()
			getAmusementParks()
		},
		error : function(response) {
			var result = $("#result")
			result.html("error: " + response.responseText)
			result.addClass("text-danger")
			result.show()
		},
		complete : function(response) {
			$("#save").attr("disabled", false)
			setTimeout(function() {
				var result = $("#result")
				result.hide()
				result.removeClass()
			}, 8000)
		}
	})
}

function parkCollectData() {
	var address = {}
	address.country = $("#country").val()
	address.zipCode = $("#zipCode").val()
	address.city = $("#city").val()
	address.street = $("#street").val()
	address.houseNumber = $("#houseNumber").val()

	var amusementPark = {}
	amusementPark.name = $("#parkName").val()
	amusementPark.capital = $("#capital").val()
	amusementPark.totalArea = $("#totalArea").val()
	amusementPark.entranceFee = $("#entranceFee").val()
	amusementPark.address = address

	return amusementPark
}

function parkFillWithSampleData() {
	$("#parkName").val("Beni parkja")
	$("#capital").val("10000")
	$("#totalArea").val("1000")
	$("#entranceFee").val("50")

	$("#country").val("Magyarország")
	$("#zipCode").val("1148")
	$("#city").val("Budapest")
	$("#street").val("Fogarasi út")
	$("#houseNumber").val("80/C")
}

function getAmusementParks() {
	$.ajax({
		url : links.amusementPark,
		success : function(response) {
			fillTableWithData(response)
		}
	})
}

function fillTableWithData(data) {
	var tableBody = []
	
	if (data._embedded !== undefined){
		$.each(data._embedded.amusementParkResourceList, function(i, e) {
			tableBody.push(convertAmusementParkToTableRow(e))
		})
	}

	$("#tableBody").html(tableBody.join())
}

function convertAmusementParkToTableRow(amusementPark) {
	var tr = []
	tr.push("<tr>")
	tr.push("<td>" + amusementPark.name + "</td>")
	tr.push("<td>" + amusementPark.capital + "</td>")
	tr.push("<td>" + amusementPark.totalArea + "</td>")
	tr.push("<td>" + amusementPark.entranceFee + "</td>")
	var address = amusementPark.address
	tr.push("<td>" + address.country + " " + address.zipCode)
	tr.push(" " + address.city + " " + address.street)
	tr.push(" " + address.houseNumber + "</td>")
	tr.push("<td> <input type=\"button\" value=\"Enter\" onclick=\"enterPark('"
			+ amusementPark._links.visitorEnterPark.href + "','" + amusementPark._links.machine.href + "')\"></td>")
	if (isAdmin) {
		tr.push("<td><input type=\"button\" onclick=\"deletePark('"
				+ amusementPark._links.self.href + "')\" value=\"Delete\"></td>")
	}
	tr.push("</tr>")
	return tr.join("")
}
function enterPark(href,machineHref) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function(data){
			
			$("#spendingMoney").html(data.spendingMoney)
			getMachinePage(data._links.leavePark.href, machineHref)
		}
	})
}

function deletePark(href) {
	$.ajax({
		url : href,
		method : "DELETE",
		success : function(data, textStatus, jqXHR) {
			console.log(data)
			console.log(textStatus)
			console.log(jqXHR)
		},
		error : function(data, textStatus, jqXHR) {
			console.log(data)
			console.log(textStatus)
			console.log(jqXHR)
		}
	})
}
function getMachinePage(leaveParkHref, machineHref) {
	$.ajax({
		url : pages.machine,
		success : function(data) {
			$("#content").html(data)
			$("#leave").attr("onclick","leavePark('" + leaveParkHref + "')")
			getMachines(machineHref)
			$("#refresh").attr("onclick","getMachines('" + machineHref + "')")
		}
	})
}