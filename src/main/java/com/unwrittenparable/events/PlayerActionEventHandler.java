package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.NarratorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
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
    private static long lastChatMessage = 0;
    private static final long ACTION_COOLDOWN = 30000;
    private static final long CHAT_COOLDOWN = 15000;

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEatMessage < ACTION_COOLDOWN) return;
        lastEatMessage = currentTime;

        sendActionMessage(player, "eat");
        sendChatCommentary(player, "eat");
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSleepMessage < ACTION_COOLDOWN) return;
        lastSleepMessage = currentTime;

        sendActionMessage(player, "sleep");
        sendChatCommentary(player, "sleep");
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        sendActionMessage(player, "wake_up");
        sendChatCommentary(player, "wake_up");
    }

    @SubscribeEvent
    public void onPlayerCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCraftMessage < ACTION_COOLDOWN) return;
        lastCraftMessage = currentTime;

        sendActionMessage(player, "craft");
        sendChatCommentary(player, "craft");
    }

    @SubscribeEvent
    public void onPlayerSmelt(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        sendActionMessage(player, "smelt");
        sendChatCommentary(player, "smelt");
    }

    @SubscribeEvent
    public void onPlayerXp(PlayerXpEvent.LevelChange event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastXpMessage < ACTION_COOLDOWN) return;
        lastXpMessage = currentTime;

        sendActionMessage(player, "xp_level");
        sendChatCommentary(player, "xp_level");
    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        String containerType = event.getContainer().getClass().getSimpleName();
        if (containerType.contains("Crafting")) {
            sendActionMessage(player, "open_crafting");
            sendChatCommentary(player, "open_crafting");
        } else if (containerType.contains("Furnace")) {
            sendActionMessage(player, "open_furnace");
            sendChatCommentary(player, "open_furnace");
        } else if (containerType.contains("Chest")) {
            sendActionMessage(player, "open_chest");
            sendChatCommentary(player, "open_chest");
        } else if (containerType.contains("Merchant") || containerType.contains("Trade")) {
            sendActionMessage(player, "trade");
            sendChatCommentary(player, "trade");
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMineMessage < ACTION_COOLDOWN) return;
        lastMineMessage = currentTime;

        sendActionMessage(player, "mine");
        sendChatCommentary(player, "mine");
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

    private void sendChatCommentary(LocalPlayer player, String category) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChatMessage < CHAT_COOLDOWN) return;
        lastChatMessage = currentTime;

        String[] chatMessages = getChatMessages(category, player.getName().getString());
        if (chatMessages.length > 0) {
            String message = chatMessages[RANDOM.nextInt(chatMessages.length)];
            String narratorName = getNarratorName();
            player.sendSystemMessage(Component.literal("\u00A77[" + narratorName + "] \u00A7f" + message));
        }
    }

    private String getNarratorName() {
        if (ChristmasEvent.isChristmasActive()) {
            if (KrampusTransformation.isTransforming()) {
                return "Krampus";
            }
            return "Father Christmas";
        }
        if (HalloweenEvent.isHalloweenActive()) {
            return "Herobrine";
        }
        return "The Narrator";
    }

    private String[] getChatMessages(String category, String playerName) {
        switch (category) {
            case "mine":
                return new String[]{
                        playerName + " swings their pickaxe. The earth yields its treasures.",
                        playerName + " mines another block. For what purpose, only they know.",
                        "The pickaxe strikes stone. " + playerName + " continues their endless task.",
                        playerName + " breaks the earth. The narrator watches in silence."
                };
            case "eat":
                return new String[]{
                        playerName + " eats. Sustenance for the journey ahead.",
                        "Food. " + playerName + " needs energy to continue the story.",
                        playerName + " takes a bite. The narrator wonders if they taste the pixels.",
                        "Eating. A fundamental human need. " + playerName + " satisfies it."
                };
            case "sleep":
                return new String[]{
                        playerName + " lies down. The narrator will wait.",
                        "Sleep. " + playerName + " dreams of blocks and adventures.",
                        playerName + " closes their eyes. The narrator keeps watch.",
                        "Good night, " + playerName + ". The narrator will be here when you wake."
                };
            case "wake_up":
                return new String[]{
                        playerName + " wakes up. A new day begins.",
                        "Rise and shine, " + playerName + ". The narrator has been waiting.",
                        playerName + " opens their eyes. The story continues.",
                        "Morning. " + playerName + " rises. The narrator resumes."
                };
            case "craft":
                return new String[]{
                        playerName + " crafts something. Creativity in action.",
                        "The crafting table sings. " + playerName + " creates.",
                        playerName + " assembles materials. The narrator approves.",
                        "Crafting. " + playerName + " makes something new."
                };
            case "smelt":
                return new String[]{
                        playerName + " smelts ore. Fire transforms the raw.",
                        "The furnace glows. " + playerName + " watches the flames.",
                        playerName + " smelts materials. The narrator likes fire.",
                        "Smelting. " + playerName + " transforms the ordinary into the useful."
                };
            case "xp_level":
                return new String[]{
                        playerName + " gains experience. Growth is beautiful.",
                        "Level up! " + playerName + " grows stronger.",
                        playerName + " learns. The narrator takes notes.",
                        "Experience gained. " + playerName + " evolves."
                };
            case "open_crafting":
                return new String[]{
                        playerName + " opens the crafting table. What will they make?",
                        "The crafting table awaits. " + playerName + " considers.",
                        playerName + " prepares to craft. The narrator watches."
                };
            case "open_furnace":
                return new String[]{
                        playerName + " opens the furnace. Fire awaits.",
                        "The furnace glows. " + playerName + " prepares to smelt.",
                        playerName + " checks the furnace. The narrator observes."
                };
            case "open_chest":
                return new String[]{
                        playerName + " opens a chest. What treasures lie within?",
                        "A chest. " + playerName + " explores its contents.",
                        playerName + " peers inside. The narrator is curious."
                };
            case "trade":
                return new String[]{
                        playerName + " trades. Commerce in action.",
                        "Trading. " + playerName + " exchanges goods.",
                        playerName + " makes a deal. The narrator approves."
                };
            default:
                return new String[0];
        }
    }
}