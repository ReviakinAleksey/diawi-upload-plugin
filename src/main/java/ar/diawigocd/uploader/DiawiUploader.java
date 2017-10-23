

package ar.diawigocd.uploader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DiawiUploader {

    private static final String DIAWI_API_URL = "https://upload.diawi.com";
    private String token;
    private Gson gson;
    private Consumer<String> console = (any) -> {
    };

    private Supplier<CloseableHttpClient> clientBuilder;

    public DiawiUploader(String token, Gson gson, Supplier<CloseableHttpClient> clientBuilder) {
        this.token = token;
        this.gson = gson;
        this.clientBuilder = clientBuilder;
    }

    public void setConsole(Consumer<String> console) {
        this.console = console;
    }

    public String doUpload(File file, DiawiUploadConfig config) throws IOException {
        console.accept(String.format("Diawi Uploading: %s started", file));
        DiawiUploadResponse diawiUploadResponse = uploadFile(config, file);
        console.accept(String.format("Diawi Uploading done, job ID: %s", file, diawiUploadResponse.getJob()));
        String jobId = diawiUploadResponse.getJob();

        while (true) {
            console.accept("Diawi Querying status");
            DiawiStatusResponse statusResponse = getStatus(jobId);
            console.accept(String.format("Diawi Status response: %s", statusResponse.getMessage()));
            if (statusResponse.isError()) {
                throw new IOException(String.format("Unexpected status response: %s (%d)", statusResponse.getErrorMessage(), statusResponse.getStatus()));
            }
            if (statusResponse.getStatus() == 2000) {
                return statusResponse.getLink();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public DiawiUploadResponse uploadFile(DiawiUploadConfig config, File file) throws IOException {
        return withHttpClient((httpClient) -> {
            HttpPost request = new HttpPost(DIAWI_API_URL);
            MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file", file)
                    .addTextBody("token", token)
                    .addTextBody("find_by_udid", config.isFindByUDID() ? "1" : "0")
                    .addTextBody("wall_of_apps", config.addToWallOfAps() ? "1" : "0")
                    .addTextBody("installation_notifications", config.installationNotifications() ? "1" : "0");

            if (config.getPassword() != null) {
                reqEntityBuilder.addTextBody("password", config.getPassword());
            }
            if (config.getComment() != null) {
                reqEntityBuilder.addTextBody("comment", config.getComment());
            }
            if (config.getCallbackUrl() != null) {
                reqEntityBuilder.addTextBody("callback_url", config.getCallbackUrl());
            }
            if (config.getCallbackEmails() != null) {
                reqEntityBuilder.addTextBody("callback_emails", config.getCallbackEmails());
            }
            request.setEntity(reqEntityBuilder.build());

            DiawiUploadResponse response = jsonRequest(httpClient, request, DiawiUploadResponse.class);
            if (response.getJob() == null) {
                throw new IOException("Unexpected upload response: empty jobId");
            }
            return response;
        });
    }


    private DiawiStatusResponse getStatus(String jodbId) throws IOException {
        return withHttpClient((httpClient) -> {
            HttpUriRequest request = RequestBuilder
                    .get(DIAWI_API_URL + "/status")
                    .addParameter("token", token)
                    .addParameter("job", jodbId)
                    .build();
            return jsonRequest(httpClient, request, DiawiStatusResponse.class);
        });
    }


    @FunctionalInterface
    private interface ClientExecutor<T> {
        T execute(HttpClient client) throws IOException;
    }


    private <T> T withHttpClient(ClientExecutor<T> clientExecutor) throws IOException {
        try (CloseableHttpClient httpclient = clientBuilder.get()) {
            return clientExecutor.execute(httpclient);
        }
    }

    private <T> T jsonRequest(HttpClient httpClient, HttpUriRequest request, Class<T> classOfT) throws IOException {
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        HttpResponse httpResponse = httpClient.execute(request);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException(String.format("Unexpected status code: %d, for request: %s %s", statusCode, request.getMethod(), request.getURI()));
        }

        try {

            HttpEntity entity = httpResponse.getEntity();
            String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consumeQuietly(entity);
            return gson.fromJson(responseBody, classOfT);

        } catch (ParseException | JsonSyntaxException ex) {
            throw new IOException(String.format("Failed to parse response for request: %s %s", request.getMethod(), request.getURI()), ex);
        }

    }
}
