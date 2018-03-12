$(document).ready(function(){
	$.ajax({
        url: "/zoo/hello",
        success: function (data) {
        	$("#hello").html(data)
        }
	})
	$.ajax({
		url: "/zoo",
		success: function(data) {
			$("#messages").html(data.join(", "))
		}
	})
})