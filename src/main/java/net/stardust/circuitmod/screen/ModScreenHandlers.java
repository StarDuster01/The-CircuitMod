package net.stardust.circuitmod.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;

public class ModScreenHandlers {


    public static final ScreenHandlerType<EfficientCoalGeneratorScreenHandler> EFFICIENT_COAL_GENERATOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"efficient_coal_generator_screen_handler"),
                    new ExtendedScreenHandlerType<>(EfficientCoalGeneratorScreenHandler::new));
    public static final ScreenHandlerType<QuarryScreenHandler> QUARRY_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"quarry_screen_handler"),
                    new ExtendedScreenHandlerType<>(QuarryScreenHandler::new));
    public static final ScreenHandlerType<PCBStationScreenHandler> PCBSTATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"pcbstation_screen_handler"),
                    new ExtendedScreenHandlerType<>(PCBStationScreenHandler::new));



    public static void registerScreenHandler() {
        CircuitMod.LOGGER.info("Registering Sreen Handler for" + CircuitMod.MOD_ID);
    }
}
