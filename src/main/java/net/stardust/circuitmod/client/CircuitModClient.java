package net.stardust.circuitmod.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.client.*;
import net.stardust.circuitmod.block.renderer.*;
import net.stardust.circuitmod.entity.ModEntities;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreen;
import net.stardust.circuitmod.screen.FuelGeneratorScreen;
import net.stardust.circuitmod.screen.ModScreenHandlers;
import net.stardust.circuitmod.screen.PCBStationScreen;
import net.stardust.circuitmod.screen.QuarryScreen;
import net.stardust.circuitmod.screen.RubberTapScreen;
import net.stardust.circuitmod.screen.QuantumTeleporterScreen;
import net.stardust.circuitmod.screen.PumpJackScreen;
import net.stardust.circuitmod.screen.CrusherScreen;

public class CircuitModClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        // SpecialModelLoaderEvents.LOAD_SCOPE.register(event);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONDUCTOR_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PIPE_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLUID_PIPE_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLUID_TANK, RenderLayer.getTranslucent());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PCBSTATION_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOVING_WALKWAY_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RUBBER_SAPLING, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FUEL_GENERATOR_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PUMP_JACK_BLOCK, RenderLayer.getTranslucent());

        ModMessages.registerS2CPackets();
        SpecialModelLoaderEvents.LOAD_SCOPE.register(location -> CircuitMod.MOD_ID.equals(location.getNamespace()));
        HandledScreens.register(ModScreenHandlers.QUARRY_SCREEN_HANDLER, QuarryScreen::new);
        HandledScreens.register(ModScreenHandlers.EFFICIENT_COAL_GENERATOR_SCREEN_HANDLER, EfficientCoalGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlers.FUEL_GENERATOR_SCREEN_HANDLER, FuelGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlers.PCBSTATION_SCREEN_HANDLER, PCBStationScreen::new);
        HandledScreens.register(ModScreenHandlers.RUBBER_TAP_SCREEN_HANDLER, RubberTapScreen::new);
        HandledScreens.register(ModScreenHandlers.QUANTUM_TELEPORTER_SCREEN_HANDLER, QuantumTeleporterScreen::new);
        HandledScreens.register(ModScreenHandlers.PUMP_JACK_SCREEN_HANDLER, PumpJackScreen::new);
        HandledScreens.register(ModScreenHandlers.CRUSHER_SCREEN_HANDLER, CrusherScreen::new);

        // CRUDE OIL
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_CRUDE_OIL, ModFluids.FLOWING_CRUDE_OIL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/crudeoil/crudeoil_still"), // Change to our texture
                        new Identifier("circuitmod:block/fluids/crudeoil/crudeoil_flow") // Change to our texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_CRUDE_OIL, ModFluids.FLOWING_CRUDE_OIL);

        // HOT CRUDE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_HOT_CRUDE_OIL, ModFluids.FLOWING_HOT_CRUDE_OIL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/crudeoil/crudeoil_still"), // Same as regular crude
                        new Identifier("circuitmod:block/fluids/crudeoil/crudeoil_flow") // Same as regular crude
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_HOT_CRUDE_OIL, ModFluids.FLOWING_HOT_CRUDE_OIL);

        // LIQUID FUEL
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_LIQUID_FUEL, ModFluids.FLOWING_LIQUID_FUEL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/liquidfuel/liquidfuel_still"), // Change to our texture
                        new Identifier("circuitmod:block/fluids/liquidfuel/liquidfuel_flow") // Change to our texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_LIQUID_FUEL, ModFluids.FLOWING_LIQUID_FUEL);

        // RESIDUE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_RESIDUE, ModFluids.FLOWING_RESIDUE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/residue/residue_still"),
                        new Identifier("circuitmod:block/fluids/residue/residue_flow")
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_RESIDUE, ModFluids.FLOWING_RESIDUE);

        // LUBE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_LUBEOIL, ModFluids.FLOWING_LUBEOIL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/lube/lubeoil_still"),
                        new Identifier("circuitmod:block/fluids/lube/lubeoil_flow")
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_LUBEOIL, ModFluids.FLOWING_LUBEOIL);

        // MOTOR OIL
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_MOTOROIL, ModFluids.FLOWING_MOTOROIL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/motoroil/motoroil_still"),
                        new Identifier("circuitmod:block/fluids/motoroil/motoroil_flow")
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_MOTOROIL, ModFluids.FLOWING_MOTOROIL);

        // NAPHTHA
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_NAPHTHA, ModFluids.FLOWING_NAPHTHA,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/naphtha/naphtha_still"),
                        new Identifier("circuitmod:block/fluids/naphtha/naphtha_flow")
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_NAPHTHA, ModFluids.FLOWING_NAPHTHA);

        // HOT NAPHTHA
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_HOT_NAPHTHA, ModFluids.FLOWING_HOT_NAPHTHA,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/naphtha/naphtha_still"),
                        new Identifier("circuitmod:block/fluids/naphtha/naphtha_flow")
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_HOT_NAPHTHA, ModFluids.FLOWING_HOT_NAPHTHA);

        // ETHYLENE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ETHYLENE, ModFluids.FLOWING_ETHYLENE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_still"), // Uses the colorless fluid texture
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_flow") // Uses the colorless fluid texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_ETHYLENE, ModFluids.FLOWING_ETHYLENE);

        // BUTADIENE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_BUTADIENE, ModFluids.FLOWING_BUTADIENE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_still"), // Uses the colorless fluid texture
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_flow") // Uses the colorless fluid texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_BUTADIENE, ModFluids.FLOWING_BUTADIENE);

        // BENZENE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_BENZENE, ModFluids.FLOWING_BENZENE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_still"), // Uses the colorless fluid texture
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_flow") // Uses the colorless fluid texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_BENZENE, ModFluids.FLOWING_BENZENE);

        // STYRENE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_STYRENE, ModFluids.FLOWING_STYRENE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_still"), // Uses the colorless fluid texture
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_flow") // Uses the colorless fluid texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_STYRENE, ModFluids.FLOWING_STYRENE);

        // ETHYLBENZENE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_ETHYLBENZENE, ModFluids.FLOWING_ETHYLBENZENE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_still"), // Uses the colorless fluid texture
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_flow") // Uses the colorless fluid texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_ETHYLBENZENE, ModFluids.FLOWING_ETHYLBENZENE);

        // POLYETHYLENE
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_POLYETHYLENE, ModFluids.FLOWING_POLYETHYLENE,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_still"), // Uses the colorless fluid texture
                        new Identifier("circuitmod:block/fluids/colorless/colorless_fluid_flow") // Uses the colorless fluid texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_POLYETHYLENE, ModFluids.FLOWING_POLYETHYLENE);




        EntityRendererRegistry.register(ModEntities.NUKE_ENTITY, NukeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.LARGE_NUKE_ENTITY, LargeNukeEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.QUARRY_BLOCK_BE, ctx -> new QuarryBlockEntityRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.PIPE_BE, ctx -> new PipeBlockEntityRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.INPUT_PIPE_BE, ctx -> new InputPipeBlockEntityRenderer(ctx));

        BlockEntityRendererRegistry.register(ModBlockEntities.PUMP_JACK_BE, ctx -> new PumpJackRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.ADVANCED_SOLAR_PANEL_BE, ctx -> new AdvancedSolarPanelRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.CRUSHER_BE, ctx -> new CrusherRenderer(ctx));

        BlockEntityRendererRegistry.register(ModBlockEntities.POWER_VOID_CUBE_BE, PowerVoidCubeRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.POWER_CUBE_CUBE_BE, PowerCubeCubeRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.FLUID_TANK_BE, FluidTankBlockEntityRenderer::new);







    }
}
