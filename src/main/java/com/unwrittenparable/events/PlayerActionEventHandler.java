package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.NarratorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class PlayerActionEventHandler {
    private static final Random RANDOM = new Random();
    private static long lastEatMessage = 0;
    private static long lastSleepMessage = 0;
    private static long lastCraftMessage = 0;
    private static long lastMineMessage = 0;
    private static long lastTradeMessage = 0;
    private static long lastXpMessage = 0;
    private static final long ACTION_COOLDOWN = 30000;

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEatMessage < ACTION_COOLDOWN) return;
        lastEatMessage = currentTime;

        sendActionMessage(player, "eat");
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSleepMessage < ACTION_COOLDOWN) return;
        lastSleepMessage = currentTime;

        sendActionMessage(player, "sleep");
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        sendActionMessage(player, "wake_up");
    }

    @SubscribeEvent
    public void onPlayerCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCraftMessage < ACTION_COOLDOWN) return;
        lastCraftMessage = currentTime;

        sendActionMessage(player, "craft");
    }

    @SubscribeEvent
    public void onPlayerSmelt(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        sendActionMessage(player, "smelt");
    }

    @SubscribeEvent
    public void onPlayerXp(PlayerXpEvent.LevelChange event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastXpMessage < ACTION_COOLDOWN) return;
        lastXpMessage = currentTime;

        sendActionMessage(player, "xp_level");
    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        String containerType = event.getContainer().getClass().getSimpleName();
        if (containerType.contains("Crafting")) {
            sendActionMessage(player, "open_crafting");
        } else if (containerType.contains("Furnace")) {
            sendActionMessage(player, "open_furnace");
        } else if (containerType.contains("Chest")) {
            sendActionMessage(player, "open_chest");
        } else if (containerType.contains("Merchant") || containerType.contains("Trade")) {
            sendActionMessage(player, "trade");
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMineMessage < ACTION_COOLDOWN) return;
        lastMineMessage = currentTime;

        sendActionMessage(player, "mine");
    }

    private void sendActionMessage(LocalPlayer player, String category) {
        String[] messages = {
                "narrator.action." + category + ".1",
                "narrator.action." + category + ".2",
                "narrator.action." + category + ".3",
                "narrator.action." + category + ".4"
        };

        String messageKey = messages[RANDOM.nextInt(messages.length)];
        NarratorManager.broadcastMessage(player, messageKey);
    }
}