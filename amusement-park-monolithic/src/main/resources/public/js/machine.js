function clearAndShowCreateMachineModal(){
	$("#machineCreateErrorMessage").html("")
	$("#machineCreateFantasyName").val("")
	$("#machineCreateSize").val("")
	$("#machineCreatePrice").val("")
	$("#machineCreateNumberOfSeats").val("")
	$("#machineCreateMinimumRequiredAge").val("")
	$("#machineCreateTicketPrice").val("")
	$("#machineCreateType").val("")
	$("#machineCreateModal").modal("show")
}
function machineCreate(url){
	$("machineCreateButton").attr("disabled", true)
	var machine = collectMachine()
	var error= []
	console.log(machine)
	if (error.length == 0){
		$.ajax({
			url : url,
			method : "POST",
			contentType : "application/json",
			data : JSON.stringify(machine),
			success : function() {
				$("#machineCreateModal").modal("hide")
				getMachines(url)
				
			},
			error : function(response) {
				$("#machineCreateErrorMessage").html("error: " + response.responseText)
			},
			complete : function() {
				$("#machineCreateButton").attr("disabled", false)
			}
		})	
	} else {
		 $("#machineCreateErrorMessage").html(error.join("<br>"))
		 $("#machineCreateButton").attr("disabled", false)
		
	}
}


function collectMachine(){
	var machine = {}
	machine.fantasyName = $("#machineCreateFantasyName").val()
	machine.size = Number($("#machineCreateSize").val())
	machine.price =  Number($("#machineCreatePrice").val())
	machine.numberOfSeats =  Number($("#machineCreateNumberOfSeats").val())
	machine.minimumRequiredAge =  Number($("#machineCreateMinimumRequiredAge").val())
	machine.ticketPrice =  Number($("#machineCreateTicketPrice").val())
	machine.type = $("#machineCreateType").val()
	return machine
}

function leavePark(href) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function() {
			getAmusementParkPage()
		}
	})
}

function machineSearchButton(){
	var machineSearch = {}
	machineSearch.name = $("#machineSearchFantasyName").val()
	var sizeMin=$("#machineSearchSizeMin").val()
	if(sizeMin !== "" && !isNaN(sizeMin)){
		machineSearch.sizeMin = Number(sizeMin)
	}
	var sizeMax=$("#machineSearchSizeMax").val()
	if(sizeMax !== "" && !isNaN(sizeMax)){
		machineSearch.sizeMax = Number(sizeMax)
	}
	var priceMin=$("#machineSearchPriceMin").val()
	if(priceMin !== "" && !isNaN(priceMin)){
		machineSearch.priceMin = Number(priceMin)
	}
	var priceMax=$("#machineSearchPriceMax").val()
	if(priceMax !== "" && !isNaN(priceMax)){
		machineSearch.priceMax = Number(priceMax)
	}
	var numberOfSeatsMin=$("#machineSearchNumberOfSeatsMin").val()
	if(numberOfSeatsMin !== "" && !isNaN(numberOfSeatsMin)){
		machineSearch.numberOfSeatsMin = Number(numberOfSeatsMin)
	}
	var numberOfSeatsMax =$("#machineSearchNumberOfSeatsMax").val()
	if(numberOfSeatsMax!== "" && !isNaN(numberOfSeatsMax)){
		machineSearch.numberOfSeatsMax = Number(numberOfSeatsMax)
	}
	var minimumRequiredAgeMin=$("#machineSearchMinimumRequiredAgeMin").val()
	if(minimumRequiredAgeMin!== "" && !isNaN(minimumRequiredAgeMin)){
		machineSearch.minimumRequiredAgeMin = Number(minimumRequiredAgeMin)
	}
	var minimumRequiredAgeMax=$("#machineSearchMinimumRequiredAgeMax").val()
	if(minimumRequiredAgeMax!== "" && !isNaN(minimumRequiredAgeMax)){
		machineSearch.minimumRequiredAgeMax = Number(minimumRequiredAgeMax)
	}
	var ticketPriceMin =$("#machineSearchTicketPriceMin").val()
	if(ticketPriceMin!== "" && !isNaN(ticketPriceMin)){
		machineSearch.ticketPriceMin = Number(ticketPriceMin)
	}
	var ticketPriceMax=$("#machineSearchTicketPriceMax").val()
	if(ticketPriceMax!== "" && !isNaN(ticketPriceMax)){
		machineSearch.ticketPriceMax = Number(ticketPriceMax)
	}
	var type=$("#machineSearchType").val()
	if(type!==""){
		machineSearch.type = $("#machineSearchType").val()
	}
	

	$.ajax({
		url : links.machine+ "?input="+encodeURI(JSON.stringify(machineSearch)),
		success : function(response) {
			fillTableWithData(response)
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

	
