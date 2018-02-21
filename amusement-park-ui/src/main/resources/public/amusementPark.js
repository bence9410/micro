$(document).ready(function(){
	$.ajax({
        url: "/amusementpark/hello",
        success: function (data) {
        	$("#result").html(data)
        }
	})
})