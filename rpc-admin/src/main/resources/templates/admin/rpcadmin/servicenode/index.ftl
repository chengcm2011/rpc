<!DOCTYPE html>
<html>
<head>
<#include "../../../common/static.ftl"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">

    <div class="row">
        <div class="col-sm-12">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>服务节点</h5>
                </div>
                <div class="ibox-content">
                    <div class="row row-lg">
                        <div class="col-sm-12">
                            <div class="row">

                            </div>
                            <div class="hidden-xs" id="menuTableToolbar" role="group">
                            <#--<button type="button" class="btn btn-primary" onclick="RpcNode.openAddMenu()" id="">-->
                                    <#--<i class="fa fa-plus"></i>&nbsp;添加下级菜单-->
                                <#--</button>-->

                                <#--<button type="button" class="btn btn-primary button-margin"-->
                                        <#--onclick="RpcNode.openChangeMenu()" id="">-->
                                    <#--<i class="fa fa-edit"></i>&nbsp;修改-->
                                <#--</button>-->
                                <#--<button type="button" class="btn btn-primary button-margin" onclick="RpcNode.delMenu()"-->
                                        <#--id="">-->
                                    <#--<i class="fa fa-remove"></i>&nbsp;删除-->
                                <#--</button>-->

                            </div>
                            <table id="rpcNodeTable" data-mobile-responsive="true" data-click-to-select="true">
                                <thead>
                                <tr>
                                    <th data-field="selectItem" data-checkbox="true"></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="/modules/rpcadmin/index.js"></script>


</div>
<script src="/js/content.js?v=1.0.0"></script>
</body>
</html>