function clearAndShowMachineCreateModal() {
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

function machineCreate(url) {
	$("machineCreateButton").attr("disabled", true)
	$.ajax({
		url : url,
		method : "POST",
		contentType : "application/json",
		data : JSON.stringify(collectMachine()),
		success : function() {
			$("#machineCreateModal").modal("hide")
			getMachines(url)

		},
		error : function(response) {
			$("#machineCreateErrorMessage").html(
					"error: " + response.responseText)
		},
		complete : function() {
			$("#machineCreateButton").attr("disabled", false)
		}
	})
}

function collectMachine() {
	var machine = {}
	machine.fantasyName = $("#machineCreateFantasyName").val()
	machine.size = Number($("#machineCreateSize").val())
	machine.price = Number($("#machineCreatePrice").val())
	machine.numberOfSeats = Number($("#machineCreateNumberOfSeats").val())
	machine.minimumRequiredAge = Number($("#machineCreateMinimumRequiredAge")
			.val())
	machine.ticketPrice = Number($("#machineCreateTicketPrice").val())
	machine.type = $("#machineCreateType").val()
	return machine
}

function getMachines(href) {
	$.ajax({
		url : href,
		success : function(data) {
			machineFillTable(data)
			machineSetTableFooter(data)
		}
	})
}

function machineFillTable(data) {
	if (data._embedded !== undefined) {
		var tableBody = []
		var array = data._embedded.machineSearchResponseDtoList
		for (var i = 0; i < array.length; i++) {
			var machine = array[i]
			var tr = []
			tr.push("<tr>")
			tr.push("<td>" + machine.fantasyName + "</td>")
			tr.push("<td>" + machine.size + "</td>")
			tr.push("<td>" + machine.price + "</td>")
			tr.push("<td>" + machine.numberOfSeats + "</td>")
			tr.push("<td>" + machine.minimumRequiredAge + "</td>")
			tr.push("<td>" + machine.ticketPrice + "</td>")

			tr.push("<td>" + machine.type.toLowerCase().replace("_", " ")
					+ "</td>")
			tr
					.push("<td> <input class=\"btn btn-secondary btn-md border border-secondary font-weight-normal\" type=\"button\" value=\"Get on\" "
							+ "onclick=\"getOnMachine('"
							+ machine._links.getOnMachine.href
							+ "','"
							+ machine.fantasyName
							+ "','"
							+ machine.type
							+ "')\"></td>")
			tr.push("</tr>")

			tableBody.push(tr.join())
		}
		$("#tableBody").html(tableBody.join())
		$("#numberOfPage").html(
				data.page.number + 1 + "/" + data.page.totalPages)
	} else {
		$("#tableBody").empty()
		$("#numberOfPage").html("0/0")
	}

}

function machineSetTableFooter(data) {
	if (data._links.first !== undefined) {
		$("#first").attr("onclick",
				"getMachines('" + data._links.first.href + "')")
		$("#first").attr("disabled", false)
	} else {
		$("#first").attr("disabled", true)
	}

	if (data._links.prev !== undefined) {
		$("#left").attr("onclick",
				"getMachines('" + data._links.prev.href + "')")
		$("#left").attr("disabled", false)
	} else {
		$("#left").attr("disabled", true)
	}

	if (data._links.next !== undefined) {
		$("#right").attr("onclick",
				"getMachines('" + data._links.next.href + "')")
		$("#right").attr("disabled", false)
	} else {
		$("#right").attr("disabled", true)
	}

	if (data._links.last !== undefined) {
		$("#last").attr("onclick",
				"getMachines('" + data._links.last.href + "')")
		$("#last").attr("disabled", false)
	} else {
		$("#last").attr("disabled", true)
	}

	$("#refresh").attr("onclick",
			"getMachines('" + data._links.self.href + "')")
}

function machineSearchButton(url) {
	var machineSearch = {}
	machineSearch.fantasyName = $("#machineSearchFantasyName").val()
	var sizeMin = $("#machineSearchSizeMin").val()
	if (sizeMin !== "" && !isNaN(sizeMin)) {
		machineSearch.sizeMin = Number(sizeMin)
	}
	var sizeMax = $("#machineSearchSizeMax").val()
	if (sizeMax !== "" && !isNaN(sizeMax)) {
		machineSearch.sizeMax = Number(sizeMax)
	}
	var priceMin = $("#machineSearchPriceMin").val()
	if (priceMin !== "" && !isNaN(priceMin)) {
		machineSearch.priceMin = Number(priceMin)
	}
	var priceMax = $("#machineSearchPriceMax").val()
	if (priceMax !== "" && !isNaN(priceMax)) {
		machineSearch.priceMax = Number(priceMax)
	}
	var numberOfSeatsMin = $("#machineSearchNumberOfSeatsMin").val()
	if (numberOfSeatsMin !== "" && !isNaN(numberOfSeatsMin)) {
		machineSearch.numberOfSeatsMin = Number(numberOfSeatsMin)
	}
	var numberOfSeatsMax = $("#machineSearchNumberOfSeatsMax").val()
	if (numberOfSeatsMax !== "" && !isNaN(numberOfSeatsMax)) {
		machineSearch.numberOfSeatsMax = Number(numberOfSeatsMax)
	}
	var minimumRequiredAgeMin = $("#machineSearchMinimumRequiredAgeMin").val()
	if (minimumRequiredAgeMin !== "" && !isNaN(minimumRequiredAgeMin)) {
		machineSearch.minimumRequiredAgeMin = Number(minimumRequiredAgeMin)
	}
	var minimumRequiredAgeMax = $("#machineSearchMinimumRequiredAgeMax").val()
	if (minimumRequiredAgeMax !== "" && !isNaN(minimumRequiredAgeMax)) {
		machineSearch.minimumRequiredAgeMax = Number(minimumRequiredAgeMax)
	}
	var ticketPriceMin = $("#machineSearchTicketPriceMin").val()
	if (ticketPriceMin !== "" && !isNaN(ticketPriceMin)) {
		machineSearch.ticketPriceMin = Number(ticketPriceMin)
	}
	var ticketPriceMax = $("#machineSearchTicketPriceMax").val()
	if (ticketPriceMax !== "" && !isNaN(ticketPriceMax)) {
		machineSearch.ticketPriceMax = Number(ticketPriceMax)
	}
	var type = $("#machineSearchType").val()
	if (type !== "") {
		machineSearch.type = $("#machineSearchType").val()
	}

	getMachines(url + "?input=" + encodeURI(JSON.stringify(machineSearch)))
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

function getOnMachine(href, name, type) {
	$
			.ajax({
				url : href,
				method : "PUT",
				success : function(data) {

					$("#machineModalTitle").html(name)
					switch (type) {

					case "CAROUSEL":
						$("#machineModalBody")
								.html(
										"<iframe width=\"100%\" height=\"400\" src=\"https://www.youtube.com/embed/oNY_R3MmIbM?autoplay=1\"></iframe>")
						break
					case "GOKART":
						$("#machineModalBody")
								.html(
										"<iframe width=\"100%\" height=\"400\" src=\"https://www.youtube.com/embed/Qa2kYagOCiw?autoplay=1\"></iframe>")
						break
					case "DODGEM":
						$("#machineModalBody")
								.html(
										"<iframe width=\"100%\" height=\"400\" src=\"https://www.youtube.com/embed/FATfO8ScbCI?autoplay=1\"></iframe>")
						break
					case "SHIP":
						$("#machineModalBody")
								.html(
										"<iframe width=\"100%\" height=\"400\" src=\"https://www.youtube.com/embed/UYWkF0BATDc?autoplay=1\"></iframe>")
						break
					case "ROLLER_COASTER":
						$("#machineModalBody")
								.html(
										"<iframe width=\"100%\" height=\"400\" src=\"https://www.youtube.com/embed/s9njwl_VzZA?autoplay=1\"></iframe>")
						break

					}
					$("#spendingMoney").html(data.spendingMoney)
					$("#machineModal").modal("show")
					$("#machineModalExit").attr(
							"onclick",
							"getOffMachine('" + data._links.getOffMachine.href
									+ "')")

					$("#machineModalGetOff").attr(
							"onclick",
							"getOffMachine('" + data._links.getOffMachine.href
									+ "')")
				}
			})
}

function getOffMachine(href) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function(data) {
			$("#machineModal").modal("hide")
			$("#machineModalTitle").empty()
			$("#machineModalBody").empty()
		}
	})
}

function showMachineGuestBook(url) {

	$("#guestBookSearchTimestampMin").val("")
	$("#guestBookSearchTimestampMax").val("")
	$("#guestBookSearchVisitorEmail").val("")
	$("#guestBookSearchText").val("")
	$("#guestBookText").val("")
	$("#guestBookModal").modal("show")

	getGuestBooks(url)

}

function machineGuestBookWrite(url) {
	var text = $("#guestBookText").val()

	$.ajax({
		url : url,
		method : "POST",
		contentType : "application/json",
		data : text,
		success : function() {
			getGuestBooks(url)
		}
	})
}