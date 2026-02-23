package com.hazebyte.crate.cratereloaded.dagger;

import com.hazebyte.crate.api.ServerVersion;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.claim.ClaimExecutor;
import com.hazebyte.crate.exception.ExceptionHandler;
import com.hazebyte.crate.cratereloaded.component.AnimationFactoryComponent;
import com.hazebyte.crate.cratereloaded.component.ClaimCrateComponent;
import com.hazebyte.crate.cratereloaded.component.ConfigServiceComponent;
import com.hazebyte.crate.cratereloaded.component.EffectResolverComponent;
import com.hazebyte.crate.cratereloaded.component.EffectServiceComponent;
import com.hazebyte.crate.cratereloaded.component.ExportComponent;
import com.hazebyte.crate.cratereloaded.component.GenerateCratePrizeComponent;
import com.hazebyte.crate.cratereloaded.component.GiveCrateComponent;
import com.hazebyte.crate.cratereloaded.component.GivePlayerItemsComponent;
import com.hazebyte.crate.cratereloaded.component.OpenCrateAdminMenuComponent;
import com.hazebyte.crate.cratereloaded.component.OpenCrateComponent;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.component.PreviewCrateComponent;
import com.hazebyte.crate.cratereloaded.component.RateLimitServiceComponent;
import com.hazebyte.crate.cratereloaded.component.RewardServiceComponent;
import com.hazebyte.crate.cratereloaded.component.SupplyChestCreateComponent;
import com.hazebyte.crate.cratereloaded.component.impl.AnimationFactoryComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.ClaimCrateComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.ConfigServiceComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.EffectResolverComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.EffectServiceComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.ExportComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.FilePluginSettingComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.GenerateCratePrizeComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.GiveCrateComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.GivePlayerItemsComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.MockEffectServiceComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.OpenCrateAdminMenuComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.OpenCrateComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.PreviewCrateComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.RewardServiceComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.SimpleRateLimitServiceComponentImpl;
import com.hazebyte.crate.cratereloaded.component.impl.SupplyChestCreateComponentImpl;
import com.hazebyte.crate.cratereloaded.crate.BlockCrateHandler;
import com.hazebyte.crate.cratereloaded.crate.CrateHandler;
import com.hazebyte.crate.cratereloaded.crate.animationV2.AnimationManager;
import com.hazebyte.crate.cratereloaded.menuV2.InventoryHistoryManager;
import com.hazebyte.crate.cratereloaded.menuV2.InventoryManager;
import com.hazebyte.crate.cratereloaded.model.Config;
import com.hazebyte.crate.cratereloaded.parser.RewardV2Parser;
import com.hazebyte.crate.cratereloaded.parser.YamlCrateV2ParserImpl;
import com.hazebyte.crate.cratereloaded.util.ConfigConstants;
import com.hazebyte.crate.cratereloaded.validation.CrateValidatorImpl;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;

@Module
public class ComponentModule {

    @Provides
    @Singleton
    public ConfigServiceComponent provideConfigManagerComponent(JavaPlugin javaPlugin) {
        return new ConfigServiceComponentImpl(javaPlugin);
    }

    @Provides
    @Singleton
    public RateLimitServiceComponent provideRateLimitManagerComponent(CorePlugin plugin, PluginSettingComponent settings) {
        return new SimpleRateLimitServiceComponentImpl(plugin, settings);
    }

    @Provides
    @Singleton
    public EffectServiceComponent provideEffectServiceComponent(JavaPlugin javaPlugin, ServerVersion version) {
        if (version.lt(ServerVersion.v1_9_R1)) {
            javaPlugin.getLogger().info("Effects are disabled for Minecraft Server Versions 1.8.X.");
            return new MockEffectServiceComponentImpl();
        } else {
            return new EffectServiceComponentImpl(javaPlugin);
        }
    }

    @Provides
    @Singleton
    public ClaimCrateComponent provideClaimCrateComponent(
            CorePlugin plugin, ExceptionHandler exceptionHandler, PluginSettingComponent settings) {
        return new ClaimCrateComponentImpl(plugin, exceptionHandler, settings);
    }

    @Provides
    @Singleton
    public ClaimExecutor provideClaimExecutor(
            CorePlugin plugin, OpenCrateComponent openCrateComponent, JavaPlugin javaPlugin) {
        return new ClaimExecutor(plugin, openCrateComponent, javaPlugin.getLogger());
    }

    @Provides
    @Singleton
    public ExportComponent provideExportComponent(CorePlugin plugin, FilePluginSettingComponentImpl settings) {
        return new ExportComponentImpl(plugin, settings);
    }

    @Provides
    @Singleton
    public GenerateCratePrizeComponent provideGenerateCratePrizeComponent(
            CorePlugin plugin, RewardServiceComponent generateRewardComponent) {
        return new GenerateCratePrizeComponentImpl(plugin, generateRewardComponent);
    }

    @Provides
    @Singleton
    public RewardServiceComponent provideGenerateRewardComponent() {
        return new RewardServiceComponentImpl();
    }

    @Provides
    @Singleton
    public GiveCrateComponent provideGiveCrateComponent(
            CorePlugin plugin, GivePlayerItemsComponent givePlayerItemsComponent, ClaimExecutor claimExecutor,
            PluginSettingComponent settings) {
        return new GiveCrateComponentImpl(plugin, givePlayerItemsComponent, claimExecutor, settings);
    }

    @Provides
    @Singleton
    public GivePlayerItemsComponent provideGivePlayerItemsComponent(CorePlugin plugin, PluginSettingComponent settings) {
        return new GivePlayerItemsComponentImpl(plugin, settings);
    }

    @Provides
    @Singleton
    public OpenCrateComponent provideOpenCrateComponent(
            CorePlugin plugin, GivePlayerItemsComponent givePlayerItemsComponent) {
        return new OpenCrateComponentImpl(plugin, givePlayerItemsComponent);
    }

    @Provides
    @Singleton
    public FilePluginSettingComponentImpl provideFilePluginSettingComponentImpl(JavaPlugin javaPlugin) {
        File configFile = new File(javaPlugin.getDataFolder(), ConfigConstants.CONFIG_FILE_NAME);
        Config config = new Config(javaPlugin, configFile);
        return new FilePluginSettingComponentImpl(config, javaPlugin.getLogger());
    }

    @Provides
    @Singleton
    public PluginSettingComponent providePluginSettingsComponent(FilePluginSettingComponentImpl impl) {
        return impl;
    }

    @Provides
    @Singleton
    public PreviewCrateComponent provPreviewCrateComponent(CorePlugin plugin, PluginSettingComponent settings) {
        return new PreviewCrateComponentImpl(plugin, settings);
    }

    @Provides
    @Singleton
    public SupplyChestCreateComponent provideSupplyChestCreateController(CorePlugin plugin) {
        return new SupplyChestCreateComponentImpl(plugin);
    }

    @Provides
    @Singleton
    public InventoryManager provideInventoryManager() {
        return new InventoryManager();
    }

    @Provides
    @Singleton
    public InventoryHistoryManager provideInventoryHistoryManager() {
        return new InventoryHistoryManager();
    }

    @Provides
    @Singleton
    public AnimationManager provideAnimationManager(
            JavaPlugin plugin, CorePlugin corePlugin, ClaimExecutor claimExecutor) {
        AnimationManager animationManager = new AnimationManager(plugin, corePlugin, claimExecutor);
        return animationManager;
    }

    @Provides
    @Singleton
    public OpenCrateAdminMenuComponent provideOpenCrateAdminMenu(CorePlugin plugin, InventoryManager inventoryManager) {
        return new OpenCrateAdminMenuComponentImpl(plugin, inventoryManager);
    }

    @Provides
    @Singleton
    public CrateHandler provideCrateHandler(
            CorePlugin plugin,
            SupplyChestCreateComponent supplyComponent,
            OpenCrateComponent openComponent,
            PreviewCrateComponent previewComponent,
            GiveCrateComponent giveComponent,
            PluginSettingComponent settings) {
        return new CrateHandler(plugin, supplyComponent, openComponent, previewComponent, giveComponent, settings);
    }

    @Provides
    @Singleton
    public BlockCrateHandler provideBlockCrateHandler(CorePlugin plugin, PluginSettingComponent settings) {
        return new BlockCrateHandler(plugin, settings);
    }

    @Provides
    @Singleton
    public RewardV2Parser provideRewardV2Parser() {
        return new RewardV2Parser();
    }

    @Provides
    @Singleton
    public YamlCrateV2ParserImpl provideYamlCrateV2Parser(
            CorePlugin plugin, RewardV2Parser rewardParser, CrateValidatorImpl crateValidator, PluginSettingComponent settings) {
        return new YamlCrateV2ParserImpl(plugin, rewardParser, crateValidator, settings);
    }

    @Provides
    @Singleton
    public AnimationFactoryComponent provideAnimationFactoryComponent(
            CorePlugin plugin, PluginSettingComponent settings) {
        return new AnimationFactoryComponentImpl(plugin, settings);
    }

    @Provides
    @Singleton
    public EffectResolverComponent provideEffectResolverComponent(EffectServiceComponent effectService) {
        return new EffectResolverComponentImpl(effectService);
    }

    @Provides
    @Singleton
    public CrateValidatorImpl provideCrateValidator() {
        return new CrateValidatorImpl();
    }
}
