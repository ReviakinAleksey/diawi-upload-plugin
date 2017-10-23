package ar.diawigocd.plugin.handlers;

import ar.diawigocd.plugin.ApiResponseBuilder;
import ar.diawigocd.plugin.TaskPlugin;
import ar.diawigocd.plugin.config.TaskConfig;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest implements ApiResponseBuilder {

    public GoPluginApiResponse execute(GoPluginApiRequest request) {

        HashMap<String, Object> validationResult = new HashMap<>();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;

        Map configMap = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

        TaskConfig taskConfig = new TaskConfig(configMap);
        validationResult.put("errors", taskConfig.getErrorMap());

        return new DefaultGoPluginApiResponse(responseCode, TaskPlugin.GSON.toJson(validationResult));
    }
}
