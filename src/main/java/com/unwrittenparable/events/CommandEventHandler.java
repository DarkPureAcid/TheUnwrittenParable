package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.commands.NarratorCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class CommandEventHandler {

    @SubscribeEvent
    public void onCommandsRegister(RegisterClientCommandsEvent event) {
        NarratorCommand.register(event.getDispatcher());
        TheUnwrittenParable.LOGGER.info("Narrator client commands registered.");
    }
}