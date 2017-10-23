package ar.diawigocd.uploader;

import ar.diawigocd.utils.HttpRequestParam;
import ar.diawigocd.utils.HttpRequestParamsParser;
import ar.diawigocd.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@DisplayName("Test that internal config values are passed to Diawi correctly")
class DiawiUploaderTest {


    private static final String TEST_JOB_ID = "TEST_JOB_ID";
    private static final Map<String, String> DIAWI_UPLOAD_SUCCESS_RESPONSE = Collections.singletonMap("job", TEST_JOB_ID);
    private static final Map<String, Integer> DIAWI_JOB_SUCCESS_RESPONSE = Collections.singletonMap("status", 2000);
    private static final String DIAWI_TEST_DOKEN = "TEST_TOKEN";
    private static final Consumer<Map<String, HttpRequestParam>> DEFAULT_JOB_CHECKER = params -> {
        assertHasParameter(params, "token", DIAWI_TEST_DOKEN);
        assertHasParameter(params, "job", TEST_JOB_ID);
    };
    private static final String TEST_PARAMETER_VALUE = "TEST_PARAMETER_VALUE";
    private File uploadFile;
    private DiawiUploadConfig diawiUploadConfig;

    @BeforeEach
    void fillUp() throws IOException {
        uploadFile = File.createTempFile("uploader", "txt");
        diawiUploadConfig = new DiawiUploadConfig();
    }


    @Test
    @DisplayName("Should pass token and file parameter")
    void testMandatoryUploadParameter() throws IOException {
        DiawiUploader diawiUploader = new DiawiUploader(DIAWI_TEST_DOKEN, Utils.GSON, testHttpClient(params -> {
            assertHasParameter(params, "token", DIAWI_TEST_DOKEN);
            assertNotNull(params.get("file"));
        }, DEFAULT_JOB_CHECKER));
        diawiUploader.doUpload(uploadFile, diawiUploadConfig);
    }

    @ParameterizedTest
    @MethodSource("booleanParamsProvider")
    @DisplayName("Should pass true boolean params as '1' and with valid names")
    void testBooleanTrueParameters(Function<DiawiUploadConfig, Consumer<Boolean>> configConsumerFunction, String paramName) throws IOException {
        configConsumerFunction.apply(diawiUploadConfig).accept(true);

        DiawiUploader diawiUploader = new DiawiUploader(DIAWI_TEST_DOKEN,
                Utils.GSON,
                testHttpClient(params -> assertHasParameter(params, paramName, "1"), DEFAULT_JOB_CHECKER));
        diawiUploader.doUpload(uploadFile, diawiUploadConfig);
    }

    @ParameterizedTest
    @MethodSource("booleanParamsProvider")
    @DisplayName("Should pass true boolean params as '0' and with valid names")
    void testBooleanFalseParameters(Function<DiawiUploadConfig, Consumer<Boolean>> configConsumerFunction, String paramName) throws IOException {
        configConsumerFunction.apply(diawiUploadConfig).accept(false);

        DiawiUploader diawiUploader = new DiawiUploader(DIAWI_TEST_DOKEN,
                Utils.GSON,
                testHttpClient(params -> assertHasParameter(params, paramName, "0"), DEFAULT_JOB_CHECKER));
        diawiUploader.doUpload(uploadFile, diawiUploadConfig);
    }


    @ParameterizedTest
    @MethodSource("stringParamsProvider")
    @DisplayName("Should pass string params with valid names and values")
    void testStringParametersWithValue(Function<DiawiUploadConfig, Consumer<String>> configConsumerFunction, String paramName) throws IOException {
        configConsumerFunction.apply(diawiUploadConfig).accept(TEST_PARAMETER_VALUE);

        DiawiUploader diawiUploader = new DiawiUploader(DIAWI_TEST_DOKEN, Utils.GSON, testHttpClient(params -> assertHasParameter(params, paramName, TEST_PARAMETER_VALUE), DEFAULT_JOB_CHECKER));
        diawiUploader.doUpload(uploadFile, diawiUploadConfig);
    }

    @ParameterizedTest
    @MethodSource("stringParamsProvider")
    @DisplayName("Should not pass string parameter if not set")
    void testStringParametersWithoutValue(Function<DiawiUploadConfig, Consumer<String>> configConsumerFunction, String paramName) throws IOException {
        DiawiUploader diawiUploader = new DiawiUploader(DIAWI_TEST_DOKEN,
                Utils.GSON,
                testHttpClient(params -> assertNull(params.get(paramName)), DEFAULT_JOB_CHECKER));
        diawiUploader.doUpload(uploadFile, diawiUploadConfig);
    }


    static Stream<Arguments> booleanParamsProvider() {
        return Stream.of(
                createTestArgument(x -> x::setAddToWallOfAps, "wall_of_apps"),
                createTestArgument(x -> x::setFindByUDID, "find_by_udid"),
                createTestArgument(x -> x::setInstallationNotifications, "installation_notifications")
        );
    }

    static Stream<Arguments> stringParamsProvider() {
        return Stream.of(
                createTestArgument(x -> x::setPassword, "password"),
                createTestArgument(x -> x::setComment, "comment"),
                createTestArgument(x -> x::setCallbackUrl, "callback_url"),
                createTestArgument(x -> x::setCallbackEmails, "callback_emails")
        );
    }


    private static <T> Arguments createTestArgument(Function<DiawiUploadConfig, Consumer<T>> m, String paramName) {
        return Arguments.of(m, paramName);
    }


    private static void assertHasParameter(Map<String, HttpRequestParam> params, String key, String value) {
        assertAll(
                () -> assertNotNull(params.get(key)),
                () -> assertEquals(value, params.get(key).getContent())
        );
    }


    private Supplier<CloseableHttpClient> testHttpClient(Consumer<Map<String, HttpRequestParam>> uploadChecker,
                                                         Consumer<Map<String, HttpRequestParam>> jobChecker) throws IOException {
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        when(client.execute(any(HttpUriRequest.class)))
                .then(postVerificator(DIAWI_UPLOAD_SUCCESS_RESPONSE, uploadChecker))
                .then(getVerificator(DIAWI_JOB_SUCCESS_RESPONSE, jobChecker));
        return () -> client;
    }


    private Answer<HttpResponse> httpVerificator(Object diawiJsonResponse, Consumer<HttpRequestBase> requestConsumer) {
        return invocation -> {
            requestConsumer.accept(invocation.getArgument(0));
            CloseableHttpResponse response = mock(CloseableHttpResponse.class);
            when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
            when(response.getEntity()).thenReturn(new StringEntity(Utils.GSON.toJson(diawiJsonResponse)));
            return response;
        };
    }

    private Answer<HttpResponse> postVerificator(Object diawiJsonResponse, Consumer<Map<String, HttpRequestParam>> paramsConsumer) {

        return httpVerificator(diawiJsonResponse, (rawRequest) -> {
            HttpPost request = (HttpPost) rawRequest;
            try {
                Map<String, HttpRequestParam> stringFormPartMap = HttpRequestParamsParser.parsePostEntity(request.getEntity());
                paramsConsumer.accept(stringFormPartMap);
            } catch (IOException e) {
                throw new IllegalArgumentException("HttpRequest could not be parsed", e);
            }
        });
    }

    private Answer<HttpResponse> getVerificator(Object diawiJsonResponse, Consumer<Map<String, HttpRequestParam>> paramsConsumer) {

        return httpVerificator(diawiJsonResponse, (rawRequest) -> {
            try {
                Map<String, HttpRequestParam> paramMap = HttpRequestParamsParser.parseGetToMap(rawRequest.getRequestLine());
                paramsConsumer.accept(paramMap);
            } catch (IOException e) {
                throw new IllegalArgumentException("HttpRequest could not be parsed", e);
            }
        });
    }
}

