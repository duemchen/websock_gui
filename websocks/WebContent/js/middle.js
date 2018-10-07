$(document).ready(
		function() {
			$("#von,#bis").datepicker({
				dateFormat: "dd.mm.yy",		
				showAnim: "slide",
				onSelect : function(date) {					
					startFillDiagramme();
				}
			});
			$("#von").datepicker("setDate", 'date -30d');
			$("#bis").datepicker("setDate", 'date +1d');
			

			$("#fill").click(function() {
				var table = $('#example').dataTable();
				table.api().clear().draw();
				// var jdata = [ {
				// 'id' : 123,
				// 'Uhrzeit' : '08:00',
				// 'Datum' : '1.2.2018',
				// 'Delta' : '10%',
				// 'Aktiv' : true
				// } ];
				// var data = [
				// [ '17', '12:00', '1.12.2018', '17%',
				// true, ],
				//
				// [ '12', '12:00', '1.12.2018', '12%',
				// true, ],
				// [ '123567', '12:00', '1.12.2018',
				// '123%', true, ]
				//
				// ];
				//
				// table.api().rows.add(data).draw();
				console.log('fill', '');
				var o = {
					cmd : 'table',
					zielid : gzielid
				};
				console.log('startFillDiagramme', JSON.stringify(o));
				sendMessage(o);

			});
			var table = $('#example').dataTable({
				"columnDefs" : [ {
					"targets" : [ 0 ], // index
					"visible" : false,
					"searchable" : false
				}, {
					"targets" : [ 1 ], // 
					"visible" : true
				}, {
					"targets" : [ 2 ], // 
					"visible" : true
				}, {
					"targets" : [ 3 ], // 
					"visible" : true
				}, {
					"targets" : [ 4 ], // löschflag
					"visible" : false
				} ],

				"createdRow" : function(row, data, dataIndex) {
					if (data[4]) {
						$(row).addClass('stroke');
					} else {
						$(row).removeClass('stroke');
					}

				}

			});
			$('#example tbody').on('click', 'tr', function() {
				// zeilenid senden um löschen zu schalten.
				var position = table.fnGetPosition(this);
				var id = table.fnGetData(position)[0];
				var flag = table.fnGetData(position)[4];
				var row = table.api().row(this).index();

				console.log('id', id, flag, row);

				var o = {
					cmd : 'edit',
					id : id,
					row : row,
					loesch : flag
				};
				var s = JSON.stringify(o);
				console.log('edit', s);
				sendMessage(o);
				// if ($(this).hasClass('selected')) {
				// $(this).removeClass('selected');
				// } else {
				// table.$('tr.selected').removeClass('selected');
				// $(this).addClass('selected');
				// }
				// table.fnUpdate('Zebra', this, 1);
			});

			// relativ zur aufrufenden URL den WebSocket berechnen
			var loc = window.location, new_uri;
			if (loc.protocol === "https:") {
				new_uri = "wss:";
			} else {
				new_uri = "ws:";
			}
			new_uri += "//" + loc.host;
			new_uri += loc.pathname + "/ws";
			// console.log('wsUrl', new_uri);

			ws = new WebSocket(new_uri);
			ws.onopen = function() {
				$('#xwsmessage').html('websocket is connected.');
			};

			// reines json. Kennung cmd als Sprungverteiler
			ws.onmessage = function(evt) {
				var msg = evt.data;
				// console.log("Message received roh: " + msg);
				var o = JSON.parse(msg);
				// console.log("Message received:" + JSON.stringify(o));
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
				if (o.cmd == 'save') {
					if (o.erfolg != null) {
						$("#save").prop("disabled", false);
						if (o.erfolg)
							$("#save").css('background-color', 'silver');
						else
							$("#save").css('background-color', 'red');
					}
					$('#xwsmessage').html("gespeichert " + JSON.stringify(o));
				}
				if (o.cmd == 'positionenTable') {
					// Die Liste füllen
					var jpos = o.positionen;
					console.log('fillToArray', jpos);
					var data = jPosToArray(jpos);
					console.log('arr', data);
					table.api().rows.add(data).draw();
					console.log('fill ready');
				}
				if (o.cmd == 'edit') {
					var id = o.id;
					var loesch = o.loesch;
					var row = o.row;
					table.fnUpdate(loesch, row, 4, false);
					var $r = table.api().rows(row).nodes().to$();
					console.log('$r', $r, table.api().rows(row));

					if (loesch) {
						$r.addClass('stroke');
					} else {
						$r.removeClass('stroke');
					}
				}
				if (o.cmd == 'bild') {
					// TODO Rahmen verändern je Bild
					var s = "data:image/png;base64," + o.data;
					$('#bild').attr("src", s);
				}
			};
			ws.onclose = function() {
				$('#xwsmessage').html('websocket is closed.');
			};

			$('#btndel').prop("disabled", true);

			$("#refresh")
			// Mit anderem Zeitraum erneuern.
			.click(function() {
				console.log("refresh");
				startFillDiagramme();

			});

			$("#btndel").click(function() {
				var message = new Paho.MQTT.Message('{"del":' + delid + '}');
				message.destinationName = 'simago/command';
				mqtt.send(message);
				$('#btndel').prop("disabled", true);
				delid = -1;
			});

			$("#selkunde").change(
					function() {
						console.log(this.selectedIndex, $(
								"#selkunde option:selected").val());
						fillSpiegel($("#selkunde option:selected").val());
					});

			$("#selspiegel").change(
					function() {
						console.log(this.selectedIndex, $(
								"#selspiegel option:selected").val());
						fillZiel($("#selspiegel option:selected").val());
					});
			$("#selziel").change(
					function() {
						console.log(this.selectedIndex, $(
								"#selziel option:selected").val());
						startFillDiagramme();
					});

			$("#selectMirror").on("change", function() {
				mqtt.unsubscribe(ziel);
				ziel = 'simago/compass/' + $(this).val();
				joy = ziel.replace('compass', 'joy');
				console.log(ziel);
				mqtt.subscribe(ziel);
			});
			// MQTTconnect();
			navigator.vibrate = navigator.vibrate || navigator.webkitVibrate
					|| navigator.mozVibrate || navigator.msVibrate;
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
				// sel = false;
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
function startFillDiagramme() {

	var zielid = $("#selziel option:selected").val();
	console.log('alt', gzielid, 'neu', zielid);
	gzielid = zielid;
	var o = {
		cmd : 'positionen',
		zielid : zielid,
		von : $("#von").val(),
		bis : $("#bis").val()
	};
	console.log('startFillDiagramme', JSON.stringify(o));
	sendMessage(o);
}

function fillDiagramme(data) {
	console.log('fillDiagramme', data);
	var jo = data;
	console.log('jo', JSON.stringify(jo));
	drawChartjsonTX(jo.zeit);
	drawChartjsonTY(jo.zeit);
	drawChartjsonTZ(jo.zeit);
	drawChartjsonX(jo.x);
	drawChartjsonY(jo.y);
	drawChartjsonZ(jo.z)
	drawChartjsonA(jo.a)
}

function jPosToArray(target) {
	var arr = [];
	$.each(target, function(i, e) {
		var item = [];
		// $.each(e, function(key, val){
		// item.push(val);
		// });
		item.push(e.id);
		item.push(e.zeit);
		item.push(e.datum);
		item.push(e.delta);
		item.push(e.loesch);
		arr.push(item);

	});
	return arr;
}
