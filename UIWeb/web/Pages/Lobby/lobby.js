var chatVersion = 0;
var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var CHAT_LIST_URL = buildUrlWithContextPath("chat");
var USER_LOGOUT_URL = buildUrlWithContextPath("logoutId")

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do

});

function logOutClicked() {
        $.get(USER_LOGOUT_URL , function (data) {

        })
}

