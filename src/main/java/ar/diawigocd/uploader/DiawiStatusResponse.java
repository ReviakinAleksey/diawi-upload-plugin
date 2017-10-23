
package ar.diawigocd.uploader;

public class DiawiStatusResponse {
    private int status;
    private String message;
    private String hash;
    private String link;


    public int getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }


    public String getHash() {
        return hash;
    }


    public String getLink() {
        return link;
    }


    public boolean isError() {
        return status != 2000 && status != 2001;
    }

    public String getErrorMessage() {
        if (message != null) {
            return message;
        } else {
            return "Unexpected error";
        }
    }
}
