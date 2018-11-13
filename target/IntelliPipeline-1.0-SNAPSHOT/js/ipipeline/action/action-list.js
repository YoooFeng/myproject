var actionList = {
    init : function () {
        ajaxGetJsonAuthc(dURIs.actionURI.actionList, null, actionList.genActionListTable, null);
    },

    genActionListTable : function (data) {
        console.log("Actions: " + JSON.stringify(data));
        var actions = data;

        if (!(actions instanceof Array)) {
            return;
        }

        var html = '';

        if(actions.length > 0){
            for(var i in actions){
                var action = actions[i];

                html += '<div class="panel panel-default">'
                        + '<div class="panel-heading" role="tab" id="action-'
                        + action.id
                        + '">'
                        + '<table class="table table-bordered table-condensed deploy-table">'
                        + '<tr><td style="width: 20%;">'
                        + action.stepName
                        + '</td><td>'
                        // 实际上每个项目只生成一行, 后面的数字表示具有的功能按钮
                        // 2 == "编辑动作"
                        + actionList.getOperationBtnHtml(action.id, 2)
                        // 3 == "删除动作"
                        + actionList.getOperationBtnHtml(action.id, 3)
                        + '</td></tr></table></div>'

            }
        }

        $("#action-list").html(html);
    },

    getOperationBtnHtml : function(actionId, flag){
        var html = '';
        var style = "fa-circle-o-notch";
        var text = "actions";
        var func = undefined;
        switch (flag) {
            case 1:
                break;
            case 2:
                text = "编辑动作";//建模
                func = "actionList.modifyAction(" + actionId + ",1)";
                break;
            case 3:
                text = "删除动作";//组件建模
                func = "actionList.deleteAction(" + actionId + ",2)";
                break;
            default:
                break;
        }

        html += '<i class="fa ' + style + ' text-success small"></i> '
            + '<a class="link-btn" href="javascript:'
            + (func == undefined ? 'void(0)' : func) + '">' + text
            + '</a>';

        return html;
    },

    modifyAction : function (actionId) {
        // 弹出一个新的小页面?
    },

    deleteAction : function (actionId) {
        if(confirm("delete action?")){
            ajaxDeleteJsonAuthc(dURIs.actionURI.deleteAction + "/" + actionId, null, actionList.operateActionCallback, defaultErrorFunc, true);
        }
    },

    operateActionCallback : function () {
        loadPage(dURIs.viewsURI.actionListView, null);
        defaultSuccessFunc();
    }
};

$(document).ready(function() {
    actionList.init();
});