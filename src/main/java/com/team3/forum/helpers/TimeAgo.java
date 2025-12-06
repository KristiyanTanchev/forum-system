package com.team3.forum.helpers;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeAgo {

    public static String toTimeAgo(LocalDateTime time, ZoneId zone) {
        LocalDateTime now = LocalDateTime.now(zone);

        // Convert to Instants to avoid DST issues
        Instant pastInstant = time.atZone(zone).toInstant();
        Instant nowInstant = now.atZone(zone).toInstant();

        long seconds = Duration.between(pastInstant, nowInstant).getSeconds();

        // Future times
        if (seconds < 0) {
            long positive = -seconds;
            if (positive < 60) return positive + " seconds from now";
            if (positive < 3600) return (positive / 60) + " minutes from now";
            if (positive < 86400) return (positive / 3600) + " hours from now";
            return (positive / 86400) + " days from now";
        }

        // Past times
        if (seconds < 5) return "just now";
        if (seconds < 60) return seconds + " seconds ago";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " minutes ago";

        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";

        long days = hours / 24;
        if (days < 30) return days + " days ago";

        long months = days / 30;
        if (months < 12) return months + " months ago";

        long years = months / 12;
        return years + " years ago";
    }

    // Convenience overload using system default zone
    public static String toTimeAgo(LocalDateTime time) {
        if (time == null) return null;
        return toTimeAgo(time, ZoneId.systemDefault());
    }
}
