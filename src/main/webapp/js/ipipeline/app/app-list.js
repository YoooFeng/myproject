var appList = {
	init : function() {
	    // 一旦访问就去获取project数据
		appList.requestAppList();
	},

	requestAppList : function() {

	    console.log("List button clicked");
		// "/IntelliPipeline/project/list", 获取的是Json数据不是页面
        // 先GET到数据然后动态生成HTML页面展示
		ajaxGetJsonAuthc(dURIs.projectDataURI.projectList, null, appList.genAppListTable,
				null);
	},

	genAppListTable : function(data) {
	    console.log("Project received");
		var apps = data;

		console.log("Size of Project: " + apps.length);
		if (!(apps instanceof Array)) {
			return;
		}
		var html = '';
		if (apps.length > 0) {
			for ( var i in apps) {
				var app = apps[i];

				html += '<div class="panel panel-default">'
						+ '<div class="panel-heading" role="tab" id="app-'
						+ app.id
						+ '">'
						+ '<table class="table table-bordered table-condensed deploy-table">'
						+ '<tr><td style="width: 20%;">'
						+ app.name
						+ '</td><td style="width: 30%;">'
						+ app.gitUrl
                        + '</td><td style="width: 10%;">'
                        + app.status
                        + '</td><td>'
                        // 实际上每个项目只生成一行, 后面的数字表示具有的功能按钮
						// 1 == "构建详情"
						+ appList.getOperationBtnHtml(app.id, 1)
                        // 4 == "流程建模"
                        + appList.getOperationBtnHtml(app.id, 2)
                        // 6 == "删除"
                        + appList.getOperationBtnHtml(app.id, 6)
					    // 7 == "构建统计"
					    + appList.getOperationBtnHtml(app.id, 7)
						+ '</td></tr></table></div>'
						+ '<div id="detail-app-'
						+ app.id
						+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="app-'
						+ app.id
						+ '">'
						+ '<div class="panel-body"><table class="table table-bordered table-condensed deploy-table">'
						+ '<tbody><tr><td style="width:30%;">项目创建时间：'
						+ getFormatDateFromLong(app.createdAt)
						+ '</td><td colspan="3">最新修改时间：'
						+ getFormatDateFromLong(app.updatedAt)
						+ '</td></tr>';


				// var instanceCnt = 0;
				// for ( var i in app.containers) {
				// 	instanceCnt += app.containers.length;
				// }
				// html += instanceCnt + '</td></tr>'; // 实例数量，以后修改
				// // for ( var i in app.containers) {
				// //
				// // 	var container = app.containers[i];
				// // 	if(container.deployment==null){
				// // 		console.log("deployment为null");
				// // 	html += '<tr><td style="font-weight:bold;">middleware-name：'
				// // 			+ container.name + '</td><td>middleware-port:'
				// // 			+ container.port + '<br />';
				// // 	for ( var k in container.params) {
				// // 		var param = container.params[k];
				// // 		if (param.key == "jvm_args") {
				// // 			html += 'JVM args:' + param.value + '<br />';
				// // 		}
				// // 		if (param.key == 'session_shared') {
				// // 			html += 'session shared:' + param.value + '<br />';
				// // 		}
				// // 	}
				// // 	html += '</td><td>middlewarestatus：'
				// // 			+ container.status
				// // 			+ '</td><td>primary-instances：' + container.initCount
				// // 			+ '<br />max-instances：' + container.maxCount
				// // 			+ ' <br />current-instances：' + container.instances.length
				// // 			+ '</td> </tr>';
				// // 	for ( var j in container.instances) {
				// // 		var instance = container.instances[j];
				// // 		html += ' <tr><td>instance name：' + instance.name + '</td><td>instance port：'
				// // 				+ instance.port + '</td><td colspan="2">instance-status：'
				// // 				+ instance.status
				// // 				+ '</td></tr>';
				// // 	}
				// // 	}
				// // }


				// 遍历builds对象获取对象的详细信息
				for ( var i = app.builds.length - 1; i >= 0; i--) {
					var build = app.builds[i];
					// console.log("Build: " + JSON.stringify(build));
					if(build != null){
						var status = build.status;
						var consoleOutput = build.consoleOutputFilePath;
						var durationTime = build.durationTime;

						// 根据commit hash和git地址生成提交的详情链接
						var commitId = build.commitId;

                        // url格式: github.com/USER-NAME/REPO-NAME/commit/COMMIT-ID
                        // 只显示前五位,然后使其可点击,直接到commit详情页面.
                        var commitUrl = "http://github.com/YoooFeng/shipping" + "/commit/" + commitId;

                        console.log("Commit Hash: " + commitId);
						console.log("commit url:" + commitUrl);

						// console.log("DurationTime: " + durationTime);
						// console.log("Build status: " + status);
                        if(commitId == undefined) {
                            commitId = "-";
                        } else {
                            commitId = commitId.substring(0, 6);
                        }
						html += '<tr>'
							      +'<td style="font-weight:bold;">构建序号:'+ i + '</td>'
								  + '<td>构建时间:'+ appList.transformDurationTime(durationTime) +'</td>'
                                  + '<td style=\'cursor:pointer\' onclick=\'appList.commitClick(\"' + commitUrl + '\");\'>代码提交:'+ commitId.substring(0, 6) + '</td>'
						          +'<td>构建状态:'+ status + '</td>';

					    html += '<td>'+ appList.getBuildGraphBtnHtml(app.id, build.id)
					        + appList.getBuildLogBtnHtml(consoleOutput)
					        + '</td>';

                        // 为正在构建的项目添加停止按钮
                        if(status == "RUNNING") {
                            html += '<td>' + appList.getBuildStopBtnHtml(build.id) + '</td></tr>';
                        }
						// html += '<tr>'
						// 	    +'<td>'+'节点IP：133.133.134.96 <br/>集群IP：' + service.spec.clusterIP + '</td>'
						//         +'<td>节点端口：'+ service.spec.ports[0].nodePort+'<br/>集群端口:'+service.spec.ports[0].port+'</td>'
						//         +'<td colspan="2">协议类型：TCP'+ '</td>'
						//         +'</tr>';
						}
				}
				html += ' </tbody></table></div></div></div>';
			}
		} else {
			html = DHtml.emptyRow(6);
		}
		$("#app-list").html(html);
	},

    commitClick : function(url) {
	    window.open(url);
    },

	getBuildGraphBtnHtml : function (appId, buildId) {
        var html = '';
        var style = "fa-circle-o-notch";
        var text = "可视化构建过程";
        var func = "appList.showBuildProcess(" + buildId + ")";

        html += '<i class="fa ' + style + ' text-success small"></i> '
            + '<a class="link-btn" href="javascript:'
            + (func == undefined ? 'void(0)' : func) + '">' + text
            + '</a>';

        return html;
    },

    getBuildStopBtnHtml : function(buildId) {
	    var html = '';
	    var style = "fa-circle-o-notch";
	    var text = "停止构建";
	    var func = "appList.stopBuildProcess(" + buildId + ")";

        html += '<i class="fa ' + style + ' text-success small"></i> '
            + '<a class="link-btn" href="javascript:'
            + (func == undefined ? 'void(0)' : func) + '">' + text
            + '</a>';

        return html;
    },

	getBuildLogBtnHtml : function(consoleOutput){
		var html = '';
		var style = "fa-circle-o-notch";
        var text = "控制台输出";
        var func = "appList.showConsoleOutput('" + consoleOutput + "')";

        html += '<i class="fa ' + style + ' text-success small"></i> '
            + '<a class="link-btn" href="javascript:'
            + (func == undefined ? 'void(0)' : func) + '">' + text
            + '</a>';

		return html;
	},

	getOperationBtnHtml : function(appId, operationId) {
		var html = '';
		var style = "fa-circle-o-notch";
		var text = "actions";
		var func = undefined;
		switch (operationId) {
			case 1:
				break;
			case 2:
				text = "流程建模";//建模
				func = "appList.orchestration(" + appId + ",1)";
				break;
			case 4:
				text = "建模";//组件建模
				func = "appList.orchestration(" + appId + ",2)";
				break;
			case 5:
				text = "容错";//容错
				func = "appList.orchestration(" + appId + ",3)";
				break;
			case 6:
				text = "删除";
				func = "appList.deleteApp(" + appId +")";
				break;
			case 7:
				text = "构建统计";
				func = "appList.showBuildStatistics(" + appId + ")";
				break;
			case 8:
				text = "控制台输出";
				// 这里的appId实际上是buildId
				func = "appList.showConsoleOutput(" + appId + ")";
				break;

		default:
			break;
		}
		// 编号1返回所有构建详情
		if (operationId == 1) {
			html += '<i class="fa '
					+ style
					+ ' text-success small"></i> '
					+ '<a class="link-btn" data-toggle="collapse" data-parent="#app-list" href="#detail-app-'
					+ appId
					+ '" aria-expanded="true" aria-controls="detail-app-'
					+ appId + '"> 构建详情 </a>';
		} else {
			html += '<i class="fa ' + style + ' text-success small"></i> '
					+ '<a class="link-btn" href="javascript:'
					+ (func == undefined ? 'void(0)' : func) + '">' + text
					+ '</a>';
		}
		return html;
	},

    // 停止构建按钮功能
    stopBuildProcess : function(buildId) {
		ajaxGetJsonAuthc(dURIs.buildDataURI.stopBuildById + "/" + buildId, null, appList.stopBuildProcessCallBack, null);
    },

	stopBuildProcessCallBack : function() {
        // 直接刷新主页
        appList.requestAppList();
    },

	showConsoleOutput : function(logFilePath){
		$("#scanFileModal").modal('show');

		var parsed = logFilePath.split("/");
		var projectName = parsed[1];
		var fileName = parsed[2];

		$.ajax({
			type: "GET",
			url : "build_data/logFile/" + projectName + "/" + fileName,
			data : "text",
			success : function (logContent) {
				// logContent = logContent.replace('/n', '<br />');
				// logContent = logContent.replace('/r/n', '<br />');
				// var reg = new RegExp("\r\n", "g");
				logContent = logContent.replace(/(?:\\n)/g, '\n');
				logContent = logContent.replace(/(?:\\r)/g, '');
                $("#scanFileBody").html(logContent);
            }
		});
		// var fso = new ActiveXObject("Scripting.FileSystemObject");
		// var logFile = fso.OpenTextFile(logFilePath, ForReading);
		// var logContent = logFile.ReadAll();
		// logFile.Close();


	},

	// 统计构建历史数据, 弹出饼状图页面的函数
    showBuildStatistics : function (appId) {

	    // 根据appId得到project对象
        ajaxGetJsonAuthc(dURIs.projectDataURI.getProject + "/" + appId, null, appList.statisticsCallback,
            null);
    },

    statisticsCallback : function(data) {

        var app = data;

        var runningCount = 0;
        var successCount = 0;
        var failCount = 0;
        var skipCount = 0;
        var cancelCount = 0;

        // 遍历builds对象获取对象的详细信息
        for ( var i in app.builds) {
            var build = app.builds[i];

            // RUNNING, SUCCEED, FAIL, ABORTED, SKIPPED
            if(build != null){
                var status = build.status;
                switch (status) {
                    case "RUNNING":
                        runningCount++;
                        break;
                    case "SUCCEED":
                        successCount++;
                        break;
                    case "FAIL":
                        failCount++;
                        break;
                    case "ABORTED":
                        cancelCount++;
                        break;
                    case "SKIPPED":
                        skipCount++;
                        break;
                    default:
                        break;
                }
            }
        }

        var chartData = [
            {
                "构建状态" : "正在构建",
                "value" : runningCount,
                "pulled" : true
            },
            {
                "构建状态" : "跳过",
                "value" : skipCount,
                "pulled" : true
            },
            {
                "构建状态" : "成功",
                "value" : successCount,
                "pulled" : true
            },
            {
                "构建状态" : "失败",
                "value" : failCount,
                "pulled" : true
            },
            {
                "构建状态" : "取消",
                "value" : cancelCount,
                "pulled" : true
            },
        ];

        // chartDiv是div id, 用来显示饼状图
        var chart = AmCharts.makeChart("chartDiv", {
            "type" : "pie",
            "theme" : "light",
            "titleField" : "构建状态",
            "valueField" : "value",
            "dataProvider" : chartData,
            "depth3D" : 15,
            "angle" : 30,
            "export" : {"enable" : true},
            "outlineAlpha" : 0.4,
            "pulledField" : "pulled",
            "balloonText" : "[[title]]<br><span style='font-size: 14px'><b>[[value]]</b> ([[percents]]%)</span>"
        });


        // 显示饼图窗口
        $("#pieChartModal").modal('show');

        // 确保PieChart显示出来?
        chart.invalidateSize();
    },

    // 展示动态的构建过程
    showBuildProcess : function (buildId) {
        ajaxGetJsonAuthc(dURIs.buildDataURI.getBuildById + "/" + buildId, null, appList.requestBuildingCallback, null);

        // 定时5秒刷新页面
        // var timerId = setInterval(function () {
        //     console.log("Refresh");
        //     appList.requestBuildingCallback(buildId);
        // }, 5000);

        // $.ajax({
        //     type:"get",
        //     async: false,
        //     url: dURIs.buildDataURI.getBuildById + "/" + buildId,
        //     timeout: 10000,
        //     success:function (data) {
        //         console.log("Build: " + JSON.stringify(data));
        //         loadPage(dURIs.viewsURI.buildOrchestration, function () {
        //             appPanel.showBuildingProcess(data);
        //         })
        //     },
        //     error: function () {
        //         alert("获取构建信息失败!");
        //     }
        // })
        // 已经得到build的数据了, 通过build的数据动态绘制执行流程图
    },

	// 得到的是某次build对象, 要转化成Graph对象来重新绘制执行流程图
    requestBuildingCallback : function(data){
        var build = data;
        console.log("Build: " + JSON.stringify(build));
        console.log(build.id);
        loadPage(dURIs.viewsURI.buildOrchestration, function () {

            // 这里先隐藏相关的按钮和div, 还是应该跳转到不同的页面?

            // 获取build数据, 然后更新页面
            appPanel.showBuildingProcess(data);
        });

        // $.ajax({
        //     type:"get",
        //     async: false,
        //     url: dURIs.buildDataURI.getBuildById + "/" + buildId,
        //     timeout: 10000,
        //     success:function (data) {
        //         console.log("Build: " + JSON.stringify(data));
        //         loadPage(dURIs.viewsURI.buildOrchestration, function () {
        //             appPanel.showBuildingProcess(data);
        //         })
        //     },
        //     error: function () {
        //         alert("获取构建信息失败!");
        //     }
        // })
	},

	orchestration : function(appId, tab) {
		console.log("拓扑化:"+appId+":"+tab);
		loadPage(dURIs.viewsURI.buildOrchestration, function() {
			appPanel.initForEdit({
				appId : appId,
				tab : tab
			});
		});
	},

	// deploy : function(appId) {
	// 	ajaxPostJsonAuthc(dURIs.appURI + "/" + appId, null,
	// 			appList.operateSuccess, defaultErrorFunc, true);
	// },

	operateSuccess : function() {
		loadPage(dURIs.viewsURI.appList, null);
		defaultSuccessFunc();
	},

    transformDurationTime : function (durationTime) {
	    var time = parseInt(durationTime);
	    // 小于1000毫秒, 直接以ms形式返回
	    if(time < 1000) return String(time) + "ms";
	    else if(time < 60000) return String((time/1000) ^ 0) + "s";
	    else return String((time / 60000) ^ 0) + "min" + String((time % 60000 / 1000) ^ 0) + "s";
    },

	deleteApp: function(appId){
		if(confirm("delete application?")){
			ajaxDeleteJsonAuthc(dURIs.projectDataURI.deleteProject + "/" + appId, null, appList.operateSuccess, defaultErrorFunc, true);
		}
	}
};

$(document).ready(function() {
	appList.init();
});













