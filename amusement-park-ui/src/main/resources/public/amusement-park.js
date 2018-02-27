$(document).ready(function(){
	$.ajax({
        url: "/amusement-park/hello",
        success: function (data) {
        	$("#result").html(data)
        }
	})
})