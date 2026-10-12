package com.unwrittenparable.narrator;

import com.unwrittenparable.TheUnwrittenParable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@OnlyIn(Dist.CLIENT)
public class ModScanner {
    private static List<String> installedMods = null;
    private static final Map<String, ModInfo> modInfoMap = new HashMap<>();

    public static void scanMods() {
        if (installedMods != null) return;

        installedMods = new ArrayList<>();
        File modsFolder = FMLPaths.MODSDIR.get().toFile();

        if (!modsFolder.exists()) {
            TheUnwrittenParable.LOGGER.warn("Mods folder not found: " + modsFolder.getAbsolutePath());
            return;
        }

        File[] modFiles = modsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (modFiles == null) return;

        for (File modFile : modFiles) {
            try {
                String modId = extractModId(modFile);
                if (modId != null) {
                    installedMods.add(modId);
                    modInfoMap.put(modId, new ModInfo(modId, modFile.getName()));
                    TheUnwrittenParable.LOGGER.debug("Found mod: " + modId + " (" + modFile.getName() + ")");
                }
            } catch (Exception e) {
                TheUnwrittenParable.LOGGER.debug("Failed to read mod: " + modFile.getName(), e);
            }
        }

        TheUnwrittenParable.LOGGER.info("Scanned " + installedMods.size() + " mods from mods folder");
    }

    private static String extractModId(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry modsToml = jar.getJarEntry("META-INF/mods.toml");
            if (modsToml != null) {
                java.io.InputStream is = jar.getInputStream(modsToml);
                java.util.Scanner scanner = new java.util.Scanner(is, "UTF-8");
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("modId=")) {
                        String modId = line.substring(6).trim().replace("\"", "");
                        scanner.close();
                        return modId;
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static List<String> getInstalledMods() {
        if (installedMods == null) {
            scanMods();
        }
        return Collections.unmodifiableList(installedMods);
    }

    public static boolean isModInstalled(String modId) {
        if (installedMods == null) {
            scanMods();
        }
        return installedMods.contains(modId);
    }

    public static ModInfo getModInfo(String modId) {
        if (installedMods == null) {
            scanMods();
        }
        return modInfoMap.get(modId);
    }

    public static class ModInfo {
        private final String modId;
        private final String fileName;

        public ModInfo(String modId, String fileName) {
            this.modId = modId;
            this.fileName = fileName;
        }

        public String getModId() {
            return modId;
        }

        public String getFileName() {
            return fileName;
        }
    }
}