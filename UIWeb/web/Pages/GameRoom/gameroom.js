var LOGGED_USERS_URL = buildUrlWithContextPath("LoggedUsersStats");
var CURR_GAME = buildUrlWithContextPath("singleGame");
var EVENTS_AND_VERSION_MANAGE_URL = buildUrlWithContextPath("singleGameEvents");
var CHAT_URL = buildUrlWithContextPath("chat");
var status;
var isMyTurn = false;
var refreshRate = 2000; //milli seconds
var gameTitle;
var initialFunds;
var totalCycles;
var unitData;
var territoryMapData;
var activePlayers;
var showWinner;
var selectedTerritoryId;
var maxPlayers;
var selectedUnitName;
var actionType;
var actionDone = false;
var playerTurn;
var winnerPlayerName;
var allRetired = false;
var showedEndGameDialog = false;
var gameStarted = false;
var gameVersion = 0;
var chatVersion = 0;

window.onload = function () {
    updateWelcomeUsernameDetail();
    getGameDetails();
    createOtherPlayersStats();
    createOwnPlayerStats();
    setInterval(updateOnlineUsers, refreshRate);
    setInterval(gameStatus, refreshRate);
    //setInterval(createOtherPlayersStats , refreshRate); // update the other players stats.
    //setInterval(createOwnPlayerStats , refreshRate);      // update the own player stats.
    setInterval(updateTerritories, refreshRate);
    setChat();
    updateChatContent();
    setInterval(updatePageByEvents,refreshRate);
};

function setChat() {
    //add a function to the submit event
    $("#chatform").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: refreshRate,
            error: function() {
                console.error("Failed to submit");
            },
            success: function(r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
            }
        });

        $("#userstring").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
}

function updatePageByEvents(){
    $.ajax({
        url: EVENTS_AND_VERSION_MANAGE_URL,
        data: "gameVersion=" + gameVersion,
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
             {
                "entries": [
                    {
                        "action":"TerritoryRelease",
                        "identity":"1",
                        "time":1485548397514
                    },
                    {
                        "action":"UnitsBuying",
                        "identity":"Ran_is_Gay",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             */
            console.log("Server game version: " + data.version + ", Current game version: " + gameVersion);
            if (data.version !== gameVersion) {
                gameVersion = data.version;
                triggerUpdatesOfPage(data.gameEvents);
            }
        }
    });
}
function triggerUpdatesOfPage(events){
    events.forEach(function(event){
        var action = event.action;
        var identityOfAffectedObject= event.identity;
        var timeOfEvent = event.time;

        switch(action){
            case "TerritoryRelease":
            //call function that returns the original color with the identity
                break;
            case "UnitsBuying":
                break;
            case "ArmyRehabilitation":
                break;
            case "Retirement":
                break;
            case "StartRoundUpdates":
                updateRemainRounds();
                break;
            case "PlayerTurnArrived":
                break;
            case "RoundEnded":
                updateRemainRounds();
                break;
            case "FundsIncrement":
                break;
            case "TerritoryConquered":
                break;
        }

    })

}
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
    if(status === "WaitingForPlayers") {
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
    playerTurn = json.currentPlayerTurnName;
    switch(newStatus)
    {
        case 'WaitingForPlayers':
            status = newStatus;
            break;
        case 'Running':
            if (playerTurn === "None") {
                startGame();
            }
            status = newStatus;
            break;
        case "Finished":
            isMyTurn = false;
            if (showWinner) {
                //alert(playerTurn + " Has won!");
                showWinner = false;
                showedEndGameDialog = true;
                showEndGameDialog();
            } else if (allRetired) {
                //alert(winnerPlayerName + " Has won because everyone retired!");
                $('.roundsLeft').text("Game Over!");
                allRetired = false;
                showedEndGameDialog = true;
                showEndGameDialog();
            }
            status = newStatus;
            break;
    }
    $('.gameStatus').text('Game status: ' + status);
    $('.currentPlayerName').text(playerTurn);
}

function showEndGameDialog() {
    showPopUp();
    var mHeader = $('.modal-header');
    var mBody = $('.modal-body');
    var item = $(document.createElement('h1'));
    item.text("Game Over!").append(mHeader);
    item = $(document.createElement('h1'));
    item.text("The winning player is " + winnerPlayerName).appendTo(mBody);
    $(document.createElement('button')).text("Exit").css("position" , "absolute").on('click' , function () {
        $.ajax
        (
            {
                url: CURR_GAME,
                data: {
                    action: 'resetGame'
                },
                type: 'GET',
                success: function() {
                    window.location = "../Lobby/lobby.html";
                }
            }
        )
    }).appendTo(mBody);
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
            success: function(result) {
                if(result !== undefined) {
                    gameStarted = result.isLoaded;
                }
            }
        }
    )
}


function setCurrentPlayerInTurn(playerName) {
    isMyTurn = playerName === playerTurn;
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
    if(activePlayers.length === 1 && !showedEndGameDialog && !showWinner) {
        allRetired = true;
        winnerPlayerName = activePlayers[0].playerName;
    }
}

function updateRemainRounds(){
    $.ajax({
        url: CURR_GAME,
        data:{
            action: "currentRound"
        },
        type: 'GET',
        success: setRemainingRounds
    });
}

function setRemainingRounds(data) {
    var remainRounds = totalCycles - data.round;
    if(remainRounds === 0) {
        $('.roundsLeft').text("Final round!");
    } else if (remainRounds < 0) {
        $('.roundsLeft').text("Game Over!");
        if(!showedEndGameDialog) {
            showWinner = true;
            winnerPlayerName = data.winnerName;
        }
    } else {
        $('.roundsLeft').text("Rounds Left: "+ remainRounds);
    }
}

function updateRegisteredPlayersSpan(){
    var activePlayersAmount = Object.keys(activePlayers).length;
    $('.registeredPlayers').text(activePlayersAmount);
}

function updateRequiredPlayersSpan(){
    var requiredPlayersAmount = maxPlayers;
    $('.requiredPlayers').text(requiredPlayersAmount);
}
function setGameDetails(data)  {
    maxPlayers = data.maxPlayers;
    gameTitle = data.gameTitle;
    initialFunds = data.initialFunds;
    totalCycles = data.totalCycles;
    unitData = data.unitMap;
    territoryMapData = data.territoryMap;
    updateRemainRounds();
    createGameBoard(data);
    updateRequiredPlayersSpan();
}

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
    var otherPlayersTable = $('#otherPlayerTable');
    otherPlayersTable.contents().remove();
    var otherPlayersArr = data;
    var sizeOfArray = otherPlayersArr.length;
    var tBody = $(document.createElement('tbody'));
    var tr = $(document.createElement('tr'));
    $(document.createElement('th')).text("Username").appendTo(tr);
    $(document.createElement('th')).text("Turings").appendTo(tr);
    $(document.createElement('th')).text("Color").appendTo(tr);
    tr.appendTo(tBody);
    tBody.appendTo(otherPlayersTable);
    for(var i=0 ;i< sizeOfArray;i++) {
        var otherPlayerStatsRow = $(document.createElement('tr'));
        $(document.createElement('td')).text(otherPlayersArr[i].playerName).appendTo(otherPlayerStatsRow);
        $(document.createElement('td')).text(otherPlayersArr[i].funds).appendTo(otherPlayerStatsRow);
        $(document.createElement('td')).text(otherPlayersArr[i].color).css({"background-color":otherPlayersArr[i].color , "color":"black"}).appendTo(otherPlayerStatsRow);
        otherPlayerStatsRow.appendTo(otherPlayersTable);
    }
}

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
    setCurrentPlayerInTurn(PlayerObject.playerName);

    var ownPlayerStatsRow = $(document.createElement('tr'));
    var ownPlayerTable = $('#ownPlayerTable');
    ownPlayerTable.contents().remove();
    var tBody = $(document.createElement('tbody'));
    var tr = $(document.createElement('tr'));
    $(document.createElement('th')).text("Username").appendTo(tr);
    $(document.createElement('th')).text("Turings").appendTo(tr);
    $(document.createElement('th')).text("Color").appendTo(tr);
    tr.appendTo(tBody);
    tBody.appendTo(ownPlayerTable);

    $(document.createElement('td')).text(PlayerObject.playerName).appendTo(ownPlayerStatsRow);
    $(document.createElement('td')).text(PlayerObject.funds).appendTo(ownPlayerStatsRow);
    $(document.createElement('td')).text(PlayerObject.color).css("background-color",PlayerObject.color).appendTo(ownPlayerStatsRow);
    ownPlayerStatsRow.appendTo(ownPlayerTable);
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
    if(isMyTurn && !actionDone) {
        var territoryId = territorySquare.currentTarget.getAttribute('territoryid');
        selectedTerritoryId = territoryId;
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

function onRetirementClick() {
    if(isMyTurn && status === "Running") {
        $.ajax
        (
            {
                async: false,
                url: CURR_GAME,
                data: {
                    action: 'retire'
                },
                type: 'GET',
                success: onRetireCallBack
            }
        )
    }
}

function onRetireCallBack() {
    window.location = "../Lobby/lobby.html";
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
//TODO: need to change erase the territorymapdata variable - should get the information from the servlet. 
function openOwnTerritoryPopup() {
    showPopUp();
    var threshHold = territoryMapData[selectedTerritoryId].armyThreshold;
    var profit = territoryMapData[selectedTerritoryId].profit;
    var currentFirePower = territoryMapData[selectedTerritoryId].conquerArmyForce.totalPower;
    var maxFirePower = territoryMapData[selectedTerritoryId].conquerArmyForce.potentialTotalPower;
    var mHeader = $('.modal-header');
    var mHeaderTitle = $(document.createElement('h1'));
    mHeaderTitle.addClass('mBodyTitle');
    mHeaderTitle.text("Territory number " + selectedTerritoryId);
    mHeader.contents().remove();

    /*HEADER*/
    mHeaderTitle.appendTo(mHeader);
    var p = $(document.createElement('p'));
    p.text("Threshold: " + threshHold);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Profit: " + profit);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Current Firepower on territory: " + currentFirePower);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Maximum firepower available: " + maxFirePower);
    p.appendTo(mHeader);
    p = $(document.createElement('button'));
    p.addClass("rehabilitateBtn");
    p.text("Rehabilitate Army");
    p.appendTo(mHeader);
    p = $(document.createElement('button'));
    p.addClass("addUnitsBtn");
    p.text("Add units");
    p.appendTo(mHeader);

    var footer = $('.modal-footer');
    footer.contents().remove();
    var mBody = $('.modal-body');
    mBody.contents().remove();

    $('.rehabilitateBtn').on('click', function() {
        actionType = "rehabilitate";
       handleDoneClick();
    });
    $('.addUnitsBtn').on('click', function () {
        actionType = "enforceTerritory";
        showBuyUnits();
    });

}
//TODO: need to change erase the territorymapdata variable - should get the information from the servlet.
function openAttackPopup() {
    showPopUp();
    var threshHold = territoryMapData[selectedTerritoryId].armyThreshold;
    var profit = territoryMapData[selectedTerritoryId].profit;
    var currentFirePower = territoryMapData[selectedTerritoryId].conquerArmyForce.totalPower;
    var mHeader = $('.modal-header');
    var mHeaderTitle = $(document.createElement('h1'));

    mHeaderTitle.addClass('mBodyTitle');
    mHeaderTitle.text("Enemy territory, ID: " + selectedTerritoryId);
    mHeader.contents().remove();

    /*HEADER*/
    mHeaderTitle.appendTo(mHeader);
    var p = $(document.createElement('p'));
    p.text("Threshold: " + threshHold);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Profit: " + profit);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Current Firepower on territory: " + currentFirePower);
    p.appendTo(mHeader);

   $(document.createElement('button'))
       .addClass("wellTimedBtn")
       .text("Well Timed Attack")
       .on('click', function () {
            actionType = "wellTimed";
            showBuyUnits();
    }).appendTo(mHeader);
    $(document.createElement('button'))
        .addClass("calculatedRiskBtn")
        .text("Calculated Risk Attack")
        .on('click', function () {
            actionType = "calculatedRisk";
            showBuyUnits();
    }).appendTo(mHeader);
}
//TODO: need to change erase the territorymapdata variable - should get the information from the servlet. 
function openNeutralPopup() {
    actionType = "neutral";
    showPopUp();
    var threshHold = territoryMapData[selectedTerritoryId].armyThreshold;
    var profit = territoryMapData[selectedTerritoryId].profit;
    var mHeader = $('.modal-header');
    var mHeaderTitle = $(document.createElement('h1'));
    mHeaderTitle.addClass('mBodyTitle');
    mHeader.contents().remove();

    /* HEADER */
    mHeaderTitle.appendTo(mHeader);
    var p = $(document.createElement('p'));
    mHeaderTitle.text("Neutral Territory");
    p.text("Threshold: " + threshHold);
    p.appendTo(mHeader);
    p = $(document.createElement('p'));
    p.text("Profit: " + profit);
    p.appendTo(mHeader);

    /*BODY AND FOOTER*/
    showBuyUnits();
}
//TODO: what is it.
function showBuyUnits() {
    var mBodyTitle = $(document.createElement('h2'));
    mBodyTitle.addClass('headerTitle');
    mBodyTitle.text("Select units to purchase: ");
    var mBody = $('.modal-body');
    mBody.contents().remove();
    mBodyTitle.appendTo(mBody);
    var unitInfoDiv = $(document.createElement('div'));
    unitInfoDiv.addClass('unitInfoDiv');
    unitInfoDiv.appendTo(mBody);
    var select = $('<select />');
    select.addClass("unitsSelect");
    $('<option/>' , {value: "" , text:"Choose here", selected:true , disabled:true , hidden:true}).appendTo(select);
    for(var unit in unitData) {
        $('<option/>', {value: unit , text: unitData[unit].type}).appendTo(select);
    }
    select.change(function() {
        $('.unitInfoDiv').contents().remove();
        selectedUnitName = jQuery(this).val();
        var bodyItem = $(document.createElement('p'));
        bodyItem.addClass("type");
        bodyItem.text("Type: " + unitData[selectedUnitName].type);
        bodyItem.appendTo(unitInfoDiv);
        bodyItem = $(document.createElement('p'));
        bodyItem.addClass("rank");
        bodyItem.text("Rank: " + unitData[selectedUnitName].rank);
        bodyItem.appendTo(unitInfoDiv);
        bodyItem = $(document.createElement('p'));
        bodyItem.addClass("purchase");
        bodyItem.text("Cost: " + unitData[selectedUnitName].purchase);
        bodyItem.appendTo(unitInfoDiv);
        bodyItem = $(document.createElement('p'));
        bodyItem.addClass("maxFirePower");
        bodyItem.text("FirePower: " + unitData[selectedUnitName].maxFirePower);
        bodyItem.appendTo(unitInfoDiv);
        bodyItem = $(document.createElement('p'));
        bodyItem.addClass("competenceReduction");
        bodyItem.text("Competence Reduction: " + unitData[selectedUnitName].competenceReduction);
        bodyItem.appendTo(unitInfoDiv);
    });
    select.appendTo(mBody);

    /* FOOTER */
    var footer = $('.modal-footer');
    footer.contents().remove();
    var item = $(document.createElement('h1'));
    item.text("Buy Units: ");
    item.appendTo(footer);
    item = $(document.createElement('input'));
    item.attr('type' , 'text');
    item.addClass("amountOfUnitsToBuy");
    item.attr('placeholder', "Enter how many units to buy , then click 'Purchase'");
    item.appendTo(footer);
    item = $(document.createElement('button'));
    item.text("Purchase");
    item.addClass("purchaseBtn");
    item.appendTo(footer);
    $('.purchaseBtn').on('click' , handlePurchaseClick);
    item = $(document.createElement('button'));
    item.text("Done");
    item.addClass("doneBtn");
    item.appendTo(footer);
    $('.doneBtn').on('click' , handleDoneClick).prop('disable' , true);
}

function isNormalInteger(str) {
    var n = Math.floor(Number(str));
    return n !== Infinity && String(n) === str && n >= 0;
}

function handlePurchaseClick() {
    var howMany = $('.amountOfUnitsToBuy').val();
    if(isNormalInteger(howMany)) {
        buyUnits(howMany, selectedUnitName);
    } else {
        alert("Please enter a positive integer!");
    }
}

function handleDoneClick() {
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'territoryAction',
                actionType: actionType
            },
            type: 'GET',
            success: territoryActionCallBack
        }
    );
    actionDone = true;
}


/*
    Returned data members:
    -----------------------
    List<Unit> unitsBought; - List of units bought so far.
    int unitsBoughtAmount; - Amount of total units bought.
    int fundsAfterPurchase; - Funds after selected unit purchased.
    String buyerName; - Name of buying player.
    boolean success - Enough funds or not.
*/
function buyUnits(howMany , whichUnit) {
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'buyUnits',
                amount: howMany,
                unit: whichUnit
            },
            type: 'GET',
            success: buyUnitsCallBack
        }
    );
}

function buyUnitsCallBack(data) {
    if(data.success){
        var howMany = $('.amountOfUnitsToBuy').val();
        $('.doneBtn').prop('disable' , false);
        var footer = $('.modal-footer');
        var shoppingList = $(document.createElement('p'));
        shoppingList.text("You just bought " + howMany + " " + selectedUnitName);
        shoppingList.appendTo(footer);
    } else {
        alert("Not enough turings!");
    }
}

function showBattleResultPopup(actionType, result) {
    showPopUp();
    //var footer = $('.modal-footer');
    var mBody = $('.modal-body');
    var mHeader = $('.modal-header');
    var headerTitle = $(document.createElement('h1'));
    var resultText = $(document.createElement('h3'));
    var defendingArmyDiv = $(document.createElement('div')).addClass("defendingArmyDiv");
    resultText.appendTo(mHeader);

    headerTitle.appendTo(mHeader);
    var unitItem = $(document.createElement('p'));
    unitItem.text("Defending army had " + result.defendingArmy.units.length + " units.");
    unitItem.appendTo(mBody);
    unitItem = $(document.createElement('h1'));
    unitItem.text("Defending army information: ");
    unitItem.appendTo(mBody);
    defendingArmyDiv.appendTo(mBody);

    for(var i = 0 ; i < result.defendingArmy.units.length ; i++) {
        unitItem = $(document.createElement('p'));
        unitItem.text("Type:" + result.defendingArmy.units[i].type);
        unitItem.appendTo(defendingArmyDiv);
        unitItem = $(document.createElement('p'));
        unitItem.text("Rank:" + result.defendingArmy.units[i].rank);
        unitItem.appendTo(defendingArmyDiv);
        unitItem = $(document.createElement('p'));
        unitItem.text("FirePower:" + result.defendingArmy.units[i].currentFirePower);
        unitItem.appendTo(defendingArmyDiv);
    }

    if(actionType === "calculatedRisk") {
        headerTitle.text("Calculated Risk Attack!");
    } else if(actionType === "wellTimed") {
        headerTitle.text("Well Timed Attack!");
    }
    if(result.success) {
        resultText.text("VICTORY!");
    } else if(result.draw) {
        resultText.text("DRAW!");
    } else {
        resultText.text("DEFEAT!");
    }
}

function territoryActionCallBack(result) {
    $("#myModal").hide();
    switch(actionType) {
        case "neutral":
            if(result.success) {
                alert("Neutral territory " + result.targetTerritoryId + " has been conquered");
            } else {
                alert("You have failed to conquer territory number " + result.targetTerritoryId);
            }
            break;
        case "rehabilitate":
            if(result.success) {
                alert("Territory " + result.targetTerritoryId + " has been rehabilitated");
            } else {
                alert("Not enough funds to rehabilitate territory number " + result.targetTerritoryId);
            }
            break;
        case "enforceTerritory":
            if(result.success) {
                alert("Territory " + result.targetTerritoryId + " has been enforced");
            }
            break;
        default:
            showBattleResultPopup(actionType, result);
            break;
    }
}

function updateTerritories() {
    $.ajax
    (
        {
            url: CURR_GAME,
            data: {
                action: 'updateTerritories'
            },
            type: 'GET',
            success: updateTerritoriesCallBack
        }
    );
}
function updateTerritoriesCallBack(territoriesMap) {
    var temp = territoriesMap;
    territoryMapData = temp;
    colorTerritories();
}

function colorTerritories() {
    var colors = [];
    for(var territory in territoryMapData) {
        var selectedTerritory = territoryMapData[territory];
        if(selectedTerritory.conquerID !== null) {
            colors[selectedTerritory.ID] = getColor(selectedTerritory)
        }else { //default color
            colors[selectedTerritory.ID] = "#f2f2f2";
        }
    }
    $('.Territory').each(function () {
        for(var player in activePlayers) {
            var selectedTerritory = territoryMapData[$(this).attr('TerritoryID')];
            var selectedPlayer = activePlayers[player];
            if(selectedTerritory.conquerID !== null) {
                if(selectedTerritory.conquerID === selectedPlayer.ID) {
                    $(this).css("background-color" , colors[selectedTerritory.ID]);
                }
            } else {
                $(this).css("background-color" , '#f2f2f2');
            }
        }
    })
}

function getColor(selectedTerritory) {
    for(var player in activePlayers) {
        if(activePlayers[player].ID === selectedTerritory.conquerID) {
            return activePlayers[player].color;
        }
    }
}

$.fn.hasAttr = function(name) {
    return this.attr(name) !== undefined;
};

function onEndTurnClick() {
    if(status === "Running" && isMyTurn) {
        $.ajax
        (
            {
                url: CURR_GAME,
                data: {
                    action: 'endTurn'
                },
                type: 'GET',
                success: endTurnCallBack
            }
        );
    }
}

function endTurnCallBack(data) {
    if(data.status === "Finished") {
        status = data.status;
    }
    actionDone = false;
}

function showPopUp() {
    var modal = $("#myModal");
    var close = $(".close");
    close.bind('click' , function () {
       modal.hide();
    });
    modal.show();
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.hide();
        }
    };

    /* CLEAR INFO */
    var footer = $('.modal-footer');
    footer.contents().remove();
    var mBody = $('.modal-body');
    mBody.contents().remove();
    var mHeader = $('.modal-header');
    mHeader.contents().remove();
}

/*----------- CHAT CODE ----------------*/
function appendToChatArea(entries) {
    // add the relevant entries
    $.each(entries || [], appendChatEntry);

    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $("#chatArea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatArea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

function ajaxChatContent() {
    $.ajax({
        url: CHAT_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function(data) {
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            updateChatContent();
        },
        error: function(error) {
            updateChatContent();
        }
    });
}

function updateChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}