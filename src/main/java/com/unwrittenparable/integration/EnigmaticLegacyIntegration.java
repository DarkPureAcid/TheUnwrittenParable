package com.unwrittenparable.integration;

import com.unwrittenparable.TheUnwrittenParable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Method;

@OnlyIn(Dist.CLIENT)
public class EnigmaticLegacyIntegration {
    private static Boolean enigmaticLegacyAvailable = null;
    private static Object dormantEyeInstance = null;
    private static Method ttsMethod = null;

    public static boolean isEnigmaticLegacyAvailable() {
        if (enigmaticLegacyAvailable == null) {
            try {
                Class.forName("com.aizistral.enigmaticlegacy.EnigmaticLegacy");
                enigmaticLegacyAvailable = true;
                TheUnwrittenParable.LOGGER.info("Enigmatic Legacy detected!");
                initializeDormantEye();
            } catch (ClassNotFoundException e) {
                enigmaticLegacyAvailable = false;
                TheUnwrittenParable.LOGGER.info("Enigmatic Legacy not found.");
            }
        }
        return enigmaticLegacyAvailable;
    }

    private static void initializeDormantEye() {
        try {
            Class<?> dormantEyeClass = Class.forName("com.aizistral.enigmaticlegacy.entities.DormantEyeEntity");
            TheUnwrittenParable.LOGGER.info("Dormant Eye class found: " + dormantEyeClass.getName());

            Class<?> ttsClass = null;
            try {
                ttsClass = Class.forName("com.aizistral.enigmaticlegacy.tts.TTSSystem");
            } catch (ClassNotFoundException e) {
                try {
                    ttsClass = Class.forName("com.aizistral.enigmaticlegacy.audio.TTSSystem");
                } catch (ClassNotFoundException e2) {
                    TheUnwrittenParable.LOGGER.info("TTS system not found in expected locations.");
                }
            }

            if (ttsClass != null) {
                TheUnwrittenParable.LOGGER.info("TTS system found: " + ttsClass.getName());
                for (Method method : ttsClass.getDeclaredMethods()) {
                    if (method.getName().toLowerCase().contains("speak") || 
                        method.getName().toLowerCase().contains("play") ||
                        method.getName().toLowerCase().contains("voice")) {
                        ttsMethod = method;
                        ttsMethod.setAccessible(true);
                        TheUnwrittenParable.LOGGER.info("Found TTS method: " + method.getName());
                    }
                }
            }
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.debug("Failed to initialize Dormant Eye", e);
        }
    }

    public static boolean speakWithDormantEye(String message) {
        if (!isEnigmaticLegacyAvailable()) return false;
        if (ttsMethod == null) return false;

        try {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return false;

            ttsMethod.invoke(dormantEyeInstance, message);
            TheUnwrittenParable.LOGGER.info("Dormant Eye spoke: " + message);
            return true;
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.debug("Failed to use Dormant Eye TTS", e);
            return false;
        }
    }

    public static boolean isTTSAvailable() {
        return isEnigmaticLegacyAvailable() && ttsMethod != null;
    }
}