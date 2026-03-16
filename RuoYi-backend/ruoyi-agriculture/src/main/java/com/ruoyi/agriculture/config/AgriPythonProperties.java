package com.ruoyi.agriculture.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Python算法调用配置
 */
@Component
@ConfigurationProperties(prefix = "agri.python")
public class AgriPythonProperties
{
    /**
     * Python可执行文件路径
     */
    private String executable;

    /**
     * Python脚本路径
     */
    private String scriptPath;

    /**
     * 调用超时时间，单位秒
     */
    private Integer timeoutSeconds = 60;

    public String getExecutable()
    {
        return executable;
    }

    public void setExecutable(String executable)
    {
        this.executable = executable;
    }

    public String getScriptPath()
    {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath)
    {
        this.scriptPath = scriptPath;
    }

    public Integer getTimeoutSeconds()
    {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds)
    {
        this.timeoutSeconds = timeoutSeconds;
    }
}
