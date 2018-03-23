/**
 * 角色管理的单例
 */
var RpcNode = {
    id: "rpcNodeTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};


/**
 * 初始化表格的列
 */
RpcNode.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true,},
        {title: 'id', field: 'nodeId', visible: false, align: 'center', valign: 'middle', width: '50px'},
        //{title: '服务或节点名称', field: 'nodeName', align: 'center', valign: 'middle', sortable: true, width: '12%'},
        {title: '服务或节点名称', field: 'value', align: 'center', valign: 'middle', sortable: true, width: '17%'}
    ]
    return columns;
};


/**
 * 检查是否选中
 */
RpcNode.check = function () {
    var selected = $('#' + this.id).bootstrapTreeTable('getSelections');
    if (selected.length == 0) {
        Feng.info("请先选中表格中的某一记录！");
        return false;
    } else {
        RpcNode.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加菜单
 */
RpcNode.openAddMenu = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '添加菜单',
            area: ['830px', '450px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/admin/system/node/edit?parentId=' + this.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 点击修改
 */
RpcNode.openChangeMenu = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '修改菜单',
            area: ['800px', '450px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/admin/system/node/edit?id=' + this.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除
 */
RpcNode.delMenu = function () {
    if (this.check()) {

        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/menu/remove", function (data) {
                Feng.success("删除成功!");
                RpcNode.table.refresh();
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("menuId", RpcNode.seItem.id);
            ajax.start();
        };

        Feng.confirm("是否刪除该菜单?", operation);
    }
};

/**
 * 搜索
 */
RpcNode.search = function () {
    var queryData = {};

    queryData['funName'] = $("#funName").val();
    queryData['funLevel'] = $("#funLevel").val();

    RpcNode.table.refresh({query: queryData});
}

$(function () {
    var defaultColunms = RpcNode.initColumn();
    var table = new BSTreeTable(RpcNode.id, "/admin/rpcadmin/servicenode/data", defaultColunms);
    table.setExpandColumn(2);
    table.setIdField("nodeId");
    table.setCodeField("nodeId");
    table.setParentCodeField("parentNodeId");
    table.setExpandAll(false);
    table.init();
    RpcNode.table = table;
});
