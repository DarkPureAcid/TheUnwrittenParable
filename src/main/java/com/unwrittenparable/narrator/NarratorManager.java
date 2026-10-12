package com.unwrittenparable.narrator;

import com.unwrittenparable.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class NarratorManager {
    private static final Map<UUID, Long> LAST_MESSAGE_TIME = new HashMap<>();
    private static final Map<UUID, Boolean> IN_COMBAT = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void sendMessage(LocalPlayer player, String category) {
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

    public static void broadcastMessage(LocalPlayer player, String category) {
        sendNarratorMessage(player, category);
    }

    private static void sendNarratorMessage(LocalPlayer player, String category) {
        ClientNarratorData data = ClientNarratorData.get();
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
            showTitleMessage(narratorName, messageKey, data.isMultiplayer());
        }
    }

    private static void showTitleMessage(String narratorName, String messageKey, boolean isMultiplayer) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        Component titleComponent;
        if (isMultiplayer) {
            titleComponent = Component.literal(narratorName).withStyle(style -> style.withColor(0xFF5555));
        } else {
            titleComponent = Component.translatable("narrator." + narratorName.toLowerCase() + ".name").withStyle(style -> style.withColor(0xFF5555));
        }

        Component subtitleComponent = Component.translatable(messageKey).withStyle(style -> style.withColor(0xFFFFFF));

        mc.gui.setTitle(titleComponent);
        mc.gui.setSubtitle(subtitleComponent);
        mc.gui.setTimes(10, 60, 10);
    }

    public static void clearCombatState(LocalPlayer player) {
        IN_COMBAT.remove(player.getUUID());
    }
}