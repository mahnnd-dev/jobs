package dev.m.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class DateUtils {
    public List<LocalDateTime> getDatesBetweenUsingJava8(LocalDateTime startDate, LocalDateTime endDate) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDaysBetween)
                .collect(Collectors.toList());
    }

    //lay list khoang time start--> endDate = list string
    public List<String> getListDatesBetween(String startDate, String endDate, String patternDate) {
        List<String> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternDate);
        LocalDateTime lcStart = LocalDateTime.parse(startDate, formatter);
        LocalDateTime lcEnd = LocalDateTime.parse(endDate, formatter);
        List<LocalDateTime> localDates = getDatesBetweenUsingJava8(lcStart, lcEnd);
        for (LocalDateTime date : localDates) {
            String text = date.format(formatter);
            list.add(text);
        }
        return list;
    }


}
