var members_names = [];

$(document).ready(function () {
 fetch("./footer.html")
            .then(response => {
            return response.text()
        }).then(data => {
            document.querySelector("footer").innerHTML = data;
    });
    $('#hideAll').show();
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
    if(response.responseText == "true") {
        $('#hideAll').show();
    }else {
        window.location.replace("index.html");
    }
    }
    ]
    });
    // $.ajax({
    //     type: 'POST',
    //     url: '/project/authorProject',
    //     dataType: 'json',
    //     complete: [
    //         (response) => {
    //         console.log(response.responseText);
    // if(response.responseText != "false"){
    //     const obj = $.parseJSON(response.responseText);
    //     obj.forEach(i =>{
    //         $('#project_list').append(`<option value="${i.projectId}">
    //                                    ${i.projectName}
    //                               </option>`);
    // });
    //     console.log($('#project_list').val());
    //
    // }
    // }
    // ]
    // });
    $.ajax({
        type: 'POST',
        url:  `/user/allUsers`,
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
    const obj = $.parseJSON(response.responseText);
    var members =  $('#member');
    listApdate(members, obj);
}
]
});
});

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
$(document).on("click", "#member_add", function(){
    var x = $('#member').val();
    if(x != null){
        var items = [],
            txt;
        $( '#member_block li span' ).each( function() {
            txt = $( this ).text();
            if ( $.inArray(txt, items ) === -1 ) {
                items.push(txt );
            }
        });
        var n = items.includes(x);
        if(n == false){
            members_names.push(x);
            console.log("push " + members_names);
            $('#member_block').append (`
                            <li id="${x}" class="member_name pb-3">
                                  <span class=" text-white">${x}</span>
                                  <button type="button" class="member_span_remove" >X</button>
                                </li>
                         `);
        }

    }
});
$(document).on("click", ".member_span_remove", function(){
    var item = $(this).parent('li').attr('id');
    console.log("383 current " + item);
    var index = members_names.indexOf(item) ;
    if (index !== -1) members_names.splice(index, 1);
    console.log(members_names);
    $(this).parent('li').remove();
});

$('#add_save').on('submit', function (e){
    console.log("prevent for save");
    e.preventDefault();
    var jsonString = JSON.stringify(members_names);
    var data = {
        name: $('#project_name').val(),
        type:$('#type_checkbox').prop("checked"),
        members:jsonString
    }
    console.log(data);
    if (FormValidation()) {
            console.log("OK")
        var data = {
            name: $('#project_name').val().trim()
        }
        $.ajax({
            type: 'post',
            url: '/project/checkName',
            dataType: "json",
            data,
            contentType: "application/json; charset=utf-8",
            complete: [
                (response) => {
                console.log("create " + response.responseText);
        if(response.responseText != "false"){
            var jsonString = JSON.stringify(members_names);
            var data = {
                name: $('#project_name').val().trim(),
                members:jsonString,
                type:$('#type_checkbox').prop("checked")
            }
            $.ajax({
                type: 'post',
                url: '/project/create',
                dataType: "json",
                data,
                contentType: "application/json; charset=utf-8",
                complete: [
                    (response) => {
                    console.log("create " + response.responseText);
            if (response.responseText != "false") {
                $('#save_message').text("Project added");
                $('#save_message').show();
                clear();
            } else {
                $('#save_message').text("An error occurred, please try again later");
                $('#save_message').show();
            }
        }
        ]
        });
        }else {
            $('#save_message').text("Project with the same name exist in system, please change name");
            $('#save_message').show();
        }
    }
    ]
    });



    }
});
function FormValidation() {
    console.log("formval");
    var fn = $('#project_name').val();
    if (fn.trim().length < 5) {
        alert('Title should be at least 5 characters');
        document.getElementById("project_name").style.borderColor = "red";
        $('#project_name').val(fn.trim());
        return false;
    } else {
        document.getElementById('project_name').style.borderColor = "green";
    }
    console.log("ssssss " + members_names);
    if(members_names.length <1){
        console.log("formval");
        alert('You must choose at least one member');
        document.getElementById("member_block").style.borderColor = "red";
        return false;
    } else{
        document.getElementById('member_block').style.borderColor = "green";
    }
    console.log("true");
    return true;
}
//$(function () {
//     $('#project_list').on('change', function () {
//         var projectId = this.value;
//         console.log(projectId);
//         if(projectId.trim().length >0){
//             showProject(projectId);
//             //save
//         }else {
//             clear();
//         }
//
//     });
// });
function showProject(val) {
    var data ={
        id:val
    }
    $.ajax({
        type: 'POST',
        url: '/project/project',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
    if(response.responseText != "false") {
        const obj = $.parseJSON(response.responseText);
        $('#project_name').val(obj.projectName);
        $.ajax({
            type: 'POST',
            url: `/project/memberList`,
            data,
            dataType: 'json',
            complete: [
                (response) => {
                console.log(response.responseText);
        const obj = $.parseJSON(response.responseText);
        if(response.responseText != "false"){
            clear();
            obj.forEach(x => {
                let but = '';
                but = but + '<button type="button" class="member_span_remove">X</button>';
            members_names.push(x);
            console.log("pushed " + members_names);
            $('#project_name').val($('#project_list').val());
            $('#button_save').text('Save');
            $('#member_block').append(`
                            <li id="${x}" class="member_name pb-3">
                                  <span class=" text-white">${x}</span> ${but}
                                </li>
                         `);
        })
        }
    }
    ]
    })
        ;
    }
    }
    ]
    });
}
function clear() {
    $('#button_save').text('Create');
    $('#project_name').val(null);
    members_names= [];
    $('#member_block').empty();

}