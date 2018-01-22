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
    });
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
 * Обновить визуализацию карт.
 */
function update_cards(cards) {

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
