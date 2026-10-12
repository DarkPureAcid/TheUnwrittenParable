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

        player.sendSystemMessage(Component.translatable("commands.narrator.info"));
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
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

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
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        int categoryIndex = IntegerArgumentType.getInteger(context, "category");
        String[] categories = {"block_break", "biome_enter", "death", "combat", "idle", "achievement"};

        if (categoryIndex < 1 || categoryIndex > categories.length) {
            player.sendSystemMessage(Component.translatable("commands.narrator.invalid_category"));
            return 0;
        }

        NarratorManager.broadcastMessage(player, categories[categoryIndex - 1]);
        return 1;
    }

    private static int showHelp(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) return 0;

        player.sendSystemMessage(Component.translatable("commands.narrator.help"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_info"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_test"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_broadcast"));
        player.sendSystemMessage(Component.translatable("commands.narrator.help_categories"));

        return 1;
    }
}