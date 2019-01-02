var appPanel = {
	// cachedHosts : new Map(),
	cachedComponents : new Map(),
	cachedNodes : new Map(),
	currentApp : null,
	cachedContainers : new Map(),
	cachedContainersById : new Map(),
	cachedCustomFiles : null,
	cachedTemplate : new Map(),
	initReady : 0,// 001 hosts ready,010 components ready
	currentTab : -1,
    currentBuild : null,
    currentTimer : null,
	init : function(param) {
		// this.requestHosts();

		// this.initCustomFileList();

		this.initReady = 2;
	},

    /**
     * @Param appId
     * @Param tab
     * */
	initForEdit : function(param) {
		console.log("初始化拓扑图"+appPanel.initReady);
		if (appPanel.initReady == 2) {
			console.log("进入初始化");

            // 清空画板
			appPanel.clearGraph();

			// 请求project
			appPanel.requestApp(param.appId);

			// 保存按钮暂时不需要
			// appPanel.showSaveAsBtn();

            // 初始化策略配置按钮
            appPanel.showStrategyBtn();

            $("#refresh-btn").hide();
            // 初始化分类器按钮
            // appPanel.showClassifierBtn();

            // 初始化事件(执行构建按钮)
            appPanel.initEvents();

            // 初始化组件列表(Step列表)
            appPanel.requestComponentList();

			appPanel.currentTab = param.tab;
			if (appPanel.currentTab == 2 || appPanel.currentTab == 3) {
				$("#group-list").hide();
			}
			$(".operation-list li[data-view-id='" + appPanel.currentTab + "']")
					.css('display', 'inline-block');

		} else {
			setTimeout(function() {
				appPanel.initForEdit(param);
			}, 2000);
		}
	},

    // 可视化构建过程初始化
    initForBuildProcess : function(buildId) {

	    // 初始化分类器按钮
        appPanel.showClassifierBtn(buildId);

        // 初始化当前构建的统计信息
    },

	// 初始化用户编写的workflow.yaml文件, 可从文件中读取CI流
	initCustomFileList : function(selectedFile) {
		if (null == appPanel.cachedCustomFiles) {
			ajaxGetJsonAuthc(dURIs.customFilesURI, null,
					appPanel.requestCustomFileListCallback, null);
		} else {
			orcheHtml.paintCustomFileList(appPanel.cachedCustomFiles.values(),
					selectedFile);
		}
	},


    /**
     * 定期刷新构建流程图框到最新状态
     * @param: buildId
     * @return
     * */
    refreshBuildProcess : function () {
        // console.log("Build: " + JSON.stringify(appPanel.currentBuild));
        console.log("BuildId: " + appPanel.currentBuild.id);

        if($("#refresh-btn").html() == "开启自动刷新构建流程图"){
            console.log("yes");
            $("#refresh-btn").html("关闭自动刷新构建流程图");

            // 每五秒刷新一次构建流程图
            appPanel.currentTimer = setInterval(function () {
                $.ajax({
                    type:"get",
                    async: true,
                    url: dURIs.buildDataURI.getBuildById + "/" + appPanel.currentBuild.id,
                    timeout: 20000,
                    success:function (data) {
                        console.log("BuildData: " + JSON.stringify(data));
                        // 直接重绘流程图
                        appPanel.showBuildingProcess(data);
                    },
                    error: function () {
                        alert("获取构建信息失败!");
                    }
                })
            }, 5000);

        } else {
            console.log("no");
            $("#refresh-btn").html("开启自动刷新构建流程图");
            clearInterval(appPanel.currentTimer);
        }
    },


	/**
	 * 请求文件列表的回调函数
	 * 
	 * @param data
	 */
	requestCustomFileListCallback : function(data) {
		var files = data;
		appPanel.cachedCustomFiles = data;
		// orcheHtml.paintCustomFileList(files);
	},

    /**
     * 显示分类器预测结果的窗口
     * */
    showClassifierBtn: function(buildId) {
        // 显示该按钮
        $("#btn-classifier").show();
        $("#btn-classifier").unbind("click");

        // 点击按钮的事件
        $("#btn-classifier").click(function () {

            // 根据当前的构建id, 读取预测结果
            ajaxGetJsonAuthc(dURIs.classifierURI.getPrediction + "/" + buildId, null,
                appPanel.showPrediction, null);
            // For test
            // appPanel.showPrediction("unknown");

            // 生成显示决策树模型的按钮
            // appPanel.genCheckbox();

            var classifierSaveBtn = $("#classifier-save-btn");
            classifierSaveBtn.unbind("click");

            // 点击确定之后, 移除之前的所有内容, 然后关闭窗口
            classifierSaveBtn.click(function() {
                var attsDiv = document.getElementById("predictParams");
                while(attsDiv.hasChildNodes()) {
                    attsDiv.removeChild(attsDiv.firstChild);
                }
                $("#classifierModal").modal('hide');
            });


            var classifierUpdateBtn = $("#update-tree-btn");
            classifierUpdateBtn.unbind("click");

            // 点击更新之后, 更新决策树模型
            classifierUpdateBtn.click(function() {
                appPanel.updateModel();
            });


            var decisionTreeBtn = $("#decision-tree-btn");
            decisionTreeBtn.unbind("click");
            decisionTreeBtn.click(function () {
                appPanel.showTreeString();
            });

            // 显示窗口
            $("#classifierModal").modal('show');
        });
    },

    showTreeString : function() {
        var buildId = appPanel.currentBuild.id;

        ajaxGetJsonAuthc(dURIs.classifierURI.showTreeString + "/" + buildId, null,
            appPanel.showTreeStringCallback,
            showError("更新模型失败!"));

    },

    showTreeStringCallback : function(treeString) {
        $("#treeStringModal").modal('show');
        $("#treeStringBody").html(treeString);
    },


    updateModel : function () {
        console.log("update btn clicked.")
        var buildId = appPanel.currentBuild.id;
        console.log("buildId: " + buildId);

        ajaxGetJsonAuthc(dURIs.classifierURI.updateModel + "/" + buildId, null,
            showSuccess("更新模型成功!"),
            showError("更新模型失败!"));
    },

    /**
     * 获取当前构建的预测结果
     * @Param prediction - 服务端返回的预测结果
     * */
    showPrediction : function(prediction) {

        // console.log("CurrentBuild: " + JSON.stringify(appPanel.currentBuild));

        // 刷新构建预测结果!
        var resultDiv = document.getElementById('predictResult');
        // 在这里设置式样
        resultDiv.style.fontFamily = "Open Sans Condensed";
        resultDiv.style.fontSize = "35px";
        switch(prediction) {
            // 返回通过的预测结果, 显示为绿色
            case "passed" :
                console.log("cast passed");
                resultDiv.innerHTML = "passed";
                resultDiv.style.color = "#00bb00";
                // resultDiv.className = "passed";
                break;
            case "failed" :
                console.log("case failed");
                resultDiv.innerHTML = "failed";
                resultDiv.style.color = "#d81217";
                // resultDiv.className = "failed";
                break;
            default :
                console.log("case unknown");
                resultDiv.innerHTML = "unknown";
                resultDiv.style.color = "#d1d213";
            // resultDiv.className = "unknown";
        }

        // 刷新决策的特征值
        var record = appPanel.currentBuild.record;
        console.log("Record: " + record);

        var atts = record.split(',');

        var nCommitter = atts[0];
        var nLine = atts[1];
        var lastBuild = atts[2];
        var projectHistory = atts[3];
        var projectRecent = atts[4];

        if(nCommitter === undefined || nCommitter === " " || nCommitter === "") {
            nCommitter = "数据不存在, 无法进行分类"
        }
        if(nLine === undefined || nLine === "" || nLine === " ") {
            nLine = "数据不存在, 无法分类"
        }

        // mock test
        // var nCommitter = "2";
        // var nLine = "50";

        console.log("attributes");
        var attsDiv = document.getElementById("predictParams");

        var committerLabel = document.createElement("label");
        committerLabel.innerText = "开发者人数: " + nCommitter + " (开发者人数指的是最近三个月代码提交的参与开发者人数)";
        committerLabel.style.fontSize = "20px";

        var lineLabel = document.createElement("label");
        lineLabel.innerText = "修改代码行数: " + nLine + " (本次构建涉及修改代码的总行数)";
        lineLabel.style.fontSize = "20px";

        var lastBuildLabel = document.createElement("label");
        lastBuildLabel.innerText = "上次构建结果为: " + lastBuild
            + " (上次构建的结果反映了构建上下文)";
        lastBuildLabel.style.fontSize = "20px";

        var projectHistoryLabel = document.createElement("label");
        projectHistoryLabel.innerText = "项目历史构建成功率为: " + projectHistory
            + " (历史构建成功率指的是当前项目的所有构建中成功构建的占比)";
        projectHistoryLabel.style.fontSize = "20px";

        var projectRecentLabel = document.createElement("label");
        projectRecentLabel.innerText = "项目近期构建成功率为: " + projectRecent
            + " (近期构建成功率指的是当前构建的前五次构建的成功率)";
        projectRecentLabel.style.fontSize = "20px";

        // 没有child
        if(!attsDiv.hasChildNodes()) {
            attsDiv.appendChild(committerLabel);
            attsDiv.appendChild(document.createElement("br"));
            attsDiv.appendChild(lineLabel);
            attsDiv.appendChild(document.createElement("br"));
            attsDiv.appendChild(lastBuildLabel);
            attsDiv.appendChild(document.createElement("br"));
            attsDiv.appendChild(projectRecentLabel);
            attsDiv.appendChild(document.createElement("br"));
            attsDiv.appendChild(projectHistoryLabel);
            attsDiv.appendChild(document.createElement("br"));
        }

    },

    showTreeGraph : function () {
        var appId = appPanel.id;

        // 直接发送请求, 显示分类器决策树的可视化模型
        ajaxGetJsonAuthc(dURIs.classifierURI.showTree + "/" + appId, null,
            null, null);
    },

    /**
     * 显示智能决策策略选择窗口
     * */
    showStrategyBtn : function () {
        // 显示该按钮
        $("#btn-strategy").show();
        $("#btn-strategy").unbind("click");

        // 点击按钮的事件
        $("#btn-strategy").click(function () {
            // 读取策略
            appPanel.loadStrategy();

            // 生成stageName的复选框
            appPanel.genCheckbox();

            // 显示窗口
            $("#strategyModal").modal('show');
        });

        $("#strategySave").unbind("click");

        // 点击保存按钮的事件
        $("#strategySave").bind("click", function () {
        	console.log("strategy save");
            // 策略应该是与当前的App绑定的, 将修改之后的数据存进数据库中进行更新
            appPanel.saveStrategies(appPanel.currentApp.id);
            // 隐藏配置窗口
            $('#strategyModal').modal('hide');
        });

    },
    /**
	 * 从数据库中读取配置的策略,并显示在策略配置窗口上
     * 已经获取到最新的策略,但是窗口上的数据没有同步更新!
     * */
    loadStrategy : function(){
        var strategy = appPanel.currentApp.strategy;

        console.log("Strategy: " + JSON.stringify(strategy));

        if(strategy === null || strategy === undefined){
            return;
        }

        var strategyModal = $('#strategyModal');

        // 加载数据库数据, 刷新策略编辑框
        if(strategy.time_interval!== undefined && strategy.time_interval > 0) {
            // 勾选
            strategyModal.find('#timeIntervalCheckBox').prop("checked", true);
            // 写值
            strategyModal.find('#timeIntervalText').val(strategy.time_interval);
            // $("#strategyModal #time_interval").val(strategy.time_interval);
        }
        if(strategy.modified_lines !== undefined && strategy.modified_lines > 0) {
            strategyModal.find('#modifiedLinesCheckBox').prop("checked", true);
            // $("#strategyModal #modified_lines").val(strategy.modified_lines);
            strategyModal.find('#modifiedLinesText').val(strategy.modified_lines);
        }
        if(strategy.skip_paths !== undefined && strategy.skip_paths !== "") {
            strategyModal.find('#skipPathCheckBox').prop("checked", true);
            // $("#strategyModal #skipPathCheckBox").prop("checked", true);

            strategyModal.find("#skipPathDiv").style.display="inline";

            strategyModal.find('#skipPathText').val(strategy.skip_paths);
            // $("#strategyModal #skipPathText").val(strategy.skip_paths);
        }
        if(strategy.model_modified !== undefined && strategy.model_modified === true) {
            strategyModal.find('#modelChangedCheckBox').prop("checked", true);
        }
        if(strategy.committers_mail !== undefined && strategy.committers_mail !== "") {
            strategyModal.find('#committerCheckBox').prop("checked", true);
            // $("#strategyModal #committerCheckBox").prop("checked", true);

            strategyModal.find("#committerDiv").style.display="inline";
            // document.getElementById("committerDiv").style.display="inline";

            strategyModal.find('#committerText').val(strategy.committers_mail);
            // $("#strategyModal #committerText").val(strategy.committers_mail);
        }
        if(strategy.key_paths !== undefined && strategy.key_paths !== "") {
            strategyModal.find('#keyPathCheckBox').prop("checked", true);
            // $("#strategyModal #keyPathCheckBox").prop("checked", true);

            strategyModal.find("#keyPathDiv").style.display="inline";
            // document.getElementById("keyPathDiv").style.display="inline";

            strategyModal.find('#committerText').val(strategy.key_paths);
            // $("#strategyModal #keyPathText").val(strategy.key_paths);
		}

		// 根据填写的stage信息生成复选框

        return;
    },

    saveStrategies : function (appId) {
        var strategy = {};

        // 如果不配置那么默认数值保存为-1!
        if(document.getElementById("timeIntervalCheckBox").checked) {
            strategy.time_interval = $("#timeIntervalText").val();
        } else {
            strategy.time_interval = -1;
        }

        if(document.getElementById("modifiedLinesCheckBox").checked) {
            strategy.modified_lines = $("#modifiedLOCText").val();
        } else {
            strategy.modified_lines = -1;
        }

        if(document.getElementById("skipPathCheckBox").checked
            && document.getElementById("skipPathText").value !== undefined
            && document.getElementById("skipPathText").value !== null
            && document.getElementById("skipPathText").value !== ""){
            strategy.skip_paths = document.getElementById("skipPathText").value;
        } else strategy.skip_paths = null;

        if(document.getElementById("keyPathCheckBox").checked
            && document.getElementById("keyPathText").value !== undefined
            && document.getElementById("keyPathText").value !== null
            && document.getElementById("keyPathText").value !== ""){
            strategy.key_paths = document.getElementById("keyPathText").value;
        } else strategy.key_paths = null;

        if(document.getElementById("committerCheckBox").checked
            && document.getElementById("committerText") !== undefined
            && document.getElementById("committerText").value !== null
            && document.getElementById("committerText").value !== ""){
            strategy.committers_mail = document.getElementById("committerText").value;
        } else strategy.committers_mail= null;

        // strategy.modelChanged = document.getElementById("modelChanged").prop('checked');
        strategy.model_modified = document.getElementById("modelChangedCheckBox").checked;

        console.log("Strategy To Save: " + JSON.stringify(strategy));

        // 发送数据保存配置的策略,参数--URL, body, callback, mask
        ajaxPostJsonAuthcWithJsonContent(dURIs.strategyDataURI.createStrategy + "/" + appId, strategy, null, null);
    },

    // 选定单选框出现输入框的函数
    skipPathCheckBoxClick : function(){
        // true for false
        // var skipPathCheckBox = document.getElementById("skipPathCheckBox").prop('checked');
        var skipPathCheckBox = document.getElementById("skipPathCheckBox").checked;
        console.log("SkipPath: " + skipPathCheckBox);

        switch(skipPathCheckBox){
            case true:
                document.getElementById("skipPathDiv").style.display="inline";
                break;
            case false:
                // 要自动清空文本输入框中的内容
                document.getElementById("skipPathText").value = "";
                document.getElementById("skipPathDiv").style.display="none";
                break;
            default :
                document.getElementById("skipPathText").value = "";
                document.getElementById("skipPathDiv").style.display="none";
                break;
        }
    },

    // 选定单选框出现输入框的函数
    keyPathCheckBoxClick : function () {
        // true for false
        var keyPathCheckBox = document.getElementById("keyPathCheckBox").checked;

        switch(keyPathCheckBox){
            case true:
                document.getElementById("keyPathDiv").style.display="inline";
                break;
            case false:
                // 要自动清空文本输入框中的内容
                document.getElementById("keyPathText").value = "";
                document.getElementById("keyPathDiv").style.display="none";
                break;
            default :
                document.getElementById("keyPathText").value = "";
                document.getElementById("keyPathDiv").style.display="none";
                break;
        }
    },

    committerCheckBoxClick : function () {
        var committerCheckBox = document.getElementById("committerCheckBox").checked;

        switch(committerCheckBox){
            case true:
                document.getElementById("committerDiv").style.display="inline";
                break;
            case false:
                // 要自动清空文本输入框中的内容
                document.getElementById("committerText").value = "";
                document.getElementById("committerDiv").style.display="none";
                break;
            default :
                document.getElementById("committerText").value = "";
                document.getElementById("committerDiv").style.display="none";
                break;
        }
    },

    genCheckbox : function() {
    	var nodes = appPanel.cachedNodes;

    	console.log("cachedNodes: " + nodes.elements);
    	for(let value of nodes.values()) {

    		console.log("Value: " + value);

    		var node = value;
    		console.log("node: " + node);

    		if(node == undefined || node.stageName == undefined || node.stageName == "") continue;
    		// 已经存在同名checkbox的情况
    		else if(document.getElementById("target-" + node.stageName) != undefined) {
    			continue;
			}
			// 新建checkbox
    		else {
    			console.log("begin to generate checkboxes");
    			var container = document.getElementById("target-label");

    			var checkbox = document.createElement('input');
    			checkbox.type = "checkbox";
    			checkbox.name = "targetHobby";
    			checkbox.value = node.stageName;
    			checkbox.id = "target-" + node.stageName;

    			var label = document.createElement('label');
    			label.htmlFor = node.stageName;
    			label.appendChild(document.createTextNode('' + node.stageName));

				container.appendChild(checkbox);
				container.appendChild(label);
    		}
        }

	},

	// 自适应的输入框大小
	inputSize : function (event) {
    	var size = event.value.length;
    	event.setAttribute("size", size);
    	event.setAttribute("style", "width:auto");
    },

	showSaveAsBtn : function() {
		$("#btn-save-as").show();
		$("#btn-save-as").unbind("click");
		$("#btn-save-as").click(function() {
			$("#save-app-btn").unbind("click");
			$("#save-app-btn").click(function() {
				console.log("另存?");
				appPanel.saveAs();
			});
			$('#saveModal').modal('show');
		});
	},

	// 初始化点击执行构建的事件
	initEvents : function() {
		// 构建按钮(名字是save系列没改)
        $("#btn-save").show();
		$("#btn-save").unbind("click");
		$("#btn-save").click(function() {
			// 模态框的保存按钮(真正提交任务的按钮)
            // 点击弹出框的确定按钮之后才真正开始执行构建
			var saveBtn = $("#save-app-btn");
			saveBtn.unbind("click");
			saveBtn.click(function() {
				appPanel.executeBuild();
			});
			$('#saveModal').modal('show');
		});
		// $("#addEnvBtn").unbind("click");
		// $("#addEnvBtn")
		// .click(
		// function() {
		// var envs = $("#envs");
		// var id = dateToId();
		// envs
		// .append('<div id='
		// + id
		// + '>'
		// + ' <input name="variableKeyName" class="input ng-pristine
		// ng-untouched ng-valid" type="text" ng-model="vm.variableName"
		// placeholder="key">'
		// + '<input name="variableValueName" class="input ng-pristine
		// ng-untouched ng-valid" type="text" ng-model="vm.variableName"
		// placeholder="vale">'
		// + '<button type="button" class="btn btn-danger"
		// onclick="appPanel.remove('
		// + id1 + ',' + id2 + ',' + id3
		// + ')">delete</button>' + '</div>');
		// });
	},

    // 新建node的函数, 两种创建方式
	nodeFactory : {
	    // 通过nodeId和提供的Action创建新的node
		createNode : function(nodeId, component) {
			var node = new ContainerNode(nodeId, component.id);
			return node;
		},

        createNodeFromTemplate : function (nodeId, stepNode) {
            var node = new templateNode(nodeId, stepNode.stepName, stepNode.displayName, stepNode.stageName, stepNode.xPos, stepNode.yPos, stepNode.params);
            return node;
        },

        // 这里要改为从step新建节点对象, 因为stage只是逻辑上的概念, 后期再进行划分和识别
        // 通过已有的container创建节点
		createNodeFromContainer : function(container) {
//			var hosts = new Array();
//			var host = appPanel.cachedHosts.get(container.masterId);
//			hosts.push(host);
//			var component = appPanel.cachedComponents
//					.get(container.componentId);
//			var containerNode = new ContainerNode(container.nodeId, component,
//					hosts, container.name, container.port, container.initCount,
//					container.maxCount, container.status, container.id);
//			containerNode.setTemplates(container.templates);
//			containerNode.setAttributes(container.attributes);
			console.log("从容器中新建节点对象");
			var containerNode = new ContainerNode(container);
			return containerNode;
		},

		createNodeFromContainerInstance : function(containerInstance,
				containerId, nodeId) {
			return new ContainerInstanceNode(nodeId, containerInstance.name,
					containerInstance.port, containerInstance.status,
					containerInstance.id, containerId);
		},

		createRunningNode : function (nodeId, nodeName, nodeDisplayName, nodeStageName, nodeParams, nodeStatus, xPos, yPos) {
            var node = new RunningStep(nodeId, nodeName, nodeDisplayName, nodeStageName, nodeParams, nodeStatus, xPos, yPos);
            return node;
        }
	},

	/**
	 * 请求主机列表，暂时木有使用
	 */
	requestHosts : function() {
		ajaxGetJsonAuthc(dURIs.hostURI, null, appPanel.requestHostsCallback,
				null);
	},

	requestHostsCallback : function(data) {
		var hosts = data;
		appPanel.cachedHosts.clear();
		for ( var i in hosts) {
			appPanel.cachedHosts.put(hosts[i].id, hosts[i]);
		}
		appPanel.initReady |= 1;
	},

	/**
	 * 请求组件类型,暂时木有使用
	 */
	requestComponentTypes : function() {
		ajaxGetJsonAuthc(dURIs.chartsURI, null,
				appPanel.requestComponentTypesCallback, null);
	},

	requestComponentTypesCallback : function(data) {
		// orcheHtml.paintComponentMeta(data);
		appPanel.requestComponentList();
	},

	/**
	 * 请求Action列表, Action即是定制化的Step, 同时也应该允许用户新建自定义的Step
	 */
	requestComponentList : function() {
		ajaxGetJsonAuthc(dURIs.actionURI.actionList, null,
				appPanel.requestComponentsCallback, null);
	},
    /**
     * 请求成功组件后, 回调函数画图, 将所有component列在右边,
     * 之后新建node的时候一个component对应一个node
     * 这里的component实际上就是action
     * */
	requestComponentsCallback : function(data) {
		var components = data;
		// 清除组件的缓存
		appPanel.cachedComponents.clear();
		for ( var i in components) {
		    console.log("StepName: " + components[i].stepName);
			appPanel.cachedComponents.put(components[i].id, components[i]);
		}
		// 绘制组件列表和绑定事件
		orcheHtml.paintComponentList(components);
		appPanel.initReady |= 2;
	},

	/**
	 * 请求project
	 * 
	 * @param appId
	 */
	requestApp : function(appId) {
		ajaxGetJsonAuthc(dURIs.projectDataURI.getProject + "/" + appId, null,
				appPanel.requestAppCallback, null);
	},

	requestAppCallback : function(data) {
		console.log("请求一个project的拓扑数据");
		console.log("------------------");
		// console.log(JSON.stringify(data));
		// console.log("------------------");
		var app = data;
		appPanel.currentApp = app;
		// appPanel.cachedContainers.clear();
		// appPanel.cachedContainersById.clear();
		// set app name
        console.log("ProjectName: " + app.name);
		$("#appName").val(app.name);

		appPanel.cachedNodes.clear();

		// 重新绘制最新一次构建的执行流程图
        if(app.template != undefined && app.template.steps != undefined) {
            var steps = app.template.steps;
            console.log("Template: " + JSON.stringify(app.template));

            var relations = [];

            // 先遍历一遍得到所有的InRelations
            for(var i in steps){
                var step = steps[i];
                if(step.relations != undefined && step.relations.length != 0){
                    relations[i] = {
                        from : step.relations[0].fromId,
                        to : step.relations[0].toId
                    };
                }
            }

            for ( var i in steps) {

                var tempNodeId;

                var step = steps[i];

                // 只适用于单点连线的情况. TODO: 当只有一个点时有Bug
                if(step.relations != undefined && step.relations.length != 0){
                    tempNodeId = step.relations[0].toId;
                } else {
                    tempNodeId = relations[1].from;
                }

                var stepNode = appPanel.nodeFactory.createNodeFromTemplate(tempNodeId, step);
                // 新生成独一无二的Id

                appPanel.cachedNodes.put(stepNode.nodeId, stepNode);

                orcheHtml.paintNode(stepNode.nodeId);

                // 先把点移动到记录的坐标位置
                appPanel.rearrangeElementWithPos(stepNode.nodeId, stepNode.xPos, stepNode.yPos);
            }

            // 再画边
            for(var i in relations){
                var relation = relations[i];
                orcheHtml.paintEdge(relation.from, relation.to);
            }
            // 所有重新绘制, 相当于刷新一次页面
            appInstance.repaintEverything();
            appInstance.repaintEverything();
        }


	},

	getEndpointIds : function(container) {
		var ids = new Array();
		if (container.status == "CREATED") {
			ids.push(container.nodeId);
		} else if (container.status == "DEPLOYED") {
			for ( var j in container.instances) {
				var instance = container.instances[j];
				ids.push(container.nodeId + "-" + instance.seq);
			}
		}
		return ids;
	},

	rearrangeElementWithPos : function(id, xPos, yPos) {
		var element = $("#" + id);
		var offset = element.offset();
		offset.left = xPos;
		offset.top = yPos;
		element.offset(offset);
	},
	/**
	 * 向绘图区添加一个新的节点
	 * 添加的节点应该是一个Step
     *
	 * @param componentId
	 */
	addNode : function(componentId) {
		var component = appPanel.cachedComponents.get(componentId);
		var nodeId = dateToId();
		console.log("addNode: " + nodeId);

		console.log("component: " + JSON.stringify(component));

		// TODO: 新加的Node实际上共用一个component的params, 这里需要新建自己的params
		var params = component.params;
        console.log("component params: " + JSON.stringify(params));

		// 通过actionItem请求step
		// var step = appPanel.requestStep(actionItem);

		//
		var node = appPanel.nodeFactory.createNode(nodeId, component);
		appPanel.cachedNodes.put(nodeId, node);
		orcheHtml.paintNode(nodeId);
	},

    // /**
    //  * 获取一个新的step的函数
    //  * @Param stepItem - 通过Action的内容来生成一个新的Step
    //  * */
    // requestStep : function(actionItem){
    //     ajaxPostJsonAuthcWithJsonContent(dURIs().stepURI.actionToStep,
    //         actionItem, appPanel.actionToStep, null);
    // },

	addEnv : function() {
		var envsHtml="";
		var divid=myuid();
		envsHtml += '<div id="' + divid + '"> ';
		envsHtml += '<input type="text"  placeholder="key">'
				 + '<input  type="text"  placeholder="value">';
		envsHtml += '<button onclick="appPanel.removeEnv('+ divid+')" type="button" class="btn btn-danger">Delete</button>';
		envsHtml += '</div>';
		$("#envs").append(envsHtml);
	},

	removeEnv : function(divId) {
		$("#"+divId+"").remove();
	},
	/**
	 * paintNode 节点的双击事件, 弹出配置框, 设置和修改params
	 * 
	 * @param nodeId
	 */
	nodeClick : function(nodeId) {

		var node = appPanel.cachedNodes.get(nodeId);

		console.log("双击节点: " + nodeId);

		if (node instanceof ContainerInstanceNode) {
			console.log("ContainerInstanceNode : 节点双击事件");
			$('.operation-list li').unbind('dblclick');
			$('#operationModal').modal('show');
			$('.operation-list li').bind('dblclick', function() {
				appPanel.doOperationOnInstance(node.getInstanceId(), $(this));
			});

		} else if (node instanceof ContainerNode || node instanceof templateNode) {
            console.log("ContainerNode : 节点双击事件");

            orcheHtml.paintNodeDetail(node, nodeId);

            $('#detailModal').modal('show');

            // orcheHtml.paintTemplateParams(node);
            // orcheHtml.paintAttributes(node);
            // $("#templates #addTemplate").unbind("click");
            // $("#templates #addTemplate").click(function(){
            // orcheHtml.addTemplate();
            // });
            // $("#attributes #addAttribute").unbind("click");
            // $("#attributes #addAttribute").click(function(){
            // orcheHtml.addAttribute();
            // });
            //
            // $("#detailModal #initCount").unbind('change');
            // $("#detailModal #initCount").bind(
            // 'change',
            // function() {
            // if (parseInt($(this).val()) > parseInt($(
            // "#detailModal #maxCount").val())) {
            // $(this).val($("#detailModal #maxCount").val());
            // alert("primary instances must be less than max instances！")
            // }
            // });

            $("#nodeSave").unbind("click");

            $("#nodeSave").bind("click", function () {
                appPanel.nodeEditSave(nodeId);
                $('#detailModal').modal('hide');
            });

        } else if(node instanceof RunningStep) {
		    console.log("RunningStep : 节点双击事件");
            orcheHtml.paintRunningStepDetail(node, nodeId);
            $('#detailModal').modal('show');

            $(".editSave").unbind("click");

            $(".editSave").bind("click", function () {
                appPanel.nodeEditSave(nodeId);
                $('#detailModal').modal('hide');
            });

		} else {
			console.log("弹出对话框，进行对话");
			$('#detailModal').modal('show');
			var component = node.component;
			orcheHtml.paintNodeDetailHost(appPanel.cachedHosts.values(),
					node.hosts);
			orcheHtml.paintNodeDetailAction(node);
			orcheHtml.paintTemplateParams(node);
			orcheHtml.paintAttributes(node);

			$("#templates #addTemplate").unbind("click");
			$("#templates #addTemplate").click(function() {
				orcheHtml.addTemplate();
			});

			$("#attributes #addAttribute").unbind("click");
			$("#attributes #addAttribute").click(function() {
				orcheHtml.addAttribute();
			});

			$("#detailModal #initCount").unbind('change');
			$("#detailModal #initCount")
					.bind(
							'change',
							function() {
								if (parseInt($(this).val()) > parseInt($(
										"#detailModal #maxCount").val())) {
									$(this).val(
											$("#detailModal #maxCount").val());
									alert("primary instances must be less than max instances！")
								}
							});
			$(".editSave").unbind("click");
			$(".editSave").bind("click", function() {
				appPanel.nodeEditSave(nodeId);
				$('#detailModal').modal('hide');
			});
		}

	},

	/**
	 * 保存节点(Step)的配置信息
	 * 
	 * @param nodeId
	 */
	nodeEditSave : function(nodeId) {
		var node = appPanel.cachedNodes.get(nodeId);
		console.log("----------------nodeEditSave-----------------");
		console.log("保存节点信息" + node.getStepName());

		// 思考: 有没有必要清空params? 还是只需要修改params[i].paramValue?
        // 不能清空, 应该赋值
		// node.params = [];

		// node.containerPort = $("#containerPort").val();
		// node.nodePort = $("#nodePort").val();

		// 保存stepName 所属stageName还有displayName
		node.stepName = $("#serviceName").val();

        node.displayName = document.getElementById(nodeId + "displayName").value;
        node.stageName = document.getElementById(nodeId + "stageName").value;

        // 编辑过
        node.status = "EDITED";

		// $('#envs div').each(
		// 		function(index) {
		// 			var key="";
		// 			var value="";
		// 			// 是不是从上到下按顺序遍历?
		// 			$(this).children("input").each(
		// 					function(index) {
		// 						console.log("遍历input" + index + ":"
		// 								+ $(this).val());
		// 						// 这里是通过序号来遍历, 0就是key, 1就是value
		// 						if(index == 0){
		// 							key=$(this).val();
		// 						}
		// 						if(index == 1){
		// 							value=$(this).val();
		// 						}
		// 					});
		// 			if(trim(key).length != 0 && trim(value).length != 0){
        //
		// 				var item=new KV(key,value);
		// 				console.log("KV: " + JSON.stringify(item));
        //
		// 				node.params.push(item);
		// 			}
		// 		});

        var params = node.params;
		// 换种思路, 遍历params, 然后通过id在div中找相对应的input框
		for(var i in params){
		    console.log("Element Id: " + nodeId + params[i].paramsKey + '-value');
		    console.log("Input Element: " + document.getElementById(nodeId + params[i].paramsKey + '-value').value);
            params[i].paramValue = document.getElementById(nodeId + params[i].paramsKey + '-value').value;

            console.log(nodeId + ": " + params[i].paramValue);
        }


		// var node = appPanel.cachedNodes.get(nodeId);
		// console.log("");
		// var component = node.component;
		// var newParams = new Array();
		// var newTemplates = new Array();
		// var newAttributes = new Array();
		// $("#actionParams tbody tr").each(function(index, element) {
		// var value = $(element).find('.paramValue textarea').val();
		// var key = $(element).find('.paramKey').text();
		// if (key != "") {
		// newParams.push({
		// key : key,
		// value : value
		// });
		// }
		// });
		// $("#templates tbody tr").each(function(index, element) {
		// var source = $(element).find('.temSource [name="source"]').val();
		// var target = $(element).find('.temTarget textarea').val();
		// var command = $(element).find('.temCommand textarea').val();
		// if (source != "" && target != "") {
		// newTemplates.push({
		// source : source,
		// target : target,
		// command: command
		// });
		// }
		// });
		// for(var i in newTemplates){
		// alert(newTemplates[i].source)
		// }
		// $("#attributes tbody tr").each(function(index, element) {
		// var attrKey = $(element).find('.attrKey textarea').val();
		// var attrValue = $(element).find('.attrValue textarea').val();
		// if (attrKey != "" && attrValue != "") {
		// newAttributes.push({
		// attrKey : attrKey,
		// attrValue : attrValue
		// });
		// }
		// });
		// node.setParams(newParams);
		// node.setTemplates(newTemplates);
		// node.setAttributes(newAttributes);
		// node.setHosts(appPanel.getTargetHosts());
		// node.setName($("#detailModal #containerName").val());
		// node.setPort(parseInt($("#detailModal #containerPort").val()));
		// node.setInitCount(parseInt($("#detailModal #initCount").val()));
		// node.setMaxCount(parseInt($("#detailModal #maxCount").val()));

        // 先删再存
		appPanel.cachedNodes.removeByKey(nodeId);
		appPanel.cachedNodes.put(nodeId, node);

		// 更新node小框上显示的信息
		$("#" + nodeId).find(".node-name span").html(node.getStepName());
		$("#" + nodeId).find(".node-displayName span").html(node.getDisplayName());
        $("#" + nodeId).find(".node-stageName span").html(node.getStageName());
        $("#" + nodeId).find(".node-status span").html(node.getStatus());
		console.log("---------------nodeEditSave-End---------------");
	},

	/**
	 * 获取选中的机器节点
	 * 
	 * @returns {Array}
	 */
	getTargetHosts : function() {
		var hosts = new Array();
		$("#hostNames input[name='hostName']").each(function() {
			if ($(this).prop('checked')) {
				var hostId = parseInt($(this).val());
				var host = appPanel.cachedHosts.get(hostId);
				if (host != false) {
					hosts.push(host);
				}
			}
		});
		return hosts;
	},

    /**
     * 执行构建的函数
     * 需要先把所有的node转换成step, 然后给后端返回List<Step>对象
     * */
    executeBuild : function () {
        // project非法的情况, 如名字中为空, 带有空格等等, 可省略此步骤
        if(!appPanel.validateApp()){
            console.log("project非法");
            return;
        }
        console.log("current project: " + appPanel.currentApp );

        // 获取到所有的node信息
        var cachedNodes = appPanel.cachedNodes.values();

        appPanel.resetGraphPanelScroll();

        // 先把所有的点的坐标记录下来, 依据坐标可以复原构建图
        var nodes = new Map();
        for(var i in cachedNodes){
            var node = cachedNodes[i];
            console.log("step name: " + node.stepName);
            // 保证当前的node不被重复处理
            if(!nodes.containsKey(node.nodeId)){
                var element = $("#" + node.nodeId);
                console.log("step节点坐标" + element.offset());
                node.xPos = element.offset().left;
                node.yPos = element.offset().top;

                console.log("node: " + JSON.stringify(node));
                // 赋予坐标之后的node结合
                nodes.put(node.nodeId, node);
            }
        }

        var connections = appInstance.getAllConnections();
        var edges = new Map();

        for(var i in connections){
            var conn = connections[i];
            // sourceId是连线的起点, targetId是连线的终点.
            // 要找出第一个step即只作为sourceId出现过的step, 这样可以顺着得到所有step的执行顺序
            var sourceId  = conn.sourceId, targetId = conn.targetId;
            // 看connections中的顺序
            console.log(sourceId + "-->" + targetId);
            var sourceNode = appPanel.cachedNodes.get(sourceId);
            var targetNode = appPanel.cachedNodes.get(targetId);

            var edge = {
                from : sourceId,
                to : targetId,
            };

            // 起点 & 边
            edges.put(edge.from, edge);
        }
        // 图对象
        var graph = {
            // 一个项目只同时对应一个构建流程, 如果构建被执行, 那么这个图应该要跟对应的build永久绑定
            name : $("#appName").val(),
            nodes : nodes.values(),
            relations : edges.values()
        };
        console.log("relations: " + JSON.stringify(edges.values()));
        // console.log("Graph: " + JSON.stringify(graph));
        // REST方式发送graph对象, 转换成List<Step>
        ajaxPostJsonAuthcWithJsonContent(dURIs.projectDataURI.executeBuild + '/' + appPanel.currentApp.id,
            graph, appPanel.executeBuildCallback, null);

    },

    /**
     * build转化完成且存储在数据库中, 触发Jenkins构建
     * @Param buildId - 保存在数据库中的build的Id
     * */
    executeBuildCallback : function(buildId){
        console.log("ExecuteBuildCallBack");
        // 要有接收到返回数据, 这样才能判断是否执行成功
        // ajaxGetJsonAuthc(dURIs.buildDataURI.triggerBuild + '/' + buildId, null, appPanel.triggerBuildCallBack, null);
        ajaxGetJsonAuthc(dURIs.buildDataURI.triggerBuild + '/' + buildId,
            null, showSuccess("构建执行成功!", loadPage(dURIs.viewsURI.projectListView, null)), showError("构建执行失败!"));
    },

    // 直接返回到项目列表界面
    triggerBuildCallBack : function (result) {

        loadPage(dURIs.viewsURI.projectListView, null);
        return result;
    },

	/**
	 * 保存应用
	 * 
	 * @param appId
	 */
	appSubmit : function() {
		if (!appPanel.validateApp()) {
			console.log("APP合法");
			return;
		}
		console.log("app Name 不空"+appPanel.currentApp);
		// 重置画板的滚动条，保证保存后能正常显示
		appPanel.resetGraphPanelScroll();
		var cachedNodes = appPanel.cachedNodes.values();
		// 新建一个矩阵图
		var nodes = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			console.log("组件节点服务名:"+node.serviceName);
			//console.log(JSON.stringify(node));
//			if (node instanceof ContainerNode) {			
				//var cloneNode=node.clone();
				if (!nodes.containsKey(node.nodeId)) {
					var element=$("#"+node.nodeId);
					console.log("组件节点坐标:"+element.offset());
					node.xPos=element.offset().left;
					node.yPos=element.offset().top;				
					console.log("------------------");
					console.log(JSON.stringify(node));
					console.log("------------------");
					nodes.put(node.nodeId, node);
				}
//			} else {
//				var container = appPanel.cachedContainersById.get(n
//						.getContainerId());
//				if (!nodes.containsKey(container.nodeId)) {
//					nodes.put(container.nodeId, container);
//				}
//			}
		}
		var connections = appInstance.getAllConnections();
		var edges = new Map();
		for ( var i in connections) {
			var conn = connections[i];
			var sourceId = conn.sourceId, targetId = conn.targetId;
			console.log(sourceId+"->"+targetId);
			var sourceNode = appPanel.cachedNodes.get(sourceId);
			var targetNode = appPanel.cachedNodes.get(targetId);
			var edge = {
				from : sourceId,
				to : targetId,
			};
			edges.put(edge.from,edge);
		}
		var graph = {
			name : $("#appName").val(),
			nodes : nodes.values(),
			relations : edges.values()
		};
		console.log("------------------graph------------------");
		console.log(JSON.stringify(graph));
		console.log("------------------graph------------------");
		ajaxPostJsonAuthcWithJsonContent(dURIs.apps.deployApp, graph,
				appPanel.createAppCallBack, defaultErrorFunc, true);
//		if (appPanel.currentApp != null) {
//			ajaxPutJsonAuthcWithJsonContent(dURIs.appURI + "/"
//					+ appPanel.currentApp.id, graph,
//					appPanel.createAppCallBack, defaultErrorFunc, true);
//		} else {
//			alert("应用名为空!");
//			// 推送数据到后端
//			
//		}
		$('#saveModal').modal('hide');
	},

	/**
	 * 另存为新的应用
	 */
	saveAs : function() {
		var cachedNodes = appPanel.cachedNodes.values();
		appPanel.cachedNodes.clear();
		var idMap = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			idMap.put(node.id, dateToId());
			node.id = idMap.get(node.id);
			appPanel.cachedNodes.put(node.id, node);
		}
		var edges = new Array();
		var connections = appInstance.getAllConnections();
		// change ids of source&target of each connection
		for ( var i in connections) {
			var conn = connections[i];
			edges.push({
				from : idMap.get(parseInt(conn.sourceId)),
				to : idMap.get(parseInt(conn.targetId)),
			});
		}
		appInstance.deleteEveryEndpoint();
		for ( var i in idMap.keys()) {
			var oldId = idMap.keys()[i];
			var newId = idMap.get(oldId);
			var element = $("#" + oldId);
			element.attr("id", newId);
			element.unbind("dblclick");
			element.dblclick(function() {
				appPanel.nodeClick($(this).attr("id"));
			});

			orcheHtml.paintEndPoint(newId);
		}
		for ( var i in edges) {
			var edge = edges[i];
			orcheHtml.paintEdge(edge.from, edge.to);
		}
		appPanel.currentApp = null;
		appPanel.appSubmit();
		$('#saveModal').modal('hide');
	},

	/**
	 * 在instance上做操作
	 * 
	 * @param instanceId
	 */
	doOperationOnInstance : function(instanceId, element) {
		if (confirm("execute " + element.find(".oper-text").html() + " action?")) {
			ajaxPutJsonAuthc(dURIs.appInstanceURI + "/" + instanceId
					+ "?operation=" + element.attr("data-oper-type"), null,
					appPanel.doOperationSuccess, appPanel.doOperationError,
					true);
			$('#operationModal').modal('hide');
		}
	},

	doOperationSuccess : function() {
		appPanel.initForEdit({
			appId : appPanel.currentApp.id
		});
		defaultSuccessFunc();
	},

	doOperationError : function(data) {
		alert(data.message);
	},

	/**
	 * 创建新应用的回调函数
	 * 
	 * @param data
	 */
	createAppCallBack : function(data) {
		
//		var app = data;
//		appPanel.initForEdit({
//			appId : app.id
//		});
//		appPanel.clearGraph();
//		//appPanel.requestApp(param.appId);
//		appPanel.showSaveAsBtn();
//		appPanel.currentTab = param.tab;
//		if (appPanel.currentTab == 2 || appPanel.currentTab == 3) {
//			$("#group-list").hide();
//		}
//		$(".operation-list li[data-view-id='" + appPanel.currentTab + "']")
//				.css('display', 'inline-block');
		loadPage(dURIs.viewsURI.projectListView, null);
		defaultSuccessFunc();
	},

	/**
     * 显示执行中的build的函数
     * */
	showBuildingProcess : function(build){
        // 首先清理缓存, TODO: 并且把不需要的按钮和列表隐藏起来
        appPanel.cachedNodes.clear();

        // 显示界面上的按钮, 把按钮上的事件与buildId绑定
        appPanel.initForBuildProcess(build.id);

        // appInstance.clearGraph();
        // orcheHtml.clear();

        // 把build作为全局变量保存下来
        appPanel.currentBuild = build;

        // 然后从数据库中重新绘图
        var nodes = build.nodes;

        for(var i in nodes){
            var node = nodes[i];


            console.log("node: " + JSON.stringify(node));
            var runningNode = appPanel.nodeFactory.createRunningNode(node.nodeId, node.stepName, node.displayName, node.stageName, node.params, node.stepStatus, node.xPos, node.yPos);

            console.log("Running Node: " + JSON.stringify(runningNode));
            appPanel.cachedNodes.put(runningNode.nodeId, runningNode);

            // 根据每一个点的状态为step上色
            orcheHtml.paintNode(runningNode.nodeId);

            // 先把点移动到记录的坐标位置
            appPanel.rearrangeElementWithPos(runningNode.nodeId, runningNode.xPos, runningNode.yPos);
        }

        // 再画边
        for(var i in build.relations){
            var relation = build.relations[i];
            orcheHtml.paintEdge(relation.from, relation.to);
        }
        // 所有重新绘制, 相当于刷新一次页面
        appInstance.repaintEverything();
    },

	/**
	 * 参数验证
	 * 
	 * @returns
	 */
	validateApp : function() {
		var appName = $("#appName").val().replace(/ /, "");
		if (appName.length <= 0) {
			showError("appname can not be empty");
			return false;
		}else{
			// appPanel.currentApp=appName;
		}
		// return appPanel.checkHostSelect();
		return true;
	},

	/**
	 * 检查是否所有节点都选择了主机
	 * 
	 * @returns {Boolean}
	 */
	checkHostSelect : function() {
		var cachedNodes = appPanel.cachedNodes.values();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			if (node instanceof ContainerNode
					&& (!verifyParam(node.getHosts()) || node.getHosts().length <= 0)) {
				showError("please select hosts for\"" + node.getName() | +"\"");
				return false;
			}
		}
		return true;
	},

	/**
	 * 清除图
	 */
	clearGraph : function() {
		if (verifyParam(appInstance)) {
			appInstance.deleteEveryEndpoint();
		}
		for ( var i in appPanel.cachedNodes.keys()) {
			var node = $("#" + appPanel.cachedNodes.keys()[i]);
			node.remove();
		}
		appPanel.cachedNodes.clear();
		appPanel.currentApp = null;
	},

	/**
	 * 将graph panel的滚动条滑动到开始位置
	 */
	resetGraphPanelScroll : function() {
		appPanel.resetScroll($("#graph-panel"));
	},

	/**
	 * 滑动scrollTo 到container的左上角
	 * 
	 * @param container
	 * @param scrollTo
	 */
	resetScroll : function(scrollTo) {
		scrollTo.scrollTop(0);
		scrollTo.scrollLeft(0);
	},

	deleteNode : function(nodeId) {
		console.log("deleteNode : " + nodeId);
		if (confirm("delete this node?")) {
			var node = $("#" + nodeId);
			var endPoints = appInstance.getEndpoints(node);
			for ( var i in endPoints) {
				appInstance.deleteEndpoint(endPoints[i].getUuid());
			}
			appPanel.cachedNodes.removeByKey(nodeId);
			node.remove();
		}
	}
};

var orcheHtml = {
    /**
     * 绘制配置对话框中的节点信息细节
     *
     * @param node
     */
    paintNodeDetail : function(node, nodeId) {
        console.log("paintNodeDetail" + nodeId + ": " + JSON.stringify(node));

        console.log("绘制节点, stepName:" + node.stepName);

        var stepName = node.stepName;

        // 弹出框中要显示的条目
        $("#detailModal #serviceName").val(stepName);

        var stageHtml = "";
        stageHtml += '<label for="' + nodeId + 'stageName" class="control-label pull-left" '
            + 'style="padding-right: 1em;">所属Stage:</label>'
            + '<input id="' + nodeId + 'stageName" class="form-control" placeholder="building" value="'+ node.stageName +'">';

        var displayNameHtml = "";
        displayNameHtml += '<label for="'+ nodeId +'displayName" class="control-label pull-left" style="padding-right: 1em;">'
            + 'DisplayName:</label>'
            + '<input id="'+ nodeId +'displayName" class="form-control" placeholder="display name" value="'+ node.displayName +'">';

        // Action对象有params参数
        var params = node.params;

        var paramsHtml = "";

        // 第一个add,其他delete
        // 遍历的是参数
        for ( var i in params) {
            // console.log("Iterate params: " + JSON.stringify(params[i].paramsKey));
            var divid = myuid();
            paramsHtml += '<div id="' + divid + '" > ';

            var optional = orcheHtml.isOptional(params[i].optional);

            // 参数名: 参数值输入框 的格式. 参数与nodeId绑定起来, 确保每个参数值独立不受影响
            paramsHtml += '<label type="text" for="' + nodeId + params[i].paramsKey + '-value" id = "' + nodeId + params[i].paramsKey + '">'
                + params[i].paramsKey + optional + ': ' + '<label/>'
                    + '<input type="text" id = "' + nodeId + params[i].paramsKey + '-value" value="' + params[i].paramValue + '">';



            // if (i == 0) {
             //    paramsHtml += '<button onclick="javascript:appPanel.addEnv()" type="button"  class="btn btn-success">Add</button>';
            // } else {
             //    paramsHtml += '<button onclick="javascript:appPanel.removeEnv('+divid+')" type="button" class="btn btn-danger">Delete</button>';
            // }
            paramsHtml += "</div>";
        }
        // 第一个add
        // if (params.length == 0) {
        //     var divid = myuid();
        //     paramsHtml += '<div id="' + divid + '"> ';
        //     paramsHtml += '<input type="text"  placeholder="key">'
        //              + '<input  type="text"  placeholder="value">';
        //     paramsHtml += '<button   onclick="javascript:appPanel.addEnv()"  type="button" class="btn btn-success">Add</button>';
        //     paramsHtml += '</div>';
        // }
        $("#stageDiv").html(stageHtml);
        $("#displayNameDiv").html(displayNameHtml);
        $("#envs").html(paramsHtml);

        // for(var i in params){
        //     document.getElementById(params[i].paramsKey).innerHTML = params[i].paramsKey + ": ";
        // }

        // $("#detailModal #containerPort").val(node.getPort());
        // $("#detailModal #initCount").val(node.getInitCount());
        // $("#detailModal #maxCount").val(node.getMaxCount());
        // orcheHtml.paintActionParams(node.action.params, node.params);

        // var dependency = node.dependency;
        // var dependencyHtml="";
        // if (dependency.size() != 0) {
        // 	var values=dependency.values();
        // 	console.log("values");
        // 	for(var i in values){
        // 		if(appPanel.cachedNodes.containsKey(values[i].nodeId)){
        // 			console.log("i is "+i);
        // 			dependencyHtml += '<div>';
        // 			dependencyHtml += '<input   type="text"  value="'
        // 				+ values[i].serviceName + '">';
        // 		    dependencyHtml += '</div>';
        // 		}else{
        // 			values[i].dependency.removeByKey(values[i].nodeId);
        // 		}
        // 	}
        // 	$("#dependent").show();
        // 	$("#dependents").html(dependencyHtml);
        // }else{
        // 	$("#dependent").hide();
        // 	$("#dependents").html(dependencyHtml);
        // }

    },

    /**
     * 绘制RunningStep的弹出对话框
     * */
    paintRunningStepDetail : function (node, nodeId) {
        console.log("Paint Running Step Node: " + JSON.stringify(node));

        var stepName = node.stepName;

        // 弹出框中要显示的条目
        $("#detailModal #serviceName").val(stepName);

        var stageHtml = "";
        stageHtml += '<label for="' + nodeId + 'stageName" class="control-label pull-left" '
            + 'style="padding-right: 1em;">所属Stage:</label>'
            + '<input id="' + nodeId + 'stageName" class="form-control" readonly value="'+ node.stageName +'">';

        var displayNameHtml = "";
        displayNameHtml += '<label for="'+ nodeId +'displayName" class="control-label pull-left" style="padding-right: 1em;">'
            + 'DisplayName:</label>'
            + '<input id="'+ nodeId +'displayName" class="form-control" readonly value="'+ node.displayName +'">';

        // Action对象有params参数
        var params = node.params;

        var paramsHtml = "";

        // 第一个add,其他delete
        // 遍历的是参数
        for ( var i in params) {
            // console.log("Iterate params: " + JSON.stringify(params[i].paramsKey));
            var divid = myuid();
            paramsHtml += '<div id="' + divid + '" > ';

            var optional = orcheHtml.isOptional(params[i].optional);

            // 参数名: 参数值输入框 的格式. 参数与nodeId绑定起来, 确保每个参数值独立不受影响
            paramsHtml += '<label type="text" for="' + nodeId + params[i].paramsKey + '-value" id = "' + nodeId + params[i].paramsKey + '">'
                + params[i].paramsKey + optional + ': ' + '<label/>'
                + '<input type="text" id = "' + nodeId + params[i].paramsKey + '-value" readonly value="' + params[i].paramValue + '">';

            paramsHtml += "</div>";
        }

        $("#stageDiv").html(stageHtml);
        $("#displayNameDiv").html(displayNameHtml);
        $("#envs").html(paramsHtml);

    },

    /**
     * 判断该参数是否是可选的
     * @Param param[i]
     * @return "" or "(optional)"
     * */
    isOptional : function(optional){
        if(optional == true) return '(optional)';
        else return '';
    },

	/**
	 * 暂时不需要，以前用来分类绘制组件的
	 * 
	 * @param componentTypes
	 */
	paintComponentMeta : function(componentTypes) {
		var types = componentTypes;
		console.log("paintComponentMeta " + componentTypes);
		var html = '';
		for ( var i in types) {
			var type = types[i];
			if (type.name != "PACKAGE::DOCKER") {
				continue;
			}
			var typeName = type.name.replace(/::/g, "_");
			var headingId = 'heading-' + typeName, bodyId = typeName + '-list';
			html += '<div class="panel panel-default">'
					+ '<div class="panel-heading" role="tab" id="'
					+ headingId
					+ '">'
					+ '<h4 class="panel-title">'
					+ '<a data-toggle="collapse" data-parent="#panel-group-operations"'
					+ ' href="#'
					+ bodyId
					+ '" aria-expanded="true" aria-controls="'
					+ bodyId
					+ '">'
					+ type.displayName
					+ ' <span class="badge">0</span></a>'
					+ '</h4></div>'
					+ '<div id="'
					+ bodyId
					+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="'
					+ headingId + '">' + '<ul class="list-group"></ul>'
					+ '</div></div>';
		}
		$("#panel-group-operations").html(html);
	},

	/**
	 * 绘制所有Step列表, step可以枚举, 人工选择可用的易用性强的step列在左边
	 * 
	 * @param components
	 */
	paintComponentList : function(components) {
		var group = $("#panel-group-operations");
		for ( var i in components) {
			var component = components[i];
			// var listId = component.type.name.replace(/::/g, "_") + "-list";
			console.log("paintComponentList:" + component.stepName);
			var html = '<li class="list-group-item">'
					+ component.stepName
					+ '<i class="fa fa-plus pull-right" onclick="javascript:appPanel.addNode('
					+ component.id + ')"></i></li>';
			// panel-group-operations
			group.append(html);
			// var badge = group.prev().find(".badge");
			// badge.html(String(parseInt(badge.html()) + 1));
		}
	},

	/**
	 * 绘制节点
	 * 
	 * @param component
	 * @param nodeId
	 * @param actionName
	 * @returns
	 */
	paintNode : function(nodeId) {
		// 根据节点
		var node = appPanel.cachedNodes.get(nodeId);
		console.log("paintNode:nodeId " + node.nodeId);
		console.log("paintNode:cId " + node.componentId);

        var nodeDisplayName = node.displayName;

		var nodeName = node.stepName;

		var nodeStatus = node.nodeStatus;

		var nodeStageName = node.stageName;

		// 画布上的小框
		var html = '<div id='
				+ nodeId
				+ ' data-component-id="'
				+ node.componentId
				+ '" class="graph-node" data-toggle="tooltip" data-placement="left" title="'
				+ nodeName
				+ '">'
				+ '<div class="node-title"><i class="fa fa-minus-circle node-del-btn" onclick="javascript:appPanel.deleteNode('
				+ nodeId + ')"></i></div>'
				+ '<div class="node-name">step: <span>' + nodeName + '</span></div>'
                + '<div class="node-displayName">name:<span>' + nodeDisplayName + '</span></div>'
                + '<div class="node-stageName">stage:<span>' + nodeStageName + '</span></div>'
				+ '<div class="node-status" style="background: '+ orcheHtml.paintColorByStatus(nodeStatus) +'"><span style="text-align: center">' + nodeStatus + '</span></div>'
				+ '</div>';
		$("#tmp-panel").append(html);
		$("#" + nodeId).unbind("dblclick");
		$("#" + nodeId).dblclick(function() {
			// 节点双击事件
			appPanel.nodeClick(nodeId);
		});
		orcheHtml.paintEndPoint(nodeId);
		return nodeId;
	},

    /**
     * 根据绘制的节点状态改变背景的颜色
     * */
    paintColorByStatus : function (nodeStatus) {
        if(nodeStatus == undefined || nodeStatus == "" || nodeStatus == "CREATED") return "#fff7f4";
        else if(nodeStatus == "SUCCESS") return "#00dc00";
        else if(nodeStatus == "SUSPENDED") return "#d1d213";
        else if(nodeStatus == "SKIPPED") return "#737875";
        else if(nodeStatus == "FAIL") return "#d81217";
        else if(nodeStatus == "EDITED") return "#8bafd8";
        else if(nodeStatus == "RUNNING") return "#0d11d8"
    },

    /**
     * 根据节点的状态改变边框的颜色
     * */
    paintEdgeColorByStatus: function (nodeStatus) {
        if(nodeStatus == undefined || nodeStatus == "" || nodeStatus == "CREATED") return "";
        else if(nodeStatus == "SUCCESS") return "graph-node-running";
        else if(nodeStatus == "SUSPENDED") return "graph-node-suspended";
        else if(nodeStatus == "SKIPPED") return "graph-node-skipped";
        else if(nodeStatus == "FAIL") return "graph-node-error";
    },

    /**
     * 绘制从数据库中取得的node信息
     * */
    paintRunningNode : function (node) {
        var nodeId = node.nodeId;
        var nodeName = node.stepName;
        var nodeDisplayName = node.displayName;
        var nodeStatus = node.status;
        var nodeStageName = node.stageName;
        var xPos = node.xPos;
        var yPos = node.yPos;
        var params = node.params;

        var html = '<div id='
            + nodeId
            + '" class="graph-node" data-toggle="tooltip" data-placement="left" title="'
            + nodeName
            + '">'
            + '<div class="node-name">step: <span>' + nodeName + '</span></div>'
            + '<div class="node-name">name:<span>' + nodeDisplayName + '</span></div>'
            // + '<div class="node-name">stage:<span>' + nodeStageName + '</span></div>'
            + '<div class="node-status" style="background: '+ orcheHtml.paintColorByStatus(nodeStatus) +'"><span style="text-align: center">' + nodeStatus + '</span></div>'
            + '</div>';

        $("#tmp-panel").append(html);
        $("#" + nodeId).unbind("dblclick");
        $("#" + nodeId).dblclick(function() {
            // 节点双击事件
            appPanel.nodeClick(nodeId);
        });
        // 重新摆放node的位置
        appPanel.rearrangeElementWithPos(nodeId, xPos, yPos);
    },

	/**
	 * 绘制部署实例节点
	 * 
	 * @param nodeId
	 * @param nodeName
	 * @param nodePort
	 * @param nodeStatus
	 * @returns
	 */
	paintInstanceNode : function(nodeId, nodeName, nodePort, nodeStatus) {
		var id = verifyParam(nodeId) ? nodeId : dateToId();
		var nodeName = verifyParam(nodeName) ? nodeName : component.displayName;
		var nodePort = verifyParam(nodePort) ? nodePort : "";
		var statusStyle = "graph-node-" + nodeStatus.toLowerCase();
		var nodeStatus = appMain.statusMap
				.get(verifyParam(nodeStatus) ? nodeStatus : "RUNNING");
		var html = '<div id=' + id + ' class="graph-node graph-node-deployed '
				+ statusStyle
				+ '" data-toggle="tooltip" data-placement="left" title="'
				+ nodeName + '">' + '<div class="node-title"></div>'
				+ '<div class="node-name">name:<span>' + nodeName
				+ '</span></div>' + '<div class="node-port">port:<span>'
				+ nodePort + '</span></div>'
				+ '<div class="node-status">status:<span>' + nodeStatus
				+ '</span></div></div>';
		$("#tmp-panel").append(html);
		$("#" + id).unbind("dblclick");
		$("#" + id).dblclick(function() {
			appPanel.nodeClick(id);
		});
		orcheHtml.paintEndPoint(id);
		return id;
	},

	/**
	 * 绘制连接点
	 * 
	 * @param id
	 */
	paintEndPoint : function(id) {
		var instance = appInstance;
		instance.doWhileSuspended(function() {

			instance.addEndpoint('' + id, targetEndpoint, {
				anchor : [ "Left" ],
				uuid : id + "Left"
			});
			instance.addEndpoint('' + id, sourceEndpoint, {
				anchor : [ "Right" ],
				uuid : id + "Right"
			});
			instance.draggable($("#" + id));
		});
	},

	/**
	 * 绘制配置对话框中的主机列表
	 * 
	 * @param hosts
	 * @param currentHosts
	 */
	paintNodeDetailHost : function(hosts, currentHosts) {
		var html = '';
		for ( var i in hosts) {
			var host = hosts[i];
			html += '<li><div class="checkbox"><label><input type="checkbox" name="hostName" value="'
					+ host.id
					+ '"> '
					+ host.hostName
					+ ' ('
					+ host.hostIP
					+ ')</label></div></li>';
		}
		$("#detailModal #hostNames ul").html(html);
		for ( var i in currentHosts) {
			var host = currentHosts[i];
			$("#hostNames input[name='hostName'][value='" + host.id + "']")
					.prop("checked", true);
		}
	},

	

	/**
	 * 绘制配置对话框中的节点参数信息
	 * 
	 * @param params
	 * @param userParams
	 */
	paintActionParams : function(params, userParams) {
		var html = '';
		if (params.length > 0) {
			for ( var i in params) {
				var param = params[i];
				var value = param.defaultValue;
				if (verifyParam(userParams)) {
					for ( var j in userParams) {
						if (userParams[j].key == param.paramKey) {
							value = userParams[j].value;
						}
					}
				}
				value = escapeToHtml(value);
				html += '<tr style="'
						+ (param.paramKey == "port" ? "display:none" : "")
						+ '"><td class="paramKey">' + param.paramKey
						+ '</td><td class="paramValue">'
						+ '<textarea class="form-control" rows="1">' + value
						+ '</textarea></td><td>' + param.description
						+ '</td></tr>';
			}
		} else {
			html = DHtml.emptyRow(3);
		}

		$("#detailModal #actionParams tbody").html(html);
	},
	/**
	 * 画边
	 * 
	 * @param from
	 * @param to
	 * @param label
	 */
	paintEdge : function(from, to) {
		console.log("绘制在");
		var conn = appInstance.connect({
			uuids : [ from + "Right", to + "Left" ]
		});
		conn.unbind("dblclick");
		conn.bind("dblclick", function(connection, originalEvent) {
			var srcId=connInfo.connection.sourceId;
			var tarId=connInfo.connection.targetId;
			console.log(connInfo.connection.sourceId+"->"+connInfo.connection.targetId);
			if (confirm("delete edge? "+connInfo.connection.sourceId+"->"+connInfo.connection.targetId)) {
				jsPlumb.detach(conn);
			}
		});

	},

	/**
	 * 增加templates
	 */
	paintTemplateParams : function(node) {
		var html = '';
		var thead = '';
		var templates = node.templates;
		if (templates.length > 0) {
			thead += "<tr>" + "<th>template</th>" + "<th>target path</th>"
					+ "<th>reload cmd(optional)</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #templates thead").html(thead);

			for ( var i in templates) {
				var template = templates[i];
				html += "<tr>" + '<td class="temSource">'
						+ '<textarea class="form-control" name="source">'
						+ template.source + '</textarea>' + "</td>"
						+ '<td class="temTarget">'
						+ '<textarea class="form-control" name="target">'
						+ template.target + '</textarea>' + "</td>"
						+ '<td class="temCommand">'
						+ '<textarea class="form-control" name="command">'
						+ template.command + '</textarea>' + "</td>"
						+ '<td class="deleterow">'
						+ '<a href="#" role="button" class="text-warning">'
						+ '<i class="fa fa-minus-circle"></i>'
				'</a>' + "</td>"
				"</tr>";
			}
			$(".deleterow").unbind("click");
			$(".deleterow").bind('click', function() {
				var $killrow = $(this).parent('tr');
				$killrow.addClass("danger");
				$killrow.fadeOut(1000, function() {
					$(this).remove();
				});
			});
		}
		$("#detailModal #templates tbody").html(html);
	},

	addTemplate : function() {
		var html = "";
		var thead = "";
		var fileSelect = '<select class="form-control" name="source">';
		for ( var i in appPanel.cachedCustomFiles) {
			fileSelect += '<option value="'
					+ appPanel.cachedCustomFiles[i].fileKey + '">'
					+ appPanel.cachedCustomFiles[i].name + '</option>';
		}
		fileSelect += "</select>";
		if ($("#detailModal #templates thead tr").length <= 0) {
			thead += "<tr>" + "<th>template</th>" + "<th>target path</th>"
					+ "<th>reload command(optional)</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #templates thead").html(thead);
		}
		html += "<tr>" + '<td class="temSource">' + fileSelect + "</td>"
				+ '<td class="temTarget">'
				+ '<textarea class="form-control" name="target">'
				+ '</textarea>' + "</td>" + '<td class="temCommand">'
				+ '<textarea class="form-control" name="command">'
				+ '</textarea>' + "</td>" + '<td class="deleterow">'
				+ '<a href="#" role="button" class="text-warning">'
				+ '<i class="fa fa-minus-circle"></i>'
		'</a>' + "</td>"
		"</tr>";
		$("#detailModal #templates tbody").append(html);
		$(".deleterow").unbind("click");
		$(".deleterow").bind('click', function() {
			var $killrow = $(this).parent('tr');
			$killrow.addClass("danger");
			$killrow.fadeOut(1000, function() {
				$(this).remove();
			});
		});
	},

	paintAttributes : function(node) {
		var attributes = node.attributes;
		var html = '';
		var thead = '';
		if (attributes.length > 0) {
			thead += "<tr>" + "<th>AttrKey</th>" + "<th>AttrValue</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #attributes thead").html(thead);
			for ( var i in attributes) {
				var attribute = attributes[i];
				html += "<tr>" + '<td class="attrKey">'
						+ '<textarea class="form-control" name="attrKey">'
						+ attribute.attrKey + '</textarea>' + "</td>"
						+ '<td class="attrValue">'
						+ '<textarea class="form-control" name="attrValue">'
						+ attribute.attrValue + '</textarea>' + "</td>"
						+ '<td class="deleterow">'
						+ '<a href="#" role="button" class="text-warning">'
						+ '<i class="fa fa-minus-circle"></i>'
				'</a>' + "</td>"
				"</tr>";
			}
			$(".deleterow").unbind("click");
			$(".deleterow").bind('click', function() {
				var $killrow = $(this).parent('tr');
				$killrow.addClass("danger");
				$killrow.fadeOut(1000, function() {
					$(this).remove();
				});
			});
		}
		$("#detailModal #attributes tbody").html(html);
	},

	addAttribute : function() {
		var html = "";
		var thead = "";
		if ($("#detailModal #attributes thead tr").length <= 0) {
			thead += "<tr>" + "<th>AttrKey</th>" + "<th>AttrValue</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #attributes thead").html(thead);
		}
		html += "<tr>" + '<td class="attrKey">'
				+ '<textarea class="form-control" name="attrKey">'
				+ '</textarea>' + "</td>" + '<td class="attrValue">'
				+ '<textarea class="form-control" name="attrValue">'
				+ '</textarea>' + "</td>" + '<td class="deleterow">'
				+ '<a href="#" role="button" class="text-warning">'
				+ '<i class="fa fa-minus-circle"></i>'
		'</a>' + "</td>"
		"</tr>";
		$("#detailModal #attributes tbody").append(html);
		$(".deleterow").unbind("click");
		$(".deleterow").bind('click', function() {
			var $killrow = $(this).parent('tr');
			$killrow.addClass("danger");
			$killrow.fadeOut(1000, function() {
				$(this).remove();
			});
		});
	},

	paintCustomFileList : function(files, selectedFile) {

	}
};

$(document).ready(function() {
	appPanel.init();
});

























