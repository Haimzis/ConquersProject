var CURR_GAME = buildUrlWithContextPath("singleGame");
var GAMES_LIST = buildUrlWithContextPath("games");
var status;
var userName;
var currentTurn = 0;
var isMyTurn = false;
var refreshRate = 2000; //milli seconds

window.onload = function () {
    //setInterval(gameStatus, refreshRate);
    getGameDetails();
};

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

function handleStatus(json)
{

    var newStatus = json.status;
    var playerTurn = json.currentPlayerTurnName;

    switch(newStatus)
    {
        case 'WaitingForPlayers':
            status = newStatus;
            break;
        case 'Running':
            if (status === 'WaitingForPlayers')
            {

            }
    }
    //$('.gameStatus').text('Game status: ' + status);
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
            success: updateGameDetails
        }
    )
}

function updateGameDetails(data)  {
var roundNumber = data.roundNumber;
var gameTitle = data.gameTitle;
var initialFunds = data.initialFunds;
var totalCycles = data.totalCycles;
var cols = data.columns;
var rows =  data.rows;
var unitData = data.unitMap;
var territoryMapData = data.territoryMap;
var activePlayers = data.playersList;

}

function createGameBoard(gameBoardData){
    var board = $('.boardBody');
    board.contents().remove();
    var rows = gameBoardData.rows;
    var columns= gameBoardData.columns;

    for(var i=0; i<rows; i++){
        var rowTable =$(document.createElement('tr'));
        rowTable.addClass('row');
        rowTable.appendTo(board);
        for(var j=0;j<columns;i++){
            var territorySquare =$(document.createElement('td'));
            territorySquare.addClass('Territory');
            territorySquare.attr('TerritoryID', j); //maybe j should start from 1
            var territoryData = $(document.createElement('territoryDataDiv'));
            var territoryID = $(document.createElement('div'));
            territoryID.addClass('territoryData');
            territoryID.text("ID");
            territoryData.appendTo(territoryData);

            var territoryArmyThreshold = $(document.createElement('div'));
            territoryArmyThreshold.addClass('territoryData');
            territoryArmyThreshold.text("ArmyThreshold");
            territoryData.appendTo(territoryData);

            var territoryProfit = $(document.createElement('div'));
            territoryProfit.addClass('territoryData');
            territoryProfit.text("ID");
            territoryProfit.appendTo(territoryData);

            territoryData.appendTo(territorySquare);
            territorySquare.appendTo(rowTable);
        }
    }

}