package com.unwrittenparable.narrator;

import com.unwrittenparable.TheUnwrittenParable;
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
public class NarratorFight {
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static boolean fightInProgress = false;
    private static ScheduledFuture<?> currentFight = null;

    private static final String[] FIGHT_MESSAGES = {
            "narrator.fight.start",
            "narrator.fight.punch1",
            "narrator.fight.punch2",
            "narrator.fight.punch3",
            "narrator.fight.punch4",
            "narrator.fight.punch5",
            "narrator.fight.punch6",
            "narrator.fight.punch7",
            "narrator.fight.punch8",
            "narrator.fight.punch9",
            "narrator.fight.punch10",
            "narrator.fight.end"
    };

    public static void startFight(LocalPlayer player, String narrator1, String narrator2) {
        if (fightInProgress) {
            player.sendSystemMessage(Component.literal("A fight is already in progress!"));
            return;
        }

        fightInProgress = true;
        player.sendSystemMessage(Component.literal(narrator1 + " vs " + narrator2 + "!"));

        currentFight = SCHEDULER.schedule(() -> {
            try {
                runFightSequence(player, narrator1, narrator2);
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.error("Fight sequence error", e);
            } finally {
                fightInProgress = false;
            }
        }, 0, TimeUnit.SECONDS);
    }

    private static void runFightSequence(LocalPlayer player, String narrator1, String narrator2) throws InterruptedException {
        Random random = new Random();
        String[] fighters = {narrator1, narrator2};

        for (int i = 0; i < FIGHT_MESSAGES.length; i++) {
            if (!fightInProgress) break;

            String fighter = fighters[i % 2];
            String messageKey = FIGHT_MESSAGES[i];

            String translatedMsg = I18n.get(messageKey, fighter);

            if (i == 0 || i == FIGHT_MESSAGES.length - 1) {
                showFightTitle(player, fighter, translatedMsg, true);
            } else {
                showFightTitle(player, fighter, translatedMsg, false);
            }

            int delay;
            if (i == 0) {
                delay = 2000;
            } else if (i == FIGHT_MESSAGES.length - 1) {
                delay = 3000;
            } else {
                delay = 1000 + random.nextInt(1000);
            }

            Thread.sleep(delay);
        }

        boolean chuckNorrisWins = random.nextBoolean();
        String winner;

        if (narrator1.equals("Chuck Norris") || narrator2.equals("Chuck Norris")) {
            winner = chuckNorrisWins ? "Chuck Norris" : (narrator1.equals("Chuck Norris") ? narrator2 : narrator1);
        } else {
            winner = random.nextBoolean() ? narrator1 : narrator2;
        }

        String winMessage = I18n.get("narrator.fight.winner", winner);
        showFightTitle(player, winner, winMessage, true);

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.AMBIENT, 1.0f, 1.0f);
        }
    }

    private static void showFightTitle(LocalPlayer player, String narratorName, String message, boolean isImportant) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        int color = isImportant ? 0xFF5555 : 0xFFFFFF;

        mc.gui.setTitle(Component.literal(narratorName).withStyle(style -> style.withColor(0xFF5555)));
        mc.gui.setSubtitle(Component.literal(message).withStyle(style -> style.withColor(color)));
        mc.gui.setTimes(5, 40, 5);

        if (isImportant && mc.player != null) {
            mc.player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.5f, 1.0f);
        }
    }

    public static boolean isFightInProgress() {
        return fightInProgress;
    }

    public static void stopFight() {
        fightInProgress = false;
        if (currentFight != null) {
            currentFight.cancel(true);
        }
    }
}