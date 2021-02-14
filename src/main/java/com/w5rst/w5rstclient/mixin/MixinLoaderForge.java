package com.w5rst.w5rstclient.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.Name("ForgeW5rstMixinLoader")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MixinLoaderForge implements IFMLLoadingPlugin {
    public static final Logger log = LogManager.getLogger("ForgeW5rst");
    private static boolean isObfuscatedEnvironment = false;

    public MixinLoaderForge() {
        log.info("ForgeWurst mixins initializing...");
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.w5rst.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        log.info("ForgeWurst mixins initialised.");
        log.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
