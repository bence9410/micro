function getContainerId() {
	$.ajax({
		url : '/docker',
		success : function(result) {
			$('#containerId').html('Container ID: ' + result);
		}
	});
}