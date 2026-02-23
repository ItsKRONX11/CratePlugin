package com.hazebyte.crate.cratereloaded.component.impl;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.component.PreviewCrateComponent;
import com.hazebyte.crate.cratereloaded.menu.Grid;
import com.hazebyte.crate.cratereloaded.menu.Size;
import com.hazebyte.crate.cratereloaded.menu.pages.CratePreviewPage;
import javax.inject.Inject;
import org.bukkit.entity.Player;

public class PreviewCrateComponentImpl implements PreviewCrateComponent {

    private final CorePlugin plugin;
    private final PluginSettingComponent settings;

    @Inject
    public PreviewCrateComponentImpl(CorePlugin plugin, PluginSettingComponent settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    @Override
    public void previewCrate(Crate crate, Player player) {
        boolean buttonsEnabled = settings.isMenuInteractionEnabled();
        int slots = crate.getPreviewSlots() + (buttonsEnabled ? Grid.ROWS : 0);
        CratePreviewPage page = new CratePreviewPage(player, crate, Size.fit(slots), settings);
        page.open(player);
    }
}
