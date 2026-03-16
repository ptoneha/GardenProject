package com.ruoyi.agriculture.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.agriculture.config.AgriPythonProperties;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.Threads;

/**
 * Python进程调用器
 */
@Component
public class PythonProcessExecutor
{
    private static final Logger log = LoggerFactory.getLogger(PythonProcessExecutor.class);

    private final AgriPythonProperties agriPythonProperties;

    public PythonProcessExecutor(AgriPythonProperties agriPythonProperties)
    {
        this.agriPythonProperties = agriPythonProperties;
    }

    public String execute(String inputJson) throws Exception
    {
        validateConfig();
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(
            agriPythonProperties.getExecutable(),
            agriPythonProperties.getScriptPath()));
        processBuilder.redirectErrorStream(false);
        Process process = processBuilder.start();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try
        {
            Future<String> stdoutFuture = executorService.submit(new StreamReader(process.getInputStream()));
            Future<String> stderrFuture = executorService.submit(new StreamReader(process.getErrorStream()));

            try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)))
            {
                writer.write(inputJson);
                writer.flush();
            }

            boolean finished = process.waitFor(agriPythonProperties.getTimeoutSeconds(), TimeUnit.SECONDS);
            if (!finished)
            {
                process.destroyForcibly();
                throw new TimeoutException("Python脚本执行超时");
            }

            String stdout = stdoutFuture.get(5, TimeUnit.SECONDS);
            String stderr = stderrFuture.get(5, TimeUnit.SECONDS);
            int exitCode = process.exitValue();
            if (exitCode != 0)
            {
                log.error("Python脚本退出异常，exitCode={}, stderr={}", exitCode, stderr);
                throw new IllegalStateException("Python脚本执行失败：" + stderr);
            }
            if (StringUtils.isEmpty(stdout))
            {
                throw new IllegalStateException("Python脚本未返回结果");
            }
            return stdout;
        }
        finally
        {
            Threads.shutdownAndAwaitTermination(executorService);
        }
    }

    private void validateConfig()
    {
        if (StringUtils.isEmpty(agriPythonProperties.getExecutable()))
        {
            throw new IllegalStateException("未配置 agri.python.executable");
        }
        if (StringUtils.isEmpty(agriPythonProperties.getScriptPath()))
        {
            throw new IllegalStateException("未配置 agri.python.script-path");
        }
    }

    private static class StreamReader implements Callable<String>
    {
        private final InputStream inputStream;

        StreamReader(InputStream inputStream)
        {
            this.inputStream = inputStream;
        }

        @Override
        public String call() throws IOException
        {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (builder.length() > 0)
                    {
                        builder.append(System.lineSeparator());
                    }
                    builder.append(line);
                }
            }
            return builder.toString();
        }
    }
}
