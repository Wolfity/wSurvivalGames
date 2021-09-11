package me.wolf.wsurvivalgames.constants;

import me.wolf.wsurvivalgames.utils.Utils;

public class Constants {

    public static class Messages {
        public static final String ADMIN_HELP = Utils.colorize(
                "&7[-------&bSG &cAdmin &bHelp&7-------]\n" +
                        "&b/sg createarena <arena> &7- Creates a new arena \n" +
                        "&b/sg deletearena <arena> &7- Deletes an arena\n" +
                        "&b/sg setworldspawn &7- Sets the world spawn \n" +
                        "&b/sg setlobby <arena> &7- Sets the lobby for the arena \n" +
                        "&b/sg setspawn <arena> &7- Sets a spawn point for the arena\n" +
                        "&b/sg tp <arena> &7- Teleports you to the arena\n" +
                        "&7[-------&bSG &cAdmin &bHelp&7-------]");

        public static final String HELP = Utils.colorize(
                "&7[------- &bSG Help &7-------]\n" +
                        "&b/sg join <arena> &7- Join the arena\n" +
                        "&b/sg leave &7- Leaves the arena\n" +
                        "&b/sg help &7- Displays the help command\n" +
                        "&7[------- &bSG Help &7-------]");

        public static final String ARENA_CREATED = Utils.colorize(
                "&aSuccessfully created the arena {arena}");

        public static final String ARENA_DELETED = Utils.colorize(
                "&cSuccessfully deleted the arena {arena}");

        public static final String SET_LOBBY_SPAWN = Utils.colorize(
                "&aSuccessfully set the lobby spawn");

        public static final String SET_WORLD_SPAWN = Utils.colorize(
                "&aSuccessfully set the world spawn");

        public static final String SET_GAME_SPAWN = Utils.colorize(
                "&aSuccessfully set a game spawn");

        public static final String JOINED_ARENA = Utils.colorize(
                "&aSuccessfully joined the arena &2{arena}");

        public static final String LEFT_ARENA = Utils.colorize(
                "&cSuccessfully left the arena &2{arena}");

        public static final String NOT_IN_ARENA = Utils.colorize(
                "&cYou are not in this arena!");

        public static final String ARENA_NOT_FOUND = Utils.colorize(
                "&cThis arena does not exist!");

        public static final String LOBBY_COUNTDOWN = Utils.colorize(
                "&bThe game will start in &3{countdown}&b seconds!");

        public static final String ARENA_IS_FULL = Utils.colorize(
                "&cThis arena is full!");

        public static final String ALREADY_IN_ARENA = Utils.colorize(
                "&cYou are already in this arena!");

        public static final String CAN_NOT_MODIFY = Utils.colorize(
                "&cThis game is going on, you can not modify the arena");

        public static final String ARENA_EXISTS = Utils.colorize(
                "&cThis arena already exists!");

        public static final String TELEPORTED_TO_ARENA = Utils.colorize(
                "&aTeleported to the arena");

        public static final String GAME_IN_PROGRESS = Utils.colorize(
                "&cThis game is in progress");

        public static final String PLAYER_LEFT_GAME = Utils.colorize(
                "&b{player} &ahas left the game!");

        public static final String PLAYER_JOINED_GAME = Utils.colorize(
                "&b{player} &ahas joined the game!");

        public static final String NO_PERMISSION = Utils.colorize(
                "&cSomething went wrong, you do not have the permissions!");


        public static final String GAME_STARTED = Utils.colorize(
                "  &a===============================================================" +
                        "&a=                        &a&lGame Started! \n" +
                        "&a=            Loot chests and kill enemies, be the last one alive!\n" +
                        "&a=                          &2Good luck!\n" +
                        "&a===============================================================");

        public static final String GAME_WILL_START_SOON = Utils.colorize(
                "&aYou have been teleported to your spawn cages, the game will start soon!");

        public static final String GAME_ENDED = Utils.colorize(
                "&a================================================================" +
                        "&a=                          &a&lGame Ended!\n\n" +
                        "&a=                             &7&lTop 3: \n" +
                        "&a=                  &6Winner: {winner} - {kills} kills\n" +
                        "&a================================================================\n\n\n" +
                        "&2Players will be teleported out in 10 seconds!");

        public static final String CAGE_COUNTDOWN = Utils.colorize(
                "&c[GAME] &eThe game will start in &c{seconds} &eseconds");

        public static final String KIT_SELECTED = Utils.colorize("&aSuccessfully selected the kit {kit}!");


        public static final String KILLEFFECT_SELECTED = Utils.colorize("&aSuccessfully selected the kill effect {killeffect}!");

        public static final String GRACE_PERIOD = Utils.colorize("&c[GAME] &eThe grace period has started, you will receive your kits in {seconds} seconds!");

        public static final String GRACE_PERIOD_END = Utils.colorize("&c[GAME] &eYou have received your kits and can now fight other players!\n" +
                "&eChest refill will occur in {seconds} seconds");

        public static final String CHESTREFILL_HALFWAY = Utils.colorize("&c[GAME] &eChest refill will happen in {seconds} seconds!");

        public static final String CHESTREFILL = Utils.colorize("&a&lChests have been refilled!");

    }


    private Constants() {

    }
}
