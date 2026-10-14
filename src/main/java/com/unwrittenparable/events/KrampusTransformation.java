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
    private static boolean krampusAwakened = false;
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
        if (!transforming && !krampusAwakened) {
            transforming = true;
            transformationStep = 0;
            lastTransformationTime = System.currentTimeMillis();
            TheUnwrittenParable.LOGGER.info("Krampus transformation started!");
        }
    }

    public static void stopTransformation() {
        transforming = false;
        krampusAwakened = false;
        transformationStep = 0;
    }

    public static boolean isTransforming() {
        return transforming;
    }

    public static boolean isKrampusAwakened() {
        return krampusAwakened;
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
        if (krampusAwakened) return "Krampus";
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
        if (krampusAwakened) return 0xFFD700;
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
        if (krampusAwakened) return BossEvent.BossBarColor.YELLOW;
        if (!transforming) return BossEvent.BossBarColor.GREEN;
        
        double progress = (double) transformationStep / MAX_STEPS;
        if (progress >= 0.8) return BossEvent.BossBarColor.YELLOW;
        if (progress >= 0.4) return BossEvent.BossBarColor.RED;
        return BossEvent.BossBarColor.RED;
    }

    public static void animateTransformation(LocalPlayer player) {
        if (!transforming) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        String baseName = "Father Christmas";
        int galacticCount = (int) Math.floor((double) transformationStep / MAX_STEPS * baseName.length());
        int color = getTransformationColor();

        for (int i = 0; i <= baseName.length(); i++) {
            final int index = i;
            final int finalColor = color;
            
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                Minecraft.getInstance().execute(() -> {
                    if (mc.gui == null) return;
                    
                    StringBuilder partialName = new StringBuilder();
                    for (int j = 0; j < baseName.length(); j++) {
                        if (j < index && j < galacticCount) {
                            char c = Character.toLowerCase(baseName.charAt(j));
                            int charIndex = c - 'a';
                            if (charIndex >= 0 && charIndex < GALACTIC_CHARS.length) {
                                partialName.append(GALACTIC_CHARS[charIndex]);
                            } else {
                                partialName.append(baseName.charAt(j));
                            }
                        } else if (j < index) {
                            partialName.append(baseName.charAt(j));
                        }
                    }
                    
                    mc.gui.setTitle(Component.literal(partialName.toString()).withStyle(style -> style.withColor(finalColor)));
                    mc.gui.setSubtitle(Component.literal("...").withStyle(style -> style.withColor(0xFFFFFF)));
                    mc.gui.setTimes(0, 10, 0);
                });
            }, i * 100, TimeUnit.MILLISECONDS);
            
            scheduler.schedule(() -> {
                scheduler.shutdown();
            }, i * 100 + 200, TimeUnit.MILLISECONDS);
        }
    }

    private static void completeTransformation() {
        TheUnwrittenParable.LOGGER.info("Krampus transformation complete!");
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        LocalPlayer player = mc.player;
        int karma = ChristmasKarma.getKarma();
        int krampusHealth = calculateKrampusHealth(karma);

        player.sendSystemMessage(Component.literal("\u00A76\u00A7l[KRAMPUS HAS AWAKENED]"));
        player.sendSystemMessage(Component.literal("\u00A76Your naughtiness has summoned Krampus!"));
        player.sendSystemMessage(Component.literal("\u00A76Krampus Health: " + krampusHealth));

        String krampusName = "\u00A76\u00A7lKrampus";
        NarratorBossBar.showBossBar(krampusName, 300000, () -> {}, 0xFFD700, true, BossEvent.BossBarColor.YELLOW);

        transforming = false;
        krampusAwakened = true;
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
        if (krampusAwakened) return 1.0;
        return (double) transformationStep / MAX_STEPS;
    }
}