var ws = new WebSocket("ws://localhost:8081/websocks/ws");
ws.onopen = function() {
	$('#xwsmessage').html('websocket is connected.');

};

// reines json. Kennung cmd als Sprungverteiler
ws.onmessage = function(evt) {
	var msg = evt.data;
	console.log("Message received roh: " + msg);
	var o = JSON.parse(msg);
	console.log("Message received:" + JSON.stringify(o));
	console.log("cmd:" + JSON.stringify(o.cmd));
	if (o.cmd == 'kunden') {
		var kunden = o.kunden;
		console.log('Wskunden', kunden);
		$('#xwsmessage').html(JSON.stringify(kunden));
		fillKunden(kunden);
	}
	if (o.cmd == 'positionen') {
		fillDiagramme(o);
	}

};
ws.onclose = function() {
	$('#xwsmessage').html('websocket is closed.');
};

function sendMessage(o) {
	ws.send(JSON.stringify(o));
}
