package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.NarratorBossBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class KrampusTransformation {
    private static boolean transforming = false;
    private static int transformationStep = 0;
    private static final int MAX_STEPS = 10;
    private static long lastTransformationTime = 0;
    private static final long TRANSFORMATION_INTERVAL = 5000;
    
    private static final String[] GALACTIC_CHARS = {
            "\u15D0", "\u15D1", "\u15D2", "\u15D3", "\u15D4", "\u15D5", "\u15D6", "\u15D7",
            "\u15D8", "\u15D9", "\u15DA", "\u15DB", "\u15DC", "\u15DD", "\u15DE", "\u15DF",
            "\u15E0", "\u15E1", "\u15E2", "\u15E3", "\u15E4", "\u15E5", "\u15E6", "\u15E7",
            "\u15E8", "\u15E9"
    };

    public static void startTransformation() {
        if (!transforming) {
            transforming = true;
            transformationStep = 0;
            lastTransformationTime = System.currentTimeMillis();
            TheUnwrittenParable.LOGGER.info("Krampus transformation started!");
        }
    }

    public static void stopTransformation() {
        transforming = false;
        transformationStep = 0;
    }

    public static boolean isTransforming() {
        return transforming;
    }

    public static void update() {
        if (!transforming) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTransformationTime < TRANSFORMATION_INTERVAL) return;
        lastTransformationTime = currentTime;

        transformationStep++;
        TheUnwrittenParable.LOGGER.info("Krampus transformation step: " + transformationStep);

        if (transformationStep >= MAX_STEPS) {
            completeTransformation();
        }
    }

    public static String getTransformedName() {
        String baseName = "Father Christmas";
        if (!transforming) return baseName;

        int galacticCount = (int) Math.floor((double) transformationStep / MAX_STEPS * baseName.length());
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < baseName.length(); i++) {
            if (i < galacticCount) {
                char c = Character.toLowerCase(baseName.charAt(i));
                int index = c - 'a';
                if (index >= 0 && index < GALACTIC_CHARS.length) {
                    result.append(GALACTIC_CHARS[index]);
                } else {
                    result.append(baseName.charAt(i));
                }
            } else {
                result.append(baseName.charAt(i));
            }
        }
        
        return result.toString();
    }

    public static int getTransformationColor() {
        if (!transforming) return 0x55FF55;
        
        double progress = (double) transformationStep / MAX_STEPS;
        if (progress >= 1.0) return 0xFFD700;
        if (progress >= 0.8) return 0xFFAA00;
        if (progress >= 0.6) return 0xFF8800;
        if (progress >= 0.4) return 0xFF5555;
        if (progress >= 0.2) return 0xFF0000;
        return 0x55FF55;
    }

    public static BossEvent.BossBarColor getBossBarColor() {
        if (!transforming) return BossEvent.BossBarColor.GREEN;
        
        double progress = (double) transformationStep / MAX_STEPS;
        if (progress >= 0.8) return BossEvent.BossBarColor.YELLOW;
        if (progress >= 0.4) return BossEvent.BossBarColor.RED;
        return BossEvent.BossBarColor.RED;
    }

    private static void completeTransformation() {
        TheUnwrittenParable.LOGGER.info("Krampus transformation complete!");
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        LocalPlayer player = mc.player;
        int karma = ChristmasKarma.getKarma();
        int krampusHealth = calculateKrampusHealth(karma);

        player.sendSystemMessage(Component.literal("\u00A74\u00A7l[KRAMPUS HAS AWAKENED]"));
        player.sendSystemMessage(Component.literal("\u00A74Your naughtiness has summoned Krampus!"));
        player.sendSystemMessage(Component.literal("\u00A74Krampus Health: " + krampusHealth));

        String krampusName = "\u00A74\u00A7lKrampus";
        NarratorBossBar.showBossBar(krampusName, 300000, () -> {}, 0xFF0000, true, BossEvent.BossBarColor.RED);

        transforming = false;
        transformationStep = 0;
    }

    private static int calculateKrampusHealth(int karma) {
        int baseHealth = 100;
        int maxHealth = 500;
        int healthRange = maxHealth - baseHealth;
        double karmaFactor = (100.0 - karma) / 100.0;
        return (int) (baseHealth + (healthRange * karmaFactor));
    }

    public static double getTransformationProgress() {
        return (double) transformationStep / MAX_STEPS;
    }
}