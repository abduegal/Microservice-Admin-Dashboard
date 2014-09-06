'use strict';

angular.module('adminDashboardApp')
  .service('GraphSrv', function GraphSrv(Restangular) {

    this.loadGraph = function(data, callback) {

        var width = $('#chart').width(),
          height = 460;

        var fill = d3.scale.category10();

        var nodes = [], labels = [], linkData = [],
          foci = function(){
            var totalIds = _.max(data, function(item) { return item.group; }).group;

            var result = [];
            for(var index = 0; index < totalIds + 1; index++) {
              var x = ($('#chart').width() / (totalIds + 1) * index);
              var y = Math.floor((Math.random() * (height - 100) ) + 100);
              result.push({x: x, y: y});
            }
            return result;
          }();

        var svg = d3.select("#chart")
          .append("svg:svg")
          .attr("width", "100%")
          .attr("height", height)
          .call(d3.behavior.zoom().on("zoom", redraw))
          .append('svg:g');

        var force = d3.layout.force()
          .nodes(nodes)
          .links(linkData)
          .charge(-2000)
          .gravity(0.1)
          .distance(200)
          .linkDistance(200)
          .size([width, height])
          .on("tick", tick);

        // build the arrow.
        svg.append("svg:defs").selectAll("marker")
          .data(["end"])      // Different link/path types can be defined here
          .enter().append("svg:marker")    // This section adds in the arrows
          .attr("id", String)
          .attr("viewBox", "0 -5 10 10")
          .attr("refX", 23)
          .attr("refY", 0)
          .attr("markerWidth", 6)
          .attr("markerHeight", 6)
          .attr("orient", "auto")
          .append("svg:path")
          .attr("d", "M0,-5L10,0L0,5");

        //var node = svg.selectAll("circle");
        var node = svg.selectAll("g");
        var link = svg.selectAll(".link");

        init();

        function tick(e) {
          var k = .1 * e.alpha;

          // Push nodes toward their designated focus.
          nodes.forEach(function(o, i) {
            o.y += (foci[o.group].y - o.y) * k;
            o.x += (foci[o.group].x - o.x) * k;
          });

          node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

          link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

          link = link.data(force.links());

          link.enter().insert("line", ".node")
            .style("stroke-width", "3")
            .style("stroke", "#777")
            .attr("marker-end", "url(#end)");
        }

        function init(){

          _.each(data, function(item) {

            nodes.push( item);

            node = node.data(nodes);

            var n = node.enter().append("g")
              .attr("class", "node")
              .attr("id", item.data.instanceId)
              .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
              .style('cursor', 'pointer')
              .on('mousedown', function(evt) {
                var sel = d3.select(this);
                sel.moveToFront();
              })
              .on('mouseover', function(data){
                callback(data);
              });


            n.append("circle")
              .attr("r",  function(d) { return d.size; })
              .style("fill", function(d) { return fill(d.group); })
              .call(function(){
                force.drag();
              });

            n.append("text")
              .text(function(d){
                return d.name;
              })
              .style("font-size", function(d) {
                return (12 + Math.min(2 * d.size, (2 * d.size - 8) / this.getComputedTextLength() * 16)) + "px";
              })
              .attr("dy", "0em")
            n.append("text")
              .text(function(d){
                return d.location;
              })
              .style("font-size", function(d) {
                return (8 + Math.min(2 * d.size, (2 * d.size - 8) / this.getComputedTextLength() * 16)) + "px";
              })
              .attr("dy", "1.5em");

            _.each(item.links, function(link) {
              var target = _.find(data, function(d) {
                return d.data.instanceId == link.instanceId;
              });
              if(target) {
                linkData.push({"source": item, "target": target, "value": 20});
              }

            })

          })

          force.start();

        }

        d3.selection.prototype.moveToFront = function() {
          return this.each(function(){
            this.parentNode.appendChild(this);
          });
        };

        function resize() {
          width = window.innerWidth;
          force.size([width, height]);
          force.start();
        }

        function redraw() {
          svg.attr("transform",
            "translate(" + d3.event.translate + ")"
              + " scale(" + d3.event.scale + ")");
        }

        d3.select(window).on('resize', resize);

    };

  });
