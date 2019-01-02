var appMain = {
	statusMap : new Map(),
	init : function() {
		appMain.initData();
		appMain.initControlEvent();
		// 初始化时自动跳转到项目列表页面
		$("#project-list-btn").click();
		calendar();
	},
	initData : function() {
		this.statusMap.put("CREATED", "新建");
		this.statusMap.put("DEPLOYED", "已部署");
		this.statusMap.put("MODIFIED", "已修改");
		this.statusMap.put("新建", "CREATED");
		this.statusMap.put("已部署", "DEPLOYED");
		this.statusMap.put("已修改", "MODIFIED");
		this.statusMap.put("RUNNING", "运行");
		this.statusMap.put("STOPPED", "停止");
		this.statusMap.put("ERROR", "故障");
		this.statusMap.put("运行", "RUNNING");
		this.statusMap.put("故障", "ERROR");
	},

	initControlEvent : function() {
		// $("#sys-list-btn").click(function() {
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	// /v2/views/applications/list
		// 	loadPage(dURIs.viewsURI.appList, null);
		// });
		$("#project-list-btn").click(function() {
			appMain.cleanState();
			$(this).addClass("active");
			// /views/project/list
			loadPage(dURIs.viewsURI.projectListView, null);
		});
		$("#workflow-orchestration-btn").click(function() {
			appMain.cleanState();
			$(this).addClass("active");
			// Orchestration : rootURI() + "/views/project/panel",
			loadPage(dURIs.viewsURI.buildOrchestration, null);
		});
        $("#new-project-btn").click(function() {
            appMain.cleanState();
            $(this).addClass("active");
            // newProject : rootURI() + "/views/project/new"
            loadPage(dURIs.viewsURI.createProject, null);
        });
        $("#new-action-btn").click(function() {
            appMain.cleanState();
            $(this).addClass("active");
            // newProject : rootURI() + "/views/action/new"
            loadPage(dURIs.viewsURI.createAction, null);
        });
        $("#all-action-btn").click(function() {
            appMain.cleanState();
            $(this).addClass("active");
            // newProject : rootURI() + "/views/action/list"
            loadPage(dURIs.viewsURI.actionListView, null);
        });
        $("#gitgrapgh-btn").click(function () {
            appMain.cleanState();
            $(this).addClass("active");
            // GitGraph : rootURI() + "/views/project/gitgraph"
            loadPage(dURIs.viewsURI.gitgraphView, null);
        });

        // 待扩展
		// $("#domain-btn").click(function() {
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.domains, null);
		// });
		// $("#config-btn").click(function(){
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.configManager, null);
		// });
		// $("#tempt-btn").click(function() {
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.temptManager, null);
		// });
		// $("#charts-btn").click(function() {
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.charts, null);
		// });
		// $("#cluster-list-btn").click(function(){
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.cluster, null);
		// });
		// $("#inf-list-btn").click(function(){
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.cluster, null);
		// });
		// $("#docker-btn").click(function() {
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.docker, null);
		// });
		// $("#consul-btn").click(function() {
		// 	appMain.cleanState();
		// 	$(this).addClass("active");
		// 	loadPage(dURIs.viewsURI.consul, null);
		// });
		
	},
	cleanState: function(){
		$("#sidebar-list li").each(function(){
			$(this).removeClass("active");
		});
	}
};
function calendar() {
    var date = new Date();
    var monthStr = new Array("Jan", "Feb", "March", "April", "May", "June", "July", "August", "Sep", "Oct", "Nov", "Dec");
    $("#avartar-top").html(monthStr[date.getMonth()]);
    $("#avartar-bottom").html(date.getDate());
}

$(document).ready(function() {
	appMain.init();
});


























