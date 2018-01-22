var stompClient = null;

function connectToTable() {
    var socket = new SockJS('/tables/games/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/invitation' + $('#table_name').text(), function(message){
            var chair = JSON.parse(message.body)
            console.log(chair);
        });
    });
}

/**
 * Отправить на сервер запрос на создание chair.
 */
function sendTableRequest(table_id) {
    var json = JSON.stringify(
        {
            'table_id': table_id,
            'user_id': $('#user_id').text(),
            'userPot': document.getElementById('userPot'+table_id).value
        }
    );
    stompClient.send("/app/tableInvitation", {}, json);
}