var chatVersion = 0;
var refreshRate = 2000; //milli seconds
var USER_URL = buildUrlWithContextPath("loggedUserName");



function WelcomeUserName(){

}
//add a method to the button in order to make that form use AJAX
//and not actually submit the form
    $(function() { // onload...do
    $.get(USER_URL, function(data){
        $("#loggedUserName").text(data);
    });
});
