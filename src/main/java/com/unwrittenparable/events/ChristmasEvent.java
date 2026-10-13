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
public class ChristmasEvent {
    private static boolean christmasActive = false;
    private static boolean christmasInitialized = false;
    private static long lastMessageTime = 0;
    private static final long MESSAGE_INTERVAL = 120000;

    private static final String[] FATHER_CHRISTMAS_MESSAGES = {
            "fatherchristmas.greeting.1",
            "fatherchristmas.greeting.2",
            "fatherchristmas.greeting.3",
            "fatherchristmas.greeting.4",
            "fatherchristmas.greeting.5"
    };

    public static boolean isChristmas() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.DECEMBER && now.getDayOfMonth() == 25;
    }

    public static boolean isChristmasSeason() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 1 && now.getDayOfMonth() <= 25;
    }

    public static void checkChristmas(LocalPlayer player) {
        if (!christmasInitialized) {
            christmasInitialized = true;
            if (isChristmas()) {
                christmasActive = true;
                activateChristmas(player);
            } else if (isChristmasSeason()) {
                christmasActive = true;
                activateChristmasSeason(player);
            }
        }

        if (christmasActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMessageTime > MESSAGE_INTERVAL) {
                lastMessageTime = currentTime;
                sendFatherChristmasMessage(player);
            }
        }
    }

    private static void activateChristmas(LocalPlayer player) {
        TheUnwrittenParable.LOGGER.info("Christmas event activated!");

        ClientNarratorData data = ClientNarratorData.get();
        data.setMultiplayer(false);

        player.sendSystemMessage(Component.literal("\u00A7a\u00A7l[FATHER CHRISTMAS HAS ARRIVED]"));
        player.sendSystemMessage(Component.literal("\u00A7a" + I18n.get("fatherchristmas.christmas_day.1", player.getName().getString())));
        player.sendSystemMessage(Component.literal("\u00A7a" + I18n.get("fatherchristmas.christmas_day.2")));
    }

    private static void activateChristmasSeason(LocalPlayer player) {
        TheUnwrittenParable.LOGGER.info("Christmas season activated!");

        player.sendSystemMessage(Component.literal("\u00A7a[FATHER CHRISTMAS IS COMING]"));
        player.sendSystemMessage(Component.literal("\u00A7a" + I18n.get("fatherchristmas.season.1")));
    }

    private static void sendFatherChristmasMessage(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();

        String messageKey = FATHER_CHRISTMAS_MESSAGES[new Random().nextInt(FATHER_CHRISTMAS_MESSAGES.length)];
        String message = I18n.get(messageKey, player.getName().getString());

        mc.gui.setTitle(Component.literal("\u00A7aFather Christmas").withStyle(style -> style.withColor(0x55FF55)));
        mc.gui.setSubtitle(Component.literal("\u00A7a" + message).withStyle(style -> style.withColor(0xFFFFFF)));
        mc.gui.setTimes(10, 100, 10);

        if (mc.player != null) {
            mc.player.playNotifySound(net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME, net.minecraft.sounds.SoundSource.AMBIENT, 0.5f, 1.2f);
        }
    }

    public static boolean isChristmasActive() {
        return christmasActive;
    }

    public static void deactivateChristmas() {
        christmasActive = false;
        lastMessageTime = 0;
    }

    public static void toggleChristmas() {
        christmasActive = !christmasActive;
        if (christmasActive) {
            christmasInitialized = true;
            lastMessageTime = 0;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                activateChristmasSeason(mc.player);
            }
        } else {
            lastMessageTime = 0;
        }
    }
}