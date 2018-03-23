var mqtt;
var reconnectTimeout = 200000; //200;
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
    //console.log('Connected too ' + host + ':' + port + path);
    $('#message').html('Connected too ' + host + ':' + port + path);
    // Connection succeeded; subscribe to our topic
    //mqtt.subscribe(topic, {qos: 2});		
    //mqtt.subscribe('simago/tinker', {qos: 0});
    //mqtt.subscribe('simago/compass', {qos: 0});
    //mqtt.subscribe('simago/joy', {qos: 0});
    //mqtt.subscribe('simago/veranda', {qos: 0});
    //mqtt.subscribe('simago/heizung', {qos: 0});
//    mqtt.subscribe('simago/position/#', {qos: 0});
    mqtt.subscribe('simago/kunden', {qos: 0});

}

function onConnectionLost(response) {
    setTimeout(MQTTconnect, reconnectTimeout);
    $('#inhalt').val("connection lost: " + response.errorMessage + ". Reconnecting");
    console.log(response.errorMessage);


}

function onMessageArrived(message) {
    var topic = message.destinationName;
    var payload = message.payloadString;
    console.log(topic + '  ' + new Date());

    //if (topic == 'simago/position/80-1F-02-ED-FD-A6') {
    if (topic.includes('simago/position/')) {
        //console.log('POSITION');
        var jo = JSON.parse(payload);
        console.log(jo.a);
        drawChartjsonY(jo.y);
        drawChartjsonX(jo.x);
        drawChartjsonZ(jo.z)
        drawChartjsonA(jo.a)
        

    }
    if (topic == 'simago/kunden') {
        //console.log(topic, payload);
        var jo = JSON.parse(payload);
        var kunden = jo.kunden;
        fillKunden(kunden);
        kunden.forEach(function (zeile) {

            // console.log(zeile.kunde, index);
            var spiegels = zeile.spiegel;
            spiegels.forEach(function (zeile) {
                console.log(zeile.name, zeile.mac);
            })
        })
    }
    if (topic == 'xxsimago/mirrors') {
        console.log(topic, payload);
        // die Liste neu füllen und anklicken
        //$('#zustand').val(payload);
//{"inhalt":"MirrorList","liste":[{"status":true,"name":"74-DA-38-3E-E8-3C","mac":"74-DA-38-3E-E8-3C"},{"status":true,"name":"80-1F-02-ED-FD-A6","mac":"80-1F-02-ED-FD-A6"}]}
        var o = jQuery.parseJSON(payload);
        var mirrors = o.liste;
        //console.log('mirrors', mirrors);
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









