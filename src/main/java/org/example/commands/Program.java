package org.example.commands;

import org.example.junction.Junction;

import java.util.ArrayList;
import java.util.List;

public class Program {
    private final List<Command> commands;
    private final Junction junction = new Junction();

    public Program() {
        commands = new ArrayList<>();
    }

    public void addCommand(Command command) {
        command.setJunction(junction);
        commands.add(command);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void execute() {
        commands.forEach(Command::execute);
    }

    public Junction getJunction() {
        return junction;
    }
}
