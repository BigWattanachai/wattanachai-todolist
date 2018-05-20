package com.bot.wattanachaitodolist.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TodoOrder {
    private List<String> todoOrder = new ArrayList<>();
}
