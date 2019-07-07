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
			.push("<td> <input type=\"button\" value=\"Get on\" "
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