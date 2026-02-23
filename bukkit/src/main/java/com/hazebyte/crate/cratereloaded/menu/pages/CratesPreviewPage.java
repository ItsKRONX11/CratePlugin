package com.hazebyte.crate.cratereloaded.menu.pages;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.menu.Grid;
import com.hazebyte.crate.cratereloaded.menu.Size;
import com.hazebyte.crate.cratereloaded.menu.buttons.PageButton;
import org.bukkit.entity.Player;

import java.util.List;

public class CratesPreviewPage extends PaginationPage {

    private final List<Crate> crates;
    private final Player player;

    public CratesPreviewPage(Player player, List<Crate> crates, Size size, PluginSettingComponent settings) {
        super(CorePlugin.getPlugin(), settings.getPreviewMenuName(), size, settings);
        this.crates = crates;
        this.player = player;
        addItems();
    }

    @Override
    public void addItems() {
        crates.forEach(crate -> {
            int size = crate.getPreviewSlots()
                    + (settings.isMenuInteractionEnabled() ? Grid.ROWS : 0);
            CratePreviewPage page = new CratePreviewPage(player, crate, Size.fit(size), this, settings);
            PageButton button = new PageButton(this.plugin, crate.getDisplayItem(), page);
            addToQueue(button);
        });
        super.addItems();
    }
}
