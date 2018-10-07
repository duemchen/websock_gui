var chartX;
var chartZ;
var chartY;
var chartA;
var chartTX;
var chartTY;
var chartTZ;

google.charts.load('current', {
	packages : [ 'corechart' ]
});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
	chartX = new google.visualization.ScatterChart($('#chart_x')[0]);
	chartZ = new google.visualization.ScatterChart($('#chart_z')[0]);
	chartY = new google.visualization.ScatterChart($('#chart_y')[0]);
	chartA = new google.visualization.ScatterChart($('#chart_a')[0]);
	chartTX = new google.visualization.ScatterChart($('#chart_tx')[0]);
	chartTY = new google.visualization.ScatterChart($('#chart_ty')[0]);
	chartTZ = new google.visualization.ScatterChart($('#chart_tz')[0]);
}

function drawChartjsonX(x) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'X');
	data.addColumn('number', 'x');
	data.addColumn('number', 'xx'); // die funktion
	x.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.X, zeile.x, zeile.xx ]);
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
				lineWidth : 10,
				opacity : 0.2,
				showR2 : true,
			}
		},
		series : {
			0 : {
				color : '#43459d'
			},
			1 : {
				color : '#f3459d'
			},

		},

	};
	chartX.draw(data, options);
}
function drawChartjsonZ(z) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Y');
	data.addColumn('number', 'z');
	data.addColumn('number', 'zz');
	z.forEach(function(zeile, index) {
		data.addRow([ zeile.X, zeile.z, 2 * zeile.z ]);
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
			},

		},
		series : {
			0 : {
				color : '#43459d'
			},
			1 : {
				color : '#f3459d'
			},

		},

	};
	chartZ.draw(data, options);
	google.visualization.events.addListener(chartZ, 'onmouseover', function(e) {
		// console.log(e.row, data.getValue(e.row, 0), data.getValue(e.row, 1));
		// setTooltipContent(data, e.row);
	});
}

function drawChartjsonY(y) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Y');
	data.addColumn('number', 'y');
	data.addColumn({
		type : 'string',
		role : 'tooltip',
		name : 'zp'
	});

	y.forEach(function(zeile, index) {
		// console.log(zeile.Y + '.+.' + zeile.y);
		data.addRow([ zeile.Y, zeile.y, zeile.zp ]);
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
				lineWidth : 10,
				opacity : 0.2,
				showR2 : false,
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
	data.addColumn('number', 'mess');
	data.addColumn({
		type : 'string',
		role : 'tooltip',
		name : 'zp'
	});
	data.addColumn('number', 'calc');

	gIndextoId = [];
	a.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.Y, zeile.a, zeile.zp, zeile.aa ]);

		gIndextoId.push(zeile.id);
	})

	var options = {
		legend : {
			position : 'top',
			alignment : 'end'
		},

		title : 'Höhe Sonnenstand zu Lage Spiegel',
		hAxis : {
			title : 'Sonne',
			xviewWindow : {
				min : 10,
				max : 70
			}
		},
		vAxis : {
			title : 'Spiegel',
			xviewWindow : {
				min : 0,
				max : 30
			}
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
		},
		series : {
			0 : {
				color : '#43459d'
			},
			1 : {
				color : '#f3459d'
			},

		},

	};

	chartA.draw(data, options);

	google.visualization.events.addListener(chartA, 'select', function() {
		var selectedItem = chartA.getSelection()[0];
		if (selectedItem) {
			var row = selectedItem.row;
			var id = gIndextoId[row];
			console.log('row:', row, 'id:', id);

		}
	});

	// google.visualization.events.addListener(chartA, 'select',
	// selectHandler());
	//	
	// function selectHandler() {
	// console.log('selectHandler',e);
	// }
	//

}


/*********************************************************************************************
 * 
 *   X
 * 
 * 
 */




function drawChartjsonTX(a) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Std');
	data.addColumn('number', 'x');
	data.addColumn({
		type : 'string',
		role : 'tooltip',
		name : 'zp'
	});
	

	gIndextoId = [];
	a.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.T, zeile.x, zeile.zp]);

		gIndextoId.push(zeile.id);
	})

	var options = {
		legend : {
			position : 'top',
			alignment : 'end'
		},

		title : 'Tagesverlauf Spiegel, horizontal',
		hAxis : {
			title : 'Zeit',
			viewWindow : {
				min : 8,
				max : 18
			}
		},
		vAxis : {
			title : 'Spiegel',
			viewWindow : {
				min : 130,
				max : 210
			}
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
		},
		series : {
			0 : {
				color : '#43459d'
			},
			1 : {
				color : '#f3459d'
			},

		},

	};

	chartTX.draw(data, options);

	google.visualization.events.addListener(chartTX, 'select', function() {
		var selectedItem = chartTX.getSelection()[0];
		if (selectedItem) {
			var row = selectedItem.row;
			var id = gIndextoId[row];
			console.log('row:', row, 'id:', id);

		}
	});
	
}







/*********************************************************************************************
 * 
 * 
 *           Y 
 * 
 */




function drawChartjsonTY(a) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Std');
	data.addColumn('number', 'x');
	data.addColumn({
		type : 'string',
		role : 'tooltip',
		name : 'zp'
	});
	

	gIndextoId = [];
	a.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.T, zeile.y, zeile.zp]);

		gIndextoId.push(zeile.id);
	})

	var options = {
		legend : {
			position : 'top',
			alignment : 'end'
		},

		title : 'Tagesverlauf Spiegel, vertikal',
		hAxis : {
			title : 'Zeit',
			viewWindow : {
				min : 8,
				max : 18
			}
		},
		vAxis : {
			title : 'Spiegel',
			xviewWindow : {
				min : 5,
				max : 20
			}
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
		},
		series : {
			0 : {
				color : '#43459d'
			},
			1 : {
				color : '#f3459d'
			},

		},

	};

	chartTY.draw(data, options);

	google.visualization.events.addListener(chartTY, 'select', function() {
		var selectedItem = chartTY.getSelection()[0];
		if (selectedItem) {
			var row = selectedItem.row;
			var id = gIndextoId[row];
			console.log('row:', row, 'id:', id);

		}
	});
	
}







/*********************************************************************************************
 * 
 *   Z
 * 
 * 
 */




function drawChartjsonTZ(a) {
	var data = new google.visualization.DataTable();
	data.addColumn('number', 'Std');
	data.addColumn('number', 'z');
	data.addColumn({
		type : 'string',
		role : 'tooltip',
		name : 'zp'
	});
	

	gIndextoId = [];
	a.forEach(function(zeile, index) {
		// console.log(zeile.X + '.+.' + zeile.x);
		data.addRow([ zeile.T, zeile.z, zeile.zp]);

		gIndextoId.push(zeile.id);
	})

	var options = {
		legend : {
			position : 'top',
			alignment : 'end'
		},

		title : 'Tagesverlauf Spiegel, Kippwinkel',
		hAxis : {
			title : 'Zeit',
			viewWindow : {
				min : 8,
				max : 18
			}
		},
		vAxis : {
			title : 'Spiegel',
			viewWindow : {
				min : -15,
				max : 10
			}
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
		},
		series : {
			0 : {
				color : '#43459d'
			},
			1 : {
				color : '#f3459d'
			},

		},

	};

	chartTZ.draw(data, options);

	google.visualization.events.addListener(chartTZ, 'select', function() {
		var selectedItem = chartTZ.getSelection()[0];
		if (selectedItem) {
			var row = selectedItem.row;
			var id = gIndextoId[row];
			console.log('row:', row, 'id:', id);

		}
	});
	
}




