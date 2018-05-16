package com.bot.wattanachaitodolist.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageCommand {
    CREATE_TODO(0, "create"),
    EDIT_TODO(1, "edit"),
    NO_COMMAND(99, "NO COMMAND");

    private int code;
    private String command;
}
