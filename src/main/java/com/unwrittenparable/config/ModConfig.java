package com.unwrittenparable.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue NARRATOR_COOLDOWN_SECONDS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_NARRATOR;
    public static final ForgeConfigSpec.BooleanValue ENABLE_COMBAT_SILENCE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEATH_COMMENTARY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Narrator Settings").push("narrator");

        ENABLE_NARRATOR = builder
                .comment("Enable or disable the narrator entirely")
                .define("enableNarrator", true);

        NARRATOR_COOLDOWN_SECONDS = builder
                .comment("Cooldown between narrator messages in seconds")
                .defineInRange("cooldownSeconds", 180, 10, 600);

        ENABLE_COMBAT_SILENCE = builder
                .comment("Silence narrator during active combat")
                .define("enableCombatSilence", true);

        ENABLE_DEATH_COMMENTARY = builder
                .comment("Narrator comments when player dies (even during combat)")
                .define("enableDeathCommentary", true);

        builder.pop();

        SPEC = builder.build();
    }
}