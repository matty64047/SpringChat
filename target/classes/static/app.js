function openNav(nav) {
    document.getElementById(nav).style.width = "400px";
  }
  
  function checkWidthAndOpenNav() {
    var windowWidth = parseInt(window.innerWidth, 10);
    if (windowWidth > 1300) {
      openNav("leftNav");
    }
    if (windowWidth < 1300) {
      closeNav("leftNav");
    }
    if (windowWidth > 1700) {
        openNav("rightNav");
    }
    if (windowWidth < 1700) {
        closeNav("rightNav");
    }
  }
  
  function closeNav(nav) {
    document.getElementById(nav).style.width = "0";
  }
  
  window.addEventListener('resize', checkWidthAndOpenNav);
  
  const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/gs-guide-websocket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    const urlParams = new URLSearchParams(window.location.search);
    const myParam = urlParams.get('stream');
    stompClient.subscribe(`/topic/streams/${myParam}`, (greeting) => {
        $("#greetings").html("");
        showGreeting(JSON.parse(greeting.body).content);
    });
    stompClient.subscribe(`/topic/streams`, (greeting) => {
        $("#streams").html("");
        addStreams(greeting.body)
    });
    stompClient.publish({
        destination: `/app/history/${myParam}`
    })
    stompClient.publish({
        destination: `/app/streams`
    })
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

window.onload = connect();

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function addStreams(streams) {
    streams = streams.replace("[", "");
    streams = streams.replace("]", "");
    streams = streams.replace(/"/g, "");
    streamList = streams.split(",");
    // Get the tbody element by its id
    for (var i = 0; i < streamList.length; i++) {
        var line = streamList[i];
        $("#streams").append("<tr><td><a href='?stream=" + line + "'>#"+line+"</a></td></tr>");
    }
}

function addStream() {
    $("#streams").append('<tr><td><input type="text" id="textInput" class="form-control" style="width:100%;padding:20px;" placeholder="Enter stream-name and press Enter"></td></tr>');
    const textInput = document.getElementById("textInput");
    textInput.addEventListener("keyup", function(event) {
        if (event.key === "Enter") {
            const label = textInput.value.trim();
            if (label) {
                $("#streams").append("<tr><td><a href='?stream=" + label + "'>#"+label+"</a></td></tr>");
                textInput.style.display = "none";
            }
        }
    });
}

function getCookieValue(cookieName) {
    const allCookies = document.cookie; // Get all cookies as a string
    const cookieArray = allCookies.split('; '); // Split cookies into an array

    for (const cookie of cookieArray) {
        const [name, value] = cookie.split('='); // Split each cookie into name and value

        if (name === cookieName) {
            return decodeURIComponent(value); // Return the value of the specified cookie
        }
    }

    return null; // Return null if the cookie is not found
}

function sendName() {
    const urlParams = new URLSearchParams(window.location.search);
    const stream = urlParams.get('stream');
    const user = getCookieValue("email");
    stompClient.publish({
        destination: `/app/streams/${stream}`,
        body: JSON.stringify({'name': user, 'message': $("#message").val(), 'stream': stream})
    });
}

function showGreeting(message) {
    var lines = message.split('@@'); // Split the message by newline characters

    for (var i = 0; i < lines.length; i++) {
        var line = lines[i];
        $("#greetings").append("<tr><td><div class='container'>" + line + "</div></td></tr>");
    }
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});