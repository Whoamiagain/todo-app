package com.nattodo.NATToDo.service;

import com.nattodo.NATToDo.model.Day;
import com.nattodo.NATToDo.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private DayRepository dayRepository;

    /*public Day getOrCreateDay(String id) {
        return dayRepository.findById(id).orElseGet(() -> {
            Day newDay = new Day();
            newDay.setId(id);
            return dayRepository.save(newDay);
        });
    }*/
    public Day getOrCreateDay(String id, String timezoneId) {
        Day day = dayRepository.findById(id).orElseGet(() -> {
            Day newDay = new Day();
            newDay.setId(id);
            // Initialize lastOpened in UTC
            newDay.setLastOpened(LocalDateTime.now(ZoneOffset.UTC));
            return dayRepository.save(newDay);
        });

        // Determine the Zone (Fall back to UTC if the string is invalid)
        ZoneId userZone;
        try {
            userZone = ZoneId.of(timezoneId);
        } catch (Exception e) {
            userZone = ZoneOffset.UTC; 
        }
        if (shouldReset(day, userZone)) {
            resetDailyTasks(day);
            day.setLastOpened(LocalDateTime.now(ZoneOffset.UTC));
            dayRepository.save(day);
        }

        return day;
    }

    private boolean shouldReset(Day day, ZoneId userZone) {
        ZonedDateTime nowUserTime = ZonedDateTime.now(userZone);
        
        // Calculate 2:00 AM today in user's local time
        ZonedDateTime targetResetTime = nowUserTime.toLocalDate()
                .atTime(2, 0)
                .atZone(userZone);
        
        if (nowUserTime.isBefore(targetResetTime)) {
            targetResetTime = targetResetTime.minusDays(1);
        }
        ZonedDateTime lastOpenedUserTime = day.getLastOpened()
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(userZone);

        return lastOpenedUserTime.isBefore(targetResetTime);
    }

    private void resetDailyTasks(Day day) {
        day.getDailyTasks().forEach(task -> task.setCompleted(false));
    }
    public void saveDay(Day day) {
        day.setLastOpened(LocalDateTime.now());
        dayRepository.save(day);
    }
    
    public void syncDay(Day day) {
        LocalDateTime lastTime = day.getLastOpened();
        dayRepository.save(day);
    }
}