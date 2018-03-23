package com.ziroom.bsrd.rpc.web;

import com.ziroom.bsrd.action.CommonAction;
import com.ziroom.bsrd.basic.vo.TreeNodeVO;
import com.ziroom.bsrd.rpc.registry.manage.ServiceNodeManage;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 控制台
 *
 * @author chengys4
 *         2018-03-07 17:19
 **/
@Controller
public class ConsoleAction extends CommonAction {

    @RequestMapping("/admin/rpcadmin/servicenode/index")
    public String index(Model model) {

//        Map<String, List<NodeVO>> map =  ServiceNodeManage.getServiceNodes();
//        查询注册中心节点
//        model.addAttribute("dataList", map);
        return "/admin/rpcadmin/servicenode/index";
    }

    @RequestMapping("/admin/rpcadmin/servicenode/data")
    @ResponseBody
    public Object data() {

        Map<String, List<NodeVO>> map = ServiceNodeManage.getServiceNodes();

        List<TreeNodeVO> nodeVOs = new ArrayList<>();
        int id = 10000;
        for (Map.Entry<String, List<NodeVO>> entry : map.entrySet()) {
            TreeNodeVO treeNodeVO = new TreeNodeVO();
            treeNodeVO.setNodeId(id);
            treeNodeVO.setNodeName(entry.getKey());
            treeNodeVO.setIsSeaf("N");
            treeNodeVO.setValue(entry.getKey());
            initChildren(nodeVOs, treeNodeVO, entry.getValue());
            nodeVOs.add(treeNodeVO);
            id++;
        }
        return nodeVOs;
    }

    private List<TreeNodeVO> initChildren(List<TreeNodeVO> nodeVOs, TreeNodeVO treeNodeVO, List<NodeVO> value) {
        int pi = 50000;
        for (NodeVO nodeVO : value) {
            TreeNodeVO childtreeNodeVO = new TreeNodeVO();
            childtreeNodeVO.setNodeId(pi);
            childtreeNodeVO.setParentNodeId(treeNodeVO.getNodeId());
            childtreeNodeVO.setNodeName(nodeVO.getNodeStr());
            childtreeNodeVO.setIsSeaf("Y");
            childtreeNodeVO.setValue(nodeVO.getNodeStr());
            nodeVOs.add(childtreeNodeVO);
            pi++;
        }
        return nodeVOs;
    }
}
