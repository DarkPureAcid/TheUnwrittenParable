package com.unwrittenparable.narrator;

import com.unwrittenparable.TheUnwrittenParable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class NarratorBossBar {
    private static boolean active = false;
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> currentTask = null;

    public static void showBossBar(String narratorName, int durationMs, Runnable onComplete) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return;

        removeBossBar();

        active = true;

        mc.gui.setTitle(Component.literal(narratorName).withStyle(style -> style.withColor(0xFF5555)));
        mc.gui.setSubtitle(Component.literal("..."));
        mc.gui.setTimes(5, durationMs / 50, 5);

        currentTask = scheduler.schedule(() -> {
            Minecraft.getInstance().execute(() -> {
                active = false;
                if (onComplete != null) {
                    onComplete.run();
                }
            });
        }, durationMs, TimeUnit.MILLISECONDS);
    }

    public static void removeBossBar() {
        if (currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
        active = false;
    }

    public static boolean isActive() {
        return active;
    }
}