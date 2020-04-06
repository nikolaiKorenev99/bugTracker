$(document).ready(function () {
    cleanStorage();
    fetch("./footer.html")
        .then(response => {
        return response.text()
    })
.then(data => {
        document.querySelector("footer").innerHTML = data;
});
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
            if(response.responseText == "true"){
        window.location.replace("index.html");
    } else{
        $('#hideAll').show();
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
    $('#sign_up').on('submit', function (e) {
        e.preventDefault();
        $('#message').hide();
        $('#message').text(null);
        if(FormValidation()){
            checkUserName();
        }
    });
    $('#code_form').on('submit', function (e) {
        e.preventDefault();
        if(ckekCode){
            var data = {
                username: localStorage.getItem("username"),
                password: localStorage.getItem("password"),
                email: localStorage.getItem("email"),
                code:$('#code').val()
            }
            create(data);
        }else {
            $('#code_message').text("Code is not correct");
        }
    });
    $('#without_code_form').on('submit', function (e) {
        e.preventDefault();
        var data = {
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
            email: localStorage.getItem("email"),
            code:null
        }
        create(data);
    });


});
function FormValidation(){
    console.log("formval");

    var fn=$('#username').val();
    if(fn.trim().length < 4){
        alert('Username should be at least 4 characters');
        document.getElementById("username").style.borderColor = "red";
        $('#username').val(fn.trim());
        return false;
    } else{
        document.getElementById('username').style.borderColor = "green";
    }

    if( !isValidEmailAddress( $('#email').val() ) ) {
        alert('Email is not valid');
        document.getElementById("email").style.borderColor = "red";
        return false;
    }else {
        document.getElementById('email').style.borderColor = "green";
    }

    var fn=$('#pwd').val();
    if(fn.trim().length < 8){
        alert('Password should be at least 8 characters');
        document.getElementById("pwd").style.borderColor = "red";
        $('#pwd').val(fn.trim());
        return false;
    } else{
        document.getElementById('pwd').style.borderColor = "green";
    }
    console.log("true");
    return true;
}
function checkUserName() {
    var data = {
        username: $('#username').val()
    }
    $.ajax({
        type: 'POST',
        url: '/user/username',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
            if(response.responseText == "true"){
                checkEmail();
    } else{
        $('#message').text('This username already exists');
        $('#message').show();
        document.getElementById("username").style.borderColor = "red";
    }
}
]
});
}
function checkEmail() {
    var data = {
        email: $('#email').val()
    }
    $.ajax({
        type: 'POST',
        url: '/user/email',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
            if(response.responseText == "true"){
                    emailValidation();
    } else{
        $('#message').text('This email already exists');
        $('#message').show();
        document.getElementById("email").style.borderColor = "red";
    }
}
]
});
}
function emailValidation() {
    localStorage.setItem("username",$('#username').val());
    localStorage.setItem("email",$('#email').val());
    localStorage.setItem("password",$('#pwd').val());
    $('#sign_up_container').hide();
    $('#valid_email').show();
    sentCode();

}
function isValidEmailAddress(emailAddress) {
    var pattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;
    return pattern.test(emailAddress);
}
function sentMail() {
    $.ajax({
        type: 'POST',
        url: '/user/email',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            if(response.responseText == "true"){
        emailValidation();
    } else{
        $('#message').text('This email already exists');
        $('#message').show();
        document.getElementById("email").style.borderColor = "red";
    }
}
]
});
}
function cleanStorage() {
    localStorage.removeItem("username");
    localStorage.removeItem("email");
    localStorage.removeItem("password");
}
function sentCode() {
     var data = {
        email: localStorage.getItem("email")
    }
    $.ajax({
        type: 'POST',
        url: '/user/sentVerifyMail',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
            if(response.responseText == "true"){
                $('#code_sent').text('An email with a verification code was just sent');
    } else{
        $('#code_sent').text('Please, check your mail or try later');
        // $('#code').hide();
        // $('#code_sent').show();
        // $('#confirm').hide();
    }
}
]
});
}
function ckekCode() {
    var data = {
        email: localStorage.getItem("email"),
        code:$('#code').val()
    }
    $.ajax({
        type: 'POST',
        url: '/user/code',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
    if(response.responseText == "true"){
      return true;
    } else{
       return false;
    }
}
]
});
}
function create(data) {
    $.ajax({
        type: 'POST',
        url: '/user/create',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
    if(response.responseText == "true"){
        localStorage.setItem("Create","Account created and you can sign in");
        window.location.replace("login.html");

    } else{
        $('#code_message').text("Please, check code or try again later");
        $('#code_message').show();
    }
}
]
});
}