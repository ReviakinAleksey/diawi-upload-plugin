package ar.diawigocd.uploader;

import ar.diawigocd.utils.JsonResourceConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Test parsing of different response types from Diawi Status API")
class DiawiStatusResponseTest {


    @ParameterizedTest
    @ValueSource(strings = {"/ar/diawigocd/uploader/processing.json"})
    void shouldHandleProcessingMessage(@ConvertWith(JsonResourceConverter.class) DiawiStatusResponse processingResponse) {
        assertEquals(2001, processingResponse.getStatus());
        assertEquals("Processing, please try again in a few seconds...", processingResponse.getMessage());
        assertEquals(false, processingResponse.isError());
        assertNull(processingResponse.getHash());
        assertNull(processingResponse.getLink());
    }


    @ParameterizedTest
    @ValueSource(strings = {"/ar/diawigocd/uploader/ready.json"})
    void shouldHandleReadyMessage(@ConvertWith(JsonResourceConverter.class) DiawiStatusResponse processingResponse) {
        assertEquals(2000, processingResponse.getStatus());
        assertEquals(false, processingResponse.isError());
        assertEquals("Ok", processingResponse.getMessage());
        assertEquals("gVy9vn", processingResponse.getHash());
        assertEquals("https://install.diawi.com/gVy9vn", processingResponse.getLink());
    }


    @ParameterizedTest
    @ValueSource(strings = {"/ar/diawigocd/uploader/error.json"})
    void shouldHandleErrorMessage(@ConvertWith(JsonResourceConverter.class) DiawiStatusResponse processingResponse) {
        assertEquals(4000, processingResponse.getStatus());
        assertEquals(true, processingResponse.isError());
        assertEquals("4001502: Invalid .apk file: content couldn't be read, doesn't seem to be a valid android app file", processingResponse.getMessage());
        assertEquals("4001502: Invalid .apk file: content couldn't be read, doesn't seem to be a valid android app file", processingResponse.getErrorMessage());
        assertNull(processingResponse.getHash());
        assertNull(processingResponse.getLink());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ar/diawigocd/uploader/empty.json"})
    void shouldHandleEmptyMessage(@ConvertWith(JsonResourceConverter.class) DiawiStatusResponse processingResponse) {
        assertEquals(true, processingResponse.isError());
        assertNull(processingResponse.getMessage());
        assertEquals("Unexpected error", processingResponse.getErrorMessage());
        assertNull(processingResponse.getHash());
        assertNull(processingResponse.getLink());
    }
}