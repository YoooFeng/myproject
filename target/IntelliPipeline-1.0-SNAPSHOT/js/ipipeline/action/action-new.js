var createAction = {
    init : function () {
        // document.getElementById("submit-btn").onclick=function(){
        //     console.log("Clicked");
        // };
        createAction.initClickEvent();
    },


    initClickEvent : function () {
        $("#submit-btn").click(function () {

            console.log("Button clicked");

            var name = document.getElementById("actionName").value;

            var actionItem = {
                stepName : JSON.stringify(name),
            };

            var params = [];

            $("#paramWrapper").find('div').each(function() {

                // var optional = $(this > input[id^='optional_']).val();

                var optional = $(this).find("input:checkbox").prop('checked');

                var paramValue = $(this).find("input:text").val();

                // if(optional == 'undefined') optional = false;
                // else optional = true;

                console.log("paramValue: " + paramValue);
                console.log("optional: " + optional);

                var param = {paramsKey : paramValue, optional : optional};

                params.push(param);

            });

            // 把params对象传进去
            actionItem["params"] = params;

            // TODO: 新建一个params对象, 一起跟Action存下来
            console.log("ActionItem: " + JSON.stringify(actionItem));
            ajaxPostJsonAuthcWithJsonContent(dURIs.actionURI.newAction, actionItem,
                createAction.transToActionsPage, null);

            return false;
        });
    },

    // 新建的action, 同时把对应的params也存下来
    transToActionsPage : function(action) {
        loadPage(dURIs.viewsURI.projectListView, null);
    },

    // addParam : function(){
    //     var top1 = 165;
    //     var xhcs = createAction.forEachTDCS();
    //     if(!xhcs.equals("")){
    //         var xhcss = xhcs.split(",");
    //         // 当前最新添加的tr的序号
    //         var lastTr = xhcss[xhcss.length - 1]*1 + 1;
    //         // 动态添加的加号的高度
    //         top1 = top1 + 36 * lastTr - 36;
    //         createAction.createTr(lastTr, top1);
    //     }
    //
    // },
    //
    // forEachTDCS : function(){
    //     var xhcs = "";
    //     // 循环所有的tr行
    //     $("#param-table").find("tr").each(function () {
    //         // 找到tr的id
    //         var tableTr = $(this).attr("id");
    //         if(!tableTr.equals("undefined") && tableTr != null){
    //             if(tableTr.indexOf("param-tr_") >= 0){
    //                 if(xhcs.equals("")){
    //                     // 用下划线分割出tr的索引
    //                     xhcs += tableTr.split("_")[1];
    //                 } else {
    //                     xhcs += "," + tableTr.split("_")[1];
    //                 }
    //             }
    //         }
    //     });
    //     return xhcs;
    // },
    //
    // createTr : function(lastTr, top1){
    //     var strTr = "";
    //     var strImg = "";
    //     strTr+="<tr id='param-tr_"+ lastTr +"'><td class='bt2'>参数" + lastTr + "</td>";
    //     strTr+="<td><input type='text' class='bd-ys1' id='ParamName_"+ lastTr +"' value='' /></td></tr>";
    //     strImg ="<div id='minusImg_" + lastTr
    //             +"' style='position: absolute; top: "+ top1 +"px; right: 210px;'>"
    //             + "<img src=\"<c:url value='/img/minus.png'/>\" "
    //             + "onclick='createAction.minusParam(" + lastTr + "," + top1 + ")'/></div>";
    //     // 告诉tr在N行减1的位置
    //     var lastTrs= lastTr * 1 - 1;
    //     $("#param-tr_"+lastTrs).after(strTr); // 一定要用after才在N行减1之后的位置顺序插入
    //     $("#param-tr_"+lastTrs).after(strImg); // 追加减号图片
    // },
    //
    // minusParam : function(index, top1) {
    //     //得到操作的所有tr
    //     var xhcs = createAction.forEachTDCS();
    //     var xhcss = xhcs.split(",");
    //     $("#minusImg_" + index).remove();// 删除图片的索引
    //     $("#param-tr_" + index).remove();// 删除tr的索引
    //     for (var i = 0; i < xhcss.length; i++) {
    //         // 所需要的索引需大于要删除的索引
    //         if (xhcss[i] > index) {
    //             //获取图片div的高
    //             var jtop = $("#minusImg_" + xhcss[i]).css('top');
    //             // 获取的高-所需要的数值
    //             var jtopS = jtop.split("px")[0] * 1 - 36 * 1;
    //             // div的高经计算后设置到div的高中  也就是从新赋值
    //             $("#minusImg_" + xhcss[i]).css({"top": jtopS});
    //             var imgId = "minusImg_" + (xhcss[i] * 1 - 1);
    //             var trId = "param-tr_" + (xhcss[i] * 1 - 1);
    //             var InventorNameid = "InventorName_" + (xhcss[i] * 1 - 1);
    //             // 改变tr索引 和 图片div索引 让其排序
    //             $("#minusImg_" + xhcss[i]).attr("id", imgId);//新的排序id赋值到div中
    //             $("#InventorName_" + xhcss[i]).attr("id", InventorNameid);//姓名--将input的id排序  赋值到input的id中
    //             var str = "<img style='abvn' src=\"<c:url value='/img/minus.png'/>\" onclick='createAction.minusParam(" + (xhcss[i] * 1 - 1) + "," + jtopS + ")'/>";
    //             $("#" + imgId).html(str);//点击onclick方法里的索引才能改变
    //             $("#param-tr_" + xhcss[i]).attr("id", trId);//新的排序id赋值到tr中
    //             $("#" + trId).find("td").eq(0).text("参数" + (xhcss[i] * 1 - 1));// 改变显示名称顺序
    //         }
    //     }
    // },
};

$(document).ready(function() {

    // 初始化不要忘记
    createAction.init();

    var MaxInputs = 8;
    var InputsWrapper = $("#paramWrapper");
    var AddButton = $("#addImg");

    var x = InputsWrapper.length;
    var FieldCount = 1;

    $(AddButton).click(function (e) {
        if(x <= MaxInputs){
            FieldCount++;
            $(InputsWrapper).append('<div><input type="text" name="param[]" id="param_'
                + FieldCount +'" placeholder="Param '
                + FieldCount +'"/>' + '<input id="optional_"' + FieldCount + ' type="checkbox">optional</input>'
                + '<a href="#" class="removeParam"> X </a></div>');
            x++;
        }
        return false;
    });

    $("body").on("click",".removeParam", function(e){ //user click on remove text
        if( x > 1 ) {
            $(this).parent('div').remove(); //remove text box
            x--; //decrement textbox
        }
        return false;
    })
});