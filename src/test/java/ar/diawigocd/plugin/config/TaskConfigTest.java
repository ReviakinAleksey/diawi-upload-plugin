package ar.diawigocd.plugin.config;

import ar.diawigocd.uploader.DiawiUploadConfig;
import ar.diawigocd.utils.factories.TaskConfigFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test parsing and validation of plugin config stored in GOCD")
class TaskConfigTest {

    @Nested
    @DisplayName("when provided data incorrect")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ProvidedDataIncorrect {

        @ParameterizedTest
        @MethodSource("notValidConfigs")
        @DisplayName("Should not be valid")
        void isValidFalseForInvalidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertFalse(taskConfig.isValid());
        }

        @ParameterizedTest
        @MethodSource("notValidConfigs")
        @DisplayName("Should have error explanation data")
        void getErrorMapNotEmptyForInvalidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertFalse(taskConfig.getErrorMap().isEmpty());
        }

        @ParameterizedTest
        @MethodSource("notValidConfigs")
        @DisplayName("Should not return Diawi token")
        void getTokenNullForInvalidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertNull(taskConfig.getToken());
        }

        @ParameterizedTest
        @MethodSource("notValidConfigs")
        @DisplayName("Should not return Artifact location")
        void getArtifactLocationNullForInvalidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertNull(taskConfig.getArtifactLocation());
        }

        @ParameterizedTest
        @MethodSource("notValidConfigs")
        @DisplayName("Should not return Diawi upload config instance")
        void getUploadConfigNullForInvalidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertNull(taskConfig.getUploadConfig());
        }

        @ParameterizedTest
        @MethodSource("notValidConfigs")
        @DisplayName("Should not return location where to store Diawi link")
        void getSaveResponseToNullForInvalidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertNull(taskConfig.getSaveResponseTo());
        }

        Stream<Map> notValidConfigs() {
            return TaskConfigFactory.notValidConfigs();
        }
    }


    @Nested
    @DisplayName("when provided data correct")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ProvidedDataCorrect {


        @ParameterizedTest
        @MethodSource("validConfigs")
        @DisplayName("Should be valid")
        void isValidTrueForValidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertTrue(taskConfig.isValid());
        }


        @ParameterizedTest
        @MethodSource("validConfigs")
        @DisplayName("Should not produce error explanation data")
        void getErrorMapEmptyForValidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertTrue(taskConfig.getErrorMap().isEmpty());
        }


        @ParameterizedTest
        @MethodSource("validConfigs")
        @DisplayName("Should return correct Diawi API token")
        void getTokenCorrectForValidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertEquals("TEST_TOKEN", taskConfig.getToken());
        }


        @ParameterizedTest
        @MethodSource("validConfigs")
        @DisplayName("Should return correct Artifact Location")
        void getArtifactCorrectForValidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertEquals("app/build/outputs/apk/app-debug.apk", taskConfig.getArtifactLocation());
        }


        @ParameterizedTest
        @MethodSource("validConfigs")
        @DisplayName("Should return not null Diawi upload config instance")
        void getUploadConfigNotNullForValidConfigs(Map config) {
            TaskConfig taskConfig = new TaskConfig(config);
            assertNotNull(taskConfig.getUploadConfig());
        }

        @Test
        @DisplayName("Should return correct location where to store Diawi link")
        void getSaveResponseToCorrect() {
            TaskConfig taskConfig = new TaskConfig(TaskConfigFactory.TOKEN_ARTIFACT_AND_LOCATION);
            assertEquals("app/build/outputs/apk/diawi-link.txt", taskConfig.getSaveResponseTo());
        }

        @Test
        @DisplayName("If Diawi upload config full data present, it should be parsed correctly")
        void hasCorrectUploadConfig() {
            TaskConfig taskConfig = new TaskConfig(TaskConfigFactory.TOKEN_ARTIFACT_LOCATION_FILLED_DIAWI);
            DiawiUploadConfig uploadConfig = taskConfig.getUploadConfig();
            assertNotNull(uploadConfig);
            assertEquals("Test Password", uploadConfig.getPassword());
            assertEquals("Test Comment", uploadConfig.getComment());
            assertEquals("URL", uploadConfig.getCallbackUrl());
            assertEquals("EMAILS", uploadConfig.getCallbackEmails());
            assertTrue(uploadConfig.addToWallOfAps());
            assertTrue(uploadConfig.isFindByUDID());
            assertTrue(uploadConfig.installationNotifications());
        }

        @Test
        @DisplayName("If Diawi upload config partial data present, it should be parsed correctly")
        void hasCorrectPartialUploadConfig() {
            TaskConfig taskConfig = new TaskConfig(TaskConfigFactory.TOKEN_ARTIFACT_LOCATION_FALSED_DIAWI);
            DiawiUploadConfig uploadConfig = taskConfig.getUploadConfig();
            assertNotNull(uploadConfig);
            assertNull(uploadConfig.getPassword());
            assertNull(uploadConfig.getComment());
            assertNull(uploadConfig.getCallbackUrl());
            assertNull(uploadConfig.getCallbackEmails());
            assertFalse(uploadConfig.addToWallOfAps());
            assertFalse(uploadConfig.isFindByUDID());
            assertFalse(uploadConfig.installationNotifications());
        }

        Stream<Map> validConfigs() {
            return TaskConfigFactory.validConfigs();
        }
    }


}