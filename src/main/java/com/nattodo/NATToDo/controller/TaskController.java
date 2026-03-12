package com.nattodo.NATToDo.controller;

import com.nattodo.NATToDo.model.Day;
import com.nattodo.NATToDo.model.Task;
import com.nattodo.NATToDo.repository.DayRepository;
import com.nattodo.NATToDo.model.DailyTask;
import com.nattodo.NATToDo.service.TaskService;
import com.nattodo.NATToDo.model.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingFormatArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired  
    private TaskRepository repository;
    @GetMapping("/my-day")
    public Day getDay(
        @AuthenticationPrincipal Jwt jwt, 
        @RequestHeader(value = "User-Timezone", defaultValue = "UTC") String timezone
    ) {
        String userId;

        if (jwt != null) {
                        userId = jwt.getSubject();
        } else {
            // Fallback for local testing if security is bypassed
            userId = "test-user-dev";
            System.out.println("DEBUG: JWT is NULL! React did not send the token.");
            System.out.println("Warning: No JWT found, using fallback ID");
        }
        System.out.println("DEBUG: Found UserID: " + userId);
        return taskService.getOrCreateDay(userId, timezone);
    }
    @PostMapping("/add")
    public Day addTask(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> payload) {
        String userId = jwt.getSubject();
        String title = payload.get("title");
        String type = payload.get("type");
        String category = payload.get("category");
        boolean highPriority = payload.get("highPriority") != null && Boolean.parseBoolean(payload.get("highPriority"));    
        //Day day = repository.findByUserId(userId)
          //      .orElse(new Day(userId, new ArrayList<>(), new ArrayList<>()));
        Day day = taskService.getOrCreateDay(userId, "UTC");
        if ("daily".equals(type)) {
            day.getDailyTasks().add(new DailyTask(title, false, category, 1, highPriority)); 
        } else {
            day.getNormalTasks().add(new Task(title, false, category, highPriority));
        }

        return repository.save(day);
    }
    @PostMapping("/toggle/{type}/{index}")
    public Day toggleTask(@AuthenticationPrincipal Jwt jwt, @PathVariable String type, @PathVariable int index) {
        String userId = jwt.getSubject();
        
        Day day = taskService.getOrCreateDay(userId, "UTC");

        if ("daily".equals(type)) {
            DailyTask task = day.getDailyTasks().get(index);
            task.setCompleted(!task.isCompleted());
        } else {
            Task task = day.getNormalTasks().get(index);
            task.setCompleted(!task.isCompleted());
        }

        return repository.save(day);
    }
    @PostMapping("/delete/{type}/{index}")
    public Day deleteTask(
        @AuthenticationPrincipal Jwt jwt, 
        @PathVariable String type, 
        @PathVariable int index
    ) {
        String userId = jwt.getSubject();
        Day day = taskService.getOrCreateDay(userId, "UTC");

        if ("daily".equals(type)) {
            if (day.getDailyTasks() != null && index < day.getDailyTasks().size()) {
                day.getDailyTasks().remove(index);
            }
        } else {
            if (day.getNormalTasks() != null && index < day.getNormalTasks().size()) {
                day.getNormalTasks().remove(index);
            }
        }

        return repository.save(day);
    }
    @PostMapping("/clear-completed")
    public Day clearCompleted(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Day day = taskService.getOrCreateDay(userId, "UTC");

        // Remove all tasks that are marked as Completed: true
        if (day.getNormalTasks() != null) {
            day.getNormalTasks().removeIf(task -> task.isCompleted());
        }


        return repository.save(day);
    }
    @PostMapping("/update")
    public void updateDay(
        Authentication authentication, 
        @RequestBody Day day
    ) {
        if (authentication instanceof JwtAuthenticationToken token) {
            String clerkUserId = token.getTokenAttributes().get("sub").toString();
            day.setId(clerkUserId); // Force the ID to match the logged-in user
        }
        
        taskService.saveDay(day);
    }
    @PostMapping("/update/{type}/{index}")
    public Day updateTask(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable String type,
        @PathVariable int index,
        @RequestBody Map<String, Object> payload
    ) {
        String userId = jwt.getSubject();
        Day day = taskService.getOrCreateDay(userId, "UTC");

        String title = (String) payload.get("title");
        String category = (String) payload.get("category");
        boolean highPriority = payload.get("highPriority") != null && (boolean) payload.get("highPriority");
        
        // Check if "completed" was sent, otherwise keep current status
        boolean completed = payload.get("completed") != null ? (boolean) payload.get("completed") : false;

        if ("daily".equals(type)) {
            if (day.getDailyTasks() != null && index < day.getDailyTasks().size()) {
                DailyTask task = day.getDailyTasks().get(index);
                task.setTitle(title);
                task.setCategory(category);
                task.setHighPriority(highPriority);
                task.setCompleted(completed);
            }
        } else {
            if (day.getNormalTasks() != null && index < day.getNormalTasks().size()) {
                Task task = day.getNormalTasks().get(index);
                task.setTitle(title);
                task.setCategory(category);
                task.setHighPriority(highPriority);
                task.setCompleted(completed);
            }
        }

        return repository.save(day);
    }
}