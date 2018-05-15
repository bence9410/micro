var username;
var authorities;

function init() {
	$.ajax({
		url : '/user',
		success : function(data) {
			username = data.name;
			$("#username").html(username);

			var auth = [];
			$.each(data.authorities, function(i, e) {
				auth.push(e.authority);
			});
			authorities = auth.join(",");
			$("#authorities").html(authorities);

			$.ajax({
				url : '/visitor/spending-money/',
				success : function(data) {
					$('#spendingMoneyP').html(data);
					$('#signedUp').removeClass();
				},
				error : function(response) {
					if (response.status == 418) {
						$('#notSignedUp').removeClass();
					}
				}
			});
		}
	});
	getAmusementParkPage();
}

function signUp() {
	$.ajax({
		url : '/visitor',
		method : 'POST',
		contentType : 'application/json',
		data : JSON.stringify(collectData()),
		success : function(data) {
			$('#notSignedUp').hide();
			$('#spendingMoneyP').html(data.spendingMoney);
			$('#signedUp').removeClass();
		}
	});
}

function collectData() {
	var visitor = {};
	visitor.name = $('#name').val();
	visitor.dateOfBirth = $('#dateOfBirth').val();
	visitor.spendingMoney = $('#spendingMoney').val();
	return visitor;
}

function fillWithSampleData() {
	$('#name').val('Németh Bence');
	$('#dateOfBirth').val('1994-10-22');
	$('#spendingMoney').val('1000');
}

function getAmusementParkPage() {
	$.ajax({
		url : '/pages/amusement-park.html',
		success : function(data) {
			$("#content").html(data);
			$('head').append("<script src='js/amusement-park.js'></script>");
		}
	});
}