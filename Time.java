/**
 * @author Pawlik Jakub S30647
 */

package zad1;

import org.yaml.snakeyaml.Yaml;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.time.format.TextStyle;


public class Time {
    public static String passed(String from, String to) {
        try {
            Locale locale = new Locale("pl", "PL");

            boolean withTime = from.contains("T") && to.contains("T");

            if (withTime) {
                ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse(from), ZoneId.of("Europe/Warsaw"));
                ZonedDateTime end = ZonedDateTime.of(LocalDateTime.parse(to), ZoneId.of("Europe/Warsaw"));

                long totalMinutes = ChronoUnit.MINUTES.between(start, end);
                long totalHours = ChronoUnit.HOURS.between(start, end);
                long days = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
                double weeks = (double) days / 7;
                StringBuilder result = new StringBuilder();

                result.append(formatDateLine(start.toLocalDate(), end.toLocalDate(), locale, true, start, end));
                result.append(" - mija: ").append(days).append(" ").append(formatUnit(days, "dzień", "dni")).append(", ");
                result.append("tygodni ").append(formatWeeks(weeks)).append("\n");
                result.append(" - godzin: ").append(totalHours).append(", minut: ").append(totalMinutes).append("\n");

                if (days > 0) {
                    Period period = Period.between(start.toLocalDate(), end.toLocalDate());
                    result.append(" - kalendarzowo:");
                    if (period.getYears() > 0)
                        result.append(" ").append(period.getYears()).append(" ").append(formatYear(period.getYears()));
                    if (period.getMonths() > 0)
                        result.append(", ").append(period.getMonths()).append(" ").append(formatMonth(period.getMonths())).append(",");
                    int remainingDays = period.getDays();
                    if (remainingDays > 0)
                        result.append(" ").append(remainingDays).append(" ").append(formatUnit(remainingDays, "dzień", "dni"));
                    result.append("\n");
                }
                
                return result.toString().trim();
            } else {
                LocalDate start = LocalDate.parse(from);
                LocalDate end = LocalDate.parse(to);

                long days = ChronoUnit.DAYS.between(start, end);
                double weeks = (double) days / 7;
                Period period = Period.between(start, end);

                StringBuilder result = new StringBuilder();
                result.append(formatDateLine(start, end, locale, false, null, null));
                result.append(" - mija: ").append(days).append(" ").append(formatUnit(days, "dzień", "dni")).append(", ");
                result.append("tygodni ").append(formatWeeks(weeks)).append("\n");


                if (days > 0) {
                    result.append(" - kalendarzowo:");
                    if (period.getYears() > 0)
                        result.append(" ").append(period.getYears()).append(" ").append(formatYear(period.getYears()));
                    if (period.getMonths() > 0)
                        result.append(", ").append(period.getMonths()).append(" ").append(formatMonth(period.getMonths())).append(",");
                    if (period.getDays() > 0)
                        result.append(" ").append(period.getDays()).append(" ").append(formatUnit(period.getDays(), "dzień", "dni"));
                    result.append("\n");
                }

                return result.toString().trim();
            }

        } catch (Exception e) {
            return "*** " + e.toString();
        }
    }

    private static String formatDateLine(LocalDate start, LocalDate end, Locale locale, boolean withTime, ZonedDateTime dtStart, ZonedDateTime dtEnd) {
        StringBuilder line = new StringBuilder();
        line.append("Od ")
                .append(start.getDayOfMonth()).append(" ")
                .append(start.getMonth().getDisplayName(TextStyle.FULL, locale)).append(" ")
                .append(start.getYear()).append(" (")
                .append(start.getDayOfWeek().getDisplayName(TextStyle.FULL, locale)).append(")");

        if (withTime) {
            line.append(" godz. ").append(String.format("%02d:%02d", dtStart.getHour(), dtStart.getMinute()));
        }

        line.append(" do ")
                .append(end.getDayOfMonth()).append(" ")
                .append(end.getMonth().getDisplayName(TextStyle.FULL, locale)).append(" ")
                .append(end.getYear()).append(" (")
                .append(end.getDayOfWeek().getDisplayName(TextStyle.FULL, locale)).append(")");

        if (withTime) {
            line.append(" godz. ").append(String.format("%02d:%02d", dtEnd.getHour(), dtEnd.getMinute()));
        }

        line.append("\n");
        return line.toString();
    }

    private static String formatUnit(long count, String singular, String plural) {
        return (count == 1) ? singular : plural;
    }

    private static String formatYear(long count) {
        if (count == 1) return "rok";
        if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) return "lata";
        return "lat";
    }

    private static String formatMonth(long count) {
        if (count == 1) return "miesiąc";
        if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) return "miesiące";
        return "miesięcy";
    }

    private static String formatWeeks(double weeks) {
        return (weeks == (long) weeks)
                ? String.format("%d", (long) weeks)
                : String.format(Locale.US, "%.2f", weeks);
    }
}
