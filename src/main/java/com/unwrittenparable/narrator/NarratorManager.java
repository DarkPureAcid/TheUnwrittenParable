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
        String messageKey;
        String narratorName;

        if (data.isMultiplayer()) {
            narratorName = data.getMultiplayerNarrator();
            messageKey = NarratorMessages.getMultiplayerMessage(category, narratorName);
        } else {
            NarratorType type = data.getNarratorType();
            narratorName = type.getDisplayName();
            messageKey = NarratorMessages.getMessage(type, category);
        }

        if (messageKey != null && !messageKey.isEmpty()) {
            sendTitleMessage(player, narratorName, messageKey, data.isMultiplayer());
        }
    }

    private static void sendTitleMessage(ServerPlayer player, String narratorName, String messageKey, boolean isMultiplayer) {
        Component titleComponent;
        if (isMultiplayer) {
            titleComponent = Component.literal(narratorName).withStyle(style -> style.withColor(0xFF5555));
        } else {
            titleComponent = Component.translatable("narrator." + narratorName.toLowerCase() + ".name").withStyle(style -> style.withColor(0xFF5555));
        }

        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(titleComponent);
        ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(Component.translatable(messageKey).withStyle(style -> style.withColor(0xFFFFFF)));
        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(10, 60, 10);

        player.connection.send(titlePacket);
        player.connection.send(subtitlePacket);
        player.connection.send(animationPacket);
    }

    public static void clearCombatState(ServerPlayer player) {
        IN_COMBAT.remove(player.getUUID());
    }
}