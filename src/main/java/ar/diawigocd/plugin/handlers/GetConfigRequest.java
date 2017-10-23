

package ar.diawigocd.plugin.handlers;

import ar.diawigocd.plugin.ApiResponseBuilder;
import ar.diawigocd.plugin.TaskPlugin;
import ar.diawigocd.plugin.config.TaskConfig;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;


public class GetConfigRequest implements ApiResponseBuilder {

    @Override
    public GoPluginApiResponse execute(GoPluginApiRequest request) {
        HashMap<String, Object> config = new HashMap<>();

        config.put(TaskConfig.DIAWI_API_TOKEN, createDescriptor(config.size(), "Diawi API TOKEN", true, true));
        config.put(TaskConfig.ARTIFACT_LOCATION, createDescriptor(config.size(), "Artifact location", true, false));

        config.put(TaskConfig.SAVE_RESPONSE_LOCATION, createDescriptor(config.size(), "Save Diawi response to:"));

        config.put(TaskConfig.DIAWI_WALL_OF_APPS, createDescriptor(config.size(), "Allow Diawi to display the app's icon"));
        config.put(TaskConfig.DIAWI_PASSWORD, createDescriptor(config.size(), "Diawi password", false, true));
        config.put(TaskConfig.DIAWI_COMMENT, createDescriptor(config.size(), "Diawi comment"));

        config.put(TaskConfig.DIAWI_CALLBACK_URL, createDescriptor(config.size(), "Diawi Callback URL"));
        config.put(TaskConfig.DIAWI_CALLBACK_EMAILS, createDescriptor(config.size(), "Diawi Callback Emails"));
        config.put(TaskConfig.DIAWI_FIND_BY_UDID, createDescriptor(config.size(), "Diawi Find by UDID"));
        config.put(TaskConfig.DIAWI_INSTALLATION_NOTIFICATIONS, createDescriptor(config.size(), "Receive app installation notifications"));


        return DefaultGoPluginApiResponse.success(TaskPlugin.GSON.toJson(config));
    }

    private HashMap<String, Object> createDescriptor(int index, String description) {
        return createDescriptor(index, description, false, false);
    }

    private HashMap<String, Object> createDescriptor(int index, String description, boolean required, boolean secure) {
        HashMap<String, Object> token = new HashMap<>();
        token.put("display-order", String.valueOf(index));
        token.put("display-name", description);
        token.put("required", required);
//        token.put("secure", secure);
        return token;

    }
}
