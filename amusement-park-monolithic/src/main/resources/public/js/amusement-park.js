$(function() {
	getAmusementParks(links.amusementPark)
	$("#amusementParkSearchButton").attr("onclick",
			"getAmusementParksSearch('" + links.amusementPark + "')")

	$("#headerButton").empty()
	$("#headerButton")
			.append(
					"<input id='amusementParkShowSearchButton' class='btn btn-secondary my-2 mr-1' type='button'"
							+ " value='Search' onclick='$(\"#amusementParkSearchDiv\").toggle()'>")
	if (isAdmin) {
		$("#headerButton").append(
				"<input class='btn btn-secondary my-2 mr-1 ' type='button' value='Create'"
						+ "onclick='clearAndShowAmusementParkCreateModal()'>")
		$("#createAmusementParkButton").attr("onclick", "amusementParkCreate('"+links.amusementPark+"')")
	}
})

function clearAndShowAmusementParkCreateModal() {
	$("#createAmusementParkErrorMessage").html("")
	$("#createAmusementParkName").val("")
	$("#createAmusementParkCapital").val("")
	$("#createAmusementParkTotalArea").val("")
	$("#createAmusementParkEntranceFee").val("")
	$("#createAmusementParkModal").modal("show")
}

function amusementParkCreate(url) {
	$("#createAmusementParkButton").attr("disabled", true)
	$.ajax({
		url : url,
		method : "POST",
		contentType : "application/json",
		data : JSON.stringify(collectAmusementPark()),
		success : function() {
			$("#createAmusementParkModal").modal("hide")
			getAmusementParks(url)
		},
		error : function(response) {
			$("#createAmusementParkErrorMessage").html(
					"error: " + response.responseText)
		},
		complete : function() {
			$("#createAmusementParkButton").attr("disabled", false)
		}
	})
}

function collectAmusementPark() {
	var amusementPark = {}
	amusementPark.name = $("#createAmusementParkName").val()
	amusementPark.capital = Number($("#createAmusementParkCapital").val())
	amusementPark.totalArea = Number($("#createAmusementParkTotalArea").val())
	amusementPark.entranceFee = Number($("#createAmusementParkEntranceFee")
			.val())
	return amusementPark
}

function getAmusementParks(url) {
	$.ajax({
		url : url,
		success : function(data) {
			amusementParkFillTable(data)
			amusementParkSetTableFooter(data)
		}
	})
}

function amusementParkFillTable(data) {
	if (data._embedded !== undefined) {
		var tableBody = []
		var array = data._embedded.amusementParkPageResponseDtoList
		for (var i = 0; i < array.length; i++) {
			var amusementPark = array[i]
			var tr = []
			tr.push("<tr onclick='detail(\"" + amusementPark._links.self.href
					+ "\")'>")
			tr.push("<td>" + amusementPark.name + "</td>")
			tr.push("<td>" + amusementPark.capital + "</td>")
			tr.push("<td>" + amusementPark.totalArea + "</td>")
			tr.push("<td>" + amusementPark.entranceFee + "</td>")
			tr.push("<td>" + amusementPark.numberOfMachines + "</td>")
			tr.push("<td>" + amusementPark.numberOfGuestBookRegistries
					+ "</td>")
			tr.push("<td>" + amusementPark.numberOfActiveVisitors + "</td>")
			tr.push("<td>" + amusementPark.numberOfKnownVisitors + "</td>")
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

function amusementParkSetTableFooter(data) {
	if (data._links.first !== undefined) {
		$("#first").attr("onclick",
				"getAmusementParks('" + data._links.first.href + "')")
		$("#first").attr("disabled", false)
	} else {
		$("#first").attr("disabled", true)
	}

	if (data._links.prev !== undefined) {
		$("#left").attr("onclick",
				"getAmusementParks('" + data._links.prev.href + "')")
		$("#left").attr("disabled", false)
	} else {
		$("#left").attr("disabled", true)
	}

	if (data._links.next !== undefined) {
		$("#right").attr("onclick",
				"getAmusementParks('" + data._links.next.href + "')")
		$("#right").attr("disabled", false)
	} else {
		$("#right").attr("disabled", true)
	}

	if (data._links.last !== undefined) {
		$("#last").attr("onclick",
				"getAmusementParks('" + data._links.last.href + "')")
		$("#last").attr("disabled", false)
	} else {
		$("#last").attr("disabled", true)
	}

	$("#tableRefresh").attr("onclick",
			"getAmusementParks('" + data._links.self.href + "')")
}

function getAmusementParksSearch(url) {
	var amusementParkSearch = {}
	amusementParkSearch.name = $("#searchName").val()
	var capitalMin = $("#searchCapitalMin").val()
	if (capitalMin !== "" && !isNaN(capitalMin)) {
		amusementParkSearch.capitalMin = Number(capitalMin)
	}
	var capitalMax = $("#searchCapitalMax").val()
	if (capitalMax !== "" && !isNaN(capitalMax)) {
		amusementParkSearch.capitalMax = Number(capitalMax)
	}
	var totalAreaMin = $("#searchTotalAreaMin").val()
	if (totalAreaMin !== "" && !isNaN(totalAreaMin)) {
		amusementParkSearch.totalAreaMin = Number(totalAreaMin)
	}
	var totalAreaMax = $("#searchTotalAreaMax").val()
	if (totalAreaMax !== "" && !isNaN(totalAreaMax)) {
		amusementParkSearch.totalAreaMax = Number(totalAreaMax)
	}
	var entranceFeeMin = $("#searchEntranceFeeMin").val()
	if (entranceFeeMin !== "" && !isNaN(entranceFeeMin)) {
		amusementParkSearch.entranceFeeMin = Number(entranceFeeMin)
	}
	var entranceFeeMax = $("#searchEntranceFeeMax").val()
	if (entranceFeeMax !== "" && !isNaN(entranceFeeMax)) {
		amusementParkSearch.entranceFeeMax = Number(entranceFeeMax)
	}

	getAmusementParks(url + "?input="
			+ encodeURI(JSON.stringify(amusementParkSearch)))
}

function detail(url) {
	$.ajax({
		url : url,
		success : function(data) {
			$("#detailName").html(data.name)
			$("#detailCapital").html(data.capital)
			$("#detailTotalArea").html(data.totalArea)
			$("#detailEntranceFee").html(data.entranceFee)
			$("#detailMachines").html(data.numberOfMachines)
			$("#detailActiveVisitors").html(data.numberOfActiveVisitors)
			$("#detailKnownVisitors").html(data.numberOfKnownVisitors)
			$("#detailGuestBookRegistries").html(
					data.numberOfGuestBookRegistries)
			$("#enterPark").attr(
					"onclick",
					"enterPark('" + data._links.visitorEnterPark.href + "','"
							+ data._links.machine.href + "')")

			$("#guestBookSearchTimestampMin").val("")
			$("#guestBookSearchTimestampMax").val("")
			$("#guestBookSearchVisitorEmail").val("")
			$("#guestBookSearchText").val("")

			getGuestBooks(data._links.addRegistry.href)
			$("#guestBookSearchButton").attr(
					"onclick",
					"getGuestBooksSearch('" + data._links.addRegistry.href
							+ "')")

			$("#amusementParkDetails").modal("show")

		}
	})
}

function getGuestBooks(url) {
	$.ajax({
		url : url,
		success : function(data) {
			guestBookFillTable(data)
			guestBookSetTableFooter(data)
		}

	})
}

function guestBookFillTable(data) {
	if (data._embedded !== undefined) {
		var tbody = []
		var array = data._embedded.guestBookRegistrySearchResponseDtoList
		for (var i = 0; i < array.length; i++) {
			var guestBook = array[i]
			var tr = []
			tr.push("<tr>")
			tr.push("<td>" + guestBook.dateOfRegistry + "</td>")
			tr.push("<td>" + guestBook.visitorEmail + "</td>")
			tr.push("<td>" + guestBook.textOfRegistry + "</td>")
			tr.push("</tr>")
			tbody.push(tr.join())
		}
		$("#guestBookTable").html(tbody.join())
		$("#guestBookNumberOfPage").html(
				data.page.number + 1 + "/" + data.page.totalPages)
	} else {
		$("#guestBookTable").empty()
		$("#guestBookNumberOfPage").html("0/0")
	}
}

function guestBookSetTableFooter(data) {
	if (data._links.first !== undefined) {
		$("#guestBookFirst").attr("onclick",
				"getGuestBooks('" + data._links.first.href + "')")
		$("#guestBookFirst").attr("disabled", false)
	} else {
		$("#guestBookFirst").attr("disabled", true)
	}

	if (data._links.prev !== undefined) {
		$("#guestBookLeft").attr("onclick",
				"getGuestBooks('" + data._links.prev.href + "')")
		$("#guestBookLeft").attr("disabled", false)
	} else {
		$("#guestBookLeft").attr("disabled", true)
	}

	if (data._links.next !== undefined) {
		$("#guestBookRight").attr("onclick",
				"getGuestBooks('" + data._links.next.href + "')")
		$("#guestBookRight").attr("disabled", false)
	} else {
		$("#guestBookRight").attr("disabled", true)
	}

	if (data._links.last !== undefined) {
		$("#guestBookLast").attr("onclick",
				"getGuestBooks('" + data._links.last.href + "')")
		$("#guestBookLast").attr("disabled", false)
	} else {
		$("#guestBookLast").attr("disabled", true)
	}

	$("#guestBookRefresh").attr("onclick",
			"getGuestBooks('" + data._links.self.href + "')")
}

function getGuestBooksSearch(url) {
	var guestBookSearch = {}

	guestBookSearch.visitorEmail = $("#guestBookSearchVisitorEmail").val()
	guestBookSearch.textOfRegistry = $("#guestBookSearchText").val()
	guestBookSearch.dateOfRegistryMin = $("#guestBookSearchTimestampMin")
			.val()
	guestBookSearch.dateOfRegistryMax = $("#guestBookSearchTimestampMax")
			.val()

	getGuestBooks(url + "?input=" + encodeURI(JSON.stringify(guestBookSearch)))
}

function enterPark(href, machineHref) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function(data) {
			$("#spendingMoney").html(data.spendingMoney)
			getMachinePage(data)
			$(".modal-backdrop").remove()
		},
		error : function(data) {
			$("#detailsError").html(data.responseText)
		}

	})
}

function getMachinePage(enterParkData) {
	$
			.ajax({
				url : pages.machine,
				success : function(data) {
					$("#content").html(data)
					getMachines(enterParkData._links.machine.href)
					$("#refresh").attr(
							"onclick",
							"getMachines('" + enterParkData._links.machine.href
									+ "')")
					$("#machineSearchButton").attr(
							"onclick",
							"machineSearchButton('"
									+ enterParkData._links.machine.href + "')")

					$("#headerButton").empty()
					$("#headerButton").append(
							"<input id='guestBookButton' type='button' class='btn btn-secondary my-2 mr-1'"
									+ "onclick='showMachineGuestBook(\""
									+ enterParkData._links.addRegistry.href
									+ "\")' value='Guest Book Writing'>")

					if (isAdmin) {
						$("#headerButton")
								.append(
										"<input class='btn btn-secondary my-2 mr-1 ' type='button' value='Create'"
												+ "onclick='clearAndShowMachineCreateModal()'>")
						$("#machineCreateButton").attr(
								"onclick",
								"machineCreate('"
										+ enterParkData._links.machine.href
										+ "')")
					}

					$("#headerButton")
							.append(
									"<input class='btn btn-secondary my-2' type='button' value='Search'"
											+ "onclick='$(\"#machineSearch\").toggle()'>")
					$("#headerButton").append(
							"<input id='leave' type='button' class='btn btn-secondary my-2 ml-1' "
									+ "value='Leave' onclick='leavePark(\""
									+ enterParkData._links.leavePark.href
									+ "\")'>")
					$("#guestBookSave").attr(
							"onclick",
							"machineGuestBookWrite('"
									+ enterParkData._links.addRegistry.href
									+ "')")
					$("#guestBookSearchButton").attr(
							"onclick",
							"getGuestBooksSearch('"
									+ enterParkData._links.addRegistry.href
									+ "')")

				}
			})

}