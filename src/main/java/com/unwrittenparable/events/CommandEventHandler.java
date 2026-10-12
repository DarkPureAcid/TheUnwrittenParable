package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import com.unwrittenparable.commands.NarratorCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandEventHandler {

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        NarratorCommand.register(event.getDispatcher());
        TheUnwrittenParable.LOGGER.info("Narrator commands registered.");
    }
}