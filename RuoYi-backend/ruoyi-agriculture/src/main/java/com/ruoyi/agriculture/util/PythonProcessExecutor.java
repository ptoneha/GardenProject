package com.ruoyi.agriculture.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.agriculture.config.AgriPythonProperties;
import com.ruoyi.agriculture.constant.AgriErrorCode;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.Threads;

/**
 * Python 进程调用器
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
        processBuilder.environment().put("PYTHONIOENCODING", StandardCharsets.UTF_8.name());
        processBuilder.environment().put("PYTHONUTF8", "1");
        processBuilder.redirectErrorStream(false);

        Process process;
        try
        {
            process = processBuilder.start();
        }
        catch (IOException ex)
        {
            log.error("Failed to start python process", ex);
            throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_UNAVAILABLE.getCode(), ex);
        }

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
                throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_TIMEOUT.getCode());
            }

            String stdout = readStream(stdoutFuture, AgriErrorCode.ERR_PYTHON_BAD_RESPONSE.getCode());
            String stderr = readStream(stderrFuture, AgriErrorCode.ERR_PYTHON_EXECUTION_FAILED.getCode());
            int exitCode = process.exitValue();
            if (exitCode != 0)
            {
                log.error("Python process exited abnormally, exitCode={}, stderr={}", exitCode, stderr);
                throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_EXECUTION_FAILED.getCode());
            }
            if (StringUtils.isEmpty(stdout))
            {
                throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_BAD_RESPONSE.getCode());
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
            throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_UNAVAILABLE.getCode());
        }
        if (StringUtils.isEmpty(agriPythonProperties.getScriptPath()))
        {
            throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_UNAVAILABLE.getCode());
        }

        Path scriptPath = Paths.get(agriPythonProperties.getScriptPath());
        if (!Files.exists(scriptPath))
        {
            throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_SCRIPT_MISSING.getCode());
        }
    }

    private String readStream(Future<String> future, String errorCode)
    {
        try
        {
            return future.get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(errorCode, ex);
        }
        catch (ExecutionException ex)
        {
            throw new IllegalStateException(errorCode, ex);
        }
        catch (TimeoutException ex)
        {
            throw new IllegalStateException(errorCode, ex);
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
