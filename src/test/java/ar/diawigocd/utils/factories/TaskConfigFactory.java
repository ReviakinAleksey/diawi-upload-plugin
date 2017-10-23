package ar.diawigocd.utils.factories;

import ar.diawigocd.plugin.config.TaskConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TaskConfigFactory {
    public static Map EMPTY = Collections.emptyMap();
    private static ArrayList<Map> invalidConfigs = new ArrayList<>(Collections.singleton(EMPTY));
    private static ArrayList<Map> validConfigs = new ArrayList<>();

    public static Map TOKEN_ONLY = buildInvalidConfig(EMPTY, TaskConfig.DIAWI_API_TOKEN, buildConfigKey("TEST_TOKEN"));
    public static Map ARTIFACT_ONLY = buildInvalidConfig(EMPTY, TaskConfig.ARTIFACT_LOCATION, buildConfigKey("app/build/outputs/apk/app-debug.apk"));
    public static Map RESPONSE_LOCATION_ONLY = buildInvalidConfig(EMPTY, TaskConfig.SAVE_RESPONSE_LOCATION, buildConfigKey("app/build/outputs/apk/diawi-link.txt"));
    public static Map FILLED_DIAWI_VALUES_ONLY = buildInvalidConfig(EMPTY,
            Collections.singletonMap(TaskConfig.DIAWI_PASSWORD, buildConfigKey("Test Password")),
            Collections.singletonMap(TaskConfig.DIAWI_COMMENT, buildConfigKey("Test Comment")),
            Collections.singletonMap(TaskConfig.DIAWI_CALLBACK_URL, buildConfigKey("URL")),
            Collections.singletonMap(TaskConfig.DIAWI_CALLBACK_EMAILS, buildConfigKey("EMAILS")),
            Collections.singletonMap(TaskConfig.DIAWI_FIND_BY_UDID, buildConfigKey("true")),
            Collections.singletonMap(TaskConfig.DIAWI_WALL_OF_APPS, buildConfigKey("true")),
            Collections.singletonMap(TaskConfig.DIAWI_INSTALLATION_NOTIFICATIONS, buildConfigKey("true"))
    );
    public static Map FALSE_DIAWI_VALUES_ONLY = buildInvalidConfig(EMPTY,
            Collections.singletonMap(TaskConfig.DIAWI_FIND_BY_UDID, buildConfigKey("false")),
            Collections.singletonMap(TaskConfig.DIAWI_WALL_OF_APPS, buildConfigKey("false")),
            Collections.singletonMap(TaskConfig.DIAWI_INSTALLATION_NOTIFICATIONS, buildConfigKey("false"))
    );


    public static Map TOKEN_AND_ARTIFACT = buildValidConfig(TOKEN_ONLY, ARTIFACT_ONLY);
    public static Map TOKEN_ARTIFACT_AND_LOCATION = buildValidConfig(TOKEN_AND_ARTIFACT, RESPONSE_LOCATION_ONLY);
    public static Map TOKEN_ARTIFACT_LOCATION_FILLED_DIAWI = buildValidConfig(TOKEN_ARTIFACT_AND_LOCATION, FILLED_DIAWI_VALUES_ONLY);
    public static Map TOKEN_ARTIFACT_LOCATION_FALSED_DIAWI = buildValidConfig(TOKEN_ARTIFACT_AND_LOCATION, FALSE_DIAWI_VALUES_ONLY);


    static {
        Stream.of(RESPONSE_LOCATION_ONLY, FILLED_DIAWI_VALUES_ONLY, FALSE_DIAWI_VALUES_ONLY).forEach((config) -> {
            buildInvalidConfig(TOKEN_ONLY, config);
            buildInvalidConfig(ARTIFACT_ONLY, config);
        });
    }


    public static Stream<Map> notValidConfigs() {
        return invalidConfigs.stream();
    }


    public static Stream<Map> validConfigs() {
        return validConfigs.stream();
    }

    private static Map buildConfigKey(Object value) {
        return Collections.singletonMap("value", value);
    }


    private static Map buildValidConfig(Map config, Map... otherConfigs) {
        return buildConfig(true, config, otherConfigs);
    }

    private static Map buildInvalidConfig(Map config, String key, Object value) {
        return buildInvalidConfig(config, Collections.singletonMap(key, value));
    }

    private static Map buildInvalidConfig(Map config, Map... otherConfigs) {
        return buildConfig(false, config, otherConfigs);
    }

    private static Map buildConfig(boolean valid, Map config, Map... otherConfigs) {
        Map<Object, Object> holder = new HashMap<Object, Object>(config);
        for (Map otherConfig : otherConfigs) {
            holder.putAll(otherConfig);
        }
        Map result = Collections.unmodifiableMap(holder);
        if (valid) {
            validConfigs.add(result);
        } else {
            invalidConfigs.add(result);
        }
        return result;
    }
}
