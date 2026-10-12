package com.unwrittenparable.narrator;

import com.unwrittenparable.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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

        sendNarratorMessage(player, category);
    }

    public static void broadcastMessage(ServerPlayer player, String category) {
        sendNarratorMessage(player, category);
    }

    private static void sendNarratorMessage(ServerPlayer player, String category) {
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
            String narratorName = data.isMultiplayer() ? data.getMultiplayerNarrator() : data.getNarratorType().getDisplayName();
            sendTitleMessage(player, narratorName, message);
        }
    }

    private static void sendTitleMessage(ServerPlayer player, String narratorName, String message) {
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(Component.literal("§c" + narratorName));
        ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(Component.literal("§f" + message));
        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(10, 60, 10);

        player.connection.send(titlePacket);
        player.connection.send(subtitlePacket);
        player.connection.send(animationPacket);
    }

    public static void clearCombatState(ServerPlayer player) {
        IN_COMBAT.remove(player.getUUID());
    }
}