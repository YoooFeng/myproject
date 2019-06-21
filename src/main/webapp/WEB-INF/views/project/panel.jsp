<%--
  Created by IntelliJ IDEA.
  User: frank
  Date: 18-5-3
  Time: 上午11:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Modeling</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Open+Sans+Condensed:300">

    <style type="text/css">
        .passed {
            font-family : 'Open Sans Condensed', sans-serif;
            font-size : 35px;
            text-align : center;
            color : #00bb00
        }

        .failed {
            font-family: 'Open Sans Condensed', sans-serif;
            font-size : 35px;
            text-align: center;
            color : #e04141
        }

        .unknown {
            font-family: 'Open Sans Condensed', sans-serif;
            font-size : 35px;
            text-align: center;
            color : #e6b900
        }

    </style>
</head>
<body>
<div id="d-main-content">
    <div class="intro">
        <h1>流程建模&nbsp;</h1>
        <p class="lead" style="margin-top: 10px">
            <em>流程建模&nbsp;</em>是用来构建持续集成流程的可视化模块, 通过
            点击右侧的小加号, 添加构建基本组件 -- Step, 拖拽, 连接和双击这些
            Step来定制符合您需求的持续集成流程.
        </p>
    </div>
    <a class="link-btn" id="refresh-btn" href="javascript:appPanel.refreshBuildProcess()">开启自动刷新构建流程图</a>
    <div id="app-panel" class="content-board">
        <div id="graph-panel" class="col-sm-10" style="overflow: auto;">
            <div id="tmp-panel"></div>
        </div>
        <div id="group-list" class="col-sm-2">
            <div>
                <div id="btn-save" class="save-btn" style="display: none;">
                    <i class="fa fa-save"></i> 执行构建
                </div>
                <div id="btn-save-as" class="save-btn" style="display: none;">
                    <i class="fa fa-save"></i><i class="fa fa-pencil"></i> Save As
                </div>
                <div id="btn-strategy" class="save-btn" style="display: none;">
                    <i class="fa fa-clipboard"></i><i class="fa fa-pencil"></i> 配置构建策略
                </div>
                <div id="btn-classifier" class="save-btn" style="display: none;">
                    <i class="fa fa-clipboard"></i><i class="fa fa-tree"></i> 构建结果预测
                </div>
            </div>
            <!-- 组件(Step)列表 -->
            <div id="panel-group-operations" class="panel-group" role="tablist"
                 aria-multiselectable="true"></div>
        </div>
    </div>

    <!-- node details start -->
    <div class="modal fade" id="detailModal" tabindex="-1" role="dialog"
         aria-labelledby="myModalLabel" aria-hidden="true"
         data-backdrop="static">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close editCancel"
                            data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">Step配置</h4>
                </div>
                <div class="modal-body">
                    <form role="form ">
                        <div class="row" id="detail9">
                            <div class="col-sm-5">
                                <div class="form-group">
                                    <label for="serviceName" class="control-label pull-left"
                                           style="padding-right: 1em;">Step: </label>
                                    <input id="serviceName" name="serviceName" class="form-control" readonly>
                                <div class="form-group" id="displayNameDiv">
                                    <%--<label for="displayName" class="control-label pull-left" style="padding-right: 1em;">--%>
                                        <%--DisplayName:--%>
                                    <%--</label>--%>
                                    <%--<input id="displayName" class="form-control" name="displayName" placeholder="display name">--%>
                                </div>

                                </div>
                                <!-- <div class="form-group">
                                    <label for="containerPort" class="control-label pull-left"
                                        style="padding-right: 2em;">服务网络类型:</label>
                                    <div class="dropdown">
                                        <button class="btn btn-default dropdown-toggle" type="button"
                                            id="dropdownMenu2" data-toggle="dropdown"
                                            aria-haspopup="true" aria-expanded="false">
                                            ClusterIP <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu" aria-labelledby="dropdownMenu2">
                                            <li><a href="#">ClusterIP</a></li>
                                            <li><a href="#">NodePort</a></li>
                                        </ul>
                                    </div>
                                </div>
                                 -->
                                <div class="form-group" id="stageDiv">
                                    <%--<label for="stageName" class="control-label pull-left"--%>
                                           <%--style="padding-right: 1em;">所属Stage:</label>--%>
                                    <%--<input id="stageName" name="stageName" class="form-control" placeholder="building">--%>
                                </div>
                                <%--<div class="form-group">--%>
                                    <%--<label for="nodePort" class="control-label pull-left">节点端口:</label>--%>
                                    <%--<input type="number" id="nodePort" name="nodePort"--%>
                                           <%--class="form-control" placeholder="30303">--%>
                                <%--</div>--%>
                                <%--<div class="form-group">--%>
                                    <%--<label for="containerPort" class="control-label pull-left"--%>
                                           <%--style="padding-right: 2em;">容器端口:</label> <input--%>
                                        <%--type="number" id="containerPort" name="containerPort"--%>
                                        <%--class="form-control" placeholder="3306">--%>
                                <%--</div>--%>

                            </div>
                            <!-- 环境变量-->
                            <div class="col-sm-7"  id="right">
                                <label  class="col-sm-9">参数配置</label>
                                <div id="envs">
                                </div>
                                <%--<label id="dependent" class="col-sm-9" >依赖项</label>--%>
                                <%--<div id="dependents">--%>

                                <%--</div>--%>
                            </div>
                            <!-- 依赖项-->

                            <!-- end right col -->
                        </div>
                    </form>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default editCancel" id="nodeCancel" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary editSave" id="nodeSave">保存</button>
                </div>
            </div>
        </div>
    </div>

    <%-- 构建策略配置框 --%>
    <div class="modal fade" id="strategyModal" tabindex="-1" role="dialog"
         aria-labelledby="myModalLabel" aria-hidden="true"
         data-backdrop="static">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close editCancle" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">关闭</span>
                    </button>
                    <h3 class="modal-title" id="strategyModalLabel">配置构建的策略</h3>
                </div>
                <div class="modal-body">
                    <form role="form">
                        <div class="row" id="strategies">
                            <div class="col-sm-5">
                                <div class="form-group">
                                    <%--<label for="buildLevel" class="control-label pull-left col-lg-1" style="padding-right: 1em">构建等级: </label>--%>
                                    <form id="buildLevel">
                                        <legend><h4>构建的执行策略</h4></legend>

                                        <label><input type="checkbox" id="timeIntervalCheckBox" />距上一次任务执行时间大于
                                            <input type="text" id="timeIntervalText" class="form-control" name="time" value="0.5" onkeyup="appPanel.inputSize(this)" size="2"/>
                                            小时</label><br>

                                        <label><input type="checkbox" id="modifiedLinesCheckBox" />距上一次任务执行累积变更大于
                                            <input type="text" id="modifiedLinesText" class="myInputUnderline" name="loc" value="50" onkeyup="appPanel.inputSize(this)" size="2"/>行</label><br>

                                        <label><input type="checkbox" id="modelChangedCheckBox" />流水线模型发生改变</label><br>

                                        <label><input type="checkbox" id="keyPathCheckBox" onclick="appPanel.keyPathCheckBoxClick()"/>关键文件发生变更</label><br>
                                        <div id="keyPathDiv" style="display: none">
                                            <input type="text" id="keyPathText" placeholder="文件路径(多个路径用逗号隔开)" onkeyup="appPanel.inputSize(this)" size="15"/>
                                        </div><br>

                                        <label><input type="checkbox" id="committerCheckBox" onclick="appPanel.committerCheckBoxClick()"/>这些开发者commit时进行构建</label><br>
                                        <div id="committerDiv" style="display: none">
                                            <input type="text" id="committerText" placeholder="开发者邮箱(多个路径用逗号隔开)" onkeyup="appPanel.inputSize(this)" size="15" />
                                        </div><br>

                                        <%-- commit message 规则实现 --%>

                                    </form><br><br>
                                        <%--<label for="modifiedLines" class="control-label pull-left" style="padding-right: 1em"><h4>修改代码行数阈值</h4></label><br>--%>
                                    <%--<input type="text" id="modifiedLines" class="input-underline" value="0" placeholder="0"/>--%>
                                </div>
                            </div>
                            <div class="col-sm-7" id="strategy-right">
                                <form id="authorLevel">
                                    <legend><h4>构建的跳过策略</h4></legend>
                                    <label><input type="checkbox" id        ="skipPathCheckBox" value="Experienced" onclick="appPanel.skipPathCheckBoxClick()" />只有非关键文件发生变更</label>
                                    <div id="skipPathDiv" style="display: none">
                                        <input type="text" id="skipPathText" placeholder="文件路径(多个路径用逗号隔开)"/></div><br>
                                </form><br><br>
                            </div>
                            <div class="col-sm-7" id="strategy-target">
                                <form id="target">
                                    <legend><h4>作用对象</h4></legend>
                                    <label id="target-label">
                                        <input type="checkbox" name="targetHobby" value="build" />整个构建
                                    </label>
                                </form>

                            </div>
                        </div>
                    </form>
                </div>
                <%--下方的按钮--%>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default editCancel" id="strategySaveCancel" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary editSave" id="strategySave">保存</button>
                </div>

            </div>
        </div>
    </div>

    <%--分类器详情框--%>
    <div class="modal fade" id="classifierModal" tabindex="-1" role="dialog"
         aria-labelledby="myModalLabel" aria-hidden="true"
         data-backdrop="static">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close editCancle" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">关闭</span>
                    </button>
                    <h3 class="modal-title">构建结果预测</h3>
                </div>
                <div class="modal-body">
                    <form role="form">
                        <label>当前构建的预测结果为: </label><br><br>
                        <div id="predictResult">

                        </div><br><br>
                        <div id="predictParams"></div>
                        <br><br>
                        <button type="button" class="btn btn-outline-success" id="decision-tree-btn">查看预测决策树</button>
                        <button type="button" class="btn btn-outline-success" id="update-tree-btn" >更新决策树模型</button>

                    </form>
                </div>
                <%--下方的按钮--%>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default editCancel" id="classifier-cancel-btn" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary editSave" id="classifier-save-btn">确定</button>
                </div>

            </div>
        </div>
    </div>


    <!-- 执行构建确认框 -->
    <div style="margin-top: 200px;" class="modal fade" id="saveModal"
         tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">执行构建? </h4>
                </div>
                <div class="modal-body">
                    <div style="width: 50%;">
                        <div class="form-group">
                            <label for="appName">当前项目名</label> <input type="text"
                                                                      class="form-control" id="appName"
                                                                      placeholder="application name" readonly>
                        </div>
                    </div>
                    <div style="width: 50%";>
                        <label>确定要执行构建吗?</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" id="save-app-btn" class="btn btn-primary">确定</button>
                </div>
            </div>
        </div>
    </div>
    <!-- task commit end -->

    <!--显示字符串形式的决策树 -->
    <div id="treeStringModal" class="modal fade" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title">
                        决策树模型 <span id="treeStringName" class="control-label"></span>
                    </h4>
                </div>
                <div class="modal-body" style="height: 500px">
								<textarea id="treeStringBody" readonly
                                          style="resize: none; width: 100%; height: 100%">
								</textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                            data-dismiss="modal">close</button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <%--结束--%>

    <!-- 任务可执行操作对话框 -->
    <div style="margin-top: 200px;" class="modal fade" id="operationModal"
         tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">actions</h4>
                </div>
                <div class="modal-body">
                    <ul class="operation-list">
                        <li id="start" data-oper-type="START" data-view-id="1">
                            <div class="oper-icon">
                                <i class="fa fa-play text-success"></i>
                            </div>
                            <div class="oper-text">start</div>
                        </li>
                        <li id="stop" data-oper-type="STOP" data-view-id="1">
                            <div class="oper-icon">
                                <i class="fa fa-stop text-danger"></i>
                            </div>
                            <div class="oper-text">stop</div>
                        </li>
                        <li id="remove" data-oper-type="REMOVE" data-view-id="2">
                            <div class="oper-icon">
                                <i class="fa fa-minus text-danger"></i>
                            </div>
                            <div class="oper-text">remove</div>
                        </li>
                        <li id="copy" data-oper-type="COPY" data-view-id="2">
                            <div class="oper-icon">
                                <i class="fa fa-plus text-primary"></i>
                            </div>
                            <div class="oper-text">copy</div>
                        </li>
                        <li id="fail" data-oper-type="FAIL" data-view-id="3">
                            <div class="oper-icon">
                                <i class="fa fa-times text-danger"></i>
                            </div>
                            <div class="oper-text">fail test</div>
                        </li>
                        <li id="fail" data-oper-type="START" data-view-id="3">
                            <div class="oper-icon">
                                <i class="fa fa-play text-success"></i>
                            </div>
                            <div class="oper-text">recover</div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <!-- operation end -->
    <!-- node details end -->
    <script src="<c:url value='/js/jsPlumb/dom.jsPlumb-1.7.2.js' />"></script>
    <script src="<c:url value='/js/ipipeline/app/graph/j-class.js' />"></script>
    <script src="<c:url value='/js/ipipeline/app/graph/node.js' />"></script>
    <script src="<c:url value='/js/ipipeline/app/app-plumb.js' />"></script>
    <script src="<c:url value='/js/ipipeline/app/workflow-orchestration.js' />"></script>
</div>
</body>
</html>
