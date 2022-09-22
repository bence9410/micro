var username;
var authorities;

function init() {
    $.ajax({
        url: '/user',
        success: function (data) {
            username = data.name;
            $("#username").html(username);

            var auth = [];
            $.each(data.authorities, function (i, e) {
                auth.push(e.authority);
            });
            authorities = auth.join(",");
            $("#authorities").html(authorities);
            $("#login").hide();
            $("#header").show();
            $("#content").show();
            
            getSpendingMoney();
        }
    });
    
}

function getSpendingMoney(){
	$.ajax({
    	url: "/visitor/hello",
    	success: function (data) {
    		$("#spendingMoney").html(data);
    	}
    });
}

function zoo(){
	$.ajax({
    	url: "/zoo.html",
        success: function (data) {
        	$("#content").html(data);
        
        }
    });
}

function amusementPark(){
	$.ajax({
    	url: "/amusement-park.html",
        success: function (data) {
        	$("#content").html(data);
        }
    });
}