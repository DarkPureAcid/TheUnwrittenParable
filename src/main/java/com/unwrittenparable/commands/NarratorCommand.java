package com.unwrittenparable.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.unwrittenparable.narrator.NarratorManager;
import com.unwrittenparable.narrator.NarratorType;
import com.unwrittenparable.narrator.WorldNarratorData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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
                .then(Commands.literal("help")
                        .executes(NarratorCommand::showHelp))
        );
    }

    private static int showInfo(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        ServerLevel level = player.serverLevel();
        WorldNarratorData data = WorldNarratorData.get(level);

        player.sendSystemMessage(Component.literal("=== Narrator Info ==="));
        if (data.isMultiplayer()) {
            player.sendSystemMessage(Component.literal("Mode: Multiplayer"));
            player.sendSystemMessage(Component.literal("Narrator: " + data.getMultiplayerNarrator()));
        } else {
            player.sendSystemMessage(Component.literal("Mode: Singleplayer"));
            player.sendSystemMessage(Component.literal("Narrator: " + data.getNarratorType().getDisplayName()));
            player.sendSystemMessage(Component.literal("Personality: " + data.getNarratorType().getDescription()));
        }

        return 1;
    }

    private static int testNarrator(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        int categoryIndex = IntegerArgumentType.getInteger(context, "category");
        String[] categories = {"block_break", "biome_enter", "death", "combat", "idle", "achievement"};

        if (categoryIndex < 1 || categoryIndex > categories.length) {
            player.sendSystemMessage(Component.literal("Invalid category. Use 1-6."));
            return 0;
        }

        NarratorManager.sendMessage(player, categories[categoryIndex - 1]);
        return 1;
    }

    private static int broadcastNarrator(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        int categoryIndex = IntegerArgumentType.getInteger(context, "category");
        String[] categories = {"block_break", "biome_enter", "death", "combat", "idle", "achievement"};

        if (categoryIndex < 1 || categoryIndex > categories.length) {
            player.sendSystemMessage(Component.literal("Invalid category. Use 1-6."));
            return 0;
        }

        NarratorManager.broadcastMessage(player, categories[categoryIndex - 1]);
        return 1;
    }

    private static int showHelp(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        player.sendSystemMessage(Component.literal("=== The Unwritten Parable Commands ==="));
        player.sendSystemMessage(Component.literal("/narrator info - Show current narrator settings"));
        player.sendSystemMessage(Component.literal("/narrator test <1-6> - Test a narrator message (respects cooldown)"));
        player.sendSystemMessage(Component.literal("/narrator broadcast <1-6> - Broadcast narrator message (bypasses cooldown)"));
        player.sendSystemMessage(Component.literal("  1: block_break, 2: biome_enter, 3: death, 4: combat, 5: idle, 6: achievement"));
        player.sendSystemMessage(Component.literal("/narrator help - Show this help"));

        return 1;
    }
}