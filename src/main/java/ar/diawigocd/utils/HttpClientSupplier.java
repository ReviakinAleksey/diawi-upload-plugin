package ar.diawigocd.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class HttpClientSupplier {

    public static CloseableHttpClient defaultClient() {
        HttpClientBuilder builder = HttpClients.custom();
        builder.setDefaultRequestConfig(
                RequestConfig
                        .custom()
                        .setSocketTimeout(30 * 1000)
                        .setConnectTimeout(30 * 100)
                        .build()
        );
        return builder.build();
    }
}
