function leavePark(href) {
	$.ajax({
		url : href,
		method : "PUT",
		success : function() {
			getAmusementParkPage()
		}
	})
}