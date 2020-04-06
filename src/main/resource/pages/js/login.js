$(document).ready(function () {
    fetch("./footer.html")
        .then(response => {
        return response.text()
    })
.then(data => {
        document.querySelector("footer").innerHTML = data;
});

    var myVar = localStorage.getItem("Create");
    console.log(myVar);
    if (typeof myVar != 'undefined') {
        $('#message').text(localStorage.getItem("Create"));
        $('#message').show();
        localStorage.removeItem("Create");
    }

    $('#button_login').hide();
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
                if(response.responseText == "true"){
                    window.location.replace("index.html");
                } else{
                    showElementForLoginPage();
                  }
            }
         ]
     });
});

$(function () {
      $(window).keypress(function (e) {
      if (e.key === ' ' || e.key === 'Spacebar') {
        e.preventDefault()
        console.log('Space pressed')
      }
    });

    $('#login').on('submit', function (e) {
        e.preventDefault();
        var username =  $('#username').val();
        username = username.trim();
        var pwd =  $('#pwd').val();
        pwd = pwd.trim();
        console.log(username);
        console.log(username.length);
        console.log(pwd);
        if(username.length <1){
            console.log("<1");
            $('#message').text("Username empty");
            $('#message').show();
            $('#username').val("");
        } else  if(pwd.length <1) {
            console.log("<2");
            $('#message').text("Password empty");
            $('#message').show();
            $('#pwd').val("");
        }else {
            var data = {
                username: username,
                password: pwd
            }
            $.ajax({
                type: 'post',
                url: '/user/login',
                dataType: "json",
                data,
                contentType: "application/json; charset=utf-8",
                complete: [
                    (response) => {
                     console.log(response.responseText);
                        if(response.responseText == "false"){
                            $('#message').text("Incorrect username or password");
                            $('#message').show();
                            $('#pwd').val("");
                        }else if(response.responseText == "true"){
                            window.location.replace("index.html");
                        }
                     }
                ]
            });
        }
    });
    $('#remind').on('submit', function (e) {
        e.preventDefault();
        $('#login').hide();
        $('#remind').hide();
        $('#resetForm').show();

    });
    $('#resetForm').on('submit', function (e) {
        e.preventDefault();
        if(isValidEmailAddress($('#emailRes').val())){
            var data ={
                email: $('#emailRes').val()
            }
            console.log(data);
            $.ajax({
                type: 'POST',
                url: '/user/restore',
                dataType: 'json',
                data,
                complete: [
                    (response) => {
                    console.log(response.responseText);
            if(response.responseText == "true"){
                console.log('fsa');
                localStorage.setItem("Create","Email with password was sent");
                window.location.replace("login.html");
            }else{
                $('#rest_message').text("Email does not exist in system");
                $('#rest_message').show();
            }
        }
        ]
        });

        }else {
            $('#rest_message').text("Email is not valid");
            $('#rest_message').show();
        }
    });

});
function isValidEmailAddress(emailAddress) {
    var pattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
    return pattern.test(emailAddress);
}
function showElementForLoginPage() {
    $('#button_all').hide();
    $('#message_search').hide();
    $('#button_search').hide();
    $("#hideAll").show();
}

