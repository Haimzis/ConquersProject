var CURR_GAME = buildUrlWithContextPath("singleGame");
var status;
var userName;
var currentTurn = 0;
var isMyTurn = false;
var isFirstStatus = true;
var refreshRate = 2000; //milli seconds

window.onload = function () {
    //setInterval(gameStatus, refreshRate);
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