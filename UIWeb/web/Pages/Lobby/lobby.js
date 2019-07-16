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
    var reader = new FileReader();
    var creatorName = getUserName();
    reader.onload = function () {
        var content = reader.result;
        $.ajax(
            {
            url: UPLOAD_XML_URL,
            data: {
                file: content,
                creator: creatorName
            },
            type: "POST",
            success: uploadXMLCallBack
        });
    }
    $.ajax // Getting creator's name.
    ({
        url: LOGGED_USERS_URL,
        data: {
            action: "getLoggedUsername"
        },
        type: 'GET',
        success: function (userName) {
            creatorName = userName;
            reader.readAsText(XMLFile);
        }
    });
}

function uploadXMLCallBack(loadStatus){

    if(loadStatus.isLoaded){
        alert("Load game Success !!");
        refreshGamesList();
        clearFileInput();
    }
    else {
        alert(loadStatus.errorMessage)
    }
}
function refreshGamesList() {
    $.ajax
    (
        {
            url: 'games',
            data: {
                action: 'gamesList'
            },
            type: 'GET',
            success: refreshGamesListCallback
        }
    )
}

function refreshGamesListCallback(gameManagers) {
    var gamesTable = $('.gamesTable tbody');
    gamesTable.empty();
    var gamesList = gameManagers;

    gamesList.forEach(function (game) {
        var tr = $(document.createElement('tr'));
        var tdGameNumber = $(document.createElement('td')).text(game.key);
        var tdGameName = $(document.createElement('td')).text(game.gameTitle);
        var tdCreatorName = $(document.createElement('td')).text(game.creatorName);
        var tdBoardSize = $(document.createElement('td')).text(game.rows + " X " + game.cols);
        var tdPlayerNumber = $(document.createElement('td')).text(game.registeredPlayers + " / " + game.requiredPlayers);
        var tdMovesNumber = $(document.createElement('td')).text(game.moves);

        tdGameNumber.appendTo(tr);
        tdGameName.appendTo(tr);
        tdCreatorName.appendTo(tr);
        tdBoardSize.appendTo(tr);
        tdPlayerNumber.appendTo(tr);
        tdMovesNumber.appendTo(tr);

        tr.appendTo(gamesTable);
    });

    var tr = $('.tableBody tr');
    for (var i = 0; i < tr.length; i++) {
        //tr[i].onclick = createGameDialog;
    }
}
function clearFileInput() {
    document.getElementById("fileInput").value = "";
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
