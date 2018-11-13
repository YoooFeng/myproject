var rootURI = function() {
	return "/IntelliPipeline";
};

var dURIs = {
	deployChartURI : rootURI() + "/v2/charts/deploy",
	appInstanceURI : rootURI() + "/v2/applications/containers/instances",
	componentURI : rootURI() + "/v2/components",
	componentTypeURI : rootURI() + "/v2/components/types",
	domainURI : rootURI() + "/v2/domains",
	hostURI : rootURI() + "/v2/hosts",
	customFilesURI : rootURI() + "/v2/resources/files/custom",
	filesURI : rootURI() + "/v2/files",
	templateURI : rootURI() + "/v2/templates",
	apps:{
		appList : rootURI() + "/v2/applications",
		deployApp: rootURI() + "/v2/applications",
		deleteApp: rootURI() + "/v2/applications/delete/",
	},
	chartsURI:{
		listCharts:rootURI() + "/v2/charts/listcharts",
		listDetailedCharts:rootURI() + "/v2/charts/listDetailedcharts",
		getChart:rootURI() + "/v2/charts/get/",
		deleteChart:rootURI() + "/v2/charts/delete",
		deployChart:rootURI() + "/v2/charts/deploy",
		uploadChart:rootURI() + "/v2/charts/upload",
	},

    actionURI:{
	    newAction : rootURI() + "/action_data/new",
        actionList : rootURI() + "/action_data/list",
		deleteAction : rootURI() + "/action_data/delete"
    },

    stepURI :{
	    actionToStep : rootURI() + "/step_data/actionToStep",
    },

	viewsURI : {
		appList : rootURI() + "/v2/views/applications/list",
		charts: rootURI() + "/v2/views/charts",
		appOrchestration : rootURI() + "/v2/views/applications/panel",
		domains : rootURI() + "/v2/views/domains",
		cluster : rootURI() + "/v2/views/service/cluster",
		consul:rootURI() + "/v2/views/service/consul",
		docker : rootURI() + "/v2/views/service/docker",
		temptManager : rootURI() + "/v2/views/templates",
		configManager: rootURI() + "/v2/views/config",


        // IntelliPipeline workflow Orchestration
        mainPage : rootURI() + "/views/project",
        buildOrchestration : rootURI() + "/views/project/panel",
        projectListView : rootURI() + "/views/project/list",
		createProject : rootURI() + "/views/project/new",

        // About action
        actionListView : rootURI() + "/views/action/list",
        createAction : rootURI() + "/views/action/new",
	},

    // Added for IntelliPipeline
	projectDataURI:{
		projectList : rootURI() + "/project_data/list",
		deleteProject : rootURI() + "/project_data/delete",
        newProject : rootURI() + "/project_data/new",
        getProject : rootURI() + "/project_data/get",

        executeBuild : rootURI() + "/project_data/build"
	},
    buildDataURI:{
	    buildList : rootURI() + "/build_data/list",
        triggerBuild : rootURI() + "/build_data/trigger",
		getBuildById : rootURI() + "/build_data/get",
		stopBuildById : rootURI() + "/build_data/stop"

    },
    strategyDataURI:{
	    createStrategy : rootURI() + "/strategy_data/new",
        getStrategy : rootURI() + "/strategy_date/get"
    },

    classifierURI:{
	    showTree : rootURI() + "/classifier/show",
        getPrediction : rootURI() + "/classifier/get"
    },

	swfs : rootURI() + "/swf",
};















