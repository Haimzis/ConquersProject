var chatVersion = 0;
var refreshRate = 2000; //milli seconds
var USER_LOGOUT_URL = buildUrlWithContextPath("logout");
var LOGGED_USERS_URL = buildUrlWithContextPath("LoggedUsersStats");
var UPLOAD_XML_URL = buildUrlWithContextPath("UploadFile");

window.onload = function ()
{
    refreshLoginStatus();
    refreshUserList();
    setInterval(refreshUserList, refreshRate);
};

function uploadXML(eventOfLoad){
    var XMLFile = eventOfLoad.target.file[0];
    $.ajax({
        url: UPLOAD_XML_URL,
        data: {
            file: XMLFile
        },
        type: "POST",
        success: uploadXMLCallBack
    });
}
function uploadXMLCallBack(isXMLLoaded){

    if(isXMLLoaded){
        alert("Load game Success !!");
        refreshGamesList();
        clearFileInput();
    }
    else {
        alert("XML didn't load well")
    }
}
function refreshGamesList() {

}

function clearFileInput() {

}
function getUserName(){
    var result="";
    $.ajax
    ({
        async: false,
        url:LOGGED_USERS_URL,
        data: {
            action: "getLoggedUsername"
        },
        type: 'GET',
        success: function (userName) {
            result = userName;
        }
    });
    return result;
}
function refreshLoginStatus() {
    $.ajax
    ({
        url: LOGGED_USERS_URL,
        data: {
            action: "getLoggedUsername"
        },
        type: 'GET',
        success: refreshLoginStatusCallback
    });
}


function onLogoutClick() {
    $.get(USER_LOGOUT_URL,function logoutCallback() {
        window.location = "/UIWeb/index.html";
    });
}

function refreshLoginStatusCallback(username)
{
        $('.userNameSpan').text("Hello " + username + " Welcome to Conquers!");
}
//refresh connected users list
function refreshUserList() {
    $.ajax(
        {
            url: LOGGED_USERS_URL,
            data: {
                action: "getLoggedUsers"
            },
            type: 'GET',
            success: refreshUserListCallback
        }
    );
}

function refreshUserListCallback(users) {
    var usersTable = $('.usersTable tbody');
    //clear all current users
    usersTable.empty();

    $.each(users || [], function(index, username){
        console.log("Adding user #" + index + ": " + username);
        var tr = $(document.createElement('tr'));
        var td = $(document.createElement('td')).text(username);
        td.appendTo(tr);
        tr.appendTo(usersTable);
    });
}
