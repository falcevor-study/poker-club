var stompClient = null;

function connect() {
    // disable_buttons(); TODO: УБРАТЬ КОММЕНТАРИЙ
    var socket = new SockJS('/tables/games/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/game/state/' + $('#table_name').text(), function(message){
            update_state(JSON.parse(message.body));
            card_update_request();
            console.log(JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/game/card/' + $('#login').text(), function(message){
            update_cards(JSON.parse(message.body));
            console.log(JSON.parse(message.body));
        });

        // это запускается автоматически при открытии страницы с игрой пользователем
        stompClient.subscribe('/topic/chairs/in', function(message){
            var chairs = JSON.parse(message.body);
            console.log(chairs);
            placeUsers(chairs);
        });

        // это запускается при выходе из игры
        stompClient.subscribe('/topic/disconnect/in', function(message){
            console.log(JSON.parse(message.body));
        });

        // это запускается в том случае, если стульев за столом больше 1
        stompClient.subscribe('/topic/startgame/in', function(message){
            var responce = JSON.parse(message.body);
            console.log(responce);
            var state = responce.currentGameState;
            if (responce.isNewGame === false) {
                if (state.card1 == null && state.card2 == null && state.card3 == null && state.card4 == null && state.card5 == null) {
                    preflop(state, responce.chairs)
                }
                if (state.card1 != null && state.card2 != null && state.card3 != null && state.card4 == null && state.card5 == null) {
                    flop(state, responce.chairs)
                }
                if (state.card1 != null && state.card2 != null && state.card3 != null && state.card4 != null && state.card5 == null) {
                    tern(state, responce.chairs)
                }
                if (state.card1 != null && state.card2 != null && state.card3 != null && state.card4 != null && state.card5 != null) {
                    river(state, responce.chairs)
                }
            }
        });
    });
}


function disconnect() {
    var json = JSON.stringify(
        {
            'table_id': $('#table_id').text(),
            'user_id': $('#user_id').text()
        }
    );
    stompClient.send("/app/disconnect/out", {}, json);
}


function river(chairs) {
    console.log('river')
    update_cards(chairs);
}


function tern(chairs) {
    console.log('tern')
    update_cards(chairs);
}


function flop(chairs) {
    console.log('flop');
    update_cards(chairs);
}


function preflop(currentGameState, chairs) {
    update_cards(chairs);
}


/**
 * функция размечает юзеров
 * @param chairs - список стульев в формате json
 */
function placeUsers(chairs) {
    var chairCount = 1;
    for (var key in chairs){
        var h4_id = '#player'+chairCount+'_name';
        var h5_pot = '#player'+chairCount+'_pot';
        $(h4_id).text(chairs[key].user.login);
        $(h5_pot).text(chairs[key].userPot);
        chairCount += 1;
    }
    if (chairCount >= 2) {
        startGame(chairs);
    }
}


function startGame(chairs) {
    var json = JSON.stringify(
        {
            'chairs': chairs,
            'table_id': $('#table_id').text()
        }
    );
    stompClient.send("/app/startgame/out", {}, json);
}


/**
 * функция получает список стульев за текущим столом
 * таймаут нужен, чтобы соединение по сокету успело установиться
 */
function getChairs() {
    setTimeout( function(){
        var json = JSON.stringify({'table_id': $('#table_id').text()});
        stompClient.send("/app/chairs/out", {}, json);
    }  , 1000 );
}

/**
 * Отправить на сервер действие пользователя на торгах.
 * Данные вытягиваются из формы и из hidden-полей.
 */
function sendAction(action) {
    var json = JSON.stringify(
        {
            'table_id': $('#table_id').text(),
            'user_id': $('#user_id').text(),
            'action': action,
            'value': document.getElementById('value').value
        }
    );
    stompClient.send("/app/game/action", {}, json);
    // disable_buttons(); TODO: УБРАТЬ КОММЕНТАРИЙ
}


/**
 * Отправить на сервер запрос от пользователя на обновление визуализации карт.
 * Данные вытягиваются из формы и из hidden-полей.
 */
function card_update_request(action) {
    var json = JSON.stringify(
        {
            'table_id': $('#table_id').text(),
            'user_id': $('#user_id').text()
        }
    );
    stompClient.send("/app/game/card", {}, json);
}


/**
 * Обновить визуализацию карт у всех юзеров
 * TODO надо добавить еще визуализацию для карт на столе
 */
function update_cards(chairs) {
    var chairCount = 1;
    for (var key in chairs){
        var new_card1_class = 'card';
        var new_card2_class = 'card';
        if (parseInt($('#user_id').text()) == chairs[key].user.id && chairs[key].user.status == "player") {
            new_card1_class = 'card ' + chairs[key].card1.suit +' '+chairs[key].card1.value;
            new_card2_class = 'card ' + chairs[key].card2.suit +' '+chairs[key].card2.value;
        }
        else {
            if (chairs[key].user.status == "player") {
                new_card1_class = 'card reverse';
                new_card2_class = 'card reverse';
            }
            else {
                new_card1_class = 'card none';
                new_card2_class = 'card none';
            }
        }
        var old_card1_class = '#player'+chairCount+'_card1';
        var old_card2_class = '#player'+chairCount+'_card2';
        $(old_card1_class).attr('class', new_card1_class);
        $(old_card2_class).attr('class', new_card2_class);
        chairCount += 1;
    }
}


/**
 * Обновить состояние отображения игры и функциональных элементов.
 *
 * @param state - полученное от сервера состояние игры.
 */
function update_state(state) {
    // TODO: Когда будет готов ответ от сервера, реализовать. Не забыть про включение определенных кнопок. То есть,
    // TODO: например, если есть ставка, ее нужно повысить, принять или сбросить карты, а просто пропустить (check) нельзя.
}


/**
 * Выключить все кнопки интерфейса, сбросить величину ставки.
 */
function disable_buttons() {
    $('#check').disable();
    $('#call').disable();
    $('#bet').disable();
    $('#raise').disable();
    $('#fold').disable();
    document.getElementById('value').value = "0";
}
