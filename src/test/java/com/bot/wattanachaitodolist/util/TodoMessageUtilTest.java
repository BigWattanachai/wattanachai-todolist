package com.bot.wattanachaitodolist.util;

import com.bot.wattanachaitodolist.common.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TodoMessageUtilTest {
    @Test
    public void shouldInitializeTodoMessageUtilSuccessfully() {
        TodoMessageUtil todoMessageUtil = new TodoMessageUtil();
        assertNotNull(todoMessageUtil);
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithCorrectTodayWithTimeTodoFormat() {
        String message = "todo : today : 15:30";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2).isNotNull();
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithCorrectTomorrowWithTimeTodoFormat() {
        String message = "todo : tomorrow : 15:30";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2).isNotNull();
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithCorrectTodayTodoFormat() {
        String message = "todo : today";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2).isNotNull();
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithCorrectTomorrowTodoFormat() {
        String message = "todo : tomorrow";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2).isNotNull();
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithCorrectDateTodoFormat() {
        String message = "todo : 11/11/11";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2.toString()).isEqualTo("Fri Nov 11 12:00:00 ICT 2011");
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithCorrectDateTimeTodoFormat() {
        String message = "todo : 11/11/11 : 11:11";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2.toString()).isEqualTo("Fri Nov 11 11:11:00 ICT 2011");
    }

    @Test
    public void shouldReturnAbsentOptionalWhenGetTuperWithWrongTotoFormat() {
        String message = "wrong format : ";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.isPresent()).isFalse();
    }

    @Test
    public void shouldReturnOptionalTuperWhenGetTuperWithWrongTimeButMatchRegex() {
        String message = "todo : 11/11/11 : xx:bb";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.get()._1).isEqualTo("todo");
        assertThat(tupleOptional.get()._2.toString()).isEqualTo("Fri Nov 11 12:00:00 ICT 2011");
    }

    @Test
    public void shouldReturnAbsentOptionalWhenGetTuperWithWrongDateFormat() {
        String message = "todo : bn/k4/8 : 15:30";

        Optional<Tuple<String, Date>> tupleOptional = TodoMessageUtil.getTaskAndDateTimeTuper(message);
        assertThat(tupleOptional.isPresent()).isFalse();

        String message2 = "todo : bn/k4/8";

        Optional<Tuple<String, Date>> tupleOptional2 = TodoMessageUtil.getTaskAndDateTimeTuper(message2);
        assertThat(tupleOptional2.isPresent()).isFalse();

    }
}
