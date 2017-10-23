package ar.diawigocd.plugin.handlers;

import ar.diawigocd.plugin.ApiResponseBuilder;
import ar.diawigocd.plugin.Result;
import ar.diawigocd.plugin.TaskPlugin;
import ar.diawigocd.plugin.config.TaskConfig;
import ar.diawigocd.uploader.DiawiUploader;
import ar.diawigocd.utils.HttpClientSupplier;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.Map;

public class ExecuteUploadTask implements ApiResponseBuilder {
    @Override
    public GoPluginApiResponse execute(GoPluginApiRequest request) {
        JobConsoleLogger console = JobConsoleLogger.getConsoleLogger();
        console.printLine("Starting Diawi Upload Task");
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        Map configMap = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        TaskConfig taskConfig = new TaskConfig(configMap);
        if (!taskConfig.isValid()) {
            console.printLine("Invalid config");
            console.printEnvironment(taskConfig.getErrorMap());
            return errorResponse("Invalid config");
        }

        String workingDirPath = (String) context.get("workingDirectory");
        if (workingDirPath == null) {
            console.printLine("Working Dir is null");
            return errorResponse("Working Dir is null");
        }

        File workingDir = new File(workingDirPath);
        File targetFile = new File(workingDir, taskConfig.getArtifactLocation());

        if (!targetFile.exists() || !targetFile.isFile()) {
            console.printLine(String.format("Target File not found: %s", targetFile.getPath()));
            return errorResponse("Target File not found");
        }


        DiawiUploader diawiUploader = new DiawiUploader(taskConfig.getToken(), TaskPlugin.GSON, HttpClientSupplier::defaultClient);
        diawiUploader.setConsole(console::printLine);

        Result response;
        try {
            String result = diawiUploader.doUpload(targetFile, taskConfig.getUploadConfig());
            console.printLine(String.format("Uploaded to Diawi: %s", result));
            String saveResponseTo = taskConfig.getSaveResponseTo();
            if (saveResponseTo != null) {
                console.printLine(String.format("Saving Diawi response to: %s", saveResponseTo));
                File linkOutputFile = new File(workingDir, saveResponseTo);
                saveResponse(console, linkOutputFile, result);
            }
            response = new Result(true, "All was ok");
            console.printLine("Diawi Upload Task finished successfully");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            console.printLine(String.format("Upload exception: %s\r\n%s", e.getMessage(), sw.toString()));
            response = new Result(false, String.format("Upload exception: %s", e.getMessage()), e);
        }
        return new DefaultGoPluginApiResponse(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE, TaskPlugin.GSON.toJson(response));
    }

    private void saveResponse(JobConsoleLogger console, File linkOutputFile, String text) {
        File linkParentDir = linkOutputFile.getParentFile();
        if (linkParentDir.exists() && linkParentDir.isDirectory()) {
            try (FileWriter fileWriter = new FileWriter(linkOutputFile)) {
                fileWriter.write(text);
                console.printLine("Diawi response saved");
            } catch (IOException e) {
                console.printLine(String.format("Failed to save Diawi answer to file: %s", e));
            }
        } else {
            console.printLine(String.format("Could not save Diawi result to file: %s (parent folder does not exist: $%s)", linkOutputFile.getPath(), linkParentDir.getPath()));
        }
    }


    private GoPluginApiResponse errorResponse(String message) {
        Result result = new Result(false, message);
        return new DefaultGoPluginApiResponse(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE, TaskPlugin.GSON.toJson(result));
    }


}
