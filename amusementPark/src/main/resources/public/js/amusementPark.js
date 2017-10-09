function save() {
	console.log('before ajax');
	$('#save').attr('disabled', true);
	$.ajax({
		url : '/amusementPark',
		method : 'POST',
		contentType : 'application/json',
		data : JSON.stringify(collectData()),
		success : function(response) {
			console.log('success:');
			var result = $('#result');
			result.removeClass();
			result.html('success');
			result.addClass('greenText');
			result.show();
			getAmusementParks();
		},
		error : function(response) {
			console.log('error:');
			var result = $('#result');
			result.removeClass();
			result.html('error: ' + response.responseText);
			result.addClass('redText');
			result.show();
		},
		complete : function(response) {
			console.log(response);
			$('#save').attr('disabled', false);
			setTimeout(function() {
				$('#result').hide();
			}, 8000);
		}
	});
	console.log('after ajax sent');
}

function collectData() {
	var address = {};
	address.zipCode = $('#zipCode').val();
	address.country = $('#country').val();
	address.city = $('#city').val();
	address.street = $('#street').val();
	address.houseNumber = $('#houseNumber').val();

	var amusementPark = {};
	amusementPark.name = $('#name').val();
	amusementPark.capital = $('#capital').val();
	amusementPark.totalArea = $('#totalArea').val();
	amusementPark.entranceFee = $('#entranceFee').val();
	amusementPark.address = address;

	console.log('before collectData returns');
	console.log(amusementPark);

	return amusementPark;
}

function fillWithSampleData() {
	$('#name').val('Orsi parkja');
	$('#capital').val('10000');
	$('#totalArea').val('1000');
	$('#entranceFee').val('50');

	$('#zipCode').val('1148');
	$('#country').val('Magyarország');
	$('#city').val('Budapest');
	$('#street').val('Fogarasi út');
	$('#houseNumber').val('80/C');
}

function getAmusementParks() {
	$.ajax({
		url : '/amusementPark',
		success : function(response) {
			console.log('success:');
			fillTableWithData(response);
		},
		error : function(response) {
			console.log('error:');
			alert("Error in getAmusementParks, can't fill table with data.");
		},
		complete : function(response) {
			console.log(response);
		}
	});
}

function fillTableWithData(data) {
	var tableBody = [];

	console.log(data);
	
	$.each(data, function(i, e) {
		tableBody.push(convertAmusementParkToTableRow(e));
	});

	$('#tableBody').html(tableBody.join(''));
}

function convertAmusementParkToTableRow(amusementPark) {
	var tr = [];
	tr.push('<tr>')
	tr.push('<td>' + amusementPark.name + '</td>');
	tr.push('<td>' + amusementPark.capital + '</td>');
	tr.push('<td>' + amusementPark.totalArea + '</td>');
	tr.push('<td>' + amusementPark.entranceFee + '</td>');
	var address = amusementPark.address;
	tr.push('<td>' + address.country + ' ' + address.zipCode + ' ' + address.city 
			+ address.street + address.houseNumber + '</td>');
	tr.push('</tr>')
	return tr.join('');
}







