var chatVersion = 0;
var refreshRate = 2000; //milli seconds
var USER_LOGOUT_URL = buildUrlWithContextPath("logout");
var LOGGED_USERS_URL = buildUrlWithContextPath("LoggedUsersStats");
var UPLOAD_XML_URL = buildUrlWithContextPath("uploadFile");
var GAMES_LIST = buildUrlWithContextPath("games");

window.onload = function ()
{
    $('#fileUploader').on('change' , uploadFileEvent); //add listener
    refreshLoginStatus();
    refreshUserList();
    setInterval(refreshUserList, refreshRate);
    setInterval(refreshGamesList , refreshRate);
};

function uploadXML(data){
    $.ajax(
        {
        url: UPLOAD_XML_URL,
        type: "POST",
        processData: false,
        contentType: false,
        dataType: 'json',
        cache: false,
        data:data,
        success: uploadXMLCallBack
    });
}
function uploadFileEvent(event)
{
    event.stopPropagation();
    event.preventDefault();
    var file = event.target.files[0];
    var data = new FormData();
    data.append('xml' , file);
    uploadXML(data);
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
            url: GAMES_LIST,
            data: {
                action: 'roomsList'
            },
            type: 'GET',
            success: refreshGamesListCallback
        }
    )
}

function refreshGamesListCallback(rooms) {
    var gamesTable = $('.gamesTable tbody');
    var len = rooms.length;
    gamesTable.empty();
    
    for(i = 0 ;i < len ; i++) {
        var manager = rooms[i];
        var tr = $(document.createElement('tr'));
        var tdGameNumber = $(document.createElement('td')).text(manager.id);
        var tdGameName = $(document.createElement('td')).text(manager.gameTitle);
        var tdCreatorName = $(document.createElement('td')).text(manager.creatorName);
        var tdBoardSize = $(document.createElement('td')).text(manager.rows + " X " + manager.cols);
        var tdPlayerNumber = $(document.createElement('td')).text(manager.registeredPlayers + " / " + manager.requiredPlayers);
        var tdMovesNumber = $(document.createElement('td')).text(manager.moves);

        tdGameNumber.appendTo(tr);
        tdGameName.appendTo(tr);
        tdCreatorName.appendTo(tr);
        tdBoardSize.appendTo(tr);
        tdPlayerNumber.appendTo(tr);
        tdMovesNumber.appendTo(tr);

        tr.appendTo(gamesTable);
        
    }
    var tr = $('.tableBody tr');
    for (var i = 0; i < tr.length; i++) {
        tr[i].onclick = createGameDialog;
    }
}
function clearFileInput() {
    document.getElementById("fileUploader").value = "";
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

function createGameDialog(event) {
    var td = event.currentTarget.children[0];
    var number = td.innerText;
    $.ajax
    (
        {
            url: GAMES_LIST,
            data: {
                action: 'roomDetails',
                id: number
            },
            type: 'GET',
            success: createGameDialogCallback
        }
    )
}

function createGameDialogCallback(json) {
    var div = $('.dialogDiv')[0];
    div.style.display = "block";
    var playersNamesDiv = $('.playersNames');
    var i;
    var key = json.id;
    var creatorName = json.creatorName;
    var gameName = json.gameTitle;
    var boardSize = json.rows + " X " + json.cols;
    var rounds = json.moves;
    var playerNumber = json.registeredPlayers + " / " + json.requiredPlayers;

    $('.key').text("Game id: " + key + ".");
    $('.creatorName').text("Game Creator: " + creatorName + ".");
    $('.gameName').text("Game Title: " + gameName);
    $('.boardSize').text("Map size: " + boardSize);
    $('.moves').text("Rounds number: " + rounds);
    $('.playerNumber').text("Players : " + playerNumber);
    for (i = 0; i < json.registeredPlayers; i++) {
        var playerDiv = $(document.createElement('div'));
        playerDiv.addClass('playerDiv');
        playerDiv.appendTo(playersNamesDiv);
    }

    var playerDivs = $('.playerDiv');
    for (i = 0; i < json.registeredPlayers; i++) {
        playerDivs[i].innerHTML = (+i + 1) + '. ' + json.activePlayers[i].playerName + '.';
    }

    createGameBoard(json.rows, json.cols);
}
//TODO: I need you to send whole information about the board and not just the cols and rows -
// that was in the gridler and its not enough in this game
function createGameBoard(rows,cols){
    var board = $('.board');
    board.contents().remove();

    var id_index= 1;
    for(var i=0; i<rows; i++){
        var rowTable =$(document.createElement('tr'));
        rowTable.addClass('row');
        rowTable.appendTo(board);
        for(var j=0;j<cols;j++){
            var territorySquare =$(document.createElement('td'));
            territorySquare.addClass('Territory');

            var territoryData = $(document.createElement('div'));
            territoryData.addClass('territoryDataDiv');

            //territory data members
            var territoryID = $(document.createElement('div'));
            territoryID.addClass('id_Data');
            territoryID.text(id_index);

            territoryData.append(territoryID);
            territoryData.appendTo(territorySquare);
            territorySquare.appendTo(rowTable);
            id_index++;
        }
    }
}
function removeGameDialog() {
    $('.dialogDiv')[0].style.display = "none";
}

function joinGameClicked() {
    var name = getUserName();
    var roomId = getRoomId();
    $.ajax
    (
        {
            url: GAMES_LIST,
            data: {
                action: 'joinRoom',
                user: name,
                roomId: roomId
            },
            type: 'GET',
            success: joinGameClickedCallback
        }
    );
}

function joinGameClickedCallback(json) {

    if (json.isLoaded)
    {
        window.location = "../GameRoom/gameroom.html";
    }
    else {
        alert(json.errorMessage);
    }
}

function getRoomId() {
    var string = $('.key').text();
    var result = +0;
    var i = 9;
    var temp = +string[i];
    while (!isNaN(temp)) // while temp is a number..
    {
        result = result * 10 + temp;
        i++;
        temp = +string[i];
    }
    return result;
}

