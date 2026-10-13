package com.unwrittenparable.narrator;

import com.unwrittenparable.TheUnwrittenParable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class NarratorBossBar {
    private static UUID bossBarId = null;
    private static boolean active = false;
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> currentTask = null;

    public static void showBossBar(String narratorName, int durationMs, Runnable onComplete) {
        showBossBar(narratorName, durationMs, onComplete, 0xFF5555, true, BossEvent.BossBarColor.RED);
    }

    public static void showBossBar(String narratorName, int durationMs, Runnable onComplete, int color) {
        showBossBar(narratorName, durationMs, onComplete, color, true, BossEvent.BossBarColor.RED);
    }

    public static void showBossBar(String narratorName, int durationMs, Runnable onComplete, int color, boolean deplete) {
        showBossBar(narratorName, durationMs, onComplete, color, deplete, BossEvent.BossBarColor.RED);
    }

    public static void showBossBar(String narratorName, int durationMs, Runnable onComplete, int color, boolean deplete, BossEvent.BossBarColor barColor) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return;

        removeBossBar();

        bossBarId = UUID.randomUUID();
        active = true;

        try {
            BossHealthOverlay overlay = mc.gui.getBossOverlay();

            LerpingBossEvent bossEvent = new LerpingBossEvent(
                    bossBarId,
                    Component.literal(narratorName).withStyle(style -> style.withColor(color)),
                    1.0f,
                    barColor,
                    BossEvent.BossBarOverlay.PROGRESS,
                    false,
                    false,
                    false
            );

            Field[] fields = BossHealthOverlay.class.getDeclaredFields();
            for (Field field : fields) {
                TheUnwrittenParable.LOGGER.debug("BossHealthOverlay field: " + field.getName() + " type: " + field.getType().getName());
                if (field.getType() == Map.class) {
                    field.setAccessible(true);
                    Map<UUID, LerpingBossEvent> events = (Map<UUID, LerpingBossEvent>) field.get(overlay);
                    events.put(bossBarId, bossEvent);
                    TheUnwrittenParable.LOGGER.info("Boss bar added to field: " + field.getName());
                    break;
                }
            }

            if (deplete) {
                int totalTicks = durationMs / 50;
                float decrementPerTick = 1.0f / totalTicks;

                currentTask = scheduler.scheduleAtFixedRate(() -> {
                    Minecraft.getInstance().execute(() -> {
                        if (!active) return;

                        float progress = bossEvent.getProgress() - decrementPerTick;
                        if (progress <= 0) {
                            removeBossBar();
                            if (onComplete != null) {
                                onComplete.run();
                            }
                            return;
                        }

                        bossEvent.setProgress(progress);
                    });
                }, 0, 50, TimeUnit.MILLISECONDS);
            } else {
                TheUnwrittenParable.LOGGER.info("Boss bar created without depletion (Christmas mode)");
            }

        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.error("Failed to create boss bar", e);
            active = false;
        }
    }

    public static void removeBossBar() {
        if (currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }

        if (bossBarId != null) {
            try {
                Minecraft mc = Minecraft.getInstance();
                BossHealthOverlay overlay = mc.gui.getBossOverlay();

                Field[] fields = BossHealthOverlay.class.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == Map.class) {
                        field.setAccessible(true);
                        Map<UUID, LerpingBossEvent> events = (Map<UUID, LerpingBossEvent>) field.get(overlay);
                        events.remove(bossBarId);
                        TheUnwrittenParable.LOGGER.info("Boss bar removed from field: " + field.getName());
                        break;
                    }
                }
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.debug("Failed to remove boss bar", e);
            }
            bossBarId = null;
        }

        active = false;
    }

    public static boolean isActive() {
        return active;
    }
}