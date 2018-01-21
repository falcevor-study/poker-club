var stompClient = null;

function connect() {
    var socket = new SockJS('/tables/games/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/in', function(message){
            console.log(JSON.parse(message.body).login);
        });
    });
}

function send() {
    stompClient.send("/app/out", {}, JSON.stringify({ 'login': 'ksenia', 'password': '123', 'money': 2000}));
}