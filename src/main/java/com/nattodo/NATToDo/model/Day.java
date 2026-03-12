package com.nattodo.NATToDo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "days")
public class Day {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.mapping.Field("userId")
    private String userId; 
    private List<Task> normalTasks = new ArrayList<>();
    private List<DailyTask> dailyTasks = new ArrayList<>();
    private LocalDateTime lastOpened = LocalDateTime.now();

    public Day(String userId, List<Task> normalTasks, List<DailyTask> dailyTasks) {
        this.userId = userId;
        this.normalTasks = normalTasks;
        this.dailyTasks = dailyTasks;
        this.lastOpened = LocalDateTime.now();
    }
    public String getUserId() {
        return id;
    }
    public void increment(DailyTask t) { t.addn(1); }
    public void decrement(DailyTask t) {
        t.subtractn(1);
        if (t.getNum() == 0) t.setCompleted(true);
    }
}