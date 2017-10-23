package ar.diawigocd.plugin.handlers;

import ar.diawigocd.plugin.ApiResponseBuilder;
import ar.diawigocd.plugin.TaskPlugin;
import ar.diawigocd.plugin.Util;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;

public class GetViewRequest implements ApiResponseBuilder {
    public GoPluginApiResponse execute(GoPluginApiRequest reques) {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        HashMap<String, String> view = new HashMap<>();
        view.put("displayValue", "Diawi Uploader");
        try {
            view.put("template", Util.readResource("/task.template.html"));
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            TaskPlugin.LOGGER.error(errorMessage, e);
        }
        return new DefaultGoPluginApiResponse(responseCode, TaskPlugin.GSON.toJson(view));
    }
}
