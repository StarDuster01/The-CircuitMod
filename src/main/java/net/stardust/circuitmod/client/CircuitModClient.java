package net.stardust.circuitmod.client;

import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.block.entity.ModBlockEntities;
import net.stardust.circuitmod.block.entity.client.AdvancedSolarPanelRenderer;
import net.stardust.circuitmod.block.entity.client.CrusherRenderer;
import net.stardust.circuitmod.block.entity.client.PumpJackRenderer;
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


        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_CRUDE_OIL, ModFluids.FLOWING_CRUDE_OIL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/crudeoil_still"), // Change to our texture
                        new Identifier("circuitmod:block/fluids/crudeoil_flow") // Change to our texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_CRUDE_OIL, ModFluids.FLOWING_CRUDE_OIL);

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_LIQUID_FUEL, ModFluids.FLOWING_LIQUID_FUEL,
                new SimpleFluidRenderHandler(
                        new Identifier("circuitmod:block/fluids/petrol_still"), // Change to our texture
                        new Identifier("circuitmod:block/fluids/petrol_flow") // Change to our texture
                ));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ModFluids.STILL_LIQUID_FUEL, ModFluids.FLOWING_LIQUID_FUEL);

        EntityRendererRegistry.register(ModEntities.NUKE_ENTITY, NukeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.LARGE_NUKE_ENTITY, LargeNukeEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.QUARRY_BLOCK_BE, ctx -> new QuarryBlockEntityRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.PIPE_BE, ctx -> new PipeBlockEntityRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.INPUT_PIPE_BE, ctx -> new InputPipeBlockEntityRenderer(ctx));

        BlockEntityRendererRegistry.register(ModBlockEntities.PUMP_JACK_BE, ctx -> new PumpJackRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.ADVANCED_SOLAR_PANEL_BE, ctx -> new AdvancedSolarPanelRenderer(ctx));
        BlockEntityRendererRegistry.register(ModBlockEntities.CRUSHER_BE, ctx -> new CrusherRenderer(ctx));






    }
}
