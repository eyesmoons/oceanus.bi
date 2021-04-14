package com.oceanus.bi.controller;


import com.oceanus.bi.domain.Project;
import com.oceanus.bi.service.BIProjectService;
import com.oceanus.common.utils.SecurityUtils;
import com.oceanus.framework.web.controller.BaseController;
import com.oceanus.framework.web.domain.AjaxResult;
import com.oceanus.framework.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/project")
public class BIProjectController extends BaseController {

    @Autowired
    private BIProjectService biProjectService;


    @PostMapping("/create")
    public AjaxResult add(@RequestBody Project project){
        int rows = this.biProjectService.saveProject(project);
        Map<String,String> id = new HashMap<>();
        id.put("id",project.getProject_id());
        AjaxResult ajax = AjaxResult.success();
        ajax.put(AjaxResult.DATA_TAG, id);
        return ajax;
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody Project project){
        Project pj = this.biProjectService.getBiProjectById(project.getProject_id());
        long currentUserId = SecurityUtils.getLoginUser().getUser().getUserId();
        if(currentUserId == pj.getCreator_id()) {
            int rows = this.biProjectService.updateBiproject(project);
            Map<String, String> id = new HashMap<>();
            id.put("id", project.getProject_id());
            AjaxResult ajax = AjaxResult.success();
            ajax.put(AjaxResult.DATA_TAG, id);
            return ajax;
        } else {
            AjaxResult ajax = AjaxResult.error(403,"不是该项目的创建者,不能修改");
            return ajax;
        }
    }

    /**
     * 根据项目编号获取项目信息
     * @param project_id
     * @return
     */
    @GetMapping("/query/{project_id}")
    public AjaxResult query(@PathVariable(value = "project_id", required = true) String project_id) {
        AjaxResult ajax = AjaxResult.success();
        Project project = this.biProjectService.getBiProjectById(project_id);
        if(project.getPrivate_status() ==1){
            project.setIs_public(false);
        } else {
            project.setIs_public(true);
        }
        ajax.put(AjaxResult.DATA_TAG, project);
        return ajax;
    }

    /**
     *
     * @return
     */
    @DeleteMapping("/delete/{project_id}")
    public AjaxResult delete(@PathVariable(value = "project_id", required = true) String project_id) {
        Project pj = this.biProjectService.getBiProjectById(project_id);
        long currentUserId = SecurityUtils.getLoginUser().getUser().getUserId();
        if(currentUserId == pj.getCreator_id()) {
            int rows = this.biProjectService.deleteBiProjectById(project_id);
            return toAjax(rows);
        } else {
            AjaxResult ajax = AjaxResult.error(403,"不是该项目的创建者,不能删除");
            return ajax;
        }
    }
        @GetMapping("/list")
    public TableDataInfo list(){
        List<Project> list = this.biProjectService.getAllProjectByCreateor();
        return getDataTable(list) ;
    }
}
