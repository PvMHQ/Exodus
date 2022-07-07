package Model.Commandsets;


import Model.Interfaces.Command;
import Model.Interfaces.CommandSet;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.Map;

//!TODO not implemented (V1.1 update)
public class Recipe implements CommandSet {
    @Override
    public Map<String, Command> getCommandset(Map<String, Command> commands) {
        return null;
    }

    @Override
    public void commandHelp(MessageCreateEvent event) {

    }
}
