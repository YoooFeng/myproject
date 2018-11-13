<%--
  Created by IntelliJ IDEA.
  User: frank
  Date: 18-5-4
  Time: 下午2:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>动作列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
    <div class="intro">
        <h1>动作列表&nbsp;</h1>
        <p class="lead" style="margin-top:10px">
            查看, 添加和删除您的动作.
        </p>
    </div>

    <div class="content-board">
        <table style="margin-bottom: 0px; border-bottom: 0px;"
               class="table table-bordered table-condensed deploy-table">
            <thead>
            <tr>
                <th style="width: 20%;">动作名</th>
                <th>更多操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
        <div style="margin-top: 0px;" class="panel-group" id="action-list"
             role="tablist" aria-multiselectable="true">
        </div>
    </div>

    <script src="<c:url value='/js/ipipeline/action/action-list.js' />"></script>
</div>
</body>
</html>
