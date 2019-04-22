<%--
  Created by IntelliJ IDEA.
  User: frank
  Date: 18-4-26
  Time: 下午3:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Projects</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta username="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
    <div class="intro">
        <h1>项目管理&nbsp;</h1>
        <p class="lead" style="margin-top:10px">
            查看持续集成项目详情.
        </p>
    </div>

    <div class="content-board">
        <table style="margin-bottom: 0px; border-bottom: 0px;"
               class="table table-bordered table-condensed deploy-table">
            <thead>
            <tr>
                <th style="width: 20%;">项目名</th>
                <th style="width: 30%;">仓库地址</th>
                <th style="width: 10%;">状态</th>
                <th>更多操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
        <div style="margin-top: 0px;" class="panel-group" id="app-list"
             role="tablist" aria-multiselectable="true">

        </div>
    </div>

    <!--浏览文件对话框 -->
    <div id="scanFileModal" class="modal fade" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title">
                        控制台输出 <span id="scanFileName" class="control-label"></span>
                    </h4>
                </div>
                <div class="modal-body" style="height: 500px">
								<textarea id="scanFileBody" readonly
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

    <!-- 查看构建饼状图对话框 -->
    <div id="pieChartModal" class="modal fade" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title">
                        构建结果统计饼状图 <span id="chartName" class="control-label"></span>
                    </h4>

                </div>
                <div class="modal-body" style="height: 500px">
                    <div id="chartDiv" style="width: 600px; height: 400px;"></div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                            data-dismiss="modal">close</button>
                </div>
            </div>
        </div>
    </div>

    <!-- TODO: 停止构建确认对话框 未使用-->
    <div style="margin-top: 200px;" class="modal fade" id="stopModal"
         tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true" data-backdrop="static">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">停止构建? </h4>
                </div>
                <div class="modal-body">
                    <div style="width: 50%;">
                        <div class="form-group">
                            <label for="appName">当前项目名</label> <input type="text"
                                                                      class="form-control" id="appName"
                                                                      placeholder="application username" readonly>
                        </div>
                    </div>
                    <div style="width: 50%";>
                        <label>确定要停止构建吗?</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" id="stop-build-btn" class="btn btn-primary">确定</button>
                </div>
            </div>
        </div>
    </div>


    <script src="<c:url value='/js/ipipeline/app/app-list.js' />"></script>
    <script src="<c:url value='/js/ipipeline/amcharts/amcharts.js' />"></script>
    <script src="<c:url value='/js/ipipeline/amcharts/pie.js' />"></script>
    <script src="<c:url value='/js/ipipeline/amcharts/serial.js' />"></script>
    <script src="<c:url value='/js/ipipeline/amcharts/themes/light.js' />"></script>
</div>
</body>
</html>
