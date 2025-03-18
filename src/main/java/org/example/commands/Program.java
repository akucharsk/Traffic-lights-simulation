package org.example.commands;

import org.example.location.Junction;

import java.util.ArrayList;
import java.util.List;

public class Program {
    private final List<Command> commands;
    private Junction junction = new Junction();
    private int commandIdx = 0;

    public enum Status {
        PENDING, FINISHED
    }

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

    public Status getStatus() {
        return commandIdx >= commands.size() ? Status.FINISHED : Status.PENDING;
    }

    public Command getCommand() {
        if (commandIdx >= commands.size()) {return null;}
        commandIdx++;
        return commands.get(commandIdx - 1);
    }

    public Junction getJunction() {
        return junction;
    }

    public void setJunction(Junction junction) {
        this.junction = junction;
    }
}
