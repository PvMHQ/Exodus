package Model.Interfaces;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.Map;

public interface CommandSet {
    Map<String, Command> getCommandset(Map<String, Command> commands);
    void commandHelp(MessageCreateEvent event);
}
