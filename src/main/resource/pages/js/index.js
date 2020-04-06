$(document).ready(function () {
    fetch("./footer.html")
        .then(response => {
        return response.text()
    })
.then(data => {
        document.querySelector("footer").innerHTML = data;
});

    localStorage.removeItem("current_project");
    $("#button_login").hide();
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
            $("#block_user").hide();
            if(response.responseText == "true"){
                  $('#user_block').show();
                    $('#project_manager').show();
                  $("#button_login").hide();
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
                    $('#name_for_user').text(obj.userName.substr(0, 8)+'...');
                }

            }
            ]
            });
           } else{
              $("#button_login").show();
                $('#user_block').hide();
             }
            }
        ]
    });


    $.ajax({
        type: 'POST',
        url: '/project/allProjects',
        dataType: 'json',
        complete: [
            (response) => {
            console.log(response.responseText);
              if(response.responseText != "false"){
               const obj = $.parseJSON(response.responseText);
                 obj.forEach(i =>{
                   $('#project').append(`<option value="${i.projectId}">
                                       ${i.projectName}
                                  </option>`);
                     });
                console.log($('#project').val());
                 localStorage.setItem("current_project",$('#project').val());
                showAllTickets();

              }
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

$(document).on("click", "#el_tik", function(){
    var v = $(this).data('value');
    console.log(v);
    localStorage.setItem("current_ticket",v);
});

$(function () {
    $('#button_all').on('submit', function (e) {
        console.log("prevent for butffffffffffton_all");
        $('#message_search').hide();
        // var mySpan = document.getElementById('message_search');
        // mySpan.style.display = "";

        $('#search_text').val(null);
        e.preventDefault();
        showAllTickets();
    });
    $('#button_search').on('submit', function (e) {
        console.log("prevent for button_search");
        e.preventDefault();
        var name =$('#search_text').val();
        name = name.trim();
        console.log(name);
        if(name.trim().length > 0){
            console.log("length oooooooooo > 0");
            $('#message_search').text(null);
            $('#message_search').hide();
                showAllTicketsByName(name);
        }else{
            console.log("show");
            $('#message_search').text("Empty value for search");
            $('#message_search').show();
        }
    });
    $('#project').on('change', function() {
        var projectId = this.value;
        localStorage.setItem("current_project",projectId);
        console.log(projectId);
        console.log(localStorage.getItem("current_project"));
        showAllTickets();
    });

});

function showAllTicketsByName(val){

    var data = {
        id: localStorage.getItem("current_project"),
        name: val,
        summary:$('#summary_checkbox').prop("checked")
    }
    $.ajax({
        type: 'POST',
        url: '/mainPage/allTicketsByName',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
    if(response.responseText === "false"){
        $('#message_search').text("Ticket is not found");
        $('#message_search').show();
    } else {
        const obj = $.parseJSON(response.responseText);
        fiilTicketFromJson(obj);
    }
}
]
});
}
function showAllTickets(){

    var data ={
        id: localStorage.getItem("current_project")
    }
    $.ajax({
        type: 'POST',
        url: '/ticket/canCreateTicket',
        dataType: 'json',
        data,
        complete: [
            (response) => {
            console.log(response.responseText);
    if(response.responseText == "true"){
        $("#create_ticket").show();
    }else {
        $("#create_ticket").hide();
    }
}
]
});

    $('#cont').html(null);
    $.ajax({
        type: 'POST',
        url: `/mainPage/allTickets?id=${localStorage.getItem("current_project")}`,
        dataType: 'json',
        complete: [
            (response) => {
            const obj = $.parseJSON(response.responseText);
    if(response.responseText != "false"){
        fiilTicketFromJson(obj);
    }else {
        console.log("error");
    }
    $('#hideAll').show();
}
]
});
}
function fiilTicketFromJson(obj){
    let html = '';
    obj.forEach(i => {
        var summary =replaceTag(i.summary);
        summary='<div class="card-text body-card-my" id="p_summary">'+summary+'</div>'
        let text = `
                        <div class="col-xl-4 col-md-6 pt-3">
                        <div class="card">
                        <h5 class="card-header title-card-my">${i.name}</h5>
                        <div class="card-body ">
                        <h5 class="card-title">${i.bugStatus.replace("_", " ")}</h5>
                        ${summary}
                        <a href="/ticket_open_change.html" id ="el_tik" data-value="${i.id}" class="btn btn-primary">Open ticket</a>
                        </div>
                        </div>
                        </div>
                        `
        html = html+text;
})
    $('#cont').html(html);

}
function replaceTag(val){
    console.log(val)
    var text =[];
    for (var i = 0, j = 0; i < val.length; i++,j++) {
        if (val[i] == '&' && val[i+1] =='#' && val[i+4] ==';') {
            text[j] = String.fromCharCode((val[i+2]+val[i+3]));
            i = i+4;
        }else {
            text[j] = val[i];
        }
    }
    console.log(text.join(""));
    return text.join("");

    //return ${text.join("")}</p>';
}
