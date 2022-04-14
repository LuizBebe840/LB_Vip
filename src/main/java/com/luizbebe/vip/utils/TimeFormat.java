package com.luizbebe.vip.utils;

import com.luizbebe.vip.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.TimeUnit;

public class TimeFormat {

    protected Main main;

    private final FileConfiguration config;

    private String daysFormat, dayFormat;
    private String hoursFormat, hourFormat;
    private String minutesFormat, minuteFormat;
    private String secondsFormat, secondFormat;

    private long time;

    public TimeFormat(long time) {
        main = Main.getPlugin(Main.class);

        config = main.getConfig();

        dayFormat = config.getString("Time-Format.Days").split(":")[0];
        daysFormat = config.getString("Time-Format.Days").split(":")[1];
        hourFormat = config.getString("Time-Format.Hours").split(":")[0];
        hoursFormat = config.getString("Time-Format.Hours").split(":")[1];
        minuteFormat = config.getString("Time-Format.Minutes").split(":")[0];
        minutesFormat = config.getString("Time-Format.Minutes").split(":")[1];
        secondFormat = config.getString("Time-Format.Seconds").split(":")[0];
        secondsFormat = config.getString("Time-Format.Seconds").split(":")[1];

        this.time = time;
    }

    public String format() {
        if (time == 0)
            return "Sem delay";

        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days + (days == 1 ? " " + dayFormat : " " + daysFormat));

        if (hours > 0)
            sb.append(days > 0 ? (minutes > 0 ? ", " : " e ") : "").append(hours + (hours == 1 ? " " + hourFormat : " " + hoursFormat));

        if (minutes > 0)
            sb.append(days > 0 || hours > 0 ? (seconds > 0 ? ", " : " e ") : "").append(minutes + (minutes == 1 ? " " + minuteFormat : " " + minutesFormat));

        if (seconds > 0)
            sb.append(days > 0 || hours > 0 || minutes > 0 ? " e " : (sb.length() > 0 ? ", " : "")).append(seconds + (seconds == 1 ? " " + secondFormat : " " + secondsFormat));

        String s = sb.toString();
        return s.isEmpty() ? "0 segundos" : s;
    }

    public static String format(long time) {
        return new TimeFormat(time).format();
    }

}
