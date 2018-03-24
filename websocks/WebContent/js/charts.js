var chartX;
var chartZ;
var chartY;
var chartA;

google.charts.load('current', {
	packages : [ 'corechart' ]
});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
	chartX = new google.visualization.ScatterChart($('#chart_x')[0]);
	chartZ = new google.visualization.ScatterChart($('#chart_z')[0]);
	chartY = new google.visualization.ScatterChart($('#chart_y')[0]);
	chartA = new google.visualization.ScatterChart($('#chart_a')[0]);

	//Baustelle!
	function selectHandler() {
		var selectedItem = this.getSelection()[0];
		if (selectedItem) {
			console.log(chartA.getSelection().length);
			console.log(selectedItem.row);
			if (!(selectedItem.row == null)) {
				var value = data.getValue(selectedItem.row, 0) + ', '
						+ data.getValue(selectedItem.row, 1);
				// alert('The user selected ' + value);
				console.log('row:', selectedItem.row, 'val:', value + ', id:'
						+ gIndextoId[selectedItem.row]);
				delid = gIndextoId[selectedItem.row];
				$('#btndel').prop("disabled", false);

			} else {
				console.log('deselect');
				$('#btndel').prop("disabled", true);
				delid = -1;
			}
		}

	}
	//	google.visualization.events.addListener(chartA, 'select', selectHandler());

}

function drawChartjsonX(x) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'X');
	data.addColumn('number', 'x');
	x.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.X, zeile.x, ]);
	})

	var options = {
		title : 'Horizontal Sonnen- zu Spiegelstand',
		hAxis : {
			title : 'Sonne'
		},
		vAxis : {
			title : 'Spiegel'
		},
		legend : 'right',
		trendlines : {
			0 : {
				type : 'linear', // exponential',
				visibleInLegend : true,
				color : 'purple',
				lineWidth : 20,
				opacity : 0.2,
				showR2 : true,
			}
		}
	// Draw a trendline for data series 0.
	};
	chartX.draw(data, options);
}
function drawChartjsonZ(z) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Y');
	data.addColumn('number', 'z');
	z.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.X, zeile.z, ]);
	})

	var options = {
		title : 'Horizontal Sonnen- zu Kippwinkel Spiegel',
		hAxis : {
			title : 'Sonne'
		},
		vAxis : {
			title : 'Spiegel'
		},
		legend : 'right',
		trendlines : {
			0 : {
				type : 'linear', // exponential',
				visibleInLegend : true,
				color : 'purple',
				lineWidth : 20,
				opacity : 0.2,
				showR2 : true,
			}
		}
	// Draw a trendline for data series 0.
	};
	chartZ.draw(data, options);
}

function drawChartjsonY(y) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Y');
	data.addColumn('number', 'y');
	y.forEach(function(zeile, index) {
		// console.log(zeile.Y + '.+.' + zeile.y);
		data.addRow([ zeile.Y, zeile.y, ]);
	})

	var options = {
		title : 'Höhe Sonnenstand zu Höhe Spiegelstand',
		hAxis : {
			title : 'Sonne'
		},
		vAxis : {
			title : 'Spiegel'
		},
		legend : 'right',
		trendlines : {
			0 : {
				type : 'linear', // exponential',
				visibleInLegend : true,
				color : 'purple',
				lineWidth : 20,
				opacity : 0.2,
				showR2 : true,
			}
		}
	};
	chartY.draw(data, options);
}

var gIndextoId = [];
var delid = -1;
function drawChartjsonA(a) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Sonne');
	data.addColumn('number', 'Spiegel');
	gIndextoId = [];
	a.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.Y, zeile.a, ]);

		gIndextoId.push(zeile.id);
	})

	var options = {
		title : 'Höhe Sonnenstand zu Lage Spiegel',
		hAxis : {
			title : 'Sonne'
		},
		vAxis : {
			title : 'Spiegel'
		},
		legend : 'right',
		trendlines : {
			0 : {
				type : 'linear', // exponential',
				visibleInLegend : true,
				color : 'purple',
				lineWidth : 10,
				opacity : 0.2,
				showR2 : true,
			}
		}
	};

	chartA.draw(data, options);
}
