package com.ziroom.bsrd.rpc.web;

import com.ziroom.bsrd.action.CommonAction;
import com.ziroom.bsrd.basic.vo.Page;
import com.ziroom.bsrd.basic.vo.PageInfoBT;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 控制台
 *
 * @author chengys4
 *         2018-03-07 17:19
 **/
public class ConsoleAction extends CommonAction {

    @RequestMapping("/admin/task/asynctask/index")
    public String index(Model model) {

        //查询注册中心节点
        model.addAttribute("dataList", null);

        return "/admin/task/asynctask/index";
    }

    @RequestMapping("/admin/task/asynctask/data")
    @ResponseBody
    public PageInfoBT data() {
        Page<Object> page = null;
        return new PageInfoBT(page.getList(), page.getTotal());
    }
}
