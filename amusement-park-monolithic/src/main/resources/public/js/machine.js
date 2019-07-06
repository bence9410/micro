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
	
	if (data._embedded !== undefined){
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
	tr.push("<td>"+"</td>")
	tr.push("</tr>")
	return tr.join("")
}