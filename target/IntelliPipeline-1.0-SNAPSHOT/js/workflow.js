var instance = window.jsp = jsPlumb.getInstance({
		Container: "canvas"
	});

var connectorPaintStyle = {
		strokeWidth: 2,
		stroke: "#61B7CF",
		joinstyle: "round",
		outlineStroke: "white",
		outlineWidth: 2
	},
	connectorHoverStyle = {
		strokeWidth: 3,
		stroke: "#216477",
		outlineWidth: 5,
		outlineStroke: "white"
	},
	endpointHoverStyle = {
		fill: "#216477",
		stroke: "#216477"
	},
	sourceEndpoint = {
		endpoint: "Dot",
		paintStyle: {
			stroke: "#7AB02C",
			fill: "transparent",
			radius: 7,
			strokeWidth: 1
		},
		isSource: true,
		connector: [ "Flowchart", { stub: [40, 60], gap: 10, cornerRadius: 5, alwaysRespectStubs: true } ],
		connectorStyle: connectorPaintStyle,
		hoverPaintStyle: endpointHoverStyle,
		connectorHoverStyle: connectorHoverStyle,
		dragOptions: {},
		overlays: [
			[ "Label", {
				location: [0.5, 1.5],
				label: "Drag",
				cssClass: "endpointSourceLabel",
				visible:false
			} ]
		]
	},
	targetEndpoint = {
		endpoint: "Dot",
		paintStyle: { fill: "#7AB02C", radius: 7 },
		hoverPaintStyle: endpointHoverStyle,
		maxConnections: -1,
		dropOptions: { hoverClass: "hover", activeClass: "active" },
		isTarget: true,
		overlays: [
			[ "Label", { location: [0.5, -0.5], label: "Drop", cssClass: "endpointTargetLabel", visible:false } ]
		]
	},
	init = function (connection) {
		connection.getOverlay("label").setLabel(connection.sourceId.substring(15) + "-" + connection.targetId.substring(15));
	};

var _addEndpoints = function (toId, sourceAnchors, targetAnchors) {
	for (var i = 0; i < sourceAnchors.length; i++) {
		var sourceUUID = toId + sourceAnchors[i];
		instance.addEndpoint(toId, sourceEndpoint, {
			anchor: sourceAnchors[i], uuid: sourceUUID
		});
	}
	for (var j = 0; j < targetAnchors.length; j++) {
		var targetUUID = toId + targetAnchors[j];
		instance.addEndpoint(toId, targetEndpoint, { anchor: targetAnchors[j], uuid: targetUUID });
	}
};



function newstep(stepname,stepnodeid,column,row)
{
	var stepstatus = "running ...";
	var ttop = 100+200*(row-1);
	var tleft = 100+250*(column-1); 
	var stepnode = document.createElement("div");
	stepnode.setAttribute("class","window jtk-node");
	stepnode.setAttribute("id","stepnode"+stepnodeid);
	stepnode.setAttribute("style","top: " + ttop +"px; left: "+tleft+"px;display:table-row;");
	var stepnodecontent1 = document.createElement("p");
	stepnodecontent1.setAttribute("id","stepname")
	stepnodecontent1.setAttribute("style","font-size:10px;margin:6px;")
	stepnodecontent1.innerHTML="<strong>"+stepname+"</strong>"
	stepnode.appendChild(stepnodecontent1)
	var stepnodecontent2 = document.createElement("p");
	stepnodecontent2.setAttribute("id","stepstatus"+stepnodeid)
	stepnodecontent2.setAttribute("style","font-size:10px;color:blue;margin:6px;")
	stepnodecontent2.innerHTML="<strong>"+stepstatus+"</strong>"
	stepnode.appendChild(stepnodecontent2)
	$("#canvas").append(stepnode);
	var timer = $.timer(0,function(){
		$("#stepstatus"+stepnodeid).fadeOut('slow', function(){
			$(this).fadeIn('slow');
		});
	},false);
	return timer
}

function stepover(stepnodeid,stepstatus,timer)
{
	timer.stop()
	$("#stepstatus"+stepnodeid).html("<strong>"+stepstatus+"</strong>")
	if (stepstatus == "finished"){
		$("#stepstatus"+stepnodeid).attr("style","font-size:10px;color:green;margin:6px;")
	}
	else{
		$("#stepstatus"+stepnodeid).attr("style","font-size:10px;color:red;margin:6px;")
	}
}

function drawarrows(tails,heads)
{	
	instance.batch(function(){
	var tails_anchors = new Array();
	var heads_anchors = new Array();
	for (var tail of tails){
		var tail_anchors = new Array();
		var bottomlocation = 0;
		var locationstep = 0.05;
		if(tail_anchors.length%2==0){
			bottomlocation = 0.5+0.025+locationstep*(tails.length/2-1);
		}
		else{
			bottomlocation = 0.5+locationstep*(tails.length/2);
		}
		for (var i=0;i<tails.length;i++)
		{
			tail_anchors.push([1,bottomlocation-i*locationstep,1,0])

		}
		_addEndpoints(tail, tail_anchors, []);
		tails_anchors.push(tail_anchors);
	}

	for (var head of heads){
		var head_anchors = new Array();
		var bottomlocation = 0;
		var locationstep = 0.05;
		if(head_anchors.length%2==0){
			bottomlocation = 0.5+0.025+locationstep*(heads.length/2-1);
		}
		else{
			bottomlocation = 0.5+locationstep*(haeds.length/2);
		}
		for (var i=0;i<heads.length;i++)
		{
			head_anchors.push([0,bottomlocation-i*locationstep,0,0])

		}
		_addEndpoints(head, [], head_anchors);
		heads_anchors.push(head_anchors);
	}
	var i = 0;
	var j = 0;
	for (var tail of tails){
		for (var tail_anchor of tails_anchors[i]){
			for (var head of heads){
				for (var head_anchors of heads_anchors[j]){
					instance.connect({uuids: [tail+tail_anchors, head+head_anchors], editable: false});
				}
				j++;
			}
		}
		i++;
	}
	});
}




var timer1 = newstep("check:check",1,1,1);
setTimeout("stepover(1,'finished',timer1)",3000);
setTimeout("var timer2 = newstep('build:build',2,2,1)",3500);
setTimeout("drawarrows(['stepnode1'],['stepnode2'])",3500);
setTimeout("stepover(2,'finished',timer2)",6500)
setTimeout("var timer3 = newstep('test:test',3,3,1)",7000);
setTimeout("drawarrows(['stepnode2'],['stepnode3'])",7000);
setTimeout("stepover(3,'finished',timer3)",10000);
setTimeout("var timer4 = newstep('deploy:deploy',4,4,1)",10500);
setTimeout("drawarrows(['stepnode3'],['stepnode4'])",10500);
setTimeout("stepover(4,'finished',timer4)",13500);
