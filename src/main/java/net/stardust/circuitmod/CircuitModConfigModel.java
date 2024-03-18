package net.stardust.circuitmod;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = "circuitmod")
@Config(name = "circuitmod-config", wrapperName = "CircuitModConfig")
public class CircuitModConfigModel {
    @Nest
    public powerScalingCat powerScaling = new powerScalingCat();

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public static class powerScalingCat {
        @RangeConstraint(min = 0.1d, max = 2.0d)
        public double powerGenerationScale = 1.0;
        public solarGenerationMode solarMode = solarGenerationMode.NORMAL;
        public enum solarGenerationMode {
            NORMAL, REALISTIC, BUFFED;
        }
    }

    @Nest
    public AltNest altNested = new AltNest();
    public static class AltNest {
        public boolean testBool = false;
    }
}
