


var fix_names = [];
var st = false;
$(document).ready(function () {
    fetch("./footer.html")
        .then(response => {
        return response.text()
    })
.then(data => {
        document.querySelector("footer").innerHTML = data;
});
    tinymce.init({
        selector: "textarea"
    });
    $('#user_block').show();
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
            if(response.responseText == "true"){
                console.log("fasfas");
        $.ajax({
            type: 'POST',
            url: `/user/user`,
            dataType: 'json',
            complete: [
                (response) => {
                console.log(response.responseText);
        if(response.responseText == "false"){
            console.log("sssssssssss false user");
        }else {
            const obj = $.parseJSON(response.responseText);
            $('#name_for_user').val(obj.userName);
            console.log( $('#name_for_user').val());
            $('#button_all').hide();
            $('#message_search').hide();
            $('#button_search').hide();
            $('#button_login').hide();
            $('#user_block').show();
            $('#hideAll').show();
        }

    }
    ]
    });
        afterLoadPage();
    } else{
        window.location.replace("index.html");
    }
}
]
});

    $.ajax({
        type: 'POST',
        url:  `/ticket/userList?id=${localStorage.getItem("current_project")}`,
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
    const obj = $.parseJSON(response.responseText);
    var fixer =  $('#fixer');
    listApdate(fixer, obj);
}
]
});
    $("#log_out").click(function() {
        console.log("logOut");
        $.ajax({
            type: 'POST',
            url: `/user/logout`,
            dataType: 'json',
            complete: [
                (response) => {
                window.localStorage.clear();
        window.location.replace("index.html");
    }
    ]
    });
    });

});
    function FormValidation(){
        console.log("formval");
        var fn=$('#ticket_name').val();
        if(fn.trim().length < 5){
            alert('Title should be at least 5 characters');
            document.getElementById("ticket_name").style.borderColor = "red";
            $('#ticket_name').val(fn.trim());
            return false;
        } else{
            document.getElementById('ticket_name').style.borderColor = "green";
        }
        console.log("formval");
        var fn=$('#ticket_summary').val();
        if(fn.trim().length < 5){
            alert('Summary should be at least 5 characters');
            document.getElementById("ticket_summary").style.borderColor = "red";
            $('#ticket_summary').val(fn.trim());
            return false;
        } else{
            document.getElementById('ticket_summary').style.borderColor = "green";
        }
        console.log("formval");
        var fn=$('#status').val();
        if(fn.trim().length < 3){
            console.log("formval");
            document.getElementById("status").style.borderColor = "red";
        } else{
            document.getElementById('status').style.borderColor = "green";
        }
        console.log("formval");
        var fn=$('#priority').val();
        if(fn.trim().length < 3){
            console.log("formval");
            document.getElementById("priority").style.borderColor = "red";
            return false;
        } else{
            document.getElementById('priority').style.borderColor = "green";
        }
        console.log("formval");
        var fn=$('#severity').val();
        if(fn.trim().length < 3){
            console.log("formval");
            document.getElementById("severity").style.borderColor = "red";
            return false;
        } else{
            document.getElementById('severity').style.borderColor = "green";
        }
        console.log("ssssss " + fix_names);
        if(fix_names.length <1){
            console.log("formval");
            alert('You must choose at least one fixer');
            document.getElementById("fixer_block").style.borderColor = "red";
            return false;
        } else{
            document.getElementById('fixer_block').style.borderColor = "green";
        }

        console.log("true");
        return true;
    }

    $('#add_save').on('submit', function (e){
        console.log("prevent for save");
        e.preventDefault();
        var jsonString = JSON.stringify(fix_names);
        var data = {
            id:localStorage.getItem("current_project"),
            name: $('#ticket_name').val(),
            summary: $('#ticket_summary').val(),
            status: $('#status').val(),
            priority: $('#priority').val(),
            severity: $('#severity').val(),
            actualResult:$('#actual_result').val(),
            steptsToReproduce:$('#steps').val(),
            expectedResult:$('#expected_result').val(),
            fixers:jsonString
        }
        console.log(data);
        if (FormValidation()) {
            $.ajax({
                type: 'post',
                url: '/ticket/create',
                dataType: "json",
                data,
                contentType: "application/json; charset=utf-8",
                complete: [
                    (response) => {
                    console.log("create " + response.responseText);
            if(response.responseText != "false"){
                localStorage.setItem("current_ticket",response.responseText);
                window.location.replace("ticket_open_change.html");
            }else {
                $('#save_message').text("An error occurred, please try again later");
                $('#save_message').show();
            }
        }
        ]
        })
            ;
        } else {
            console.log("empty ")
        }
    });

function afterLoadPage(){
    loadStatusList();
    loadProrityList();
    loadSeverity();
}

function loadStatusList() {
    $.ajax({
        type: 'POST',
        url: '/ticket/status',
        dataType: 'json',
        complete: [
            (response) => {
            const obj = $.parseJSON(response.responseText);
    var status =  $('#status');
    listApdate(status, obj);
}
]
});
}

function loadProrityList() {
    $.ajax({
        type: 'POST',
        url: '/ticket/prority',
        dataType: 'json',
        complete: [
            (response) => {
            const obj = $.parseJSON(response.responseText);
    var priority =  $('#priority');
    listApdate(priority, obj);
}
]
});
}

function loadSeverity() {
    $.ajax({
        type: 'POST',
        url: '/ticket/severity',
        dataType: 'json',
        complete: [
            (response) => {
            const obj = $.parseJSON(response.responseText);
    var severity =  $('#severity');
    listApdate(severity, obj);
}
]
});
}

function listApdate(obj, json) {
    var val = obj.val();
    console.log(val);
    json.forEach(x => {
        x=x.replace("_", " ");
    if(val == x){
        obj.children("option:selected").remove();
        obj.append(`<option value="${x}">
                                 ${x}
                                 </option>`);
        obj.val(x);
    } else {
        obj.append(`<option value="${x}">
                                   ${x}
                                 </option>`);
    }
})
}

$(document).on("click", ".fixer_span_remove", function(){
    var item = $(this).parent('li').attr('id');
    console.log("383 current " + item);
    var index = fix_names.indexOf(item) ;
    if (index !== -1) fix_names.splice(index, 1);
    console.log(fix_names);
    $(this).parent('li').remove();
});

$(document).on("click", "#fixer_add", function(){
    var x = $('#fixer').val();
    if(x != null){
        var items = [],
            txt;
        $( '#fixer_block li span' ).each( function() {
            txt = $( this ).text();
            if ( $.inArray(txt, items ) === -1 ) {
                items.push(txt );
            }
        });
        var n = items.includes(x);
        if(n == false){
            fix_names.push(x);
            console.log("push " + fix_names);
            $('#fixer_block').append (`
                            <li id="${x}" class="fixer_name pb-3">
                                  <span class=" text-white">${x}</span>
                                  <button type="button" class="fixer_span_remove" >X</button>
                                </li>
                         `);
        }

    }
});
