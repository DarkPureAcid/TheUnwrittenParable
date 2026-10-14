package com.unwrittenparable.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.ClientNarratorData;
import com.unwrittenparable.narrator.NarratorBossBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.*;

@OnlyIn(Dist.CLIENT)
public class ChristmasEvent {
    private static final String FILE_NAME = "theunwrittenparable_christmas.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ChristmasEvent INSTANCE;

    private boolean christmasActive = false;
    private boolean christmasInitialized = false;
    private transient long lastMessageTime = 0;
    private static final long MESSAGE_INTERVAL = 120000;

    private static final String[] FATHER_CHRISTMAS_MESSAGES = {
            "fatherchristmas.greeting.1",
            "fatherchristmas.greeting.2",
            "fatherchristmas.greeting.3",
            "fatherchristmas.greeting.4",
            "fatherchristmas.greeting.5"
    };

    public ChristmasEvent() {
    }

    public static ChristmasEvent get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static boolean isChristmas() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.DECEMBER && now.getDayOfMonth() == 25;
    }

    public static boolean isChristmasSeason() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 1 && now.getDayOfMonth() <= 25;
    }

    public static void checkChristmas(LocalPlayer player) {
        ChristmasEvent data = get();
        if (!data.christmasInitialized) {
            data.christmasInitialized = true;
            if (isChristmas()) {
                data.christmasActive = true;
                activateChristmas(player);
            } else if (isChristmasSeason()) {
                data.christmasActive = true;
                activateChristmasSeason(player);
            }
            save();
        }

        if (data.christmasActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - data.lastMessageTime > MESSAGE_INTERVAL) {
                data.lastMessageTime = currentTime;
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

        showChristmasBossBar();
    }

    private static void activateChristmasSeason(LocalPlayer player) {
        TheUnwrittenParable.LOGGER.info("Christmas season activated!");

        player.sendSystemMessage(Component.literal("\u00A7a[FATHER CHRISTMAS IS COMING]"));
        player.sendSystemMessage(Component.literal("\u00A7a" + I18n.get("fatherchristmas.season.1")));

        showChristmasBossBar();
    }

    private static void showChristmasBossBar() {
        String listStatus = ChristmasKarma.getListStatus();
        String displayName = "Father Christmas (" + listStatus + " list)";
        int karmaColor = ChristmasKarma.getKarmaColor();
        BossEvent.BossBarColor barColor = ChristmasKarma.isOnNiceList() ? BossEvent.BossBarColor.GREEN : 
                                          ChristmasKarma.isOnNaughtyList() ? BossEvent.BossBarColor.RED : BossEvent.BossBarColor.YELLOW;
        NarratorBossBar.showBossBar(displayName, 180000, () -> {}, karmaColor, false, barColor);
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
        return get().christmasActive;
    }

    public static void deactivateChristmas() {
        ChristmasEvent data = get();
        data.christmasActive = false;
        data.lastMessageTime = 0;
        save();
    }

    public static void toggleChristmas() {
        ChristmasEvent data = get();
        data.christmasActive = !data.christmasActive;
        if (data.christmasActive) {
            data.christmasInitialized = true;
            data.lastMessageTime = 0;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                activateChristmasSeason(mc.player);
            }
        } else {
            data.lastMessageTime = 0;
            NarratorBossBar.removeBossBar();
        }
        save();
    }

    private static File getConfigFile() {
        return new File(FMLPaths.CONFIGDIR.get().toFile(), FILE_NAME);
    }

    private static ChristmasEvent load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                ChristmasEvent data = GSON.fromJson(reader, ChristmasEvent.class);
                if (data != null) {
                    return data;
                }
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.error("Failed to load Christmas data", e);
            }
        }
        return new ChristmasEvent();
    }

    private static void save() {
        File file = getConfigFile();
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(get(), writer);
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.error("Failed to save Christmas data", e);
        }
    }
}