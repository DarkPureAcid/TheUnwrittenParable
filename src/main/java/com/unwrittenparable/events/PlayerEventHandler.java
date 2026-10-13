package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class PlayerEventHandler {
    private static final Map<UUID, ResourceLocation> LAST_BIOME = new HashMap<>();
    private static final Map<UUID, Long> BIOME_CHECK_COOLDOWN = new HashMap<>();
    private static final Map<UUID, Long> LAST_IDLE_MESSAGE = new HashMap<>();
    private static final Map<UUID, Boolean> LAST_MULTIPLAYER_STATE = new HashMap<>();
    private static final Random RANDOM = new Random();

    private static final long IDLE_CHECK_INTERVAL = 30000;
    private static final long LAN_CHECK_INTERVAL = 5000;
    private static long lastLANCheck = 0;
    private static String lastCommand = "";
    private static net.minecraft.world.level.GameType lastGameMode = null;
    private static boolean wasDead = false;

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof LocalPlayer player)) return;
        NarratorManager.sendMessage(player, "block_break");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof LocalPlayer player)) return;
        if (event.phase != TickEvent.Phase.END) return;

        checkBiomeChange(player);
        checkIdle(player);
        checkLANState(player);
        checkCommandInput(player);
        checkGameModeChange(player);
        checkPlayerDeath(player);
        HalloweenEvent.checkHalloween(player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        NarratorManager.sendMessage(player, "death");
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        DamageSource source = event.getSource();
        if (isMobDamage(source)) {
            NarratorManager.sendMessage(player, "combat");
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientNarratorData data = ClientNarratorData.get();
        boolean isMultiplayer = !Minecraft.getInstance().isSingleplayer();
        data.setMultiplayer(isMultiplayer);

        LAST_MULTIPLAYER_STATE.put(player.getUUID(), isMultiplayer);

        showAssignedNarrator(player, data);

        ModScanner.scanMods();

        if (ModScanner.isModInstalled("enigmatic_legacy")) {
            NarratorManager.broadcastMessage(player, "mod_enigmatic_legacy");
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        NarratorManager.clearCombatState(player);
    }

    @SubscribeEvent
    public void onAdvancement(AdvancementEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        boolean hasDisplay = event.getAdvancement().getDisplay() != null;

        if (hasDisplay) {
            NarratorManager.broadcastMessage(player, "achievement");
        }
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        TheUnwrittenParable.LOGGER.info("CommandEvent fired: " + command);

        Minecraft.getInstance().execute(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            if (command.startsWith("/")) {
                String reactionKey = CommandReactions.getReaction(command);
                TheUnwrittenParable.LOGGER.info("Command: " + command + " -> Reaction: " + reactionKey);
                if (reactionKey != null) {
                    NarratorManager.broadcastMessage(player, reactionKey);
                }
            }
        });
    }

    private void checkBiomeChange(LocalPlayer player) {
        UUID uuid = player.getUUID();
        long currentTime = System.currentTimeMillis();
        long lastCheck = BIOME_CHECK_COOLDOWN.getOrDefault(uuid, 0L);

        if (currentTime - lastCheck < 5000) return;
        BIOME_CHECK_COOLDOWN.put(uuid, currentTime);

        BlockPos pos = player.blockPosition();
        Biome currentBiome = player.level().getBiome(pos).value();

        ResourceLocation lastBiome = LAST_BIOME.get(uuid);
        ResourceLocation currentBiomeId = player.level().registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(currentBiome);

        if (lastBiome != null && !lastBiome.equals(currentBiomeId)) {
            NarratorManager.sendMessage(player, "biome_enter");
        }

        LAST_BIOME.put(uuid, currentBiomeId);
    }

    private void checkIdle(LocalPlayer player) {
        UUID uuid = player.getUUID();
        long currentTime = System.currentTimeMillis();
        long lastIdle = LAST_IDLE_MESSAGE.getOrDefault(uuid, 0L);

        if (currentTime - lastIdle < IDLE_CHECK_INTERVAL) return;

        if (player.getDeltaMovement().lengthSqr() < 0.01) {
            NarratorManager.sendMessage(player, "idle");
            LAST_IDLE_MESSAGE.put(uuid, currentTime);
        }
    }

    private void checkLANState(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLANCheck < LAN_CHECK_INTERVAL) return;
        lastLANCheck = currentTime;

        UUID uuid = player.getUUID();
        boolean currentMultiplayer = !Minecraft.getInstance().isSingleplayer();
        Boolean lastState = LAST_MULTIPLAYER_STATE.get(uuid);

        if (lastState != null && !lastState && currentMultiplayer) {
            ClientNarratorData data = ClientNarratorData.get();
            data.setMultiplayer(true);

            String narratorName = data.getMultiplayerNarrator();

            if (narratorName.equals("Herobrine")) {
                player.sendSystemMessage(Component.translatable("narrator.herobrine.found"));
            } else {
                player.sendSystemMessage(Component.translatable("narrator.lan.taken_over", Component.literal(narratorName)));
            }

            NarratorManager.broadcastMessage(player, "biome_enter");
        }

        LAST_MULTIPLAYER_STATE.put(uuid, currentMultiplayer);
    }

    private void checkCommandInput(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        try {
            java.lang.reflect.Field chatField = mc.gui.getClass().getDeclaredField("f_93056_");
            chatField.setAccessible(true);
            net.minecraft.client.gui.components.EditBox chatBox = (net.minecraft.client.gui.components.EditBox) chatField.get(mc.gui);
            if (chatBox != null) {
                String currentInput = chatBox.getValue();
                if (currentInput != null && currentInput.startsWith("/") && !currentInput.equals(lastCommand)) {
                    lastCommand = currentInput;

                    TheUnwrittenParable.LOGGER.info("Command input detected: " + currentInput);
                    String reactionKey = CommandReactions.getReaction(currentInput);
                    if (reactionKey != null) {
                        TheUnwrittenParable.LOGGER.info("Command reaction: " + reactionKey);
                        NarratorManager.broadcastMessage(player, reactionKey);
                    }
                }

                if (currentInput == null || !currentInput.startsWith("/")) {
                    lastCommand = "";
                }
            }
        } catch (Exception e) {
        }
    }

    private void checkGameModeChange(LocalPlayer player) {
        try {
            java.lang.reflect.Field gameModeField = player.getClass().getSuperclass().getDeclaredField("f_108620_");
            gameModeField.setAccessible(true);
            Object gameModeController = gameModeField.get(player);

            if (gameModeController != null) {
                java.lang.reflect.Method getGameModeMethod = gameModeController.getClass().getMethod("m_104805_");
                net.minecraft.world.level.GameType currentGameMode = (net.minecraft.world.level.GameType) getGameModeMethod.invoke(gameModeController);

                if (lastGameMode != null && !lastGameMode.equals(currentGameMode)) {
                    TheUnwrittenParable.LOGGER.info("Game mode changed from " + lastGameMode + " to " + currentGameMode);
                    NarratorManager.broadcastMessage(player, "gamemode_change");
                }

                lastGameMode = currentGameMode;
            }
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.debug("Failed to check game mode", e);
        }
    }

    private void checkPlayerDeath(LocalPlayer player) {
        boolean isDead = player.isDeadOrDying();

        if (isDead && !wasDead) {
            TheUnwrittenParable.LOGGER.info("Player died!");
            NarratorManager.broadcastMessage(player, "death");
        }

        wasDead = isDead;
    }

    private boolean isMobDamage(DamageSource source) {
        return source.getEntity() != null && !(source.getEntity() instanceof Player);
    }

    private void showAssignedNarrator(LocalPlayer player, ClientNarratorData data) {
        player.sendSystemMessage(Component.translatable("narrator.join.title"));

        if (data.isMultiplayer()) {
            player.sendSystemMessage(Component.translatable("narrator.join.subtitle", Component.literal(data.getMultiplayerNarrator())));
        } else {
            String narratorKey = data.getNarratorType().getDisplayName().toLowerCase();
            player.sendSystemMessage(Component.translatable("narrator.join.subtitle", Component.translatable("narrator." + narratorKey + ".name")));
            player.sendSystemMessage(Component.translatable("narrator." + narratorKey + ".desc"));
        }
    }
}