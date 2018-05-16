package com.bot.wattanachaitodolist.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class JsonMapperTest {

    @Test
    public void shouldInitializeJsonMapperSuccessfully() {
        JsonMapper jsonMapper = new JsonMapper();
        assertNotNull(jsonMapper);
    }

    @Test
    public void toJson_shouldReturnEmptyOptionalObjectWhenInputObjectIsInvalid() {
        Optional<String> jsonStringOpt = JsonMapper.toJson(new Object());
        assertThat(jsonStringOpt.isPresent()).isFalse();
    }

    @Test
    public void toJson_shouldReturnJsonStringOptionalObject() {
        Optional<String> jsonStringOpt = JsonMapper.toJson(new SampleClass());
        assertThat(jsonStringOpt.isPresent()).isTrue();
        jsonStringOpt.ifPresent(jsonString -> assertThat(jsonString).isNotEmpty());
    }

    @Test
    public void fromJson_shouldReturnEmptyOptinalObjectWhenInputJsonStringIsInvalid() {
        Optional<SampleClass> sampleOpt = JsonMapper.fromJson("{ invalid jsonString", SampleClass.class);
        assertThat(sampleOpt.isPresent()).isFalse();
    }

    @Test
    public void fromJson_shouldReturnOptionalObject() {
        Optional<SampleClass> sampleOpt = JsonMapper.fromJson("{\"sampleVariable\": \"sampleValue\"}",
                SampleClass.class);
        assertThat(sampleOpt.isPresent()).isTrue();
        sampleOpt.ifPresent(sampleClass -> assertThat(sampleClass.getSampleVariable()).isEqualTo("sampleValue"));
    }

    private static class SampleClass {
        private String sampleVariable;

        public String getSampleVariable() {
            return sampleVariable;
        }

        public void setSampleVariable(String sampleVariable) {
            this.sampleVariable = sampleVariable;
        }
    }
}
