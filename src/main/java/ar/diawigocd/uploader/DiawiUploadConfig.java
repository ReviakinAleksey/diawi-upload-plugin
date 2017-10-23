package ar.diawigocd.uploader;

public class DiawiUploadConfig {
    private boolean findByUDID;
    private boolean addToWallOfAps;
    private boolean installationNotifications;
    private String password;
    private String comment;
    private String callbackUrl;
    private String callbackEmails;

    public boolean isFindByUDID() {
        return findByUDID;
    }

    public void setFindByUDID(boolean findByUDID) {
        this.findByUDID = findByUDID;
    }

    public boolean addToWallOfAps() {
        return addToWallOfAps;
    }

    public void setAddToWallOfAps(boolean addToWallOfAps) {
        this.addToWallOfAps = addToWallOfAps;
    }

    public boolean installationNotifications() {
        return installationNotifications;
    }

    public void setInstallationNotifications(boolean installationNotifications) {
        this.installationNotifications = installationNotifications;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackEmails() {
        return callbackEmails;
    }

    public void setCallbackEmails(String callbackEmails) {
        this.callbackEmails = callbackEmails;
    }
}
