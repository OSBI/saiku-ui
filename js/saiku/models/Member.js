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
 * Controls member selections
 */
var Member = Backbone.Model.extend({
    initialize: function(args, options) {
        this.cube = options.cube;
        var dimension = decodeURI(options.dimension).split("/");
        this.dimension = dimension[0];
        this.hierarchy = dimension[2];
        this.level = dimension[3];
    },
    
    url: function() {
        var url = encodeURI(Saiku.session.username + "/discover/") +
            this.cube + encodeURI("/dimensions/" + this.dimension +
            "/hierarchies/" + this.hierarchy + "/levels/" + this.level);
        
        return url;
    }
});
