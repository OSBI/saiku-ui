/*
 * WorkspaceDropZone
.js
 * 
 * Copyright (c) 2011, OSBI Ltd. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
/**
 * Sets up workspace drop zones for DnD and other interaction
 */
var WorkspaceDropZone = Backbone.View.extend({
    template: function() {
        var template = $("#template-workspace-dropzones").html() || "";
        return _.template(template)();
    },
    
    events: {
        'sortbeforestop': 'select_dimension',
        'click .d_dimension': 'selections',
        'click .d_measure' : 'remove_dimension'
    },
    
    initialize: function(args) {
        // Keep track of parent workspace
        this.workspace = args.workspace;
        
        // Maintain `this` in jQuery event handlers
        _.bindAll(this, "select_dimension", "move_dimension", 
                "remove_dimension", "update_selections");
    },
    
    render: function() {
        // Generate drop zones from template
        $(this.el).html(this.template());
        
        // Activate drop zones
        $(this.el).find('.connectable').sortable({
            connectWith: $(this.el).find('.connectable'),
            cursorAt: {
                top: 10,
                left: 35
            },
            forcePlaceholderSize: true,
            items: '> li',
            opacity: 0.60,
            placeholder: 'placeholder',
            tolerance: 'pointer',
            
            start: function(event, ui) {
                ui.placeholder.text(ui.helper.text());
            }

        });
        
        return this; 
    },
    
    select_dimension: function(event, ui) {

        $axis = ui.item.parents('.fields_list_body');
        var target = "";
        
        if ($axis.hasClass('rows')) target = "ROWS";
        if ($axis.hasClass('columns')) target = "COLUMNS";
        if ($axis.hasClass('filter')) target = "FILTER";


        // Short circuit if this is a move
        if (ui.item.hasClass('d_measure') ||
                ui.item.hasClass('d_dimension')) {
            this.move_dimension(event, ui, target);
            return;
        }

        // Make the element and its parent bold
        var original_href = ui.item.find('a').attr('href');
        var $original = $(this.workspace.el).find('.sidebar')
            .find('a[href="' + original_href + '"]').parent('li');
        $original
            .css({fontWeight: "bold"})
            .draggable('disable');
        $original.parents('.parent_dimension')
            .find('.folder_collapsed')
            .css({fontWeight: "bold"});
        

        // Wrap with the appropriate parent element
        if (ui.item.find('a').hasClass('dimension')) {
            var $icon = $("<span />").addClass('sprite');
            ui.item.addClass('d_dimension').prepend($icon);
        } else {
            ui.item.addClass('d_measure');
        }



        var member = ui.item.find('a').attr('href').replace('#', '');
        var dimension = member.split('/')[0];
        var dimensions = [];

        this.update_selections(event,ui);

        $axis.find('a').each( function(i,element) {
            var imember = $(element).attr('href');
            var idimension = imember.replace('#', '').split('/')[0]; 
            if (dimensions.indexOf(idimension) == -1) {
                dimensions.push(idimension);
            }
        });

        var index = dimensions.indexOf(dimension);


        // Notify the model of the change
        this.workspace.query.move_dimension(member, 
                target, index);

        // Prevent workspace from getting this event
        return true;
    },
    
    move_dimension: function(event, ui, target) {
        if (! ui.item.hasClass('deleted')) {
            $axis = ui.item.parents('.fields_list_body');

            // Notify the model of the change
            var dimension = ui.item.find('a').attr('href').replace('#', '').split('/')[0];
            var dimensions = [];

            this.update_selections(event,ui);

            $axis.find('a').each( function(i,element) {
                var imember = $(element).attr('href');
                var idimension = imember.replace('#', '').split('/')[0]; 
                if (dimensions.indexOf(idimension) == -1) {
                    dimensions.push(idimension);
                }
            });
            var index = dimensions.indexOf(dimension);

            
                this.workspace.query.move_dimension(dimension, 
                    target, index);
        }
        
        // Prevent workspace from getting this event
        event.stopPropagation();
        return false;
    },

    update_selections: function(event, ui) {
        var member = ui.item.find('a').attr('href');
        var dimension = member.replace('#', '').split('/')[0];
        var index = ui.item.parent('.connectable').children().index(ui.item);
        var axis = ui.item.parents('.fields_list_body');
        var allAxes = axis.parent().parent();
        var target = '';
        var source = '';
        var myself = this;
        var $originalItem =  $(myself.workspace.el).find('.sidebar')
                                    .find('a[href="' + member + '"]').parent();
        var insertElement = $(ui.item);
        var type = $(ui.item).hasClass('d_dimension') ? "d_dimension" : "d_measure";

        source = ".rows, .columns, .filter";
        allAxes.find(source).find('a').each( function(index, element) {
            var p_member = $(element).attr('href').replace('#', '');
            var p_dimension = p_member.split('/')[0];
            if (p_dimension == dimension && (( "#" + p_member) != member )) {
                $(element).parent().remove();
            }
        });
        
        var n_dimension = null;
        var p_dimension = null;

        
        var prev = $(ui.item).prev();
        if (prev && prev.length > 0) {
            var p_member = prev.find('a').attr('href');
            p_dimension = p_member.replace('#', '').split('/')[0];
        }
        var next = $(ui.placeholder).next();

        while (p_dimension != null && next && next.length > 0 ) {
            var n_member = next.find('a').attr('href');
            n_dimension = n_member.replace('#', '').split('/')[0]; 
            if (p_dimension == n_dimension) {
                next.insertBefore($(ui.item));
            } else {
                p_dimension = null;
            }
            next = $(ui.placeholder).next();
        }
            

        $originalItem.parent().find('.ui-draggable-disabled').clone().attr('class', 'ui-draggable').removeAttr('style')
                                .addClass(type)
                                .insertAfter(insertElement);
        

        axis.find('.d_dimension a').each( function(index, element) {
            element = $(element);
            if (!element.prev() || (element.prev() && element.prev().length == 0)) {
                var $icon = $("<span />").addClass('sprite');
                $icon.insertBefore(element);
            }
        });

        $(ui.item).remove();

    },


    remove_dimension: function(event, ui) {
        // Reenable original element
        var $source = ui ? ui.draggable : $(event.target).parent();
        var original_href = $source.find('a').attr('href');
        var $original = $(this.workspace.el).find('.sidebar')
            .find('a[href="' + original_href + '"]').parent('li');
        $original
            .draggable('enable')
            .css({ fontWeight: 'normal' });
        
        // Unhighlight the parent if applicable
        if ($original.parents('.parent_dimension')
                .children().children('.ui-state-disabled').length === 0) {
            $original.parents('.parent_dimension')
                .find('.folder_collapsed')
                .css({fontWeight: "normal"});
        }
        
        // Notify server
        var target = '';
        var dimension = original_href.replace('#', '');
        $target_el = $source.parent().parent('div.fields_list_body');
        if ($target_el.hasClass('rows')) target = "ROWS";
        if ($target_el.hasClass('columns')) target = "COLUMNS";
        if ($target_el.hasClass('filter')) target = "FILTER";
        
        var url = "/axis/" + target + "/dimension/" + dimension;
        this.workspace.query.action.del(url, {
            success: this.workspace.query.run
        });
        
        // Remove element
        $source.addClass('deleted').remove();
        
        // Prevent workspace from getting this event
        event.stopPropagation();
        event.preventDefault();
        return false;
    },
    
    selections: function(event, ui) {
        // Determine dimension
        var $target = $(event.target).hasClass('sprite') ?
            $(event.target).parent().find('.dimension') :
            $(event.target);
        var key = $target.attr('href').replace('#', '');
        
        // Launch selections dialog
        (new SelectionsModal({
            target: $target,
            name: $target.text(),
            key: key,
            workspace: this.workspace
        })).open();
        
        // Prevent default action
        try {
            event.preventDefault();
        } catch (e) { }
        return false;
    }
});
