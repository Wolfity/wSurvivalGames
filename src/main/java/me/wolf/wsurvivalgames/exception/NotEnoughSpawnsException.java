package me.wolf.wsurvivalgames.exception;

public class NotEnoughSpawnsException extends RuntimeException {

    // will be thrown if there are more players then available spawns
    public NotEnoughSpawnsException(final String exceptionMessage) {
        super(exceptionMessage);
    }

}
