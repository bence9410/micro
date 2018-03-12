$(document).ready(function() {
	$.ajax({
		url : "/amusement-park/hello",
		success : function(data) {
			$("#hello").html(data)
		}
	})
	$.ajax({
		url : "/amusement-park",
		success : function(data) {
			$("#messages").html(data.join(", "))
		}
	})
})