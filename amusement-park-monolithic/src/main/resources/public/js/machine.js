function leavePark(href) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function() {
			getAmusementParkPage()
		}
	})
}
function getMachines(href) {
	$.ajax({
		url : href,
		success : function(response) {
			machineFillTableWithData(response)
		}
	})
}

function machineFillTableWithData(data) {
	var tableBody = []

	if (data._embedded !== undefined) {
		$.each(data._embedded.machineResourceList, function(i, e) {
			tableBody.push(convertMachineToTableRow(e))
		})
	}

	$("#tableBody").html(tableBody.join())
}

function convertMachineToTableRow(machine) {
	var tr = []
	tr.push("<tr>")
	tr.push("<td>" + machine.fantasyName + "</td>")
	tr.push("<td>" + machine.size + "</td>")
	tr.push("<td>" + machine.price + "</td>")
	tr.push("<td>" + machine.numberOfSeats + "</td>")
	tr.push("<td>" + machine.minimumRequiredAge + "</td>")
	tr.push("<td>" + machine.ticketPrice + "</td>")
	tr.push("<td>" + machine.type.toLowerCase() + "</td>")
	tr
			.push("<td> <input class=\"btn btn-secondary btn-md border border-secondary font-weight-normal\" type=\"button\" value=\"Get on\" "
					+ "onclick=\"getOnMachine('"
					+ machine._links.getOnMachine.href + "','"
					+ machine.fantasyName + "','" + machine.type + "')\"></td>")
	tr.push("</tr>")
	return tr.join("")
}
function getOnMachine(href, name, type) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function(data) {
			$("#machineModal").modal("show")
			$("#machineModalTitle").html(name)
			switch (type) {
			case "CAROUSEL":
				$("#machineModalImage").attr("src", "/img/carousel.jpg")
				break
			}
			$("#spendingMoney").html(data.spendingMoney)
			$("#machineModalExit").attr("onclick","getOffMachine('" + data._links.getOffMachine.href + "')")
			$("#machineModalGetOff").attr("onclick","getOffMachine('" + data._links.getOffMachine.href + "')")
		}
	})
}
function getOffMachine(href){
	$.ajax({
		url : href,
		method : "PUT",
		success : function(data) {
			$("#machineModal").modal("hide")
			$("#machineModalTitle").html("")
			$("#machineModalImage").attr("src", "")
		}
	})
}
function guestBookWirte(){
	$("#guestBookModal").modal("show")
}
function machineSearchButton(){
	var machineSearch = {}
	machineSearch.machineSearchFantasyName = $("#machineSearchFantasyName").val()
	var machineSearchSize=$("#machineSearchSize").val()
	if(machineSearchSize !== "" && !isNaN(machineSearchSize)){
		 machineSearch.machineSearchSize = Number(machineSearchSize)
	}
	var machineSearchPrice=$("#machineSearchPrice").val()
	if(machineSearchPrice !== "" && !isNaN(machineSearchPrice)){
		machineSearch.machineSearchPrice = Number(machineSearchPrice)
	}
	var machineSearchNumberOfSeats=$("#machineSearchNumberOfSeats").val()
	if(machineSearchNumberOfSeats !== "" && !isNaN(machineSearchNumberOfSeats)){
		machineSearch.machineSearchNumberOfSeats = Number(machineSearchNumberOfSeats)
	}
	var machineSearchMinimumRequiredAge=$("#machineSearchMinimumRequiredAge").val()
	if(machineSearchMinimumRequiredAge !== "" && !isNaN(machineSearchMinimumRequiredAge)){
		machineSearch.machineSearchMinimumRequiredAge = Number(machineSearchMinimumRequiredAge)
	}
	var machineSearchTicketPrice=$("#machineSearchTicketPrice").val()
	if(machineSearchTicketPrice !== "" && !isNaN(machineSearchTicketPrice)){
		machineSearch.machineSearchTicketPrice = Number(machineSearchTicketPrice)
	}
	var machineSearchType=$("#machineSearchType").val()
	if(machineSearchType!== "" && !isNaN(machineSearchType)){
		machineSearch.machineSearchType = Number(machineSearchType)
	}
	
	$.ajax({
		url : links.machine+ "?input="+encodeURI(JSON.stringify(machineSearch)),
		success : function(response) {
			fillTableWithData(response)
		}
	})
	
	
	console.log(machineSearch)
}
