
var SaikuTableRenderer = _.extend(SaikuRenderer, {

    key: "table",
});

SaikuTableRenderer.prototype._render = function(data, options) {
        var self = this;
        if (data) {
            this._data = data;
        }
        if (options) {
            this._options = _.extend({}, SaikuRendererOptions, options);
        }

        if (typeof this._data == "undefined") {
            return;
        }

        if (this._data != null && this._data.error != null) {
            return;
        }        
        if (this._data == null || (this._data.cellset && this._data.cellset.length === 0)) {
            return;
        }
        if (this._options.htmlObject) {
            // in case we have some left over scrollers
            $(self._options.htmlObject).parent().parent().unbind('scroll');

            _.defer(function(that) {
                var html =  self.internalRender(self._data, options);
                $(self._options.htmlObject).html(html);
                _.defer(function(that) {
                    if (options.batch && options.hasBatchResult) {                        
                        var batchRow = 0;
                        var batchIsRunning = false;
                        var batchIntervalSize = options.hasOwnProperty('batchIntervalSize') ? options.batchIntervalSize : 10;
                        var batchIntervalTime = options.hasOwnProperty('batchIntervalTime') ? options.batchIntervalTime : 10;

                        var len = options.batchResult.length;
                        
                        var batchInsert = function() {
                            // maybe add check for reach table bottom - ($('.workspace_results').scrollTop() , $('.workspace_results table').height()
                            if (!batchIsRunning && len > 0 && batchRow < len) {
                                batchIsRunning = true;
                                var batchContent = "";
                                var startb = batchRow;
                                for (var i = 0;  batchRow < len && i < batchIntervalSize ; i++, batchRow++) {
                                    batchContent += options.batchResult[batchRow];
                                }
                                if (batchRow > startb) {
                                    $(self._options.htmlObject).append( $(batchContent));
                                }
                                batchIsRunning = false;
                            }
                            if (batchRow >= len) {
                                $(self._options.htmlObject).parent().parent().unbind('scroll');
                            }
                        };

                        var lazyBatchInsert = _.debounce(batchInsert, batchIntervalTime);
                        $(self._options.htmlObject).parent().parent().scroll(function () { 
                            lazyBatchInsert();
                        });
                    }
                });
                return html;
            });
        } else {
            var html =  this.internalRender(this._data.cellset, options);
            return html;
        }
        
};

SaikuTableRenderer.prototype._processData = function(data, options) {
    this._hasProcessed = true;
};

function genTotalDataCells(currentIndex, cellIndex, scanSums, scanIndexes, lists) {
	var contents = '';
	var lists = lists[ROWS];
	for (var i = scanSums.length - 1; i >= 0; i--) {
		if (currentIndex == scanSums[i]) {
			var currentListNode = lists[i][scanIndexes[i]];
			for (var m = 0; m < currentListNode.cells.length; m++)
				contents += '<td class="data total">' + currentListNode.cells[m][cellIndex].value + '</td>';
			scanIndexes[i]++;
			if (scanIndexes[i] < lists[i].length)
				scanSums[i] += lists[i][scanIndexes[i]].width;
		}
	}
	return contents;
}

function genTotalHeaderCells(currentIndex, bottom, scanSums, scanIndexes, lists) {
	var contents = '';
	for (var i = bottom; i >= 0; i--) {
		if (currentIndex == scanSums[i]) {
			var currentListNode = lists[i][scanIndexes[i]];
			var cssClass;
			if (i == 0 && bottom == 1)
				cssClass = "col";
			else if (i == bottom)
				cssClass = "col_total_corner";
			else if (i == bottom - 1 && currentListNode.captions)
				cssClass = "col_total_first";
			else cssClass = "col_null";
			
			for (var m = 0; m < currentListNode.cells.length; m++) {
				var text = '&nbsp;';
				if (bottom == lists.length - 1) {
					if (currentListNode.captions) {
						text = lists[i][scanIndexes[i]].captions[m];
					}
					if (i == 0 && scanIndexes[i] == 0) {
						if (currentListNode.captions)
							text += "<span class='i18n'> Grand Total</span>";
						else text = "<span class='i18n'>Grand Total</span>";
					}
				}
				contents += '<th class="' + cssClass + '"><div>' + text + '</div></th>';
			}
			scanIndexes[i]++;
			if (scanIndexes[i] < lists[i].length)
				scanSums[i] += lists[i][scanIndexes[i]].width;
		}
	}
	return contents;
}

function totalIntersectionCells(currentIndex, bottom, scanSums, scanIndexes, lists) {
	var contents = '';
	for (var i = bottom; i >= 0; i--) {
		if (currentIndex == scanSums[i]) {
			var currentListNode = lists[i][scanIndexes[i]];
			var cssClass = "data total";
			for (var m = 0; m < currentListNode.cells.length; m++) {
				var text = '&nbsp;';
				contents += '<td class="' + cssClass + '">' + text + '</td>';
			}
			scanIndexes[i]++;
			if (scanIndexes[i] < lists[i].length)
				scanSums[i] += lists[i][scanIndexes[i]].width;
		}
	}
	return contents;
}

function genTotalHeaderRowCells(currentIndex, scanSums, scanIndexes, totalsLists) {
	var colLists = totalsLists[COLUMNS];
	var colScanSums = scanSums[COLUMNS];
	var colScanIndexes = scanIndexes[COLUMNS];
	var bottom = colLists.length - 2;
	var contents = '';
	for (var i = bottom; i >= 0; i--) {
		if (currentIndex == colScanSums[i]) {
			for (var m = 0; m < colLists[i][colScanIndexes[i]].cells.length; m++) {
				contents += '<tr>';
				for (var j = 0; j <= bottom; j++) {
					var cssClass;
					var text = '&nbsp;';
					if (i == 0 && j == 0)
						cssClass = 'row';
					else if (i == j + 1) 
						cssClass = 'row_total_corner';
					else if (i == j && colLists[i][colScanIndexes[i]].captions) {
						cssClass = 'row_total_first';
					} else if (i < j + 1)
						cssClass = 'row_total';
					else
						cssClass = 'row_null';
					if (j == bottom ) {
						if (colLists[i][colScanIndexes[i]].captions) {
							text = colLists[i][colScanIndexes[i]].captions[m];
						}
						if (i == 0 && colScanIndexes[i] == 0) {
							if (colLists[i][colScanIndexes[i]].captions)
								text += "<span class='i18n'> Grand Total</span>";
							else text = "<span class='i18n'>Grand Total</span>";
						}
					}
					contents += '<th class="' + cssClass + '"><div>' + text + '</div></th>';
				}
				
				var scanIndexes = {};
				var scanSums = {};
				for (var z = 0; z < totalsLists[ROWS].length; z++) {
		        	scanIndexes[z] = 0;
		        	scanSums[z] = totalsLists[ROWS][z][scanIndexes[z]].width;
		        }
				for (var k = 0; k < colLists[i][colScanIndexes[i]].cells[m].length; k++) {
					contents += '<td class="data total">' + colLists[i][colScanIndexes[i]].cells[m][k].value + '</td>';
					contents += totalIntersectionCells(k + 1, totalsLists[ROWS].length - 1, scanSums, scanIndexes, totalsLists[ROWS]);
				}
				contents += '</tr>';
			}
			colScanIndexes[i]++;
			if (colScanIndexes[i] < colLists[i].length)
				colScanSums[i] += colLists[i][colScanIndexes[i]].width;
		}
	}
	return contents;
}

var ROWS = "ROWS";
var COLUMNS = "COLUMNS";

function nextParentsDiffer(data, row, col) {
	while (row-- > 0)
		if (data[row][col].properties.uniquename != data[row][col + 1].properties.uniquename)
			return true;
	return false;
}


function topParentsDiffer(data, row, col) {
	while (col-- > 0)
		if (data[row][col].properties.uniquename != data[row - 1][col].properties.uniquename)
			return true;
	return false;
}

SaikuTableRenderer.prototype.internalRender = function(allData, options) {
    var tableContent = "";
    var rowContent = "";
	var data = allData.cellset;

    var table = data ? data : [];
    var colSpan;
    var colValue;
    var isHeaderLowestLvl;
    var isBody = false;
    var firstColumn;
    var isLastColumn, isLastRow;
    var nextHeader;
    var processedRowHeader = false;
    var lowestRowLvl = 0;
    var rowGroups = [];
    var batchSize = null;
    var batchStarted = false;
    var resultRows = [];
    if (options) {
        batchSize = options.hasOwnProperty('batchSize') ? options.batchSize : null;
    }
    var totalsLists = {};
    totalsLists[COLUMNS] = allData.rowTotalsLists;
    totalsLists[ROWS] = allData.colTotalsLists;
    
    var scanSums = {};
    var scanIndexes = {};
    
    var dirs = [ROWS, COLUMNS];
    
    for (var i = 0; i < dirs.length; i++) {
        scanSums[dirs[i]] = new Array();
        scanIndexes[dirs[i]] = new Array();
    }
    if (totalsLists[COLUMNS])
		for (var i = 0; i < totalsLists[COLUMNS].length; i++) {
	    	scanIndexes[COLUMNS][i] = 0;
	    	scanSums[COLUMNS][i] = totalsLists[COLUMNS][i][scanIndexes[COLUMNS][i]].width;
	    }

    for (var row = 0, rowLen = table.length; row < rowLen; row++) {
    	var rowShifted = row - allData.topOffset;
        colSpan = 1;
        colValue = "";
        isHeaderLowestLvl = false;
        isLastColumn = false;
        isLastRow = false;
        var headerSame = false;

        if (totalsLists[ROWS])
	        for (var i = 0; i < totalsLists[ROWS].length; i++) {
	        	scanIndexes[ROWS][i] = 0;
	        	scanSums[ROWS][i] = totalsLists[ROWS][i][scanIndexes[ROWS][i]].width;
	        }
        rowContent = "<tr>";
        for (var col = 0, colLen = table[row].length; col < colLen; col++) {
		    var colShifted = col - allData.leftOffset;
            var header = data[row][col];


            // If the cell is a column header and is null (top left of table)
            if (header.type === "COLUMN_HEADER" && header.value === "null" && (firstColumn == null || col < firstColumn)) {
                rowContent += '<th class="all_null"><div>&nbsp;</div></th>';
            } // If the cell is a column header and isn't null (column header of table)
            else if (header.type === "COLUMN_HEADER") {
                if (firstColumn == null) {
                    firstColumn = col;
                }
                if (table[row].length == col+1)
                    isLastColumn = true;
                else
                    nextHeader = data[row][col+1];


                if (isLastColumn) {
                    // Last column in a row...
                    if (header.value == "null") {
                        rowContent += '<th class="col_null"><div>&nbsp;</div></th>';
                    } else {
                    	if (totalsLists[ROWS])
                    		colSpan = totalsLists[ROWS][row + 1][scanIndexes[ROWS][row + 1]].span;
                        rowContent += '<th class="col" style="text-align: center;" colspan="' + colSpan + '" title="' + header.value + '"><div rel="' + row + ":" + col +'">' + header.value + '</div></th>';    
                    }
                    
                } else {
                    // All the rest...
                    var groupChange = (col > 1 && row > 1 && !isHeaderLowestLvl && col > firstColumn) ?
                        data[row-1][col+1].value != data[row-1][col].value || data[row-1][col+1].properties.uniquename != data[row-1][col].properties.uniquename
                        : false;

                    var maxColspan = colSpan > 999 ? true : false;
                    if (header.value != nextHeader.value || nextParentsDiffer(data, row, col) || isHeaderLowestLvl || groupChange || maxColspan) {
                        if (header.value == "null") {
                            rowContent += '<th class="col_null" colspan="' + colSpan + '"><div>&nbsp;</div></th>';
                        } else {
                        	if (totalsLists[ROWS])
                        		colSpan = totalsLists[ROWS][row + 1][scanIndexes[ROWS][row + 1]].span;
                            rowContent += '<th class="col" style="text-align: center;" colspan="' + (colSpan == 0 ? 1 : colSpan) + '" title="' + header.value + '"><div rel="' + row + ":" + col +'">' + header.value + '</div></th>';
                        }
                        colSpan = 1;
                    } else {
                        colSpan++;
                    }
                }
                if (totalsLists[ROWS])
                	rowContent += genTotalHeaderCells(col - allData.leftOffset + 1, row + 1, scanSums[ROWS], scanIndexes[ROWS], totalsLists[ROWS]);
            } // If the cell is a row header and is null (grouped row header)
            else if (header.type === "ROW_HEADER" && header.value === "null") {
                rowContent += '<th class="row_null"><div>&nbsp;</div></th>';
            } // If the cell is a row header and isn't null (last row header)
            else if (header.type === "ROW_HEADER") {
                if (lowestRowLvl == col)
                    isHeaderLowestLvl = true;
                else
                    nextHeader = data[row][col+1];

                var previousRow = data[row - 1];

                var same = !headerSame && !isHeaderLowestLvl && (col == 0 || !topParentsDiffer(data, row, col)) && header.value === previousRow[col].value;
                headerSame = !same;                var value = (same ? "<div>&nbsp;</div>" : '<div rel="' + row + ":" + col +'">' + header.value + '</div>');
                var tipsy = "";
                /* var tipsy = ' original-title="';
                if (!same && header.metaproperties) {
                    for (key in header.metaproperties) {
                        if (key.substring(0,1) != "$" && key.substring(1,2).toUpperCase() != key.substring(1,2)) {
                            tipsy += "<b>" + safe_tags_replace(key) + "</b> : " + safe_tags_replace(header.metaproperties[key]) + "<br>";
                        }
                    }
                }
                tipsy += '"';
                */
                var cssclass = (same ? "row_null" : "row");
                var colspan = 0;

                if (!isHeaderLowestLvl && (typeof nextHeader == "undefined" || nextHeader.value === "null")) {
                    colspan = 1;
                    var group = header.properties.dimension;
                    var level = header.properties.level;
                    var groupWidth = (group in rowGroups ? rowGroups[group].length - rowGroups[group].indexOf(level) : 1);
                    for (var k = col + 1; colspan < groupWidth && k <= (lowestRowLvl+1) && data[row][k] !== "null"; k++) {
                        colspan = k - col;
                    }
                    col = col + colspan -1;
                }
                rowContent += '<th class="' + cssclass + '" ' + (colspan > 0 ? ' colspan="' + colspan + '"' : "") + tipsy + '>' + value + '</th>';
            }
            else if (header.type === "ROW_HEADER_HEADER") {
                rowContent += '<th class="row_header"><div>' + header.value + '</div></th>';
                isHeaderLowestLvl = true;
                processedRowHeader = true;
                lowestRowLvl = col;
                if (header.properties.hasOwnProperty("dimension")) {
                    var group = header.properties.dimension;
                    if (!(group in rowGroups)) {
                        rowGroups[group] = [];
                    }
                    rowGroups[group].push(header.properties.level);
                }
            } // If the cell is a normal data cell
            else if (header.type === "DATA_CELL") {
                batchStarted = true;
                var color = "";
                var val = header.value;
                var arrow = "";
                if (header.properties.hasOwnProperty('image')) {
                    var img_height = header.properties.hasOwnProperty('image_height') ? " height='" + header.properties.image_height + "'" : "";
                    var img_width = header.properties.hasOwnProperty('image_width') ? " width='" + header.properties.image_width + "'" : "";
                    val = "<img " + img_height + " " + img_width + " style='padding-left: 5px' src='" + header.properties.image + "' border='0'>";
                }

                if (header.properties.hasOwnProperty('style')) {
                    color = " style='background-color: " + header.properties.style + "' ";
                }
                if (header.properties.hasOwnProperty('link')) {
                    val = "<a target='__blank' href='" + header.properties.link + "'>" + val + "</a>";
                }
                if (header.properties.hasOwnProperty('arrow')) {
                    arrow = "<img height='10' width='10' style='padding-left: 5px' src='./images/arrow-" + header.properties.arrow + ".gif' border='0'>";
                }

                rowContent += '<td class="data" ' + color + '><div alt="' + header.properties.raw + '" rel="' + header.properties.position + '">' + val + arrow + '</div></td>';
                if (totalsLists[ROWS])
                	rowContent += genTotalDataCells(colShifted + 1, rowShifted, scanSums[ROWS], scanIndexes[ROWS], totalsLists);
            }
        }
        rowContent += "</tr>";
        if (batchStarted && batchSize) {
                if (row <= batchSize) {
                    tableContent += rowContent;
                } else {
                    resultRows.push(rowContent);        
                }
        } else {
            tableContent += rowContent;
        }
        if (totalsLists[COLUMNS] && rowShifted >= 0) {
        	var totals = "";
       		totals += genTotalHeaderRowCells(rowShifted + 1, scanSums, scanIndexes, totalsLists);
        	
        	if (totals.length > 0)
        		tableContent += totals;
        }
        
    }
    if (options) {
        options['batchResult'] = resultRows;
        options['hasBatchResult'] = true;
    }
    return "<table>" + tableContent + "</table>";
}