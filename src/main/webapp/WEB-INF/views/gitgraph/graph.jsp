<%--
  Created by IntelliJ IDEA.
  User: frank
  Date: 18-11-2
  Time: 下午3:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>提交和构建历史</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<body>

<div id="d-main-content">

    <div class="intro">
        <h1>提交和构建历史&nbsp;</h1>
        <p class="lead" style="margin-top: 10px">
            <em>提交和构建历史&nbsp;</em>用时间轴的形式展示了项目的代码提交历史记录和与之对应的构建历史记录.
        </p>
    </div>

    <%--展示git graph的画板--%>
    <div id="app-panel" class="content-board">
        <canvas id="gitgraph"></canvas>
    </div>

    <script src="<c:url value='/js/gitgraph/gitgraph.min.js' />"></script>
    <script src="<c:url value='/js/ipipeline/app/app-gitgraph.js' />"></script>
</div>

</body>
</html>
