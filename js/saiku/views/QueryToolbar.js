/*  
 *   Copyright 2012 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
 
/**
 * The query toolbar, and associated actions
 */
var QueryToolbar = Backbone.View.extend({

    

    events: {
        'click .options a.button': 'call',
        'click .renderer a.button' : 'switch_render_button'
    },
    
    table: {
        sparkType: null
    },
    chart: {},

    render_mode: "table",


    initialize: function(args) {
        // Keep track of parent workspace
        this.workspace = args.workspace;
        
        // Maintain `this` in callbacks
        _.bindAll(this, "call","activate_buttons", "spark_bar", "spark_line", "render_row_viz", "run_row_viz", "switch_render_button");
                
        // Activate buttons when a new query is created or run
        this.workspace.bind('query:new', this.activate_buttons);
        this.workspace.bind('query:result', this.activate_buttons);
        this.workspace.bind('table:rendered', this.run_row_viz);
        
    },
    
    activate_buttons: function(args) {
        if (typeof args != "undefined" && args != null ) {
            $(this.el).find('a').removeClass('disabled_toolbar');
            if (!args.data) {
                $(this.el).find('a.export_button, a.stats').addClass('disabled_toolbar');
            }
            if (isIE) {
                $(this.el).find('a.export_button').addClass('disabled_toolbar');
            }
        }      

    },

    template: function() {
        var template = $("#template-query-toolbar").html() || "";
        return _.template(template)();
    },
    
    render: function() {
        $(this.el).html(this.template());

        $(this.el).find('render_table').addClass('on');
        $(this.el).find('ul.table').show();
        return this; 
    },
    
    switch_render_button: function(event) {
        $target = $(event.target);
        event.preventDefault();
        if ($(event.target).hasClass('disabled_toolbar')) {
            return false;
        }
        $target.parent().siblings().find('.on').removeClass('on');
        if ($target.hasClass('render_chart')) {
            this.switch_render('chart');
            this.workspace.query.setProperty('saiku.ui.render.mode', 'chart');
            var c = $(this.el).find('ul.chart li a.on:first').size() > 0 ?
                        $(this.el).find('ul.chart li a.on:first').attr('href').replace('#', '')
                        : null;
            if (c != null) {
                this.workspace.query.setProperty('saiku.ui.render.type', c);
            }
        } else {
            this.switch_render('table');
            this.workspace.query.setProperty('saiku.ui.render.mode', 'table');
            this.workspace.query.setProperty('saiku.ui.render.type', this.table.sparkType);

        }


    },
    switch_render: function(render_type) {
        render_type = (typeof render_type != "undefined" ? render_type.toLowerCase() : "table");
        $(this.el).find('ul.renderer a.on').removeClass('on');
        $(this.el).find('ul.renderer a.render_' + render_type).addClass('on');
        if ("chart" == render_type) {
            $(this.el).find('ul.chart').show();
            $(this.el).find('ul.table').hide();
            this.render_mode = "chart";
            $(this.workspace.el).find('.workspace_results').children().hide();
            $(this.workspace.chart.el).children().hide();
            this.workspace.chart.show();
        } else {
            $(this.el).find('ul.chart').hide();
            $(this.el).find('ul.table').show();
            $(this.el).find('ul.table .stats').removeClass('on');
            $(this.workspace.el).find('.workspace_results table').show();
            $(this.workspace.chart.el).hide().children().hide();
            this.render_mode = "table";
            var hasRun = this.workspace.query.result.hasRun();
            if (hasRun) {
                this.workspace.table.render({ data: this.workspace.query.result.lastresult() });
            }
            
        }
        return false;
    },

    call: function(event) {
        event.preventDefault();
        $target = $(event.target).hasClass('button') ? $(event.target) : $(event.target).parent();
        if (! $target.hasClass('disabled_toolbar')) {
            // Determine callback
            var callback = $target.attr('href').replace('#', '');
            
            // Attempt to call callback
            if (this.render_mode == "table" && this[callback]) {
                this[callback](event);
            } else if (this.render_mode == "chart" && this.workspace.chart[callback]) {
                this.workspace.chart.button(event);
                this.workspace.chart[callback](event);
            }
        }
        return false;
    },

    spark_bar: function() {
        $(this.el).find('ul.table .spark_bar').toggleClass('on');
        $(this.el).find('ul.table .spark_line').removeClass('on');

        $(this.workspace.table.el).find('td.spark').remove();
        if ($(this.el).find('ul.table .spark_bar').hasClass('on')) {
            this.table.sparkType = "spark_bar";
            this.workspace.query.setProperty('saiku.ui.render.type', 'spark_bar');
            _.delay(this.render_row_viz, 10, "spark_bar");
        } else {
            this.table.sparkType = null;
        }
    },

    spark_line: function() {
        $(this.el).find('ul.table .spark_line').toggleClass('on');
        $(this.el).find('ul.table .spark_bar').removeClass('on');

        $(this.workspace.table.el).find('td.spark').remove();
        if ($(this.el).find('ul.table .spark_line').hasClass('on')) {
            this.table.sparkType = "spark_line";
            this.workspace.query.setProperty('saiku.ui.render.type', 'spark_line');
            _.delay(this.render_row_viz, 10, "spark_line");
        } else {
            this.table.sparkType = null;
        }
    },

    run_row_viz: function(args) {
        if (this.render_mode == "table" && this.table.sparkType != null) {
            this.render_row_viz(this.table.sparkType);
        }

    },

    render_row_viz: function(type) {
        $(this.workspace.table.el).find('tr').each(function(index, element) {
            var rowData = [];
            $(element).find('td.data div').each(function(i,data) {
                var val = $(data).attr('alt');
                val = (typeof val != "undefined" && val != "" && val != null && val  != "undefined") ? parseFloat(val) : 0;
                rowData.push(val);
            });
            
            $("<td class='data spark'>&nbsp;<div id='chart" + index + "'></div></td>").appendTo($(element));

            var width = rowData.length * 9;

                if (rowData.length > 0) {
                    var vis = new pv.Panel()
                        .canvas('chart' + index)
                        .height(12)
                        .width(width)
                        .margin(0);

                    if (type == "spark_bar") {
                        vis.add(pv.Bar)
                            .data(rowData)
                            .left(pv.Scale.linear(0, rowData.length).range(0, width).by(pv.index))
                            .height(pv.Scale.linear(0,_.max(rowData)).range(0, 12))
                            .width(6)
                            .bottom(0);        
                    } else if (type == "spark_line") {
                        width = width / 2;
                        vis.width(width);
                        vis.add(pv.Line)
                            .data(rowData)
                            .left(pv.Scale.linear(0, rowData.length - 1).range(0, width).by(pv.index))
                            .bottom(pv.Scale.linear(rowData).range(0, 12))
                            .strokeStyle("#000")
                            .lineWidth(1);        
                    }
                    vis.render();
                }
        });
    }
});
