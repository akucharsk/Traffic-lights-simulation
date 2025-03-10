package org.example;

public interface Command {
    void execute();
    boolean equals(Command command);
    void setJunction(Junction junction);
}
