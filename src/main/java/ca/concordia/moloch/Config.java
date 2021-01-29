package ca.concordia.moloch;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private ForgeConfigSpec configs;

    public Config() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        this.configs = builder.build();
    }
}
