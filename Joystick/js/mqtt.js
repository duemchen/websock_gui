var mqtt;
var reconnectTimeout = 200;
var ziel;
var joy;
var mirrors = [];
var richtung = {
    HOCH: 0, LINKS: 1, RECHTS: 2, RUNTER: 3
};

function MQTTconnect() {
    if (typeof path == "undefined") {
        path = '/mqtt';
    }

    mqtt = new Paho.MQTT.Client(
            host,
            port,
            path,
            "web_" + parseInt(Math.random() * 100, 10)
            );
    var options = {
        timeout: 3,
        useSSL: useTLS,
        cleanSession: cleansession,
        onSuccess: onConnect,
        onFailure: function (message) {
            $('#inhalt').val("Connection failed: " + message.errorMessage + "Retrying");
            console.log(message.errorMessage);
            setTimeout(MQTTconnect, reconnectTimeout);
        }
    };

    mqtt.onConnectionLost = onConnectionLost;
    mqtt.onMessageArrived = onMessageArrived;



    if (username != null) {
        options.userName = username;
        options.password = password;
    }
    console.log("Host=" + host + ", port=" + port + ", path=" + path + " TLS = " + useTLS + " username=" + username + " password=" + password);
    mqtt.connect(options);
}

function onConnect() {
    $('#inhalt').val('Connected to IP ' + host + ':' + port + path);
    // Connection succeeded; subscribe to our topic
    //mqtt.subscribe(topic, {qos: 2});		
    //mqtt.subscribe('simago/tinker', {qos: 0});
    //mqtt.subscribe('simago/compass', {qos: 0});
    //mqtt.subscribe('simago/joy', {qos: 0});
    //mqtt.subscribe('simago/veranda', {qos: 0});
    //mqtt.subscribe('simago/heizung', {qos: 0});
    //mqtt.subscribe('simago/camera', {qos: 0});
    //mqtt.subscribe('simago/compass/74-DA-38-3E-E8-3C', {qos: 0});
    //mqtt.subscribe('simago/compass', {qos: 0});
    //mqtt.subscribe(ziel, {qos: 0});
    //mqtt.subscribe('simago/compass/#', {qos: 0});
    //mqtt.subscribe('simago/joy/#', {qos: 0});
    //mqtt.subscribe('simago/save/#', {qos: 0});
    mqtt.subscribe('simago/zustand', {qos: 0});
    //mqtt.subscribe('simago/mirrors', {qos: 0});
	mqtt.subscribe('simago/wind', {qos: 0});	
    $('#topic').val('warte auf Message...');

}

function onConnectionLost(response) {
    setTimeout(MQTTconnect, reconnectTimeout);
    $('#inhalt').val("connection lost: " + response.errorMessage + ". Reconnecting");
    console.log(response.errorMessage);


}
;

function onMessageArrived(message) {
    var topic = message.destinationName;
    var payload = message.payloadString;

//                if ($('#ws li').length > 10) {
//                    $('#ws').empty();
//                }
//                currentdate = new Date();
//                $('#ws').prepend('<li>' + topic + ' = ' + currentdate.getSeconds() + '</li>');



    if (topic == 'simago/camera') {
        var s = "data:image/png;base64," + payload;
        $('#bild').attr("src", s);
    }
    if (topic == 'simago/compass/80-1F-02-ED-FD-A6') {
        //  console.log(topic, payload);
        $('#topic').val(topic);
        $('#inhalt').val(payload);
    }
    if (topic == 'simago/compass/74-DA-38-3E-E8-3C') {
        //  console.log(topic, payload);
        $('#topic').val(topic);
        $('#inhalt').val(payload);

    }
    if (topic == 'simago/joy') {
        console.log(topic, payload);
    }
    if (topic == 'simago/save') {
        console.log(topic, payload);
        //alert(payload);
    }
    if (topic == 'simago/zustand') {
        console.log(topic, payload);
        $('#zustand').val(payload);

    }
    if (topic == 'simago/mirrors') {
        console.log(topic, payload);
        // die Liste neu füllen und anklicken
        //$('#zustand').val(payload);
//{"inhalt":"MirrorList","liste":[{"status":true,"name":"74-DA-38-3E-E8-3C","mac":"74-DA-38-3E-E8-3C"},{"status":true,"name":"80-1F-02-ED-FD-A6","mac":"80-1F-02-ED-FD-A6"}]}
        var o = jQuery.parseJSON(payload);
        var mirrors = o.liste;
        console.log('mirrors', mirrors);
        $("#selectMirror").empty();
        var sel = "";
        for (var i = 0, len = mirrors.length; i < len; i++) {
            console.log(mirrors[i].name);
            sel = "selected";
            if (i != 0) {
                sel = "xsel";
            }
            $("#selectMirror").append($("<option>").attr("value", mirrors[i].mac).attr(sel, sel).text(mirrors[i].name));
        }
        $('#selectMirror').selectmenu('refresh');
        $('#selectMirror').change();
    }
	
	if (topic == 'simago/wind') {
        console.log(topic, payload);
        $('#windmesser').val(payload);

    }




}
;

// den Inhalt des gewählten compass senden.
function saveme() {
    if (navigator.vibrate) {
        navigator.vibrate(500);
    }
    var s = $('#inhalt').val();
    var obj = JSON.parse(s);
    obj["cmd"] = "save";
    obj["topic"] = $('#topic').val();
    s = JSON.stringify(obj);
    var message = new Paho.MQTT.Message(s);
    message.destinationName = 'simago/save';
    mqtt.send(message);
}


function movemi(dir) {
    if (navigator.vibrate) {
        navigator.vibrate(50);
    }
    console.log("movemi", dir);
    //$('#cmitte').attr("fill", 'yellow');
    var message = new Paho.MQTT.Message('{"cmd":' + dir + '}');
    message.destinationName = joy;
    mqtt.send(message);
   
    //$('#cmitte').attr("fill", 'silver');
}

$(document).ready(function () {



    $("#top").click(function () {
        var message = new Paho.MQTT.Message('{"cmd":' + richtung.HOCH + '}');
        message.destinationName = joy;
        mqtt.send(message);
    });
    $("#down").click(function () {
        var message = new Paho.MQTT.Message('{"cmd":' + richtung.RUNTER + '}');
        message.destinationName = joy;
        mqtt.send(message);
    });
    $("#left").click(function () {
        var message = new Paho.MQTT.Message('{"cmd":' + richtung.LINKS + '}');
        message.destinationName = joy;
        mqtt.send(message);
    });
    $("#right").click(function () {
        var message = new Paho.MQTT.Message('{"cmd":' + richtung.RECHTS + '}');
        message.destinationName = joy;
        mqtt.send(message);
    });
    $("#save").click(function () {
        // den Inhalt des gewählten compass senden.
        var s = $('#inhalt').val();
        var obj = JSON.parse(s);
        obj["cmd"] = "save";
        obj["topic"] = $('#topic').val();
        s = JSON.stringify(obj);
        var message = new Paho.MQTT.Message(s);
        message.destinationName = 'simago/save';
        mqtt.send(message);
    });

    $("#selectMirror").on("change", function () {
        mqtt.unsubscribe(ziel);
        ziel = 'simago/compass/' + $(this).val();
        joy = ziel.replace('compass', 'joy');
        console.log(ziel);
        mqtt.subscribe(ziel);
    });

    ziel = 'simago/compass/';
    joy = ziel.replace('compass', 'joy');
    MQTTconnect();
    navigator.vibrate = navigator.vibrate || navigator.webkitVibrate || navigator.mozVibrate || navigator.msVibrate;

    if (navigator.vibrate) {
        console.log('vi yes');
        navigator.vibrate([50, 500, 50]);
    }





});


