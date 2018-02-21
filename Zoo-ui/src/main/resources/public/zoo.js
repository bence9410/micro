$(document).ready(function(){
	$.ajax({
        url: "/zoo/hello",
        success: function (data) {
        	$("#result").html(data)
        }
	})
})