package net.stardust.circuitmod.client;

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.entity.ModEntities;
import net.stardust.circuitmod.fluid.ModFluids;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreen;
import net.stardust.circuitmod.screen.FuelGeneratorScreen;
import net.stardust.circuitmod.screen.ModScreenHandlers;
import net.stardust.circuitmod.screen.PCBStationScreen;
import net.stardust.circuitmod.screen.QuarryScreen;
import net.stardust.circuitmod.screen.RubberTapScreen;
import net.stardust.circuitmod.block.entity.explosives.NukeEntityRenderer;

public class CircuitModClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        // SpecialModelLoaderEvents.LOAD_SCOPE.register(event);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONDUCTOR_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PCBSTATION_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOVING_WALKWAY_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RUBBER_SAPLING, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FUEL_GENERATOR_BLOCK, RenderLayer.getCutoutMipped());
        ModMessages.registerS2CPackets();
        SpecialModelLoaderEvents.LOAD_SCOPE.register(location -> CircuitMod.MOD_ID.equals(location.getNamespace()));
        HandledScreens.register(ModScreenHandlers.QUARRY_SCREEN_HANDLER, QuarryScreen::new);
        HandledScreens.register(ModScreenHandlers.EFFICIENT_COAL_GENERATOR_SCREEN_HANDLER, EfficientCoalGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlers.FUEL_GENERATOR_SCREEN_HANDLER, FuelGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlers.PCBSTATION_SCREEN_HANDLER, PCBStationScreen::new);
        HandledScreens.register(ModScreenHandlers.RUBBER_TAP_SCREEN_HANDLER, RubberTapScreen::new);


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



    }
}
