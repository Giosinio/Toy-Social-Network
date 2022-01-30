package utils;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter SENT_DATE_MESSAGE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final int PDF_MAX_LINE_COUNT = 50;
    public static final int PDF_MAX_CHARS_PER_LINE = 80;

    public static int NUMBER_MESSAGES_ON_PAGE = 13;

    public static int NUMBER_EVENTS_ON_PAGE = 3;

    public static int NUMBER_MY_EVENTS_ON_PAGE = 3;

}
