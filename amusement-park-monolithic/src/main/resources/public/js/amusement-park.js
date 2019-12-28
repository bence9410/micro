$(function() {
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
	var amusementPark = collectAmusementPark()
	var error=validateAmusementPark(amusementPark)
	if (error.length == 0){
		$.ajax({
			url : links.amusementPark,
			method : "POST",
			contentType : "application/json",
			data : JSON.stringify(amusementPark),
			success : function() {
				$("#createAmusementParkModal").modal("hide")
				getAmusementParks()
			},
			error : function(response) {
				$("#createAmusementParkErrorMessage").html("error: " + response.responseText)
			},
			complete : function() {
				$("#createAmusementParkButton").attr("disabled", false)
			}
		})	
	} else {
		 $("#createAmusementParkErrorMessage").html(error.join("<br>"))
		 $("#createAmusementParkButton").attr("disabled", false)
		
	}
}

function collectAmusementPark() {
	var amusementPark = {}
	amusementPark.name = $("#createAmusementParkName").val()
	amusementPark.capital = Number($("#createAmusementParkCapital").val())
	amusementPark.totalArea = Number($("#createAmusementParkTotalArea").val())
	amusementPark.entranceFee = Number($("#createAmusementParkEntranceFee").val())
	return amusementPark
}
function validateAmusementPark(amusementPark){
	var error = []
	if (amusementPark.name.length < 5 || amusementPark.name.length > 20 ){
		error.push("The length of name must be between 5 and 20.")
	} 
	if (amusementPark.capital < 500 || amusementPark.capital.length >50000){
		error.push("The value of capital must be between 500 and 50000.")
	}
	if (amusementPark.totalArea < 50 || amusementPark.totalArea.length >2000){
		error.push("The value of total area must be between 50 and 2000.")
	}
	if (amusementPark.entranceFee < 5 || amusementPark.entranceFee.length >200){
		error.push("The value of entrance fee must be between 5 and 200.")
	}
	return error
}

function search(){
	var searchAmusementPark = {}
	searchAmusementPark.name = $("#searchName").val()
	var capitalMin=$("#searchCapitalMin").val()
	if(capitalMin !== "" && !isNaN(capitalMin)){
			searchAmusementPark.capitalMin = Number(capitalMin)
	}
	var capitalMax=$("#searchCapitalMax").val()
	if(capitalMax !== "" && !isNaN(capitalMax)){
			searchAmusementPark.capitalMax = Number(capitalMax)
	}
	var totalAreaMin=$("#searchTotalAreaMin").val()
	if(totalAreaMin !== "" && !isNaN(totalAreaMin)){
		searchAmusementPark.totalAreaMin = Number(totalAreaMin)
	}
	var totalAreaMax=$("#searchTotalAreaMax").val()
	if(totalAreaMax !== "" && !isNaN(totalAreaMax)){
		searchAmusementPark.totalAreaMax = Number(totalAreaMax)
	}
	var entranceFeeMin=$("#searchEntranceFeeMin").val()
	if(entranceFeeMin !== "" && !isNaN(entranceFeeMin)){
		searchAmusementPark.entranceFeeMin = Number(entranceFeeMin)
	}
	var entranceFeeMax=$("#searchEntranceFeeMax").val()
	if(entranceFeeMax!== "" && !isNaN(entranceFeeMax)){
		searchAmusementPark.entranceFeeMax = Number(entranceFeeMax)
	}
		
	$.ajax({
		url : links.amusementPark+ "?input="+encodeURI(JSON.stringify(searchAmusementPark)),
		success : function(response) {
			fillTableWithData(response)
		}
	})
	
	console.log(searchAmusementPark)
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
		$.each(data._embedded.amusementParkPageResponseDtoList, function(i, e) {
			tableBody.push(convertAmusementParkToTableRow(e))
		})
	}

	$("#tableBody").html(tableBody.join())
}

function convertAmusementParkToTableRow(amusementPark) {
	var tr = []
	tr.push("<tr onclick='detail(\""+amusementPark._links.self.href+"\")'>")
	tr.push("<td>" + amusementPark.name + "</td>")
	tr.push("<td>" + amusementPark.capital + "</td>")
	tr.push("<td>" + amusementPark.totalArea + "</td>")
	tr.push("<td>" + amusementPark.entranceFee + "</td>")
	tr.push("<td>" + amusementPark.numberOfGuestBookRegistries + "</td>")
	tr.push("<td>" + amusementPark.numberOfActiveVisitors + "</td>")
	tr.push("<td>" + amusementPark.numberOfKnownVisitors + "</td>")
	tr.push("</tr>")
	return tr.join("")
}

function detail(url){
	$("#amusementParkDetails").modal("show")
	$("#amusementParkDetailsText").html(url)
	
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