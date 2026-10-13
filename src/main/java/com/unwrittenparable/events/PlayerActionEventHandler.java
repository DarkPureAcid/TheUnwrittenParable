package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.narrator.NarratorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class PlayerActionEventHandler {
    private static final Random RANDOM = new Random();
    private static long lastEatMessage = 0;
    private static final long ACTION_COOLDOWN = 30000;

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEatMessage < ACTION_COOLDOWN) return;
        lastEatMessage = currentTime;

        String[] messages = {
                "narrator.action.eat.1",
                "narrator.action.eat.2",
                "narrator.action.eat.3",
                "narrator.action.eat.4"
        };

        String messageKey = messages[RANDOM.nextInt(messages.length)];
        NarratorManager.broadcastMessage(player, messageKey);
    }
}