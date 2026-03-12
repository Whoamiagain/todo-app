package com.nattodo.NATToDo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DailyTask extends Task {
    private int num;
    public void addn(int n) { this.num += n; }
    public DailyTask(String title, boolean completed, String category, int num, boolean highPriority) {
        super(title, completed, category, highPriority); 
        this.num = num;
    }
    public void subtractn(int n) { 
        if (this.num > 0) this.num -= n; 
    }
}