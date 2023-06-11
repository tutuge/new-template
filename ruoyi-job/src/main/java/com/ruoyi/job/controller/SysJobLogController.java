package com.ruoyi.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.job.domain.SysJobLog;
import com.ruoyi.job.service.ISysJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度日志操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController {
    @Autowired
    private ISysJobLogService jobLogService;

    /**
     * 查询定时任务调度日志列表
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:list")
    @GetMapping("/list")
    public TableDataInfo<SysJobLog> list(SysJobLog sysJobLog, PageQuery pageQuery) {
        return jobLogService.selectJobLogPageList(sysJobLog, pageQuery);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:export")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobLog sysJobLog) {
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        ExcelUtil.exportExcel(list, "调度日志", SysJobLog.class, response);
    }

    /**
     * 根据调度编号获取详细信息
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:query")
    @GetMapping(value = "/{jobLogId}")
    public R<SysJobLog> getInfo(@PathVariable Long jobLogId) {
        return R.ok(jobLogService.selectJobLogById(jobLogId));
    }


    /**
     * 删除定时任务调度日志
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:remove")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobLogIds}")
    public R<Integer> remove(@PathVariable Long[] jobLogIds) {
        return R.ok(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    /**
     * 清空定时任务调度日志
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:remove")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        jobLogService.cleanJobLog();
        return R.ok();
    }
}
