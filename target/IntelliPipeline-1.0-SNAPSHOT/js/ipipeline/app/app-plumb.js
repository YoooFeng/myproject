var appInstance = null;
var connectorPaintStyle = {
	lineWidth : 4,
	strokeStyle : "#61B7CF",
	joinstyle : "round",
	outlineColor : "white",
	outlineWidth : 2
};
var connectorHoverStyle = {
	lineWidth : 4,
	strokeStyle : "#216477",
	outlineWidth : 2,
	outlineColor : "white"
};
var endpointHoverStyle = {
	fillStyle : "#216477",
	strokeStyle : "#216477"
};
var sourceEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		strokeStyle : "#7AB02C",
		fillStyle : "transparent",
		radius : 7,
		lineWidth : 3
	},
	isSource : true,
	connector : [ "Flowchart", {
		stub : [ 40, 60 ],
		gap : 10,
		cornerRadius : 5,
		alwaysRespectStubs : true
	} ],
	connectorStyle : connectorPaintStyle,
	hoverPaintStyle : endpointHoverStyle,
	connectorHoverStyle : connectorHoverStyle,
	dragOptions : {},
	overlays : [ [ "Label", {
		location : [ 0.5, 1.5 ],
		label : "",
		cssClass : "endpointSourceLabel"
	} ] ],
	maxConnections : -1
};
var targetEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		fillStyle : "#7AB02C",
		radius : 11
	},
	hoverPaintStyle : endpointHoverStyle,
	maxConnections : -1,
	dropOptions : {
		hoverClass : "hover",
		activeClass : "active"
	},
	isTarget : true,
	overlays : [ [ "Label", {
		location : [ 0.5, -0.5 ],
		label : "",
		cssClass : "endpointTargetLabel"
	} ] ]
};

jsPlumb.ready(function() {
	var instance = jsPlumb.getInstance({
		DragOptions : {
			cursor : 'pointer',
			zIndex : 2000
		},

        PaintStyle : {

        },

		ConnectionOverlays : [ [ "Arrow", {
			location : 1
		} ] ],
		Container : "graph-panel"
	});
	instance.bind("connection", function(connInfo, originalEvent) {
		console.log("Connection: " + connInfo.connection.sourceId+"->"+connInfo.connection.targetId);
		var src=appPanel.cachedNodes.get(connInfo.connection.sourceId);
		var tar=appPanel.cachedNodes.get(connInfo.connection.targetId);

		// 利用原本dependency来存储依赖项, 现在还可复用
		console.log("增加依赖项: "+tar.stepName);
		// dependency是一个自定义的Map对象, 里面存的是链接到的 目的地节点
        // 假设一个节点的dependency是空的, 那么说明该节是最后一个step, 如何从后往前推得到step调用链条?
		src.dependency.put(connInfo.connection.targetId, tar);
		var conn = connInfo.connection;
		conn.bind("dblclick", function(connection, originalEvent) {
			if (confirm("delete edge?")) {
				var src=appPanel.cachedNodes.get(connInfo.connection.sourceId);
				var tar=appPanel.cachedNodes.get(connInfo.connection.targetId);
				console.log("删除依赖: "+tar.stepName);
				src.dependency.removeByKey(connInfo.connection.targetId);
				jsPlumb.detach(conn);
			}
		});
	});

	appInstance = instance;
});