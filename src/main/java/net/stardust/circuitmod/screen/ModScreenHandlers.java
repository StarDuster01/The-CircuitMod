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

    public static final ScreenHandlerType<FuelGeneratorScreenHandler> FUEL_GENERATOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"fuel_generator_screen_handler"),
                    new ExtendedScreenHandlerType<>(FuelGeneratorScreenHandler::new));
    public static final ScreenHandlerType<CrusherScreenHandler> CRUSHER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"crusher_screen_handler"),
                    new ExtendedScreenHandlerType<>(CrusherScreenHandler::new));
    public static final ScreenHandlerType<QuarryScreenHandler> QUARRY_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"quarry_screen_handler"),
                    new ExtendedScreenHandlerType<>(QuarryScreenHandler::new));

    public static final ScreenHandlerType<PumpJackScreenHandler> PUMP_JACK_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"pump_jack_screen_handler"),
                    new ExtendedScreenHandlerType<>(PumpJackScreenHandler::new));
    public static final ScreenHandlerType<RefineryScreenHandler> REFINERY_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"refinery_screen_handler"),
                    new ExtendedScreenHandlerType<>(RefineryScreenHandler::new));
    public static final ScreenHandlerType<QuantumTeleporterScreenHandler> QUANTUM_TELEPORTER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"quantum_teleporter_screen_handler"),
                    new ExtendedScreenHandlerType<>(QuantumTeleporterScreenHandler::new));
    public static final ScreenHandlerType<PCBStationScreenHandler> PCBSTATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"pcbstation_screen_handler"),
                    new ExtendedScreenHandlerType<>(PCBStationScreenHandler::new));
    public static final ScreenHandlerType<RubberTapScreenHandler> RUBBER_TAP_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(CircuitMod.MOD_ID,"rubber_tap_screen_handler"),
                    new ExtendedScreenHandlerType<>(RubberTapScreenHandler::new));



    public static void registerScreenHandler() {
        CircuitMod.LOGGER.info("Registering Sreen Handler for" + CircuitMod.MOD_ID);
    }
}
