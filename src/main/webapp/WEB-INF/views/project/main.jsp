<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>IntelliPipeline</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="<c:url value='/css/bootstrap/bootstrap.min.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/font-awesome-4.2.0/css/font-awesome.min.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/ipipeline/cloudeploy.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/new/dashboard.css' />" type="text/css"
          rel="stylesheet">
    <link href="<c:url value='/css/ipipeline/app-list.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/ipipeline/app-panel.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/ipipeline/template.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/uploadify/uploadify.css' />"
          type="text/css" rel="stylesheet">
    <link href="<c:url value='/css/new/style.css'/>" type="text/css"
          rel="stylesheet" />
    <%@ include file="../share/head.jsp"%>
</head>

<body bgcolor="rgb(238,238,238)">
<script src="<c:url value='/js/jquery/jquery-1.11.0.js' />"></script>
<script src="<c:url value='/js/jquery/json/jquery.json-2.4.min.js' />"></script>
<script src="<c:url value='/js/jquery/dateFormat/jquery.dateFormat.js' />"></script>
<script src="<c:url value='/js/bootstrap/bootstrap.min.js' />"></script>
<script src="<c:url value='/js/ipipeline/uri.js' />"></script>
<script src="<c:url value='/js/ipipeline/map.js' />"></script>
<script src="<c:url value='/js/ipipeline/common.js' />"></script>
<script src="<c:url value='/js/ipipeline/app/app-main.js' />"></script>

<div class="d-main">
    <div class="navigation">
        <div class="profile">
            <div class="avartar">
                <div id="avartar-top"></div>
                <div id="avartar-bottom"></div>
            </div>
            <p class="user-username">
                <a href="javascript:void(0)">Admin</a>
            </p>
            <div class="user-link">
                <a title="index"><span class="glyphicon glyphicon-home"></span></a>
                <a href="#" title="dashboard"><span
                        class="glyphicon glyphicon-dashboard"></span></a> <a title="help"><span
                    class="glyphicon glyphicon-book"></span></a> <a href="#"
                                                                    title="logout"><span class="glyphicon glyphicon-off"></span></a>
            </div>
        </div>
        <div class="sidebar">
            <ul id="sidebar-list" class="nav nav-list">
                <li id="sys-list-btn"><a><span
                        class="glyphicon glyphicon-bookmark cool-orange"></span><span
                        class="username">CI项目管理</span><span class="title"></span></a>
                    <div class="cool-border"></div></li>

                <li id="project-list-btn"><a><span
                        class="glyphicon glyphicon-th-list cool-blue"></span><span
                        class="username">全部项目</span><span class="title">All Projects</span></a>
                    <div class="cool-border"></div></li>
                <%--<li id="build-list-btn"><a><span--%>
                <%--class="glyphicon glyphicon-th-list cool-blue"></span><span--%>
                <%--class="username">All Builds</span><span class="title">全部构建</span></a>--%>
                <%--<div class="cool-border"></div></li>--%>
                <li id="new-project-btn"><a><span
                        class="glyphicon glyphicon-th-list cool-blue"></span><span
                        class="username">新建项目</span><span class="title">New Project</span></a>
                    <div class="cool-border"></div></li>

                <li id="gitgrapgh-btn"><a><span
                        class="glyphicon glyphicon-th-list cool-blue"></span><span
                        class="username">代码提交历史</span><span class="title">Commit History</span></a>
                    <div class="cool-border"></div></li>

                <li id="sys-list-btn"><a><span
                        class="glyphicon glyphicon-bookmark cool-orange"></span><span
                        class="username">构建操作管理</span><span class="title"></span></a>
                    <div class="cool-border"></div></li>

                <li id="all-action-btn"><a><span
                        class="glyphicon glyphicon-retweet cool-blue"></span><span
                        class="username">操作列表</span><span class="title">All Actions</span></a>
                    <div class="cool-border"></div></li>

                <li id="new-action-btn"><a><span
                        class="glyphicon glyphicon-th-list cool-blue"></span><span
                        class="username">新建操作</span><span class="title">New Action</span></a>
                    <div class="cool-border"></div></li>

                <%--<li buildId="new-user-btn"><a><span--%>
                <%--class="glyphicon glyphicon-retweet cool-blue"></span><span--%>
                <%--class="username">New User</span><span class="title">用户注册</span></a>--%>
                <%--<div class="cool-border"></div></li>--%>

                <%--<li buildId="charts-btn"><a><span--%>
                <%--class="glyphicon glyphicon-road cool-blue"></span><span--%>
                <%--class="username">Component</span><span class="title">组件管理</span></a>--%>
                <%--<div class="cool-border"></div></li>--%>
                <%--<li buildId="inf-list-btn"><a><span--%>
                <%--class="glyphicon glyphicon-bookmark cool-orange"></span><span--%>
                <%--class="username">基础设施</span><span class="title"></span></a></li>--%>
                <%--<div class="cool-border"></div>--%>
                <%--<li buildId="cluster-list-btn"><a><span--%>
                <%--class="glyphicon glyphicon-cloud cool-blue"></span><span--%>
                <%--class="username">Cluster</span><span class="title">集群与资源管理</span></a>--%>
                <%--<div class="cool-border"></div></li>--%>
                <%--<li buildId="docker-btn"><a><span--%>
                <%--class="glyphicon glyphicon-th-large cool-blue "></span><span--%>
                <%--class="username">Docker</span><span class="title">镜像与容器管理</span></a>--%>
                <%--<div class="cool-border"></div></li>--%>
                <%--<li buildId="consul-btn" ><a><span--%>
                <%--class="glyphicon glyphicon-th-large cool-blue "></span><span--%>
                <%--class="username">Consul</span><span class="title">数据中心健康度</span></a>--%>
                <%--<div class="cool-border"></div></li>--%>
                <!-- 注释内容 -->
                <!--  <li buildId="config-btn"><a><span class="glyphicon glyphicon-cog cool-purple"></span><span class="username">Configure</span><span class="title">配置管理</span></a><div class="cool-border"></div></li>
        -->
                <!-- <li buildId="config-btn"><a><span class="glyphicon glyphicon-th cool-orange"></span><span class="username">Apptype</span><span class="title">应用类型</span></a><div class="cool-border"></div></li>
             -->
                <!--
             <li buildId="tempt-btn"><a><span class="glyphicon glyphicon-road cool-blue"></span><span class="username">Templates</span><span class="title">模板管理</span></a><div class="cool-border"></div></li>
              -->
            </ul>
        </div>
    </div>
    <div class="d-main-content"></div>
</div>
</body>

</html>