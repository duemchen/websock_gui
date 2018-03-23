google.charts.load('current', {
	packages : [ 'corechart' ]
});
google.charts.setOnLoadCallback(drawChart);
function drawChart() {
	var data = google.visualization.arrayToDataTable([ [ 'Diameter', 'Age' ],
			[ 8, 37 ], [ 4, 19.5 ], [ 11, 52 ], [ 4, 22 ], [ 3, 16.5 ],
			[ 6.5, 32.8 ], [ 14, 72 ] ]);
	var options = {
		title : 'Age of sugar maples vs. trunk diameter, in inches',
		hAxis : {
			title : 'Diameter'
		},
		vAxis : {
			title : 'Age'
		},
		legend : 'right',
		trendlines : {
			0 : {
				type : 'exponential',
				visibleInLegend : true,
				color : 'purple',
				lineWidth : 20,
				opacity : 0.2,
				showR2 : true,
			}
		}
	// Draw a trendline for data series 0.
	};
	// var chart = new
	// google.visualization.ScatterChart(document.getElementById('chart_div'));
	var chart = new google.visualization.ScatterChart($('#chart_div')[0]);
	chart.draw(data, options);
}

function drawChart2() {
	var data = google.visualization.arrayToDataTable([ [ 'Diameter', 'Age' ],
			[ 8, 47 ], [ 4, 19.5 ], [ 11, 52 ], [ 4, 22 ], [ 3, 16.5 ],
			[ 6.5, 32.8 ], [ 14, 72 ] ]);
	var options = {
		title : 'Age of sugar maples vs. trunk diameter, in inches',
		hAxis : {
			title : 'Diameter'
		},
		vAxis : {
			title : 'Age'
		},
		legend : 'right',
		trendlines : {
			0 : {
				type : 'exponential',
				visibleInLegend : true,
				color : 'purple',
				lineWidth : 20,
				opacity : 0.2,
				showR2 : true,
			}
		}
	// Draw a trendline for data series 0.selkunde

	};
	// var chart = new
	// google.visualization.ScatterChart(document.getElementById('chart_div'));
	var chart = new google.visualization.ScatterChart($('#chart_div')[0]);
	chart.draw(data, options);
}

function drawChartjsonY(y) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Y');
	data.addColumn('number', 'y');
	y.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
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
	// Draw a trendline for data series 0.
	};
	// var chart = new
	// google.visualization.ScatterChart(document.getElementById('chart_div'));
	var chart = new google.visualization.ScatterChart($('#chart_y')[0]);
	chart.draw(data, options);
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
		title : 'Horizontale Richtung Sonnen zu Spiegelstand',
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
	// var chart = new
	// google.visualization.ScatterChart(document.getElementById('chart_div'));
	var chart = new google.visualization.ScatterChart($('#chart_x')[0]);
	chart.draw(data, options);
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
		title : 'Horizontale Richtung Sonnen zu Kippwinkel Spiegel',
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
	// var chart = new
	// google.visualization.ScatterChart(document.getElementById('chart_div'));
	var chart = new google.visualization.ScatterChart($('#chart_z')[0]);

	chart.draw(data, options);
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
		title : 'Sonnenstand zu Lage Spiegel',
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
	// var chart = new
	// google.visualization.ScatterChart(document.getElementById('chart_div'));
	var chart = new google.visualization.ScatterChart($('#chart_a')[0]);
	function selectHandler() {
		var selectedItem = chart.getSelection()[0];
		if (selectedItem) {
			console.log(chart.getSelection().length);
			console.log(selectedItem.row);
			if (!(selectedItem.row == null)) {
				var value = data.getValue(selectedItem.row, 0) + ', '
						+ data.getValue(selectedItem.row, 1);
				// alert('The user selected ' + value);
				console.log('row:', selectedItem.row, 'val:', value + ', id:'
						+ gIndextoId[selectedItem.row]);
				delid = gIndextoId[selectedItem.row];
				$('#btndel').prop("disabled", false);
			}
		} else {
			console.log('deselect');
			$('#btndel').prop("disabled", true);
			delid = -1;
		}
	}
	google.visualization.events.addListener(chart, 'select', selectHandler);
	chart.draw(data, options);
}
