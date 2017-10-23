package ar.diawigocd.plugin;

import ar.diawigocd.plugin.handlers.ExecuteUploadTask;
import ar.diawigocd.plugin.handlers.GetConfigRequest;
import ar.diawigocd.plugin.handlers.GetViewRequest;
import ar.diawigocd.plugin.handlers.ValidateRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collections;

@Extension
public class TaskPlugin implements GoPlugin {

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static Logger LOGGER = Logger.getLoggerFor(TaskPlugin.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {


        String requestName = request.requestName();
        LOGGER.debug(String.format("Handling request: %s", requestName));
        LOGGER.debug(request.requestBody());

        ApiResponseBuilder builder;
        switch (requestName) {
            case "configuration":
                builder = new GetConfigRequest();
                break;
            case "validate":
                builder = new ValidateRequest();
                break;
            case "execute":
                builder = new ExecuteUploadTask();
                break;
            case "view":
                builder = new GetViewRequest();
                break;
            default:
                throw new UnhandledRequestTypeException(requestName);
        }

        GoPluginApiResponse response = builder.execute(request);
        LOGGER.debug(String.format("Respond with code: %s", String.valueOf(response.responseCode())));
        LOGGER.debug(response.responseBody());
        return response;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Collections.singletonList("1.0"));
    }
}
