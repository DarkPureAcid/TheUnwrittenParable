package com.unwrittenparable.narrator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.Random;

public class WorldNarratorData extends SavedData {
    private static final String DATA_NAME = "unwrittenparable_narrator";

    private NarratorType narratorType = NarratorType.STANLEY;
    private boolean isMultiplayer = false;
    private String multiplayerNarrator = "Herobrine";

    private static final String[] MULTIPLAYER_NARRATORS = {"Notch", "Herobrine", "Chuck Norris"};
    private static final Random RANDOM = new Random();

    public WorldNarratorData() {
        NarratorType[] types = NarratorType.values();
        this.narratorType = types[RANDOM.nextInt(types.length)];
    }

    public static WorldNarratorData load(CompoundTag tag) {
        WorldNarratorData data = new WorldNarratorData();
        if (tag.contains("narratorType")) {
            data.narratorType = NarratorType.valueOf(tag.getString("narratorType"));
        }
        data.isMultiplayer = tag.getBoolean("isMultiplayer");
        if (tag.contains("multiplayerNarrator")) {
            data.multiplayerNarrator = tag.getString("multiplayerNarrator");
        }
        return data;
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.putString("narratorType", narratorType.name());
        tag.putBoolean("isMultiplayer", isMultiplayer);
        tag.putString("multiplayerNarrator", multiplayerNarrator);
        return tag;
    }

    public static WorldNarratorData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WorldNarratorData::load,
                WorldNarratorData::new,
                DATA_NAME
        );
    }

    public NarratorType getNarratorType() {
        return narratorType;
    }

    public void setNarratorType(NarratorType type) {
        this.narratorType = type;
        setDirty();
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        this.isMultiplayer = multiplayer;
        if (multiplayer) {
            this.multiplayerNarrator = MULTIPLAYER_NARRATORS[RANDOM.nextInt(MULTIPLAYER_NARRATORS.length)];
        }
        setDirty();
    }

    public String getMultiplayerNarrator() {
        return multiplayerNarrator;
    }

    public void cycleMultiplayerNarrator() {
        this.multiplayerNarrator = MULTIPLAYER_NARRATORS[RANDOM.nextInt(MULTIPLAYER_NARRATORS.length)];
        setDirty();
    }
}