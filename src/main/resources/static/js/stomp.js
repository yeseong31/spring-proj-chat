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
    showGreeting(JSON.parse(greeting.body).content);
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
  // if (connected) {
  //   $("#conversation").show();
  // } else {
  //   $("#conversation").hide();
  // }
  // $("#greetings").html("");
}

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

  stompClient.publish({
    destination: "/pub/message",
    body: JSON.stringify({
      'content': $("#content").val(),
      'channelUuid': urlParams.get('uuid')
    })
  });
}

// 메시지 출력
function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

// 이벤트 바인딩
$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#send").click(() => sendMessage());
});