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
var selectedUnitName;
var actionType;

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
    setInterval(createOtherPlayersStats , refreshRate);
    setInterval(createOwnPlayerStats , refreshRate);
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

function updateRemainRounds(){
    var remainRounds = totalCycles - roundNumber;
    $('.roundsLeft').text("Rounds Left: "+remainRounds);
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
        handleDoneClick();
    }).appendTo(mHeader);
    $(document.createElement('button'))
        .addClass("calculatedRiskBtn")
        .text("Calculated Risk Attack")
        .on('click', function () {
        actionType = "calculatedRisk";
        handleDoneClick();
    }).appendTo(mHeader);
}

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
    var footer = $('.modal-footer');
    var whatDidIBuy = $(document.createElement('p'));
    if(isNormalInteger(howMany)) {
        buyUnits(howMany, selectedUnitName);
        whatDidIBuy.text("You just bought " + howMany + selectedUnitName);
        whatDidIBuy.appendTo(footer);
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
        $('.doneBtn').prop('disable' , false);
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
    resultText.appendTo(mHeader);

    headerTitle.appendTo(mHeader);
    var unitItem = $(document.createElement('p'));
    unitItem.text("Defending army had " + result.defendingArmy.units.length + " units.");
    unitItem.appendTo(mBody);
    unitItem = $(document.createElement('h1'));
    unitItem.text("Defending army information: ");
    unitItem.appendTo(mBody);

    for(var unit in result.defendingArmy.units) {
        unitItem = $(document.createElement('p'));
        unitItem.text("Type:" + unit.type);
        unitItem.appendTo(mBody);
        unitItem = $(document.createElement('p'));
        unitItem.text("Rank:" + unit.rank);
        unitItem.appendTo(mBody);
        unitItem = $(document.createElement('p'));
        unitItem.text("FirePower:" + unit.currentFirePower);
        unitItem.appendTo(mBody);
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
    updateTerritories();
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
    territoryMapData = territoriesMap;
}


function onEndTurnClick() {
    if(status === "Running") {
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

function endTurnCallBack(data) { //TODO: Deal with showing winner and changing status

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

