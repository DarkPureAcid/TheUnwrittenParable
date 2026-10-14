package com.unwrittenparable.narrator;

import com.unwrittenparable.config.ModConfig;
import com.unwrittenparable.events.ChristmasEvent;
import com.unwrittenparable.events.ChristmasKarma;
import com.unwrittenparable.events.HalloweenEvent;
import com.unwrittenparable.events.KrampusTransformation;
import com.unwrittenparable.integration.EnigmaticLegacyIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
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

    private static final int MAX_TITLE_WIDTH = 200;

    public static void sendMessage(LocalPlayer player, String category) {
        if (!ModConfig.ENABLE_NARRATOR.get()) return;
        if (!NarratorGameRule.get().isNarratorEnabled()) return;

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
            sendNarratorMessage(player, category);
            return;
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
        if (!NarratorGameRule.get().isNarratorEnabled()) return;

        if (category.startsWith("narrator.command.") || category.startsWith("narrator.mod.")) {
            if (!NarratorGameRule.get().isCommandReactionsEnabled()) return;
            sendDirectMessage(player, category);
        } else if (category.startsWith("narrator.action.")) {
            sendDirectMessage(player, category);
        } else if (category.equals("achievement")) {
            if (!NarratorGameRule.get().isAchievementsEnabled()) return;
            sendNarratorMessage(player, category);
        } else if (category.equals("gamemode_change")) {
            sendGamemodeChangeMessage(player);
        } else {
            sendNarratorMessage(player, category);
        }
    }

    private static void sendGamemodeChangeMessage(LocalPlayer player) {
        ClientNarratorData data = ClientNarratorData.get();
        String narratorName;
        boolean isHerobrine = false;

        if (data.isMultiplayer()) {
            narratorName = data.getMultiplayerNarrator();
            isHerobrine = narratorName.equals("Herobrine");
        } else {
            NarratorType type = data.getNarratorType();
            narratorName = type.getDisplayName();
        }

        String[] messages = {
                "narrator.gamemode_change.1",
                "narrator.gamemode_change.2",
                "narrator.gamemode_change.3",
                "narrator.gamemode_change.4"
        };

        String messageKey = messages[RANDOM.nextInt(messages.length)];
        showNarratorSequence(player, narratorName, messageKey, data.isMultiplayer(), isHerobrine);
    }

    private static void sendDirectMessage(LocalPlayer player, String messageKey) {
        ClientNarratorData data = ClientNarratorData.get();
        String narratorName;
        boolean isHerobrine = false;

        if (data.isMultiplayer()) {
            narratorName = data.getMultiplayerNarrator();
            isHerobrine = narratorName.equals("Herobrine");
        } else {
            NarratorType type = data.getNarratorType();
            narratorName = type.getDisplayName();
        }

        showNarratorSequence(player, narratorName, messageKey, data.isMultiplayer(), isHerobrine);
    }

    public static void testSpecificNarrator(LocalPlayer player, String narratorName, String category) {
        String messageKey;
        boolean isHerobrine = false;

        if (narratorName.equals("Herobrine") || narratorName.equals("Notch") || narratorName.equals("Chuck Norris")) {
            messageKey = NarratorMessages.getMultiplayerMessage(category, narratorName);
            isHerobrine = narratorName.equals("Herobrine");
        } else {
            try {
                NarratorType type = NarratorType.valueOf(narratorName.toUpperCase().replace(" ", "_"));
                messageKey = NarratorMessages.getMessage(type, category);
            } catch (IllegalArgumentException e) {
                player.sendSystemMessage(Component.literal("Unknown narrator: " + narratorName));
                player.sendSystemMessage(Component.literal("Valid narrators: Stanley, Nigel, Malcolm, Edgar, Chester, Reginald, Herobrine, Notch, Chuck Norris"));
                return;
            }
        }

        if (messageKey != null && !messageKey.isEmpty()) {
            boolean isMultiplayer = narratorName.equals("Herobrine") || narratorName.equals("Notch") || narratorName.equals("Chuck Norris");
            showNarratorSequence(player, narratorName, messageKey, isMultiplayer, isHerobrine);
        }
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
            showNarratorSequence(player, narratorName, messageKey, data.isMultiplayer(), isHerobrine);
        }
    }

    private static void showNarratorSequence(LocalPlayer player, String narratorName, String messageKey, boolean isMultiplayer, boolean isHerobrine) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String displayName;
        boolean isChristmas = ChristmasEvent.isChristmasActive();
        boolean isHalloween = HalloweenEvent.isHalloweenActive();

        if (isChristmas) {
            if (KrampusTransformation.isKrampusAwakened()) {
                displayName = "\u00A76\u00A7lKrampus";
            } else if (KrampusTransformation.isTransforming()) {
                displayName = KrampusTransformation.getTransformedName();
            } else {
                String listStatus = ChristmasKarma.getListStatus();
                displayName = "Father Christmas (" + listStatus + " list)";
                
                if (ChristmasKarma.isOnNaughtyList() && !KrampusTransformation.isTransforming()) {
                    KrampusTransformation.startTransformation();
                }
            }
        } else if (isHalloween) {
            displayName = "Herobrine";
        } else if (isHerobrine) {
            displayName = narratorName;
        } else if (isMultiplayer) {
            displayName = narratorName;
        } else {
            displayName = I18n.get("narrator." + narratorName.toLowerCase() + ".name");
        }

        displayName = limitTextWidth(mc, displayName);

        if (isChristmas) {
            int karmaColor;
            BossEvent.BossBarColor barColor;
            
            if (KrampusTransformation.isKrampusAwakened()) {
                karmaColor = 0xFFD700;
                barColor = BossEvent.BossBarColor.YELLOW;
            } else if (KrampusTransformation.isTransforming()) {
                karmaColor = KrampusTransformation.getTransformationColor();
                barColor = KrampusTransformation.getBossBarColor();
            } else {
                karmaColor = ChristmasKarma.getKarmaColor();
                barColor = ChristmasKarma.isOnNiceList() ? BossEvent.BossBarColor.GREEN : 
                           ChristmasKarma.isOnNaughtyList() ? BossEvent.BossBarColor.RED : BossEvent.BossBarColor.YELLOW;
            }
            NarratorBossBar.showBossBar(displayName, 180000, () -> {}, karmaColor, false, barColor);
        } else if (isHalloween) {
            NarratorBossBar.showBossBar(displayName, 180000, () -> {}, 0xAA00AA, true, BossEvent.BossBarColor.PURPLE);
        } else {
            NarratorBossBar.showBossBar(displayName, 180000, () -> {});
        }

        if (mc.player != null) {
            mc.player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.0f);
        }

        SCHEDULER.schedule(() -> {
            Minecraft.getInstance().execute(() -> {
                if (isChristmas) {
                    showChristmasMessage(player, messageKey);
                } else if (isHalloween) {
                    showHerobrineMessage(player, "Herobrine", messageKey);
                } else if (isHerobrine) {
                    animateHerobrineName(player, narratorName, messageKey);
                } else {
                    showTitleMessage(player, narratorName, messageKey, isMultiplayer, isHerobrine);
                }
            });
        }, 1500, TimeUnit.MILLISECONDS);
    }

    private static void animateHerobrineName(LocalPlayer player, String narratorName, String messageKey) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String galacticName = convertToGalactic(narratorName);
        int color = 0xAA00AA;

        for (int i = 0; i <= galacticName.length(); i++) {
            final int index = i;
            SCHEDULER.schedule(() -> {
                Minecraft.getInstance().execute(() -> {
                    if (mc.gui == null) return;
                    String partialName = galacticName.substring(0, index);
                    mc.gui.setTitle(Component.literal(partialName).withStyle(style -> style.withColor(color)));
                    mc.gui.setSubtitle(Component.literal("..."));
                    mc.gui.setTimes(0, 10, 0);
                });
            }, i * 100, TimeUnit.MILLISECONDS);
        }

        SCHEDULER.schedule(() -> {
            Minecraft.getInstance().execute(() -> {
                showHerobrineMessage(player, narratorName, messageKey);
            });
        }, galacticName.length() * 100 + 500, TimeUnit.MILLISECONDS);
    }

    private static void showHerobrineMessage(LocalPlayer player, String narratorName, String messageKey) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String playerName = player.getName().getString();
        String galacticName = convertToGalactic(narratorName);
        String translatedMsg = I18n.get(messageKey, playerName);
        String galacticMsg = convertToGalactic(translatedMsg);
        String limitedMsg = limitTextWidth(mc, galacticMsg);

        mc.gui.setTitle(Component.literal(galacticName).withStyle(style -> style.withColor(0xAA00AA)));
        mc.gui.setSubtitle(Component.literal(limitedMsg).withStyle(style -> style.withColor(0xAA00AA)));
        mc.gui.setTimes(10, 60, 10);

        if (mc.player != null) {
            mc.player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.0f);
        }
    }

    private static void showTitleMessage(LocalPlayer player, String narratorName, String messageKey, boolean isMultiplayer, boolean isHerobrine) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String playerName = player.getName().getString();
        Component titleComponent;
        Component subtitleComponent;

        if (isHerobrine) {
            String galacticName = limitTextWidth(mc, convertToGalactic(narratorName));
            titleComponent = Component.literal(galacticName).withStyle(style -> style.withColor(0xAA00AA));

            String translatedMsg = I18n.get(messageKey, playerName);
            String galacticMsg = convertToGalactic(translatedMsg);
            String limitedMsg = limitTextWidth(mc, galacticMsg);
            subtitleComponent = Component.literal(limitedMsg).withStyle(style -> style.withColor(0xAA00AA));
        } else if (isMultiplayer) {
            String limitedName = limitTextWidth(mc, narratorName);
            titleComponent = Component.literal(limitedName).withStyle(style -> style.withColor(0xFF5555));

            String translatedMsg = I18n.get(messageKey, playerName);
            String limitedMsg = limitTextWidth(mc, translatedMsg);
            subtitleComponent = Component.literal(limitedMsg).withStyle(style -> style.withColor(0xFFFFFF));
        } else {
            String displayName = I18n.get("narrator." + narratorName.toLowerCase() + ".name");
            String limitedName = limitTextWidth(mc, displayName);
            titleComponent = Component.literal(limitedName).withStyle(style -> style.withColor(0xFF5555));

            String translatedMsg = I18n.get(messageKey, playerName);
            String limitedMsg = limitTextWidth(mc, translatedMsg);
            subtitleComponent = Component.literal(limitedMsg).withStyle(style -> style.withColor(0xFFFFFF));
        }

        mc.gui.setTitle(titleComponent);
        mc.gui.setSubtitle(subtitleComponent);
        mc.gui.setTimes(10, 60, 10);

        if (mc.player != null) {
            mc.player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.0f);
        }

        if (ModConfig.ENABLE_DORMANT_EYE_TTS.get() && EnigmaticLegacyIntegration.isTTSAvailable()) {
            String translatedMsg = I18n.get(messageKey, playerName);
            EnigmaticLegacyIntegration.speakWithDormantEye(translatedMsg);
        }
    }

    private static void showChristmasMessage(LocalPlayer player, String messageKey) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String playerName = player.getName().getString();
        String translatedMsg = I18n.get(messageKey, playerName);
        String limitedMsg = limitTextWidth(mc, translatedMsg);

        if (KrampusTransformation.isTransforming()) {
            KrampusTransformation.animateTransformation(player);
            
            SCHEDULER.schedule(() -> {
                Minecraft.getInstance().execute(() -> {
                    if (mc.gui == null) return;
                    Component subtitleComponent = Component.literal(limitedMsg).withStyle(style -> style.withColor(0xFFFFFF));
                    mc.gui.setSubtitle(subtitleComponent);
                    mc.gui.setTimes(10, 60, 10);
                });
            }, 1600, TimeUnit.MILLISECONDS);
        } else {
            String listStatus = ChristmasKarma.getListStatus();
            String displayName = "Father Christmas (" + listStatus + " list)";
            int karmaColor = ChristmasKarma.getKarmaColor();
            
            Component titleComponent = Component.literal(displayName).withStyle(style -> style.withColor(karmaColor));
            Component subtitleComponent = Component.literal(limitedMsg).withStyle(style -> style.withColor(0xFFFFFF));

            mc.gui.setTitle(titleComponent);
            mc.gui.setSubtitle(subtitleComponent);
            mc.gui.setTimes(10, 60, 10);
        }

        if (mc.player != null) {
            mc.player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.2f);
        }
    }

    private static String limitTextWidth(Minecraft mc, String text) {
        Font font = mc.font;
        int guiScale = mc.options.guiScale().get();
        int screenWidth = mc.getWindow().getGuiScaledWidth();

        int maxPixelWidth;
        if (guiScale == 0) {
            maxPixelWidth = screenWidth / 2;
        } else {
            maxPixelWidth = Math.min(screenWidth - 40, MAX_TITLE_WIDTH * guiScale);
        }

        if (maxPixelWidth < 100) {
            maxPixelWidth = 100;
        }

        if (font.width(text) > maxPixelWidth) {
            String truncated = text;
            while (font.width(truncated + "...") > maxPixelWidth && truncated.length() > 0) {
                truncated = truncated.substring(0, truncated.length() - 1);
            }
            return truncated + "...";
        }

        return text;
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

    public static void clearCombatState(LocalPlayer player) {
        IN_COMBAT.remove(player.getUUID());
    }
}