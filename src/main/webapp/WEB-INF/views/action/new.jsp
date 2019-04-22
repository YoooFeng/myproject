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
    <title>添加动作</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta username="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div id="d-main-content">
    <div class="intro">
        <h1>添加动作&nbsp;</h1>
        <p class="lead" style="margin-top:10px">
            动作是构成持续集成基本单位, 您需要通过支持的动作来生成Step.
        </p>
    </div>

    <div class="content-board">
        <table style="margin-bottom: 0px; border-bottom: 0px;"
               class="table table-bordered table-condensed deploy-table" id="param-table">
            <thead>
            <div username="newAction">
                动作名称: <input type="text" username="actionName" id="actionName"
                            onmouseover="this.style.borderColor='black';this.style.backgroundColor='azure'"
                            onmouseout="this.style.borderColor='black';this.style.backgroundColor='#ffffff'"
                            style="border-width: 1px;border-color: black"/>
            </div>
            <%--&lt;%&ndash;TODO: 动态实现行数的增减&ndash;%&gt;--%>
            <%--<tr id="param-tr_1">--%>
                <%--<td class="bt2" width="4%" align="center" valign="middle">参数1</td>--%>
                <%--<td width="10%"><input type="text" id="ParamName_1" class="bd-ys1" /></td>--%>
            <%--</tr>--%>
            <div id="paramWrapper">
                <div>
                    <input type="text" username="param[]" id="param_1" placeholder="Param 1" />
                    <input id="optional_1" type="checkbox">optional</input>
                    <a href="#" class="removeParam"> X </a>
                </div>
            </div>

            </thead>
            <tbody>
            <button type="button" id="submit-btn">
                创建动作
            </button>
            </tbody>
        </table>
        <div style="position: absolute; top: 122px; left: 300px;" id="addImg">
            <img src="<c:url value='../../../img/add.png' />" />
        </div>



        <div style="margin-top: 0px;" class="panel-group" id="app-list"
             role="tablist" aria-multiselectable="true">

        </div>
    </div>
    <script src="<c:url value='/js/ipipeline/action/action-new.js' />"></script>
</div>
</body>
</html>
