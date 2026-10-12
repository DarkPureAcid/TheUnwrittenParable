package com.unwrittenparable.narrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.config.ModConfig;
import com.unwrittenparable.events.EssentialIntegration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ClientNarratorData {
    private static final String FILE_NAME = "theunwrittenparable_narrator.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Random RANDOM = new Random();

    private static ClientNarratorData INSTANCE;

    private String narratorTypeName;
    private boolean isMultiplayer;
    private String multiplayerNarrator;

    private static final String[] MULTIPLAYER_NARRATORS = {"Notch", "Herobrine", "Chuck Norris"};

    public ClientNarratorData() {
        NarratorType[] types = NarratorType.values();
        this.narratorTypeName = types[RANDOM.nextInt(types.length)].name();
        this.isMultiplayer = false;
        this.multiplayerNarrator = MULTIPLAYER_NARRATORS[RANDOM.nextInt(MULTIPLAYER_NARRATORS.length)];
    }

    public static ClientNarratorData get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public NarratorType getNarratorType() {
        try {
            return NarratorType.valueOf(narratorTypeName);
        } catch (Exception e) {
            NarratorType[] types = NarratorType.values();
            return types[RANDOM.nextInt(types.length)];
        }
    }

    public void setNarratorType(NarratorType type) {
        this.narratorTypeName = type.name();
        save();
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        if (this.isMultiplayer != multiplayer) {
            this.isMultiplayer = multiplayer;
            if (multiplayer) {
                this.multiplayerNarrator = getRandomMultiplayerNarrator();
            }
            save();
        }
    }

    public String getMultiplayerNarrator() {
        return multiplayerNarrator;
    }

    public void cycleMultiplayerNarrator() {
        this.multiplayerNarrator = getRandomMultiplayerNarrator();
        save();
    }

    private String getRandomMultiplayerNarrator() {
        if (ModConfig.ENABLE_ESSENTIAL_INTEGRATION.get() && EssentialIntegration.isEssentialAvailable()) {
            String friend = EssentialIntegration.getRandomFriend();
            if (friend != null) {
                return friend;
            }
        }
        return MULTIPLAYER_NARRATORS[RANDOM.nextInt(MULTIPLAYER_NARRATORS.length)];
    }

    private static File getConfigFile() {
        return new File(FMLPaths.CONFIGDIR.get().toFile(), FILE_NAME);
    }

    private static ClientNarratorData load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                ClientNarratorData data = GSON.fromJson(reader, ClientNarratorData.class);
                if (data != null) {
                    return data;
                }
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.error("Failed to load narrator data", e);
            }
        }
        return new ClientNarratorData();
    }

    private void save() {
        File file = getConfigFile();
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.error("Failed to save narrator data", e);
        }
    }
}