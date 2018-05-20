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
        String[] messageSplit = message.split(" : ");
        if (messageSplit.length < 2) {
            return Optional.empty();
        } else if ("today".equalsIgnoreCase(messageSplit[1].trim())) {
            DateTime dateTime = new DateTime();
            Date date = getDateByTimeStringAndDateTime(messageSplit.length > 2 ? messageSplit[2] : "", dateTime);
            return Optional.of(new Tuple<>(messageSplit[0], date));
        } else if ("tomorrow".equalsIgnoreCase(messageSplit[1].trim())) {
            DateTime dateTime = new DateTime().plusDays(1);
            Date date = getDateByTimeStringAndDateTime(messageSplit.length > 2 ? messageSplit[2] : "", dateTime);
            return Optional.of(new Tuple<>(messageSplit[0], date));
        } else {
            Boolean isMatchDate = RegexUtil.patternMatch(regexDate, messageSplit[1]).matches();
            if (!isMatchDate)
                return Optional.empty();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/YY");
            DateTime dateTime = formatter.parseDateTime(messageSplit[1]);
            Date date = getDateByTimeStringAndDateTime(messageSplit.length > 2 ? messageSplit[2] : "", dateTime);
            return Optional.of(new Tuple<>(messageSplit[0], date));

        }
    }

    private static Date getDateByTimeStringAndDateTime(String timeString, DateTime dateTime) {
        Boolean isMatchTime = RegexUtil.patternMatch(regexTime, timeString).matches();
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
