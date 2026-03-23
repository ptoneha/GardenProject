package com.ruoyi.agriculture.domain.dto;

/**
 * 执行优化任务返回对象
 */
public class AgriTaskExecuteResult
{
    private Long taskId;

    private Integer status;

    private Boolean success;

    private String message;

    private String infeasibleReason;

    private String infeasibleReasonDesc;

    private Integer resultCount;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getInfeasibleReason()
    {
        return infeasibleReason;
    }

    public void setInfeasibleReason(String infeasibleReason)
    {
        this.infeasibleReason = infeasibleReason;
    }

    public String getInfeasibleReasonDesc()
    {
        return infeasibleReasonDesc;
    }

    public void setInfeasibleReasonDesc(String infeasibleReasonDesc)
    {
        this.infeasibleReasonDesc = infeasibleReasonDesc;
    }

    public Integer getResultCount()
    {
        return resultCount;
    }

    public void setResultCount(Integer resultCount)
    {
        this.resultCount = resultCount;
    }
}
