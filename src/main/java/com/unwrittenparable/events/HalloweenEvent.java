package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.ClientNarratorData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class HalloweenEvent {
    private static boolean halloweenActive = false;
    private static boolean halloweenInitialized = false;
    private static long lastRoastTime = 0;
    private static final long ROAST_INTERVAL = 60000;

    private static final String[] ROAST_KEYS = {
            "halloween.roast.1",
            "halloween.roast.2",
            "halloween.roast.3",
            "halloween.roast.4",
            "halloween.roast.5",
            "halloween.roast.6",
            "halloween.roast.7",
            "halloween.roast.8",
            "halloween.roast.9",
            "halloween.roast.10",
            "halloween.roast.11",
            "halloween.roast.12",
            "halloween.roast.13",
            "halloween.roast.14",
            "halloween.roast.15",
            "halloween.roast.16",
            "halloween.roast.17",
            "halloween.roast.18",
            "halloween.roast.19",
            "halloween.roast.20"
    };

    public static boolean isHalloween() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.OCTOBER && now.getDayOfMonth() == 31;
    }

    public static boolean isHalloweenSeason() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.OCTOBER && now.getDayOfMonth() >= 25;
    }

    public static void checkHalloween(LocalPlayer player) {
        if (!halloweenInitialized) {
            halloweenInitialized = true;
            if (isHalloween()) {
                halloweenActive = true;
                activateHalloween(player);
            } else if (isHalloweenSeason()) {
                halloweenActive = true;
                activateHalloweenSeason(player);
            }
        }

        if (halloweenActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRoastTime > ROAST_INTERVAL) {
                lastRoastTime = currentTime;
                sendHerobrineRoast(player);
            }
        }
    }

    private static void activateHalloween(LocalPlayer player) {
        TheUnwrittenParable.LOGGER.info("Halloween event activated!");

        ClientNarratorData data = ClientNarratorData.get();
        data.setMultiplayer(true);

        player.sendSystemMessage(Component.literal("§4§l[HEROBRINE.EXE HAS BEEN FOUND]"));
        player.sendSystemMessage(Component.literal("§4" + I18n.get("halloween.activate.1")));
        player.sendSystemMessage(Component.literal("§4" + I18n.get("halloween.activate.2")));
    }

    private static void activateHalloweenSeason(LocalPlayer player) {
        TheUnwrittenParable.LOGGER.info("Halloween season activated!");

        player.sendSystemMessage(Component.literal("§4[HEROBRINE.EXE IS APPROACHING]"));
        player.sendSystemMessage(Component.literal("§4" + I18n.get("halloween.season")));
    }

    private static void sendHerobrineRoast(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();

        String roastKey = ROAST_KEYS[new Random().nextInt(ROAST_KEYS.length)];
        String roast = I18n.get(roastKey);

        mc.gui.setTitle(Component.literal("§4HEROBRINE").withStyle(style -> style.withColor(0xAA00AA)));
        mc.gui.setSubtitle(Component.literal("§4" + roast).withStyle(style -> style.withColor(0xAA00AA)));
        mc.gui.setTimes(10, 100, 10);

        if (mc.player != null) {
            mc.player.playNotifySound(net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, net.minecraft.sounds.SoundSource.AMBIENT, 0.5f, 0.5f);
        }
    }

    public static boolean isHalloweenActive() {
        return halloweenActive;
    }

    public static void deactivateHalloween() {
        halloweenActive = false;
    }

    public static void toggleHalloween() {
        halloweenActive = !halloweenActive;
        if (halloweenActive) {
            halloweenInitialized = true;
        }
    }
}