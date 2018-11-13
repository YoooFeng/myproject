var loginPage = {

    // 注册事件
    register : function (data) {
        if(data.name == "Admin"){
            alert("用户名存在,请重新输入!");
        }
    },

    // 登录事件
    login : function () {
        if($('#username').val() == "Admin"){
            console.log("Yes");
            $(this).addClass("active");
            // loadPage(dURIs.viewsURI.mainPage, null);

            // var path = "<%=path%>" + "/";
            var url = '/IntelliPipeline';
            window.location.href = encodeURI(url);
        } else {
            console.log("No");
            alert("用户名不存在, 请重新输入")
        }

        // $(this).addClass("active");
        // // loadPage(dURIs.viewsURI.mainPage, null);
        //
        // // var path = "<%=path%>" + "/";
        // var url = '/IntelliPipeline';
        // window.location.href = encodeURI(url);

    }

};