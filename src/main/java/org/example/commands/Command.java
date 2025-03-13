package org.example.commands;

import org.example.junction.Junction;

public interface Command {
    void execute();
    boolean equals(Command command);
    void setJunction(Junction junction);
}
