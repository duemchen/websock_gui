
var ws; 

function sendMessage(o) {
	ws.send(JSON.stringify(o));
}
