$(document).ready(function(){
    
    if (authorities.indexOf("ADMIN") != -1){
        $("#saveDiv").removeClass();
        $("thead").children("tr").append("<th></th>");
    }
    
    getAmusementParks();
    
});

function save() {
	$('#save').attr('disabled', true);
	$.ajax({
		url : '/amusementPark',
		method : 'POST',
		contentType : 'application/json',
		data : JSON.stringify(parkCollectData()),
		success : function(response) {
			var result = $('#result');
			result.removeClass();
			result.html('success');
			result.addClass('greenText');
			result.show();
			getAmusementParks();
		},
		error : function(response) {
			var result = $('#result');
			result.removeClass();
			result.html('error: ' + response.responseText);
			result.addClass('redText');
			result.show();
		},
		complete : function(response) {
			$('#save').attr('disabled', false);
			setTimeout(function() {
				$('#result').hide();
			}, 8000);
		}
	});
}

function parkCollectData() {
	var address = {};
	address.country = $('#country').val();
	address.zipCode = $('#zipCode').val();
	address.city = $('#city').val();
	address.street = $('#street').val();
	address.houseNumber = $('#houseNumber').val();

	var amusementPark = {};
	amusementPark.name = $('#parkName').val();
	amusementPark.capital = $('#capital').val();
	amusementPark.totalArea = $('#totalArea').val();
	amusementPark.entranceFee = $('#entranceFee').val();
	amusementPark.address = address;

	return amusementPark;
}

function parkFillWithSampleData() {
	$('#parkName').val('Beni parkja');
	$('#capital').val('10000');
	$('#totalArea').val('1000');
	$('#entranceFee').val('50');

	$('#country').val('Magyarország');
	$('#zipCode').val('1148');
	$('#city').val('Budapest');
	$('#street').val('Fogarasi út');
	$('#houseNumber').val('80/C');
}

function getAmusementParks() {
	$.ajax({
		url : '/amusementPark',
		success : function(response) {
			fillTableWithData(response);
		}
	});
}

function fillTableWithData(data) {
	var tableBody = [];

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
	tr.push('<td>' + address.country + ' ' + address.zipCode);
        tr.push(' ' + address.city + ' ' + address.street);
        tr.push(' ' + address.houseNumber + '</td>');
        if (authorities.indexOf("ADMIN") != -1){
            tr.push("<th><input type='button' onclick='deletePark("+amusementPark.identifier+")' value='Delete'></th>");
        }
        tr.push('</tr>');
	return tr.join('');
}

function deletePark(id){
    $.ajax({
        url: "/amusementPark/"+id,
        method : 'DELETE',
        success: function (data, textStatus, jqXHR) {
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
            
        },
        error: function (data, textStatus, jqXHR) {
             console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
        }
        
    });
}


