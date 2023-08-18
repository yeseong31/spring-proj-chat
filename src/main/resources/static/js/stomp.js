$(document).ready(function() {
  connect();
});

// 서버 연결: 엔트포인트(브로커 URL) 설정
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/ws-stomp'
});

// 소켓 연결에 성공한 경우: 클라이언트는 '/sub/channel/{channelUuid}로 구독'
stompClient.onConnect = (frame) => {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const path = '/sub/channel/' + urlParams.get('uuid');

  setConnected(true);

  console.log('Connected: ' + frame);
  console.log('Path: ' + path);

  stompClient.subscribe(path, (greeting) => {
    var body = JSON.parse(greeting.body);
    showGreeting(body.content, body.memberUuid);
  });
};

// 소켓 연결에 실패한 경우
stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

// STOMP에 오류가 생긴 경우
stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

// 'CONNECT, 'DISCONNECT' 버튼 클릭 시 버튼 활성화/비활성화
function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#content").prop("disabled", !connected);
  $("#send").prop("disabled", !connected);

  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

// 소켓 연결
function connect() {
  stompClient.activate();
}

// 소켓 연결이 해제된 경우
function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

// 양방향 메시지 송수신: 수신 대상은 '/pub/message'
function sendMessage() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const content = $("#content");
  const memberUuid = $("#memberUuid").text();

  stompClient.publish({
    destination: "/pub/message",
    body: JSON.stringify({
      'content': content.val(),
      'channelUuid': urlParams.get('uuid'),
      'memberUuid': memberUuid,
    })
  });

  content.val("");
}

// 메시지 출력
function showGreeting(message, uuid) {
  var time = new Date();
  if (uuid === $("#memberUuid").text()) {  // 본인
    message = "<div class=\"d-flex flex-row justify-content-end mb-4 pt-1\"><div>"
        + "<p class=\"small p-2 me-3 mb-1 text-white rounded-3 bg-primary\">"
        + message
        + "</p><p class=\"small me-3 mb-3 rounded-3 text-muted d-flex justify-content-end\">"
        + ('0' + time.getHours()).slice(-2) + ":" + ('0' + time.getMinutes()).slice(-2)
        + "</p></div></div>"
  } else {
    message = "<div class=\"d-flex flex-row justify-content-start\"><div>"
        + "<p class=\"small p-2 ms-3 mb-1 rounded-3\" style=\"background-color: #f5f6f7; color: black\">"
        + message
        + "</p><p class=\"small ms-3 mb-3 rounded-3 text-muted d-flex\">"
        + ('0' + time.getHours()).slice(-2) + ":" + ('0' + time.getMinutes()).slice(-2)
        + "</p></div></div>"
  }
  $("#greetings").append(message);
}

// 이벤트 바인딩
$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#send").click(() => sendMessage());
});