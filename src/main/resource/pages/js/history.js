const id = localStorage.getItem("current_ticket");
$(document).ready(function () {
    $("#button_login").hide();
    var id = localStorage.getItem("current_ticket");
    console.log(id);
    if (typeof id == 'undefined') {
        window.location.replace("index.html");
    }
    $.ajax({
        type: 'POST',
        url: '/session/status',
        dataType: 'json',
        complete: [
            (response) => {
                console.log(response.responseText);
    if(response.responseText == "true"){
        $.ajax({
            type: 'POST',
            url: `/ticket/history?ticketId=${id}`,
            dataType: 'json',
            complete: [
                (response) => {
                console.log(id);
                console.log(localStorage.getItem("current_ticket"));
                console.log(response.responseText);
        if(response.responseText != "false"){
            $('#hideAll').show();
            const obj = $.parseJSON(response.responseText);
            obj.forEach(i =>{
            var m = i.changedDate.monthValue;
            if (m < 10) {
                m = "0" + m;
            }
            var d = i.changedDate.dayOfMonth;
            if (d < 10) {
                d = "0" + d;
            }
            var h = i.changedDate.hour;
            if (h < 10) {
                h = "0" + h;
            }
            var mm = i.changedDate.minute;
            if (mm < 10) {
                mm = "0" + mm;
            }
            var ss = i.changedDate.second;
            if (ss < 10) {
                ss = "0" + ss;
            }
            var modDate = i.changedDate.year + "-" +m+ "-" + d + " " + h + ":" + mm + ":" + ss;

            $('#history_table').find('tbody').append("            <tr class='row'>\n" +
                "                <th class='col-xl-2 col-lg-2 col-md-2 col-sm-2 text_width_fix ' scope=\"row\">"+modDate+"</th>\n" +
                "                <td class='col-xl-2 col-lg-2 col-md-2 col-sm-2 text_width_fix'>"+i.userName+"</td>\n" +
                "                <td class='col-xl-2 col-lg-2 col-md-2 col-sm-2 text_width_fix'>"+i.filedName+"</td>\n" +
                "                <td class='col-xl-3 col-lg-3 col-md-3 col-sm-3 text_width_fix'>"+i.oldValue+"</td>\n" +
                "                <td class='col-xl-3 col-lg-3 col-md-3 col-sm-3  text_width_fix'>"+i.newValue+"</td>\n" +
                "            </tr>");
        });
        } else{
           window.location.replace("ticket_open_change.html");
        }
    }
    ]
    });

    } else{
        window.location.replace("ticket_open_change.html");
    }
}
]
});

});