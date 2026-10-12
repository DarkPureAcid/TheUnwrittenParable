package com.unwrittenparable.narrator;

import com.unwrittenparable.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.*;

public class NarratorManager {
    private static final Map<UUID, Long> LAST_MESSAGE_TIME = new HashMap<>();
    private static final Map<UUID, Boolean> IN_COMBAT = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void sendMessage(ServerPlayer player, String category) {
        if (!ModConfig.ENABLE_NARRATOR.get()) return;

        UUID playerUUID = player.getUUID();

        if (category.equals("combat") && ModConfig.ENABLE_COMBAT_SILENCE.get()) {
            IN_COMBAT.put(playerUUID, true);
            return;
        }

        if (!category.equals("death") && IN_COMBAT.getOrDefault(playerUUID, false)) {
            return;
        }

        if (category.equals("death")) {
            IN_COMBAT.put(playerUUID, false);
            if (!ModConfig.ENABLE_DEATH_COMMENTARY.get()) return;
        }

        if (!category.equals("death") && !category.equals("combat")) {
            IN_COMBAT.put(playerUUID, false);
        }

        long currentTime = System.currentTimeMillis();
        long lastTime = LAST_MESSAGE_TIME.getOrDefault(playerUUID, 0L);
        int cooldownMs = ModConfig.NARRATOR_COOLDOWN_SECONDS.get() * 1000;

        if (currentTime - lastTime < cooldownMs) return;

        LAST_MESSAGE_TIME.put(playerUUID, currentTime);

        WorldNarratorData data = WorldNarratorData.get((ServerLevel) player.level());
        String message;

        if (data.isMultiplayer()) {
            String narratorName = data.getMultiplayerNarrator();
            message = NarratorMessages.getMultiplayerMessage(category, narratorName);
        } else {
            NarratorType type = data.getNarratorType();
            message = NarratorMessages.getMessage(type, category);
        }

        if (message != null && !message.isEmpty()) {
            String formattedMessage = formatNarratorMessage(message, data);
            player.sendSystemMessage(Component.literal(formattedMessage));
        }
    }

    public static void clearCombatState(ServerPlayer player) {
        IN_COMBAT.remove(player.getUUID());
    }

    private static String formatNarratorMessage(String message, WorldNarratorData data) {
        String prefix;
        if (data.isMultiplayer()) {
            prefix = data.getMultiplayerNarrator();
        } else {
            prefix = data.getNarratorType().getDisplayName();
        }
        return "[" + prefix + "] " + message;
    }
}