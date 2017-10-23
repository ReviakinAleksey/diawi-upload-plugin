package ar.diawigocd.plugin;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

@FunctionalInterface
public interface ApiResponseBuilder {
    GoPluginApiResponse execute(GoPluginApiRequest request);
}
