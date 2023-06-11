package com.ruoyi.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.job.domain.SysJob;
import com.ruoyi.job.service.ISysJobService;
import com.ruoyi.job.util.CronUtils;
import com.ruoyi.job.util.ScheduleUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度任务信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {
    @Autowired
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:list")
    @GetMapping("/list")
    public TableDataInfo<SysJob> list(SysJob sysJob, PageQuery pageQuery) {
        return jobService.selectJobPageList(sysJob, pageQuery);
    }

    /**
     * 导出定时任务列表
     */
    @SaCheckPermission("monitor:job:export")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil.exportExcel(list, "定时任务", SysJob.class, response);

    }

    /**
     * 获取定时任务详细信息
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:query")
    @GetMapping(value = "/{jobId}")
    public R<SysJob> getInfo(@PathVariable("jobId") Long jobId) {
        return R.ok(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:add")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Object> add(@RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return R.fail("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setCreateBy(getUsername());
        return R.ok(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:edit")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Object> edit(@RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return R.fail("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setUpdateBy(getUsername());
        return R.ok(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Integer> changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return R.ok(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public R<Object> run(@RequestBody SysJob job) throws SchedulerException {
        boolean result = jobService.run(job);
        return result ? R.ok() : R.fail("任务不存在或已过期！");
    }

    /**
     * 删除定时任务
     */
    @SaCheckPermission("ruoyi-quartzmonitor:job:remove")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public R<Void> remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException {
        jobService.deleteJobByIds(jobIds);
        return R.ok();
    }
}
