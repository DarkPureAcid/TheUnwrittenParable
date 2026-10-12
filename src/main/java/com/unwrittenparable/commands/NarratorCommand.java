package com.unwrittenparable.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.unwrittenparable.events.HalloweenEvent;
import com.unwrittenparable.narrator.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NarratorCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("narrator")
                .requires(source -> source.hasPermission(0))
                .then(Commands.literal("info")
                        .executes(NarratorCommand::showInfo))
                .then(Commands.literal("test")
                        .then(Commands.argument("category", IntegerArgumentType.integer(1, 6))
                                .executes(NarratorCommand::testNarrator)))
                .then(Commands.literal("broadcast")
                        .then(Commands.argument("category", IntegerArgumentType.integer(1, 6))
                                .executes(NarratorCommand::broadcastNarrator)))
                .then(Commands.literal("testnarrator")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("category", IntegerArgumentType.integer(1, 6))
                                        .executes(NarratorCommand::testSpecificNarrator))))
                .then(Commands.literal("fight")
                        .then(Commands.argument("narrator1", StringArgumentType.word())
                                .then(Commands.argument("narrator2", StringArgumentType.word())
                                        .executes(NarratorCommand::startFight))))
                .then(Commands.literal("stopfight")
                        .executes(NarratorCommand::stopFight))
                .then(Commands.literal("on")
                        .executes(NarratorCommand::enableNarrator))
                .then(Commands.literal("off")
                        .executes(NarratorCommand::disableNarrator))
                .then(Commands.literal("toggle")
                        .executes(NarratorCommand::toggleNarrator))
                .then(Commands.literal("rules")
                        .executes(NarratorCommand::showRules))
                .then(Commands.literal("halloween")
                        .executes(NarratorCommand::toggleHalloween))
                .then(Commands.literal("help")
                        .executes(NarratorCommand::showHelp))
        );
    }

    private static int showInfo(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        ClientNarratorData data = ClientNarratorData.get();
        NarratorGameRule rules = NarratorGameRule.get();

        player.sendSystemMessage(Component.translatable("commands.narrator.info"));
        player.sendSystemMessage(Component.literal("Narrator: " + (rules.isNarratorEnabled() ? "ON" : "OFF")));

        if (data.isMultiplayer()) {
            player.sendSystemMessage(Component.translatable("commands.narrator.mode_multiplayer"));
            player.sendSystemMessage(Component.translatable("commands.narrator.narrator", Component.literal(data.getMultiplayerNarrator())));
        } else {
            String narratorKey = data.getNarratorType().getDisplayName().toLowerCase();
            player.sendSystemMessage(Component.translatable("commands.narrator.mode_singleplayer"));
            player.sendSystemMessage(Component.translatable("commands.narrator.narrator", Component.translatable("narrator." + narratorKey + ".name")));
            player.sendSystemMessage(Component.translatable("commands.narrator.personality", Component.translatable("narrator." + narratorKey + ".desc")));
        }

        return 1;
    }

    private static int testNarrator(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        int categoryIndex = IntegerArgumentType.getInteger(context, "category");
        String[] categories = {"block_break", "biome_enter", "death", "combat", "idle", "achievement"};

        if (categoryIndex < 1 || categoryIndex > categories.length) {
            player.sendSystemMessage(Component.translatable("commands.narrator.invalid_category"));
            return 0;
        }

        NarratorManager.sendMessage(player, categories[categoryIndex - 1]);
        return 1;
    }

    private static int broadcastNarrator(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        int categoryIndex = IntegerArgumentType.getInteger(context, "category");
        String[] categories = {"block_break", "biome_enter", "death", "combat", "idle", "achievement"};

        if (categoryIndex < 1 || categoryIndex > categories.length) {
            player.sendSystemMessage(Component.translatable("commands.narrator.invalid_category"));
            return 0;
        }

        NarratorManager.broadcastMessage(player, categories[categoryIndex - 1]);
        return 1;
    }

    private static int testSpecificNarrator(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        String narratorName = StringArgumentType.getString(context, "name");
        int categoryIndex = IntegerArgumentType.getInteger(context, "category");
        String[] categories = {"block_break", "biome_enter", "death", "combat", "idle", "achievement"};

        if (categoryIndex < 1 || categoryIndex > categories.length) {
            player.sendSystemMessage(Component.translatable("commands.narrator.invalid_category"));
            return 0;
        }

        NarratorManager.testSpecificNarrator(player, narratorName, categories[categoryIndex - 1]);
        return 1;
    }

    private static int startFight(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        String narrator1 = StringArgumentType.getString(context, "narrator1");
        String narrator2 = StringArgumentType.getString(context, "narrator2");

        NarratorFight.startFight(player, narrator1, narrator2);
        return 1;
    }

    private static int stopFight(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        NarratorFight.stopFight();
        player.sendSystemMessage(Component.literal("Fight stopped!"));
        return 1;
    }

    private static int enableNarrator(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        NarratorGameRule.get().setNarratorEnabled(true);
        player.sendSystemMessage(Component.literal("Narrator enabled!"));
        return 1;
    }

    private static int disableNarrator(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        NarratorGameRule.get().setNarratorEnabled(false);
        player.sendSystemMessage(Component.literal("Narrator disabled!"));
        return 1;
    }

    private static int toggleNarrator(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        NarratorGameRule rules = NarratorGameRule.get();
        rules.setNarratorEnabled(!rules.isNarratorEnabled());
        player.sendSystemMessage(Component.literal("Narrator " + (rules.isNarratorEnabled() ? "enabled" : "disabled") + "!"));
        return 1;
    }

    private static int showRules(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        NarratorGameRule rules = NarratorGameRule.get();

        player.sendSystemMessage(Component.literal("=== Narrator Game Rules ==="));
        player.sendSystemMessage(Component.literal("Narrator: " + (rules.isNarratorEnabled() ? "ON" : "OFF")));
        player.sendSystemMessage(Component.literal("Command Reactions: " + (rules.isCommandReactionsEnabled() ? "ON" : "OFF")));
        player.sendSystemMessage(Component.literal("Achievements: " + (rules.isAchievementsEnabled() ? "ON" : "OFF")));
        player.sendSystemMessage(Component.literal("Combat Silence: " + (rules.isCombatSilenceEnabled() ? "ON" : "OFF")));
        player.sendSystemMessage(Component.literal("Death Commentary: " + (rules.isDeathCommentaryEnabled() ? "ON" : "OFF")));

        return 1;
    }

    private static int toggleHalloween(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        HalloweenEvent.toggleHalloween();
        player.sendSystemMessage(Component.literal("Halloween mode " + (HalloweenEvent.isHalloweenActive() ? "ON" : "OFF")));
        return 1;
    }

    private static int showHelp(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        player.sendSystemMessage(Component.translatable("commands.narrator.help"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_info"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_test"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_broadcast"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_categories"));
        player.sendSystemMessage(Component.literal("/narrator testnarrator <name> <1-6> - Test a specific narrator"));
        player.sendSystemMessage(Component.literal("/narrator fight <narrator1> <narrator2> - Start a fight between two narrators"));
        player.sendSystemMessage(Component.literal("/narrator stopfight - Stop the current fight"));
        player.sendSystemMessage(Component.literal("/narrator on - Enable narrator"));
        player.sendSystemMessage(Component.literal("/narrator off - Disable narrator"));
        player.sendSystemMessage(Component.literal("/narrator toggle - Toggle narrator"));
        player.sendSystemMessage(Component.literal("/narrator rules - Show game rules"));

        return 1;
    }
}