package net.stardust.circuitmod;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = "circuitmod")
@Config(name = "circuitmod-config", wrapperName = "CircuitModConfig")
public class CircuitModConfigModel {
    @SectionHeader("powerSettings")
    @Nest
    public powerScalingCat powerScaling = new powerScalingCat();
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public static class powerScalingCat {
        public float powerGenerationScale = 1.0f;
        public solarGenerationMode solarMode = solarGenerationMode.NORMAL;
        public enum solarGenerationMode {
            NORMAL, REALISTIC, BUFFED;
        }
    }

    @Nest
    public windMultiplierCat windSettings = new windMultiplierCat();
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public static class windMultiplierCat {
        public boolean windUseBiomes = true;
        public float WindBiomeMultiplierStandard = 1.0f;
        public float windBiomeMultiplierPeaks = 1.4f;
        public float WindBiomeMultiplierHills = 1.2f;
        public float WindBiomeMultiplierShore = 1.1f;
        public float WindBiomeMultiplierPlains = 1.0f;
        public float WindBiomeMultiplierForest = 0.8f;
        public boolean windUseWeather = true;
        public float WindWeatherMultiplierClear = 1.0f;
        public float WindWeatherMultiplierRain = 1.3f;
        public float WindWeatherMultiplierStorm = 1.5f;
    }
}
