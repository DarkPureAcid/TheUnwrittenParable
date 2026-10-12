package com.unwrittenparable.events;

import com.unwrittenparable.narrator.NarratorManager;
import com.unwrittenparable.narrator.NarratorType;
import com.unwrittenparable.narrator.WorldNarratorData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class PlayerEventHandler {
    private static final Map<UUID, ResourceLocation> LAST_BIOME = new HashMap<>();
    private static final Map<UUID, Long> BIOME_CHECK_COOLDOWN = new HashMap<>();
    private static final Map<UUID, Long> LAST_IDLE_MESSAGE = new HashMap<>();
    private static final Random RANDOM = new Random();

    private static final long IDLE_CHECK_INTERVAL = 30000;

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        NarratorManager.sendMessage(player, "block_break");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) return;
        if (event.phase != TickEvent.Phase.END) return;

        checkBiomeChange(player);
        checkIdle(player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();
        String deathMessage = getDeathContext(source);
        NarratorManager.sendMessage(player, "death");
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();
        if (isMobDamage(source)) {
            NarratorManager.sendMessage(player, "combat");
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ServerLevel level = player.serverLevel();
        WorldNarratorData data = WorldNarratorData.get(level);

        boolean isServer = !player.server.isSingleplayer();
        data.setMultiplayer(isServer);

        if (!isServer) {
            showNarratorSelection(player, data);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        NarratorManager.clearCombatState(player);
    }

    private void checkBiomeChange(ServerPlayer player) {
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

    private void checkIdle(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long currentTime = System.currentTimeMillis();
        long lastIdle = LAST_IDLE_MESSAGE.getOrDefault(uuid, 0L);

        if (currentTime - lastIdle < IDLE_CHECK_INTERVAL) return;

        if (player.getDeltaMovement().lengthSqr() < 0.01) {
            NarratorManager.sendMessage(player, "idle");
            LAST_IDLE_MESSAGE.put(uuid, currentTime);
        }
    }

    private boolean isMobDamage(DamageSource source) {
        return source.getEntity() != null && !(source.getEntity() instanceof Player);
    }

    private String getDeathContext(DamageSource source) {
        if (source.getEntity() != null) {
            return "killed_by_entity";
        }
        if (source.is(DamageTypes.GENERIC_KILL)) {
            return "void";
        }
        if (source.is(DamageTypes.DROWN)) {
            return "drowned";
        }
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)) {
            return "fire";
        }
        return "generic";
    }

    private void showNarratorSelection(ServerPlayer player, WorldNarratorData data) {
        player.sendSystemMessage(Component.literal("=== The Unwritten Parable ==="));
        player.sendSystemMessage(Component.literal("Choose your narrator personality:"));

        NarratorType[] types = NarratorType.values();
        for (int i = 0; i < types.length; i++) {
            player.sendSystemMessage(Component.literal((i + 1) + ". " + types[i].getDisplayName() + " - " + types[i].getDescription()));
        }

        player.sendSystemMessage(Component.literal("Type the number in chat to select."));
        player.sendSystemMessage(Component.literal("Default: Stanley"));
    }
}