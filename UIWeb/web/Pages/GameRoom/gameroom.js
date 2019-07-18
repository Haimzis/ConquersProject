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