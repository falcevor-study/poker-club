var stompClient = null;

function connect() {
    var socket = new SockJS('/tables/games/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/in', function(message){
            console.log(JSON.parse(message.body));
        });
    });
    // send()
}

function send() {
    stompClient.send("/app/out", {}, JSON.stringify({'table_name': $('#table_name').text(),
        'login': $('#login').text()}));
}