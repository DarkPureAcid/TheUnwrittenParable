package com.unwrittenparable.narrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unwrittenparable.TheUnwrittenParable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;

@OnlyIn(Dist.CLIENT)
public class NarratorGameRule {
    private static final String FILE_NAME = "theunwrittenparable_gamerules.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static NarratorGameRule INSTANCE;

    private boolean narratorEnabled = true;
    private boolean commandReactionsEnabled = true;
    private boolean achievementsEnabled = true;
    private boolean combatSilenceEnabled = true;
    private boolean deathCommentaryEnabled = true;

    public NarratorGameRule() {
    }

    public static NarratorGameRule get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public boolean isNarratorEnabled() {
        return narratorEnabled;
    }

    public void setNarratorEnabled(boolean enabled) {
        this.narratorEnabled = enabled;
        save();
    }

    public boolean isCommandReactionsEnabled() {
        return commandReactionsEnabled;
    }

    public void setCommandReactionsEnabled(boolean enabled) {
        this.commandReactionsEnabled = enabled;
        save();
    }

    public boolean isAchievementsEnabled() {
        return achievementsEnabled;
    }

    public void setAchievementsEnabled(boolean enabled) {
        this.achievementsEnabled = enabled;
        save();
    }

    public boolean isCombatSilenceEnabled() {
        return combatSilenceEnabled;
    }

    public void setCombatSilenceEnabled(boolean enabled) {
        this.combatSilenceEnabled = enabled;
        save();
    }

    public boolean isDeathCommentaryEnabled() {
        return deathCommentaryEnabled;
    }

    public void setDeathCommentaryEnabled(boolean enabled) {
        this.deathCommentaryEnabled = enabled;
        save();
    }

    private static File getConfigFile() {
        return new File(FMLPaths.CONFIGDIR.get().toFile(), FILE_NAME);
    }

    private static NarratorGameRule load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                NarratorGameRule data = GSON.fromJson(reader, NarratorGameRule.class);
                if (data != null) {
                    return data;
                }
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.error("Failed to load game rules", e);
            }
        }
        return new NarratorGameRule();
    }

    private void save() {
        File file = getConfigFile();
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.error("Failed to save game rules", e);
        }
    }
}