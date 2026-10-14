package com.unwrittenparable.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.ModScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class ChristmasKarma {
    private static final String FILE_NAME = "theunwrittenparable_karma.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ChristmasKarma INSTANCE;

    private int karma = 50;
    private static final int MIN_KARMA = 0;
    private static final int MAX_KARMA = 100;
    private static final int NEUTRAL_KARMA = 50;

    private static boolean useBornInChaosNaughtiness = false;
    private transient long lastKarmaMessage = 0;
    private static final long KARMA_MESSAGE_COOLDOWN = 30000;

    public ChristmasKarma() {
    }

    public static ChristmasKarma get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static void init() {
        useBornInChaosNaughtiness = ModScanner.isModInstalled("born_in_chaos_v1");
        TheUnwrittenParable.LOGGER.info("Christmas Karma initialized. Born in Chaos: " + useBornInChaosNaughtiness);
    }

    public static int getKarma() {
        if (useBornInChaosNaughtiness) {
            return getBornInChaosKarma();
        }
        return get().karma;
    }

    public static void addKarma(int amount) {
        if (useBornInChaosNaughtiness) return;
        ChristmasKarma data = get();
        data.karma = Math.max(MIN_KARMA, Math.min(MAX_KARMA, data.karma + amount));
        TheUnwrittenParable.LOGGER.debug("Karma changed by " + amount + ". New karma: " + data.karma);
        save();
    }

    public static void onVillagerHurt() {
        addKarma(-5);
        sendKarmaMessage("hurt");
    }

    public static void onVillagerKilled() {
        addKarma(-10);
        sendKarmaMessage("killed");
    }

    public static void onAnimalKilled() {
        addKarma(-3);
    }

    public static void onHostileMobKilled() {
        addKarma(2);
    }

    public static void onBossKilled() {
        addKarma(10);
    }

    public static void onCropPlanted() {
        addKarma(1);
    }

    public static void onAnimalBred() {
        addKarma(3);
    }

    public static boolean isOnNiceList() {
        return getKarma() >= 60;
    }

    public static boolean isOnNaughtyList() {
        return getKarma() <= 40;
    }

    public static boolean isNeutral() {
        return getKarma() > 40 && getKarma() < 60;
    }

    public static String getListStatus() {
        if (isOnNiceList()) return "Nice";
        if (isOnNaughtyList()) return "Naughty";
        return "Judging";
    }

    public static int getKarmaColor() {
        int karmaValue = getKarma();
        if (karmaValue >= 80) return 0x00AA00;
        if (karmaValue >= 60) return 0x55FF55;
        if (karmaValue >= 40) return 0xFFFF55;
        if (karmaValue >= 20) return 0xFFAA00;
        return 0xFF5555;
    }

    private static int getBornInChaosKarma() {
        return NEUTRAL_KARMA;
    }

    private static void sendKarmaMessage(String type) {
        ChristmasKarma data = get();
        long currentTime = System.currentTimeMillis();
        if (currentTime - data.lastKarmaMessage < KARMA_MESSAGE_COOLDOWN) return;
        data.lastKarmaMessage = currentTime;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        LocalPlayer player = mc.player;
        String playerName = player.getName().getString();

        String messageKey;
        if (isOnNaughtyList()) {
            messageKey = "fatherchristmas.naughty." + (new Random().nextInt(3) + 1);
        } else if (isOnNiceList()) {
            messageKey = "fatherchristmas.nice." + (new Random().nextInt(3) + 1);
        } else {
            return;
        }

        String message = I18n.get(messageKey, playerName);
        player.sendSystemMessage(Component.literal("\u00A7a" + message));
    }

    public static void reset() {
        ChristmasKarma data = get();
        data.karma = NEUTRAL_KARMA;
        save();
    }

    private static File getConfigFile() {
        return new File(FMLPaths.CONFIGDIR.get().toFile(), FILE_NAME);
    }

    private static ChristmasKarma load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                ChristmasKarma data = GSON.fromJson(reader, ChristmasKarma.class);
                if (data != null) {
                    return data;
                }
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.error("Failed to load karma data", e);
            }
        }
        return new ChristmasKarma();
    }

    private static void save() {
        File file = getConfigFile();
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(get(), writer);
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.error("Failed to save karma data", e);
        }
    }
}