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
    <title>新建项目</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
    <div class="intro">
        <h1>新建项目&nbsp;</h1>
        <p class="lead" style="margin-top:10px">
            新建您的持续集成项目, 输入项目名称和项目代码所在的仓库地址.
        </p>
    </div>

    <div class="content-board">
        <table style="margin-bottom: 0px; border-bottom: 0px;"
               class="table table-bordered table-condensed deploy-table">
            <thead>
                <div name="newProject">
                    项目名: <input type="text" name="projectName" id="projectName"
                                onmouseover="this.style.borderColor='black';this.style.backgroundColor='azure'"
                                onmouseout="this.style.borderColor='black';this.style.backgroundColor='#ffffff'"
                                style="border-width: 1px;border-color: black"/>
                    仓库地址: <input type="text" name="GitURL" id="GitURL"
                                 onmouseover="this.style.borderColor='black';this.style.backgroundColor='azure'"
                                 onmouseout="this.style.borderColor='black';this.style.backgroundColor='#ffffff'"
                                 style="border-width: 1px;border-color: black"/>
                </div>
                <li><button type="button" id="submit-btn">
                    创建项目
                </button></li>
            </thead>
            <tbody>
            </tbody>
        </table>
        <div style="margin-top: 0px;" class="panel-group" id="app-list"
             role="tablist" aria-multiselectable="true">

        </div>
    </div>
    <script src="<c:url value='/js/ipipeline/app/project-new.js' />"></script>
</div>
</body>
</html>
