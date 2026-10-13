package com.unwrittenparable.narrator;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class CommandReactions {
    private static final Random RANDOM = new Random();

    private static final Map<String, List<String>> COMMAND_REACTIONS = new HashMap<>();

    static {
        COMMAND_REACTIONS.put("tp", Arrays.asList(
                "narrator.command.tp.1",
                "narrator.command.tp.2",
                "narrator.command.tp.3",
                "narrator.command.tp.4"
        ));

        COMMAND_REACTIONS.put("teleport", Arrays.asList(
                "narrator.command.tp.1",
                "narrator.command.tp.2",
                "narrator.command.tp.3",
                "narrator.command.tp.4"
        ));

        COMMAND_REACTIONS.put("gamemode", Arrays.asList(
                "narrator.command.gamemode.1",
                "narrator.command.gamemode.2",
                "narrator.command.gamemode.3",
                "narrator.command.gamemode.4"
        ));

        COMMAND_REACTIONS.put("gamerule", Arrays.asList(
                "narrator.command.gamerule.1",
                "narrator.command.gamerule.2",
                "narrator.command.gamerule.3",
                "narrator.command.gamerule.4"
        ));

        COMMAND_REACTIONS.put("time", Arrays.asList(
                "narrator.command.time.1",
                "narrator.command.time.2",
                "narrator.command.time.3",
                "narrator.command.time.4"
        ));

        COMMAND_REACTIONS.put("weather", Arrays.asList(
                "narrator.command.weather.1",
                "narrator.command.weather.2",
                "narrator.command.weather.3",
                "narrator.command.weather.4"
        ));

        COMMAND_REACTIONS.put("give", Arrays.asList(
                "narrator.command.give.1",
                "narrator.command.give.2",
                "narrator.command.give.3",
                "narrator.command.give.4"
        ));

        COMMAND_REACTIONS.put("kill", Arrays.asList(
                "narrator.command.kill.1",
                "narrator.command.kill.2",
                "narrator.command.kill.3",
                "narrator.command.kill.4"
        ));

        COMMAND_REACTIONS.put("effect", Arrays.asList(
                "narrator.command.effect.1",
                "narrator.command.effect.2",
                "narrator.command.effect.3",
                "narrator.command.effect.4"
        ));

        COMMAND_REACTIONS.put("enchant", Arrays.asList(
                "narrator.command.enchant.1",
                "narrator.command.enchant.2",
                "narrator.command.enchant.3",
                "narrator.command.enchant.4"
        ));

        COMMAND_REACTIONS.put("summon", Arrays.asList(
                "narrator.command.summon.1",
                "narrator.command.summon.2",
                "narrator.command.summon.3",
                "narrator.command.summon.4"
        ));

        COMMAND_REACTIONS.put("difficulty", Arrays.asList(
                "narrator.command.difficulty.1",
                "narrator.command.difficulty.2",
                "narrator.command.difficulty.3",
                "narrator.command.difficulty.4"
        ));
    }

    public static String getReaction(String command) {
        String baseCommand = command.split(" ")[0].toLowerCase();

        if (baseCommand.startsWith("/")) {
            baseCommand = baseCommand.substring(1);
        }

        List<String> reactions = COMMAND_REACTIONS.get(baseCommand);
        if (reactions != null && !reactions.isEmpty()) {
            return reactions.get(RANDOM.nextInt(reactions.size()));
        }

        return null;
    }

    public static boolean hasReaction(String command) {
        String baseCommand = command.split(" ")[0].toLowerCase();

        if (baseCommand.startsWith("/")) {
            baseCommand = baseCommand.substring(1);
        }

        return COMMAND_REACTIONS.containsKey(baseCommand);
    }
}