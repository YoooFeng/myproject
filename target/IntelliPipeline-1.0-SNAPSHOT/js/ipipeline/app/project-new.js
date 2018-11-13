var createProject = {
    init : function () {
        // document.getElementById("submit-btn").onclick=function(){
        //     console.log("Clicked");
        // };
        createProject.initClickEvent();
    },

    // 将表单里的数据作为Project.Item的参数传过去新建Project.
    // 是否需要提供所有Project.Item中的参数如Id, createTime等等?
    initClickEvent : function () {
        $("#submit-btn").click(function () {

            console.log("Button clicked");

            var name = document.getElementById("projectName");
            console.log("projectName: " + JSON.stringify(name.value));

            var gitURL = document.getElementById("GitURL");
            console.log("gitURL: " + JSON.stringify(gitURL.value));

            var projectItem = {
              name : JSON.stringify(name.value),
              gitURL : JSON.stringify(gitURL.value)
            };

            ajaxPostJsonAuthcWithJsonContent(dURIs.projectDataURI.newProject, projectItem,
                createProject.transToProjectsPage, null);
        });
    },

    // 需要错误处理. 暂时直接返回到项目列表
    transToProjectsPage : function(project) {
        if(project == null) {
            alert("创建项目失败, 项目名已存在, 请重新输入");
            loadPage(dURIs.viewsURI.createProject, null);
        } else {
            loadPage(dURIs.viewsURI.projectListView, null);
        }

    },
};

// 注意需要初始化事件
$(document).ready(function() {
    createProject.init();
});