package net.stardust.circuitmod.client;

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.stardust.circuitmod.CircuitMod;
import net.stardust.circuitmod.block.ModBlocks;
import net.stardust.circuitmod.networking.ModMessages;
import net.stardust.circuitmod.screen.EfficientCoalGeneratorScreen;
import net.stardust.circuitmod.screen.ModScreenHandlers;
import net.stardust.circuitmod.screen.PCBStationScreen;
import net.stardust.circuitmod.screen.QuarryScreen;

public class CircuitModClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        // SpecialModelLoaderEvents.LOAD_SCOPE.register(event);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CONDUCTOR_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PCBSTATION_BLOCK, RenderLayer.getCutoutMipped());
        ModMessages.registerS2CPackets();
        SpecialModelLoaderEvents.LOAD_SCOPE.register(location -> CircuitMod.MOD_ID.equals(location.getNamespace()));
        HandledScreens.register(ModScreenHandlers.QUARRY_SCREEN_HANDLER, QuarryScreen::new);
        HandledScreens.register(ModScreenHandlers.EFFICIENT_COAL_GENERATOR_SCREEN_HANDLER, EfficientCoalGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlers.PCBSTATION_SCREEN_HANDLER, PCBStationScreen::new);

    }
}
