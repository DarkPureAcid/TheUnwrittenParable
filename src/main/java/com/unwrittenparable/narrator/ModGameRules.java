package com.unwrittenparable.narrator;

import net.minecraft.world.level.GameRules;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> NARRATOR_ENABLED =
            GameRules.register("tupNarratorEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> COMMAND_REACTIONS =
            GameRules.register("tupCommandReactions", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> ACHIEVEMENTS =
            GameRules.register("tupNarratorAchievements", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> COMBAT_SILENCE =
            GameRules.register("tupCombatSilence", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> DEATH_COMMENTARY =
            GameRules.register("tupDeathCommentary", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> HALLOWEEN_EVENT =
            GameRules.register("tupHalloweenEvent", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> CHRISTMAS_EVENT =
            GameRules.register("tupChristmasEvent", GameRules.Category.MISC, GameRules.BooleanValue.create(true));

    public static void register() {
        // Game rules are registered in static initializers
    }
}