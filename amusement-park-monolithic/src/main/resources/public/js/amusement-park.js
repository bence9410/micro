$(document).ready(function() {

	if (isAdmin) {
		$("thead").children("tr").append("<th></th>")
	}

	getAmusementParks()

})

function clearAndShowCreateAmusementParkModal(){
	$("#createAmusementParkErrorMessage").html("")
	$("#createAmusementParkName").val("")
	$("#createAmusementParkCapital").val("")
	$("#createAmusementParkTotalArea").val("")
	$("#createAmusementParkEntranceFee").val("")
	$("#createAmusementParkModal").modal("show")
	
}

function create() {
	$("#createAmusementParkButton").attr("disabled", true)
	$.ajax({
		url : links.amusementPark,
		method : "POST",
		contentType : "application/json",
		data : JSON.stringify(parkCollectData()),
		success : function() {
			$("#createAmusementParkModal").modal("hide")
			getAmusementParks()
		},
		error : function(response) {
			var result = $("#createAmusementParkErrorMessage")
			result.html("error: " + response.responseText)
			result.addClass("text-danger")
		},
		complete : function() {
			$("#createAmusementParkButton").attr("disabled", false)
		}
	})
}

function parkCollectData() {
	var amusementPark = {}
	amusementPark.name = $("#createAmusementParkName").val()
	amusementPark.capital = $("#createAmusementParkCapital").val()
	amusementPark.totalArea = $("#createAmusementParkTotalArea").val()
	amusementPark.entranceFee = $("#createAmusementParkEntranceFee").val()
	
	var address = {}
	address.zipCode = 1000
	address.country="Budapest"
	address.street="Peterdy"
	address.city="Valami"
	address.houseNumber="23"
	amusementPark.address = address
	return amusementPark
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