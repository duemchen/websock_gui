$(document)
		.ready(
				function() {
					$('#btndel').prop("disabled", true);
					$("#btndel").click(
							function() {
								var message = new Paho.MQTT.Message('{"del":'
										+ delid + '}');
								message.destinationName = 'simago/command';
								mqtt.send(message);
								$('#btndel').prop("disabled", true);
								delid = -1;
							});

					$("#selkunde").change(
							function() {
								console.log(this.selectedIndex, $(
										"#selkunde option:selected").val());
								fillSpiegel($("#selkunde option:selected")
										.val());
							});

					$("#selspiegel")
							.change(
									function() {
										console.log(this.selectedIndex, $(
												"#selspiegel option:selected")
												.val());
										fillZiel($(
												"#selspiegel option:selected")
												.val());
									});
					$("#selziel")
							.change(
									function() {
										console.log(this.selectedIndex, $(
												"#selziel option:selected")
												.val());
										startFillDiagramme($(
												"#selziel option:selected")
												.val());
									});

					$("#top").click(
							function() {
								var message = new Paho.MQTT.Message('{"cmd":'
										+ richtung.HOCH + '}');
								message.destinationName = joy;
								mqtt.send(message);
							});
					$("#down").click(
							function() {
								var message = new Paho.MQTT.Message('{"cmd":'
										+ richtung.RUNTER + '}');
								message.destinationName = joy;
								mqtt.send(message);
							});
					$("#left").click(
							function() {
								var message = new Paho.MQTT.Message('{"cmd":'
										+ richtung.LINKS + '}');
								message.destinationName = joy;
								mqtt.send(message);
							});
					$("#right").click(
							function() {
								var message = new Paho.MQTT.Message('{"cmd":'
										+ richtung.RECHTS + '}');
								message.destinationName = joy;
								mqtt.send(message);
							});
					$("#save").click(function() {
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
					$("#selectMirror").on("change", function() {
						mqtt.unsubscribe(ziel);
						ziel = 'simago/compass/' + $(this).val();
						joy = ziel.replace('compass', 'joy');
						console.log(ziel);
						mqtt.subscribe(ziel);
					});
					ziel = 'simago/compass/';
					joy = ziel.replace('compass', 'joy');
					MQTTconnect();
					navigator.vibrate = navigator.vibrate
							|| navigator.webkitVibrate || navigator.mozVibrate
							|| navigator.msVibrate;
					if (navigator.vibrate) {
						// console.log('vi yes');
						navigator.vibrate([ 50, 500, 50 ]);
					}

				});

// **********************************************************************************************************************

var gkunde;
var gzielid = -1;

function fillKunden(kunden) {
	gkunden = kunden;
	$("#selkunde").empty();
	console.log('KUNDEN', kunden);
	var sel = true;
	kunden.forEach(function(kunde) {
		$("#selkunde").append(
				$("<option>").attr("value", kunde.id).attr('selected', sel)
						.text(kunde.name));
		sel = false;
	})
	// $('#selkunde').selectmenu('refresh');
	$('#selkunde').change();

}

function fillSpiegel(kundenid) {
	// console.log('fillspiegel', kundenid);
	$('#selspiegel').empty();
	var sel = true;
	gkunden.forEach(function(kunde) {
		if (kunde.id == kundenid) {
			var spiegels = kunde.spiegel;
			spiegels.forEach(function(spiegel) {
				$('#selspiegel').append(
						$('<option>').attr('value', spiegel.id).attr(
								'selected', sel).text(
								spiegel.name + ' (' + spiegel.mac + ')'));
				sel = false;
			});
		}
	});
	$('#selspiegel').change();
}

function fillZiel(spiegelid) {
	// console.log('fillziel', spiegelid);
	$('#selziel').empty();
	var sel = true;
	gkunden.forEach(function(kunde) {
		var spiegels = kunde.spiegel;
		spiegels.forEach(function(spiegel) {
			// console.log('spiegel', spiegel);
			if (spiegel.id == spiegelid) {
				// ziele
				var ziele = spiegel.ziele;
				ziele.forEach(function(ziel) {
					$('#selziel').append(
							$('<option>').attr('value', ziel.id).attr(
									'selected', sel).text(
									ziel.name + ' (' + ziel.id + ')'));
					sel = false;
				});
			}
		});
	});
	$('#selziel').change();
}
// schickt das Kommando an den Server, die Diagramme für das Ziel zu übertragen
function startFillDiagramme(zielid) {
	console.log('alt', gzielid, 'neu', zielid);
	gzielid = zielid;
	var o = {
		cmd : 'positionen',
		zielid : zielid
	};
	// console.log('o', JSON.stringify(o));
	sendMessage(o);
}

function fillDiagramme(data) {
	console.log(data);
	var jo = data;
	console.log('jo', JSON.stringify(jo));
	drawChartjsonY(jo.y);
	drawChartjsonX(jo.x);
	drawChartjsonZ(jo.z)
	drawChartjsonA(jo.a)

}
