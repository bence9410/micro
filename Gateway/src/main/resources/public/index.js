var username;
var authorities;

function init() {
    $.ajax({
        url: '/me',
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
            
            appSelector();
        }
    });
}

function appSelector(){
    $.ajax({
    	url: '/appSelector.html',
        success: function (data) {
        	$("#content").html(data);
        	var scripts = $("head").find("script");
        	for (var i=3; i < scripts.length; i++){
        		scripts[i].remove();
        	}
        }
    });
}

function zoo(){
	$.ajax({
    	url: "/zooui/zoo.html",
        success: function (data) {
        	$("#content").html(data);
        	$("head").append("<script type='text/javascript'  src='zooui/zoo.js'></script>");
        }
    });
}