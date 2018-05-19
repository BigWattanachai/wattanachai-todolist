package com.bot.wattanachaitodolist.util;

import com.bot.wattanachaitodolist.common.Tuple;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Optional;

public class TodoMessageUtil {

    private static final String regexDate = "^[0-3]?[0-9]/[0-3]?[0-9]/[0-3]?[0-9]";
    private static final String regexTime = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";

    public static Optional<Tuple<String, Date>> getTaskAndDateTimeTuper(String message) {
        String[] stringSplited = message.split(" : ");
        if (stringSplited.length == 3) {
            String task = stringSplited[0];
            String dateString = stringSplited[1];
            if ("today".equalsIgnoreCase(dateString)) {
                DateTime dateTime = new DateTime();
                Date date = getDateByTimeStringAndDateTime(stringSplited[2], dateTime);
                return Optional.of(new Tuple<>(task, date));
            } else if ("tomorrow".equalsIgnoreCase(dateString)) {
                DateTime dateTime = new DateTime().plusDays(1);
                Date date = getDateByTimeStringAndDateTime(stringSplited[2], dateTime);
                return Optional.of(new Tuple<>(task, date));
            } else {
                Boolean isMatchDate = RegexUtils.patternMatch(regexDate, dateString).matches();
                if (isMatchDate) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/YY");
                    DateTime dateTime = formatter.parseDateTime(dateString);
                    Date date = getDateByTimeStringAndDateTime(stringSplited[2], dateTime);
                    return Optional.of(new Tuple<>(task, date));
                }
            }
        } else if (stringSplited.length == 2) {
            String task = stringSplited[0];
            String dateString = stringSplited[1];
            if ("today".equalsIgnoreCase(dateString)) {
                DateTime dateTime = new DateTime();
                Date date = setDefaultTimeToDate(dateTime);
                return Optional.of(new Tuple<>(task, date));
            } else if ("tomorrow".equalsIgnoreCase(dateString)) {
                DateTime dateTime = new DateTime().plusDays(1);
                Date date = setDefaultTimeToDate(dateTime);
                return Optional.of(new Tuple<>(task, date));
            } else {
                Boolean isMatchDate = RegexUtils.patternMatch(regexDate, dateString).matches();
                if (isMatchDate) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/YY");
                    DateTime dateTime = formatter.parseDateTime(dateString);
                    Date date = setDefaultTimeToDate(dateTime);
                    return Optional.of(new Tuple<>(task, date));
                }
            }
        }
        return Optional.empty();
    }

    private static Date getDateByTimeStringAndDateTime(String timeString, DateTime dateTime) {
        Boolean isMatchTime = RegexUtils.patternMatch(regexTime, timeString).matches();
        if (isMatchTime) {
            String[] timeSprite = timeString.split(":");
            return dateTime.withHourOfDay(Integer.parseInt(timeSprite[0]))
                    .withMinuteOfHour(Integer.parseInt(timeSprite[1]))
                    .withSecondOfMinute(0).toDate();
        } else {
            return setDefaultTimeToDate(dateTime);
        }
    }

    private static Date setDefaultTimeToDate(DateTime dateTime) {
        return dateTime.withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
    }
}
