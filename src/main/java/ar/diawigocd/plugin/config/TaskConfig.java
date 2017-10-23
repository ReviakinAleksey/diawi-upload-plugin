package ar.diawigocd.plugin.config;

import ar.diawigocd.uploader.DiawiUploadConfig;

import java.util.HashMap;
import java.util.Map;

public class TaskConfig {

    public static final String DIAWI_API_TOKEN = "TOKEN";
    public static final String ARTIFACT_LOCATION = "LOCATION";

    public static final String SAVE_RESPONSE_LOCATION = "RESPONSE";

    public static final String DIAWI_PASSWORD = "PASSWORD";
    public static final String DIAWI_COMMENT = "COMMENT";

    public static final String DIAWI_CALLBACK_URL = "CALLBACK_URL";
    public static final String DIAWI_CALLBACK_EMAILS = "CALLBACK_EMAILS";
    public static final String DIAWI_FIND_BY_UDID = "FIND_BY_UDID";
    public static final String DIAWI_WALL_OF_APPS = "WALL_OF_APPS";
    public static final String DIAWI_INSTALLATION_NOTIFICATIONS = "INSTALLATION_NOTIFICATIONS";

    private Map<String, String> errorMap = new HashMap<>();
    private String token;
    private String artifactLocation;
    private String saveResponseTo;
    private DiawiUploadConfig uploadConfig;


    public TaskConfig(Map configMap) {
        token = getConfigValue(configMap, DIAWI_API_TOKEN);
        if (token == null) {
            errorMap.put(TaskConfig.DIAWI_API_TOKEN, "Diawi API TOKEN cannot be empty");
        }

        artifactLocation = getConfigValue(configMap, ARTIFACT_LOCATION);
        if (artifactLocation == null) {
            errorMap.put(TaskConfig.ARTIFACT_LOCATION, "Artifact location must be specified");
        }


        saveResponseTo = getConfigValue(configMap, SAVE_RESPONSE_LOCATION);

        uploadConfig = new DiawiUploadConfig();

        /*DIAWI_PASSWORD*/
        String diawiPassword = getConfigValue(configMap, DIAWI_PASSWORD);
        if (diawiPassword != null) {
            uploadConfig.setPassword(diawiPassword);
        }

        /*DIAWI_COMMENT*/
        String diawiComment = getConfigValue(configMap, DIAWI_COMMENT);
        if (diawiComment != null) {
            uploadConfig.setComment(diawiComment);
        }

        /*DIAWI_CALLBACK_EMAILS*/
        String diawiCallbackEmails = getConfigValue(configMap, DIAWI_CALLBACK_EMAILS);
        if (diawiCallbackEmails != null) {
            uploadConfig.setCallbackEmails(diawiCallbackEmails);
        }

        /*DIAWI_CALLBACK_URL*/
        String diawiCallbackUrl = getConfigValue(configMap, DIAWI_CALLBACK_URL);
        if (diawiCallbackUrl != null) {
            uploadConfig.setCallbackUrl(diawiCallbackUrl);
        }

        /*DIAWI_FIND_BY_UDID*/
        boolean findByUDID = getConfigBooleanValue(configMap, DIAWI_FIND_BY_UDID);
        uploadConfig.setFindByUDID(findByUDID);

        /*DIAWI_WALL_OF_APPS*/
        boolean wallOfAps = getConfigBooleanValue(configMap, DIAWI_WALL_OF_APPS);
        uploadConfig.setAddToWallOfAps(wallOfAps);


        /*DIAWI_INSTALLATION_NOTIFICATIONS*/
        boolean notifications = getConfigBooleanValue(configMap, DIAWI_INSTALLATION_NOTIFICATIONS);
        uploadConfig.setInstallationNotifications(notifications);


        if (!errorMap.isEmpty()) {
            token = null;
            artifactLocation = null;
            uploadConfig = null;
            saveResponseTo = null;
        }
    }


    public boolean isValid() {
        return errorMap.isEmpty();
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }

    public String getToken() {
        return token;
    }

    public String getArtifactLocation() {
        return artifactLocation;
    }

    public DiawiUploadConfig getUploadConfig() {
        return uploadConfig;
    }

    public String getSaveResponseTo() {
        return saveResponseTo;
    }

    private String getConfigValue(Map configMap, String key) {
        if (!configMap.containsKey(key)) {
            return null;
        }
        Object holder = configMap.get(key);
        if (!(holder instanceof Map)) {
            return null;
        }
        Map holderMap = (Map) (holder);
        Object value = holderMap.get("value");
        if (value == null || !(value instanceof String)) {
            return null;
        }
        String result = ((String) value).trim();
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    private boolean getConfigBooleanValue(Map configMap, String key) {
        String stringValue = getConfigValue(configMap, key);
        return "true".equalsIgnoreCase(stringValue);
    }

}
