package com.unwrittenparable;

import com.unwrittenparable.commands.NarratorCommand;
import com.unwrittenparable.config.ModConfig;
import com.unwrittenparable.events.PlayerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheUnwrittenParable.MOD_ID)
public class TheUnwrittenParable {
    public static final String MOD_ID = "theunwrittenparable";
    public static final Logger LOGGER = LogManager.getLogger();

    public TheUnwrittenParable() {
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, "theunwrittenparable-common.toml");

        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

        LOGGER.info("The Unwritten Parable loaded.");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onCommandsRegister(RegisterCommandsEvent event) {
            NarratorCommand.register(event.getDispatcher());
            LOGGER.info("Narrator commands registered.");
        }
    }
}