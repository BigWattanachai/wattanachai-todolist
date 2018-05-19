package com.bot.wattanachaitodolist.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class TodoOrder {
    private List<String> todoOrder = new ArrayList<>();
}
