// joystick

var richtung = {
    HOCH: 0, LINKS: 1, RECHTS: 2, RUNTER: 3
};


function movemi(dir) {
    if (navigator.vibrate) {
        navigator.vibrate(50);
    }
    console.log("movemi", dir);
    //$('#cmitte').attr("fill", 'yellow');
//    var message = new Paho.MQTT.Message('{"cmd":' + dir + '}');
//    message.destinationName = joy;
//    mqtt.send(message);
   
    //$('#cmitte').attr("fill", 'silver');
    
    
 // SpiegelNr senden.
	var o = {
		cmd : 'control',
		direction: dir,
		ziel : $("#selziel option:selected").val(),
		spiegel : $("#selspiegel option:selected").val()
		
	};
	var s = JSON.stringify(o);
	console.log('control', s);
	sendMessage(o);    
}


function saveme() {
    if (navigator.vibrate) {
        navigator.vibrate(500);
    }
//    var s = $('#inhalt').val();
//    var obj = JSON.parse(s);
//    obj["cmd"] = "save";
//    obj["topic"] = $('#topic').val();
//    s = JSON.stringify(obj);
//    var message = new Paho.MQTT.Message(s);
//    message.destinationName = 'simago/save';
//    mqtt.send(message);
    
    var o = {
    		cmd : 'save',
    		ziel : $("#selziel option:selected").val(),
    		spiegel : $("#selspiegel option:selected").val()
    		
    	};
    	$("#save").prop("disabled", true);
    	var s = JSON.stringify(o);
    	console.log('save', s);
    	sendMessage(o);        
    
}


