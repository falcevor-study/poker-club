function connect() {
    var socket = new SockJS('/tables/games/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/tables/game', function(message){
            console.log(message);
        });
    });
}