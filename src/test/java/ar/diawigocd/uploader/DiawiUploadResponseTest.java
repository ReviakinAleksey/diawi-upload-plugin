package ar.diawigocd.uploader;


import ar.diawigocd.utils.JsonResourceConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test parsing of response from Diawi Upload API")
public class DiawiUploadResponseTest {


    @ParameterizedTest
    @ValueSource(strings = {"/ar/diawigocd/uploader/uploaded.json"})
    void shouldHandleUploadedMessage(@ConvertWith(JsonResourceConverter.class) DiawiUploadResponse uploadResponse) {
        assertEquals("identifierOfYourUploadJob", uploadResponse.getJob());
    }
}
