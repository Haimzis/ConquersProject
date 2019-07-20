var LOGGED_USERS_URL = buildUrlWithContextPath("LoggedUsersStats");
var CURR_GAME = buildUrlWithContextPath("singleGame");
var GAMES_LIST = buildUrlWithContextPath("games");
var status;
var isMyTurn = false;
var refreshRate = 2000; //milli seconds
var roundNumber;
var gameTitle;
var initialFunds;
var totalCycles;
var unitData;
var territoryMapData;
var activePlayers;
var interval;
var showWinner;
var selectedTerritoryId;
var maxPlayers;

window.onload = function () {
    updateWelcomeUsernameDetail();
    getGameDetails();
    setInterval(updateOnlineUsers, refreshRate);
    setInterval(gameStatus, refreshRate);
    setInterval(updateRemainRounds,refreshRate);
};

function updateWelcomeUsernameDetail(){
    $.ajax
    ({
        url: LOGGED_USERS_URL,
        data:{
            action: "getLoggedUsername"
        },
        type: 'GET',
        success: function updateWelcomeUsernameDetailCallBack(userName){
            $('.userNameSpan').text('Hello, '+ userName + " enjoy playing.");
        }
    })
}

function onLeaveGameClick()
{
    $.ajax
    ({
        async: false,
        url: CURR_GAME,
        data: {
            action: "leaveGame"
        },
        type: 'GET',
        success: function() {
            window.location = "../Lobby/lobby.html";
        }
    });
}

function gameStatus()
{
    $.ajax
    (
        {
            async: false,
            url: CURR_GAME,
            data:
                {
                    action: 'gameStatus'
                },
            type: 'GET',
            success: handleStatus
        }
    )
}

function handleStatus(json) {
    var newStatus = json.status;
    var playerTurn = json.currentPlayerTurnName;
    switch(newStatus)
    {
        case 'WaitingForPlayers':
            status = newStatus;
            $('.currentPlayerName')[0].innerHTML = playerTurn;
            break;
        case 'Running':
            if (status === 'WaitingForPlayers') {
                alert("Game started");
                startGame();
            }
            status = newStatus;
            break;
        case "Finished":
            isMyTurn = false;
            if (showWinner) {
                //showEndGameDialog();
                showWinner = false;
            }
            status = newStatus;
            break;
    }
    $('.gameStatus').text('Game status: ' + status);
}

function startGame() {
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'startGame'
            },
            type: 'GET',
            success: startGameCallBack
        }
    )
}
function startGameCallBack() {
    $('.currentPlayerName')[0].innerHTML = activePlayers[0].playerName;
    enableButtons();
    enableBoard();
    setInterval(createOwnPlayerStats,refreshRate);
    setInterval(createOtherPlayersStats , refreshRate);

}

function getGameDetails() {
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'singleGameDetails'
            },
            type: 'GET',
            success: setGameDetails
        }
    )
}

function updateOnlineUsers() {
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'singleGameOnlinePlayers'
            },
            type: 'GET',
            success: updateOnlineUsersCallBack
        }
    )
}

function updateOnlineUsersCallBack(players) {
    activePlayers = players;
    var usersList = $("#usersList");
    usersList.contents().remove();

    activePlayers.forEach(function (player) {
        var playerLi = $(document.createElement('li'));
        playerLi.attr('PlayerName', player.playerName);
        playerLi.addClass('onlinePlayer');
        playerLi.text(player.playerName);
        playerLi.appendTo(usersList);
    });
    updateRegisteredPlayersSpan();
}
//TODO: Enjoy. fix the error - it supposed to use the information that you toke from the getGameDetails.
function updateRemainRounds(){
    var remainRounds = totalCycles - roundNumber;
    $('.roundsLeft').text("Rounds Left: "+remainRounds);
}
//TODO: Enjoy. fix the error - it supposed to use the information that you toke from the getGameDetails.
function updateRegisteredPlayersSpan(){
    var activePlayersAmount = Object.keys(activePlayers).length;
    $('.registeredPlayers').text(activePlayersAmount);
}
//TODO: Enjoy. fix the error - it supposed to use the information that you toke from the getGameDetails.
function updateRequiredPlayersSpan(){
    var requiredPlayersAmount = maxPlayers;
    $('.requiredPlayers').text(requiredPlayersAmount);
}
function setGameDetails(data)  {
    maxPlayers = data.maxPlayers;
    roundNumber = data.roundNumber;
    gameTitle = data.gameTitle;
    initialFunds = data.initialFunds;
    totalCycles = data.totalCycles;
    unitData = data.unitMap;
    territoryMapData = data.territoryMap;
    createGameBoard(data);
    disableBoard();
    disableButtons();
    updateRequiredPlayersSpan();
}

function disableBoard() {
    $(".board").prop('disabled',true);
}

function disableButtons() {
    $(".actions").prop('disabled',true);
}
function enableBoard() {
    $(".board").prop('disabled',false);
}

function enableButtons() {
    $(".actions").prop('disabled',false);
}
//TODO: Ran create this action too, read the url message that I left for you.
function createOtherPlayersStats(){
    $.ajax({
        url: CURR_GAME,
        data:{
            action: "getOtherPlayersStats"
        },
        type: 'GET',
        success: createOtherPlayersStatsTable
    })
}
function createOtherPlayersStatsTable(data){
    var otherPlayersArr = data;
    var sizeOfArray = otherPlayersArr.length;
    $('#otherPlayerTable').empty();

    var headingRow = $(document.createElement('tr'));
    var userNameHeadCol=$(document.createElement('th'));
    userNameHeadCol.text("Username");
    var fundsHeadCol=$(document.createElement('th'));
    fundsHeadCol.text("Turings");
    var colorHeadCol=$(document.createElement('th'));
    colorHeadCol.text("Color");
    headingRow.append(userNameHeadCol,fundsHeadCol,colorHeadCol);
    $('#otherPlayerTable > tbody:last-child').append(headingRow);

    for(var i=0 ;i< sizeOfArray;i++) {
        var otherPlayerStatsRow = $(document.createElement('tr'));
        otherPlayerStatsRow.attr('PlayerID', otherPlayersArr[i].ID); //maybe j should start from 1
        var userNameCol = $(document.createElement('td'));
        var otherUserName = $(document.createElement('div'));
        otherUserName.addClass('otherUserName');
        otherUserName.text(otherPlayersArr[i].playerName);
        otherUserName.appendTo(userNameCol);

        var fundsCol = $(document.createElement('td'));
        var otherFunds = $(document.createElement('div'));
        otherFunds.addClass('otherFunds');
        otherUserName.text(otherPlayersArr[i].funds);
        otherFunds.appendTo(fundsCol);

        var colorCol = $(document.createElement('td'));
        var otherColor = $(document.createElement('div'));
        otherColor.addClass('otherColor');
        otherColor.css("background-color", otherPlayersArr[i].color);
        otherColor.appendTo(colorCol);

        $('#otherPlayerTable > tbody:last-child').append(otherPlayerStatsRow);
    }
}
//TODO: Ran make the action in the right servlet and call this function from the onload.
function createOwnPlayerStats(){
    $.ajax({
        url: CURR_GAME,
        data:{
            action: "getOwnPlayerStats"
        },
        type: 'GET',
        success: createOwnPlayerStatsTable
    })
}
//this function gets player object from the servlet
function createOwnPlayerStatsTable(PlayerObject){
    var ownPlayerStatsRow = $(document.createElement('tr'));

    var userNameCol =$(document.createElement('td'));
    var ownUserName = $(document.createElement('div'));
    ownUserName.addClass('ownUserName');
    ownUserName.text(PlayerObject.playerName);
    ownUserName.appendTo(userNameCol);

    var fundsCol =$(document.createElement('td'));
    var ownFunds = $(document.createElement('div'));
    ownFunds.addClass('ownFunds');
    ownUserName.text(PlayerObject.funds);
    ownFunds.appendTo(fundsCol);

    var colorCol =$(document.createElement('td'));
    var ownColor = $(document.createElement('div'));
    ownColor.addClass('ownColor');
    ownColor.css("background-color",PlayerObject.color);
    ownColor.appendTo(colorCol);

    ownPlayerStatsRow.append(userNameCol,fundsCol,colorCol);
    $('#ownPlayerTable > tbody:last-child').append(ownPlayerStatsRow);
}

function createGameBoard(gameBoardData){
    var board = $('.boardBody');
    board.contents().remove();
    var rows = gameBoardData.rows;
    var columns= gameBoardData.columns;

    var id_index= 1;
    for(var i=0; i<rows; i++){
        var rowTable =$(document.createElement('tr'));
        rowTable.addClass('row');
        rowTable.appendTo(board);
        for(var j=0;j<columns;j++){
            var territorySquare =$(document.createElement('td'));
            territorySquare.addClass('Territory');
            territorySquare.attr('TerritoryID', gameBoardData.territoryMap[id_index].ID); //maybe j should start from 1
            var territoryData = $(document.createElement('div'));
            territoryData.addClass('territoryDataDiv');

            //territory data members
            var territoryID = $(document.createElement('div'));
            territoryID.addClass('id_Data');
            territoryID.text("ID: " + gameBoardData.territoryMap[id_index].ID);

            var territoryArmyThreshold = $(document.createElement('div'));
            territoryArmyThreshold.addClass('armyThreshold_Data');
            territoryArmyThreshold.text("Army Threshold: " + gameBoardData.territoryMap[id_index].armyThreshold);

            var territoryProfit = $(document.createElement('div'));
            territoryProfit.addClass('profitData');
            territoryProfit.text("Profit: " + gameBoardData.territoryMap[id_index].profit);

            territoryData.append(territoryID,territoryArmyThreshold,territoryProfit);
            territoryData.appendTo(territorySquare);
            territorySquare.appendTo(rowTable);
            id_index++;
        }
    }

    var tr = $('.boardBody tr td');
    for (var i = 0; i < tr.length; i++) {
        tr[i].onclick = setSelectedTerritory;
    }
}
function setSelectedTerritory(territorySquare) {
    var territoryId = territorySquare.currentTarget.getAttribute('territoryid');
    selectedTerritoryId = territoryId;
    console.log("In set selected");
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'selectTerritory',
                id: territoryId
            },
            type: 'GET'
        }
    );
    checkTerritory();
}
function checkTerritory() {
    console.log("In check territory");
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'checkTerritory'
            },
            type: 'GET',
            success: checkTerritoryCallBack
        }
    )
}

function checkTerritoryCallBack(result) {
    console.log("In check territory callback");
    if(result.isBelongToCurrentPlayer) {
        openOwnTerritoryPopup();
    } else {
        if(result.isValid) {
            if(result.isConquered) {
                openAttackPopup();
            } else {
                openNeutralPopup();
            }
        } else {
            alert(result.message);
        }
    }
}
function openOwnTerritoryPopup() {
    showPopUp();
    var threshHold = territoryMapData[selectedTerritoryId].armyThreshold;
    var profit = territoryMapData[selectedTerritoryId].profit;
    var currentFirePower = territoryMapData[territoryMapData].conquerArmyForce.totalPower;
    var maxFirePower = territoryMapData[territoryMapData].conquerArmyForce.potentialTotalPower;
    var mBody = $('.modal-body');
    var p = $(document.createElement('p'));
    p.text("Threshold: " + threshHold);
    p.appendTo(mBody);
    p.text("Profit: " + profit);
    p.appendTo(mBody);
    p.text("Current Firepower on territory: " + currentFirePower);
    p.appendTo(mBody);
    p.text("Maximum firepower available: " + maxFirePower);
    p.appendTo(mBody);
    mBody = $('.modal-footer');
    p = $(document.createElement('button'));
    p.addClass("rehabilitateBtn");
    p.innerHTML = "Rehabilitate Army";
    p.appendTo(mBody);
    p = $(document.createElement('button'));
    p.addClass("addUnitsBtn");
    p.innerHTML = "Add units";
    p.appendTo(mBody);
}

function openAttackPopup() {

}

function openNeutralPopup() {
    showPopUp();
    var threshHold = territoryMapData[selectedTerritoryId].armyThreshold;
    var profit = territoryMapData[selectedTerritoryId].profit;
    var mHeader = $('.modal-header');
    var mHeaderTitle = $('#headerTitle');
    var mBodyTitle = $('#mBodyTitle');
    var mBody = $('.modal-body');

    /* HEADER */
    var p = $(document.createElement('p'));
    mHeaderTitle.text("Neutral Territory");
    p.text("Threshold: " + threshHold);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Profit: " + profit);
    p.appendTo(mHeader);

    /* BODY */
    mBodyTitle.text("Select units to purchase: ");
    var select = $('<select />');
    var bodyItem = $(document.createElement('p'));
    bodyItem.addClass("type");
    bodyItem.appendTo(mBody);
    bodyItem = $(document.createElement('p'));
    bodyItem.addClass("rank");
    bodyItem.appendTo(mBody);
    bodyItem = $(document.createElement('p'));
    bodyItem.addClass("purchase");
    bodyItem.appendTo(mBody);
    bodyItem = $(document.createElement('p'));
    bodyItem.addClass("maxFirePower");
    bodyItem.appendTo(mBody);
    bodyItem = $(document.createElement('p'));
    bodyItem.addClass("competenceReduction");
    bodyItem.appendTo(mBody);
    for(var unit in unitData) {
        $('<option/>', {value: unit , text: unitData[unit].type}).addClass("dropDownItem").appendTo(select);
    }
    
    select.appendTo(mBody);

    /* FOOTER */
    var footer = $('.modal-footer');
    var item = $(document.createElement('h1'));
    item.text("Buy Units: ");
    item.appendTo(footer);
    item = $(document.createElement('input'));
    item.attr('type' , 'text');
    item.addClass("amountOfUnitsToBuy");
    item.attr('placeholder', "Enter how many units to buy , then click 'Purchase'");
    item.appendTo(footer);
}

function showPopUp() {
    var modal = $("#myModal");
    var close = $(".close");
    //modal.contents().remove();
    close.onclick = function() {
        modal.hide();
    };
    modal.show();
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.hide();
        }
    }
}

