package com.unwrittenparable.narrator;

import com.unwrittenparable.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class NarratorManager {
    private static final Map<UUID, Long> LAST_MESSAGE_TIME = new HashMap<>();
    private static final Map<UUID, Boolean> IN_COMBAT = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    private static final String[] GALACTIC_CHARS = {
            "\u15D0", "\u15D1", "\u15D2", "\u15D3", "\u15D4", "\u15D5", "\u15D6", "\u15D7",
            "\u15D8", "\u15D9", "\u15DA", "\u15DB", "\u15DC", "\u15DD", "\u15DE", "\u15DF",
            "\u15E0", "\u15E1", "\u15E2", "\u15E3", "\u15E4", "\u15E5", "\u15E6", "\u15E7",
            "\u15E8", "\u15E9"
    };

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
        boolean isHerobrine = false;

        if (data.isMultiplayer()) {
            narratorName = data.getMultiplayerNarrator();
            messageKey = NarratorMessages.getMultiplayerMessage(category, narratorName);
            isHerobrine = narratorName.equals("Herobrine");
        } else {
            NarratorType type = data.getNarratorType();
            narratorName = type.getDisplayName();
            messageKey = NarratorMessages.getMessage(type, category);
        }

        if (messageKey != null && !messageKey.isEmpty()) {
            showBossBarThenMessage(player, narratorName, messageKey, data.isMultiplayer(), isHerobrine);
        }
    }

    private static void showBossBarThenMessage(LocalPlayer player, String narratorName, String messageKey, boolean isMultiplayer, boolean isHerobrine) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String displayText;
        if (isHerobrine) {
            displayText = convertToGalactic(narratorName);
        } else if (isMultiplayer) {
            displayText = narratorName;
        } else {
            displayText = I18n.get("narrator." + narratorName.toLowerCase() + ".name");
        }

        mc.gui.setTitle(Component.literal(displayText).withStyle(style -> style.withColor(isHerobrine ? 0xAA00AA : 0xFF5555)));
        mc.gui.setSubtitle(Component.literal("..."));
        mc.gui.setTimes(5, 20, 5);

        SCHEDULER.schedule(() -> {
            Minecraft.getInstance().execute(() -> {
                showTitleMessage(player, narratorName, messageKey, isMultiplayer, isHerobrine);
            });
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private static void showTitleMessage(LocalPlayer player, String narratorName, String messageKey, boolean isMultiplayer, boolean isHerobrine) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        Component titleComponent;
        Component subtitleComponent;

        if (isHerobrine) {
            titleComponent = Component.literal(convertToGalactic(narratorName)).withStyle(style -> style.withColor(0xAA00AA));

            String translatedMsg = I18n.get(messageKey);
            String galacticMsg = convertToGalactic(translatedMsg);
            String cutOffMsg = cutOffText(galacticMsg, 0.4f);
            subtitleComponent = Component.literal(cutOffMsg).withStyle(style -> style.withColor(0xAA00AA));
        } else if (isMultiplayer) {
            titleComponent = Component.literal(narratorName).withStyle(style -> style.withColor(0xFF5555));
            subtitleComponent = Component.translatable(messageKey).withStyle(style -> style.withColor(0xFFFFFF));
        } else {
            titleComponent = Component.translatable("narrator." + narratorName.toLowerCase() + ".name").withStyle(style -> style.withColor(0xFF5555));
            subtitleComponent = Component.translatable(messageKey).withStyle(style -> style.withColor(0xFFFFFF));
        }

        mc.gui.setTitle(titleComponent);
        mc.gui.setSubtitle(subtitleComponent);
        mc.gui.setTimes(10, 60, 10);

        if (mc.player != null) {
            mc.player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.0f);
        }
    }

    private static String convertToGalactic(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toLowerCase().toCharArray()) {
            int index = c - 'a';
            if (index >= 0 && index < GALACTIC_CHARS.length) {
                result.append(GALACTIC_CHARS[index]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static String cutOffText(String text, float percentage) {
        int cutOffIndex = (int) (text.length() * percentage);
        if (cutOffIndex >= text.length()) {
            return text;
        }
        return text.substring(0, cutOffIndex) + "...";
    }

    public static void clearCombatState(LocalPlayer player) {
        IN_COMBAT.remove(player.getUUID());
    }
}