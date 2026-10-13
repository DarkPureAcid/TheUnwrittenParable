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
    private static net.minecraft.world.Difficulty lastDifficulty = null;
    private static boolean wasDead = false;
    private static String lastHealthState = "";
    private static String lastHungerState = "";
    private static long lastHealthMessage = 0;
    private static long lastHungerMessage = 0;
    private static final long HEALTH_MESSAGE_COOLDOWN = 30000;

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof LocalPlayer player)) return;

        boolean isCreative = false;
        try {
            java.lang.reflect.Field[] fields = player.getClass().getSuperclass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getType() == net.minecraft.world.level.GameType.class) {
                    field.setAccessible(true);
                    net.minecraft.world.level.GameType gameMode = (net.minecraft.world.level.GameType) field.get(player);
                    if (gameMode == net.minecraft.world.level.GameType.CREATIVE) {
                        isCreative = true;
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }

        if (isCreative) {
            NarratorManager.sendMessage(player, "creative_break");
        } else {
            NarratorManager.sendMessage(player, "block_break");
        }
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
        checkDifficultyChange(player);
        checkHealthState(player);
        checkHungerState(player);
        HalloweenEvent.checkHalloween(player);
        ChristmasEvent.checkChristmas(player);
        KrampusTransformation.update();
        checkChatVisibility(player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        NarratorManager.sendMessage(player, "death");

        if (ChristmasEvent.isChristmasActive()) {
            String entityType = event.getEntity().getClass().getSimpleName();
            if (entityType.contains("Villager")) {
                ChristmasKarma.onVillagerKilled();
            } else if (entityType.contains("Animal") || entityType.contains("Cow") || entityType.contains("Pig") || 
                       entityType.contains("Sheep") || entityType.contains("Chicken") || entityType.contains("Rabbit")) {
                ChristmasKarma.onAnimalKilled();
            } else if (entityType.contains("Monster") || entityType.contains("Zombie") || entityType.contains("Skeleton") ||
                       entityType.contains("Creeper") || entityType.contains("Spider") || entityType.contains("Enderman")) {
                ChristmasKarma.onHostileMobKilled();
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        DamageSource source = event.getSource();
        if (isMobDamage(source)) {
            NarratorManager.sendMessage(player, "combat");
        }

        if (ChristmasEvent.isChristmasActive()) {
            String entityType = event.getEntity().getClass().getSimpleName();
            if (entityType.contains("Villager")) {
                ChristmasKarma.onVillagerHurt();
            }
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

            String reactionKey = CommandReactions.getReaction(command);
            TheUnwrittenParable.LOGGER.info("Command: " + command + " -> Reaction: " + reactionKey);
            if (reactionKey != null) {
                NarratorManager.broadcastMessage(player, reactionKey);
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
            // Look for gameMode field in LocalPlayer and its superclasses
            Class<?> clazz = player.getClass();
            while (clazz != null) {
                java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if (field.getType().getName().contains("MultiPlayerGameMode") || 
                        field.getType().getName().contains("PlayerGameMode") ||
                        field.getType().getName().contains("GameType")) {
                        field.setAccessible(true);
                        Object value = field.get(player);
                        if (value != null) {
                            net.minecraft.world.level.GameType currentGameMode = null;
                            
                            if (value instanceof net.minecraft.world.level.GameType) {
                                currentGameMode = (net.minecraft.world.level.GameType) value;
                            } else {
                                // Try to get GameType from MultiPlayerGameMode via getPlayerMode()
                                try {
                                    java.lang.reflect.Method method = value.getClass().getMethod("getPlayerMode");
                                    if (method != null) {
                                        Object result = method.invoke(value);
                                        if (result instanceof net.minecraft.world.level.GameType) {
                                            currentGameMode = (net.minecraft.world.level.GameType) result;
                                        }
                                    }
                                } catch (NoSuchMethodException ignored) {
                                }
                            }
                            
                            if (currentGameMode != null) {
                                TheUnwrittenParable.LOGGER.debug("Game mode detected: " + currentGameMode);
                                if (lastGameMode != null && !lastGameMode.equals(currentGameMode)) {
                                    TheUnwrittenParable.LOGGER.info("Game mode changed from " + lastGameMode + " to " + currentGameMode);
                                    NarratorManager.broadcastMessage(player, "gamemode_change");
                                }
                                lastGameMode = currentGameMode;
                                return;
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
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

    private void checkDifficultyChange(LocalPlayer player) {
        try {
            net.minecraft.world.Difficulty currentDifficulty = null;

            if (player.level().getServer() != null) {
                currentDifficulty = player.level().getServer().getWorldData().getDifficulty();
            } else {
                currentDifficulty = player.level().getDifficulty();
            }

            if (currentDifficulty != null && lastDifficulty != null && !lastDifficulty.equals(currentDifficulty)) {
                TheUnwrittenParable.LOGGER.info("Difficulty changed from " + lastDifficulty + " to " + currentDifficulty);
                NarratorManager.broadcastMessage(player, "difficulty_change");
            }

            if (currentDifficulty != null) {
                lastDifficulty = currentDifficulty;
            }
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.debug("Failed to check difficulty", e);
        }
    }

    private void checkHealthState(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHealthMessage < HEALTH_MESSAGE_COOLDOWN) return;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = (health / maxHealth) * 100;

        String currentState;
        if (healthPercent >= 100) {
            currentState = "full_health";
        } else if (healthPercent <= 25) {
            currentState = "low_health";
        } else if (healthPercent <= 50) {
            currentState = "half_health";
        } else {
            currentState = "";
        }

        if (!currentState.isEmpty() && !currentState.equals(lastHealthState)) {
            lastHealthState = currentState;
            lastHealthMessage = currentTime;
            NarratorManager.broadcastMessage(player, currentState);
        }
    }

    private void checkHungerState(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHungerMessage < HEALTH_MESSAGE_COOLDOWN) return;

        int foodLevel = player.getFoodData().getFoodLevel();
        int maxFood = 20;
        float foodPercent = ((float) foodLevel / maxFood) * 100;

        String currentState;
        if (foodPercent >= 100) {
            currentState = "full_hunger";
        } else if (foodPercent <= 25) {
            currentState = "low_hunger";
        } else if (foodPercent <= 50) {
            currentState = "half_hunger";
        } else {
            currentState = "";
        }

        if (!currentState.isEmpty() && !currentState.equals(lastHungerState)) {
            lastHungerState = currentState;
            lastHungerMessage = currentTime;
            NarratorManager.broadcastMessage(player, currentState);
        }
    }

    private static long lastChatVisibilityMessage = 0;
    private static final long CHAT_VISIBILITY_COOLDOWN = 60000;

    private void checkChatVisibility(LocalPlayer player) {
        if (!HalloweenEvent.isHalloweenActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        net.minecraft.client.Options options = mc.options;
        net.minecraft.world.entity.player.ChatVisiblity chatVisibility = options.chatVisibility().get();

        if (chatVisibility == net.minecraft.world.entity.player.ChatVisiblity.HIDDEN) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastChatVisibilityMessage < CHAT_VISIBILITY_COOLDOWN) return;
            lastChatVisibilityMessage = currentTime;

            player.sendSystemMessage(Component.literal("\u00A74HEROBRINE: You cannot silence me."));
            player.sendSystemMessage(Component.literal("\u00A74I see everything. Even when you hide."));

            Minecraft.getInstance().gui.setTitle(Component.literal("\u00A74HEROBRINE").withStyle(style -> style.withColor(0xAA00AA)));
            Minecraft.getInstance().gui.setSubtitle(Component.literal("\u00A74You cannot hide from me.").withStyle(style -> style.withColor(0xAA00AA)));
            Minecraft.getInstance().gui.setTimes(10, 100, 10);
        }
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