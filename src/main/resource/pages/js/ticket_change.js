
const id = localStorage.getItem("current_ticket");
console.log(id);
var fix_names = [];
var st = false;
$(document).ready(function () {
    fetch("./footer.html")
        .then(response => {
        return response.text()
    }).then(data => {
        document.querySelector("footer").innerHTML = data;
});
    tinymce.init({
        selector: 'textarea'
    });

    $('#button_all').hide();
    $('#message_search').hide();
    $('#button_search').hide();
    $('#button_login').hide();
    $('#user_block').show();
    var myVar = localStorage.getItem("current_ticket");
    console.log(myVar);
    if (typeof myVar === 'undefined') {
        window.location.replace("index.html");
    }
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
            if(response.responseText == "true")
    {
        st = true;
        $('#ticket_history').show();
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
            console.log(obj);
            $('#name_for_user').val(obj.userName);
            localStorage.setItem("current_user_name",obj.userName);
            $('#add_comment').show();
        }
    }
    ]
    });
        var data ={
            id: localStorage.getItem("current_ticket")
        }
        $.ajax({
            type: 'POST',
            url: '/ticket/canUpdateTicket',
            dataType: 'json',
            data,
            complete: [
                (response) => {
                console.log(response.responseText);
        if(response.responseText == "true"){
            showElements()
        }
    }
    ]
    });

    }else{
        $('#button_login').show();
        $('#user_block').hide();
    }
}
]
})
    ;

    $.ajax({
        type: 'POST',
        url: `/ticket/autor?id=${id}`,
        dataType: 'json',
        complete: [
            (response) => {
            if(response.responseText != "false")
    {
        const obj = $.parseJSON(response.responseText);
        $('#author').val(obj.userName);
    }
}
]
})
    ;

    $.ajax({
        type: 'POST',
        url: `/ticket/ticket?id=${id}`,
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
    if (response.responseText != "false") {
        const obj = $.parseJSON(response.responseText);
        $('#ticket_name').val(obj.name);
        $('#ticket_summary').val(replaceTag(obj.summary));
        $('#steps').val(replaceTag(obj.steptsToReproduce));
        $('#expected_result').val(replaceTag(obj.expectedResult));
        $('#actual_result').val(replaceTag(obj.actualResult));
        var m = obj.createdDate.monthValue;
        if (m < 10) {
            m = "0" + m;
        }
        var d = obj.createdDate.dayOfMonth;
        if (d < 10) {
            d = "0" + d;
        }
        var h = obj.createdDate.hour;
        if (h < 10) {
            h = "0" + h;
        }
        var mm = obj.createdDate.minute;
        if (mm < 10) {
            mm = "0" + mm;
        }
        var ss = obj.createdDate.second;
        if (ss < 10) {
            ss = "0" + ss;
        }
        var createdDate = obj.createdDate.year + "-" + m + "-" + d + " " + h + ":" + mm + ":" + ss;
        $('#date_create').val(createdDate);

        var m = obj.modifiedDate.monthValue;
        if (m < 10) {
            m = "0" + m;
        }
        var d = obj.modifiedDate.dayOfMonth;
        if (d < 10) {
            d = "0" + d;
        }
        var h = obj.modifiedDate.hour;
        if (h < 10) {
            h = "0" + h;
        }
        var mm = obj.modifiedDate.minute;
        if (mm < 10) {
            mm = "0" + mm;
        }
        var ss = obj.modifiedDate.second;
        if (ss < 10) {
            ss = "0" + ss;
        }
        var modDate = obj.modifiedDate.year + "-" + m + "-" + d + " " + h + ":" + mm + ":" + ss;
        $('#date_change').val(modDate);
        $('#status').append(`<option value="${obj.bugStatus.replace("_"," ")}">
                                       ${obj.bugStatus}
                                  </option>`);
        $('#severity').append(`<option value="${obj.severity}">
                                       ${obj.severity}
                                  </option>`);
        $('#priority').append(`<option value="${obj.priority}">
                                       ${obj.priority}
                                  </option>`);
        $('#priority').val(obj.priority);
    } else {
        // location another
    }
    $('#hideAll').show();
    afterLoadPage();
}

]
})
    ;
    $.ajax({
        type: 'POST',
        url: `/ticket/fixer?id=${id}`,
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
    const obj = $.parseJSON(response.responseText);
    if(response.responseText != "false"){
        obj.forEach(x => {
            let but = '';
        if (st == true) {
            but = but + '<button type="button" class="fixer_span_remove" >X</button>';
        } else {
            but = but + '<button type="button" class="fixer_span_remove"disabled>X</button>';
        }
        fix_names.push(x);
        console.log("pushed " + fix_names);
        $('#fixer_block').append(`
                            <li id="${x}" class="fixer_name pb-3">
                                  <span class=" text-white">${x}</span> ${but}
                                </li>
                         `);
    })
    }
}
]
})
    ;
    $.ajax({
        type: 'POST',
        url: `/ticket/comments?id=${id}`,
        dataType: 'json',
        complete: [
            (response) => {
            let html = '';
    const obj = $.parseJSON(response.responseText);
    console.log("rwwwr");
    $.each(obj, function (key, value) {
        $.each(value, function (inKey, inValue) {
            var time = key.replace("T", " ");
            let text = `
                                 <h5 class="mt-0 text-white" id ="comment_autor">${inKey}</h5>
                                  <textarea class="form-control" onclick="textAreaAdjust(this)" style="overflow:hidden"  readonly>${inValue.value}</textarea>
                                  <span class="text-grey">Comment added: ${time}</span>
                                  <br><br>
                                   `
            html = html + text;
        })
    })
    $('#comment').html(html);
    tinymce.init({
        selector: 'textarea'
    });
}
]
})
    ;

    $.ajax({
        type: 'POST',
        url: `/ticket/userList?id=${localStorage.getItem("current_project")}`,
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
            if(response.responseText != 'false') {
                const obj = $.parseJSON(response.responseText);
                var fixer = $('#fixer');
                listApdate(fixer, obj);
            }
}
]
})
    ;
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

}add_save);

function FormValidation() {

    var fn = $('#ticket_name').val();
    if (fn.trim().length < 5) {
        alert('Title should be at least 5 characters');
        document.getElementById("ticket_name").style.borderColor = "red";
        $('#ticket_name').val(fn.trim());
        return false;
    } else {
        document.getElementById('ticket_name').style.borderColor = "green";
    }

    var fn = $('#ticket_summary').val();
    if (fn.trim().length < 5) {
        alert('Summary should be at least 5 characters');
        document.getElementById("ticket_summary").style.borderColor = "red";
        $('#ticket_summary').val(fn.trim());
        return false;
    } else {
        document.getElementById('ticket_summary').style.borderColor = "green";  // цвет поменять
    }

    var fn = $('#status').val();
    if (fn.trim().length < 3) {
        document.getElementById("status").style.borderColor = "red";
    } else {
        document.getElementById('status').style.borderColor = "green";
    }

    var fn = $('#priority').val();
    if (fn.trim().length < 3) {
        document.getElementById("priority").style.borderColor = "red";
        return false;
    } else {
        document.getElementById('priority').style.borderColor = "green";
    }

    var fn = $('#severity').val();
    if (fn.trim().length < 3) {
        document.getElementById("severity").style.borderColor = "red";
        return false;
    } else {
        document.getElementById('severity').style.borderColor = "green";
    }

    if (fix_names.length < 1) {
        alert('You must choose at least one fixer');
        document.getElementById("fixer_block").style.borderColor = "red";
        return false;
    } else {
        document.getElementById('fixer_block').style.borderColor = "green";
    }


    return true;
}

$(function () {
    $('#add_comment').on('submit', function (e) {
        console.log("prevent for comment");
        e.preventDefault();
        var comment = $('#new_comment').val();
        comment = comment.trim();
        if (comment.length < 5) {
            alert('Comment should be at least 5 characters');
            document.getElementById("new_comment").style.borderColor = "red";
        } else {
            document.getElementById("new_comment").style.borderColor = "green";
            var data = {
                ticketId: id,
                comment: comment
            }
            $.ajax({
                type: 'post',
                url: '/ticket/comments/add',
                dataType: "json",
                data,
                contentType: "application/json; charset=utf-8",
                complete: [
                    (response) => {
                    comment: $('#new_comment').val("");
            console.log(response.responseText);
            const obj = $.parseJSON(response.responseText);
            $.each(obj, function (inKey, inValue) {
                var time = inKey.replace("T", " ");
                let text = `
                                                <h5 class="mt-0" id ="comment_autor">${localStorage.getItem("current_user_name")}</h5>
                                                <textarea class="form-control" onclick="textAreaAdjust(this)" style="overflow:hidden"  readonly>${inValue.value}</textarea>
                                               <span>Comment added: ${time}</span>
                                               <br><br>
                                                `
                $('#comment').append(text);
                tinymce.init({
                    selector: 'textarea'
                });
                $('#new_comment').val(null);
            })
        }
        ]
        })
            ;
        }
    });
    $('#add_save').on('submit', function (e) {
        console.log("prevent for save");
        e.preventDefault();
        var jsonString = JSON.stringify(fix_names);
        var data = {
            id: localStorage.getItem("current_ticket"),
            name: $('#ticket_name').val(),
            summary: $('#ticket_summary').val(),
            status: $('#status').val(),
            priority: $('#priority').val(),
            severity: $('#severity').val(),
            actualResult: $('#actual_result').val(),
            steptsToReproduce: $('#steps').val(),
            expectedResult: $('#expected_result').val(),
            fixers: jsonString
        }
        console.log(data);
        if (FormValidation()) {
            $.ajax({
                type: 'post',
                url: '/ticket/update',
                dataType: "json",
                data,
                contentType: "application/json; charset=utf-8",
                complete: [
                    (response) => {
                    console.log(response.responseText);
            if(response.responseText =="true"){
                $('#save_message').text("Information saved");
                $('#save_message').show();
            }
            else{
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

});

function showElements() {
    $('#ticket_name').attr('readonly', false);
    $('#ticket_summary').attr('readonly', false);
    $('#status').attr('readonly', false);
    $('#severity').attr('readonly', false);
    $('#priority').attr('readonly', false);
    $('#status').attr('disabled', false);
    $('#severity').attr('disabled', false);
    $('#priority').attr('disabled', false);
    $('#fixer').attr('disabled', false)
    $('#fixer_add').attr('disabled', false);
    $('#steps').attr('readonly', false);
    $('#fixer').attr('readonly', false);
    $('#expected_result').attr('readonly', false);
    $('#actual_result').attr('readonly', false);
    $("#save").show();
    $("#comment").show();
    $('#add_comment').show();

}

function afterLoadPage() {
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
    var status = $('#status');
    listApdate(status, obj);
}
]
})
    ;
}

function loadProrityList() {
    $.ajax({
        type: 'POST',
        url: '/ticket/prority',
        dataType: 'json',
        complete: [
            (response) => {
            const obj = $.parseJSON(response.responseText);
    var priority = $('#priority');
    listApdate(priority, obj);
}
]
})
    ;
}

function loadSeverity() {
    $.ajax({
        type: 'POST',
        url: '/ticket/severity',
        dataType: 'json',
        complete: [
            (response) => {
            const obj = $.parseJSON(response.responseText);
    var severity = $('#severity');
    listApdate(severity, obj);
}
]
})
    ;
}

function listApdate(obj, json) {
    var val = obj.val();
    console.log(val);
    json.forEach(x => {
        x = x.replace("_", " ");
    if (val == x) {
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
function replaceTag(val){
    console.log(val)
    var text =[];
    for (var i = 0, j = 0; i < val.length; i++,j++) {
        if (val[i] == '&' && val[i+1] =='#' && (val[i+4] ==';' || val[i+5] ==';')) {
            if(val[i+4] ==';' ){
                text[j] = String.fromCharCode((val[i+2]+val[i+3]));
                i = i+4;
            }else {
                text[j] = String.fromCharCode((val[i+2]+val[i+3]+val[i+4]));
                i = i+5;
            }
        }else {
            text[j] = val[i];
        }
    }
    console.log(text.join(""));
    return text.join("");
}
$(document).on("click", ".fixer_span_remove", function () {
    var item = $(this).parent('li').attr('id');
    console.log("383 current " + item);
    var index = fix_names.indexOf(item);
    if (index !== -1) fix_names.splice(index, 1);
    console.log(fix_names);
    $(this).parent('li').remove();
});

$(document).on("click", "#fixer_add", function () {
    var x = $('#fixer').val();
    if (x != null) {
        var items = [],
            txt;
        $('#fixer_block li span').each(function () {
            txt = $(this).text();
            if ($.inArray(txt, items) === -1) {
                items.push(txt);
            }
        });
        var n = items.includes(x);
        if (n == false) {
            fix_names.push(x);
            console.log("push " + fix_names);
            $('#fixer_block').append(`
                            <li id="${x} " class="fixer_name pb-3">
                                  <span >${x}</span>
                                  <button type="button" class="fixer_span_remove" >X</button>
                                </li>
                         `);
        }
    }
});
