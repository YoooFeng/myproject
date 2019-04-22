<%--
  Created by IntelliJ IDEA.
  User: frank
  Date: 19-4-17
  Time: 上午11:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
    <title>修改用户资料</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
    <div class="intro">
        <h1>修改用户资料&nbsp;</h1>
        <p class="lead" style="margin-top:10px">
            修改用户资料, 如邮箱地址和密码.
        </p>
    </div>

    <div class="content-board">
        <table style="margin-bottom: 0px; border-bottom: 0px;"
               class="table table-bordered table-condensed deploy-table">
            <thead>
            <div name="editUser">
                <form:form modelAttribute="editUserCommand">
                    <form:errors path="*" element="div" cssClass="errors" />
                    <div><div class="form-label">用户邮箱: </div><form:input path="email" /></div>
                    <div><div class="form-label">用户密码: </div><form:input path="password" /></div>
                    <div><input type="button"
                                onclick="document.location.href='<c:url value="./project/main.jsp" />'"
                                value="取消修改" />&nbsp;<input type="submit" value="保存修改" /></div>
                </form:form>

            </div>
            </thead>
        </table>
        <div style="margin-top: 0px;" class="panel-group" id="app-list"
             role="tablist" aria-multiselectable="true">

        </div>
    </div>
    <script type="text/javascript">
        document.getElementById('userEmail').focus();
    </script>
</div>
</body>
</html>

