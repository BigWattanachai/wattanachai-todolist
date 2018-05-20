package com.bot.wattanachaitodolist.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class RegexUtilTest {
    @Test
    public void shouldInitializeRegexUtilSuccessfully() {
        RegexUtil regexUtil = new RegexUtil();
        assertNotNull(regexUtil);
    }

    @Test
    public void shouldReturnMatcherWhenGetMatcherByRegexAndTargetString() {
        Matcher matcher = RegexUtil.patternMatch("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", "12:12");
        assertThat(matcher.matches()).isTrue();
    }
}
