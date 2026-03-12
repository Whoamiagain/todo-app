package com.nattodo.NATToDo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String title;
    private boolean Completed;
    private String category;
    private boolean highPriority;
}