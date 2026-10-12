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
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return;

        removeBossBar();

        bossBarId = UUID.randomUUID();
        active = true;

        try {
            BossHealthOverlay overlay = mc.gui.getBossOverlay();

            LerpingBossEvent bossEvent = new LerpingBossEvent(
                    bossBarId,
                    Component.literal(narratorName).withStyle(style -> style.withColor(0xFF5555)),
                    1.0f,
                    BossEvent.BossBarColor.RED,
                    BossEvent.BossBarOverlay.PROGRESS,
                    false,
                    false,
                    false
            );

            Field eventsField = BossHealthOverlay.class.getDeclaredField("f_93698_");
            eventsField.setAccessible(true);
            Map<UUID, LerpingBossEvent> events = (Map<UUID, LerpingBossEvent>) eventsField.get(overlay);
            events.put(bossBarId, bossEvent);

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

                Field eventsField = BossHealthOverlay.class.getDeclaredField("f_93698_");
                eventsField.setAccessible(true);
                Map<UUID, LerpingBossEvent> events = (Map<UUID, LerpingBossEvent>) eventsField.get(overlay);
                events.remove(bossBarId);
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