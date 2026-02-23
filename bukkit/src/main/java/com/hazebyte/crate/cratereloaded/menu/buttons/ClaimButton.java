package com.hazebyte.crate.cratereloaded.menu.buttons;

import com.hazebyte.crate.api.claim.Claim;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.util.ItemBuilder;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.menu.Button;
import com.hazebyte.crate.cratereloaded.menu.ItemClickEvent;
import com.hazebyte.crate.cratereloaded.util.MoreObjects;
import com.hazebyte.crate.cratereloaded.util.format.CustomFormat;
import com.hazebyte.crate.cratereloaded.util.item.ItemUtil;
import com.hazebyte.util.Mat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClaimButton extends Button {

    private final Claim claim;
    private final PluginSettingComponent settings;
    private boolean used = false;

    public ClaimButton(Claim claim, PluginSettingComponent settings) {
        this.claim = claim;
        this.settings = settings;

        ItemStack item = null;
        Reward reward = claim.getRewards().get(0);

        if (reward != null) {
            item = reward.getDisplayItem(claim.getOwner() instanceof Player player ? player : null);
        } else {
            item = ItemBuilder.of(Mat.RED_WOOL.toItemStack())
                    .displayName("Claim Reward")
                    .lore(String.format("Unable to display: %d", claim.getTimestamp()))
                    .asItemStack();
        }

        List<String> format = settings.getClaimItemFormat();
        List<String> toBeLore = new ArrayList<>();

        for (String f : format) {
            if (f.contains("{lore}")) {
                toBeLore.addAll(MoreObjects.firstNonNull(ItemUtil.getLore(item), new ArrayList<>()));
            } else {
                String string = CustomFormat.format(f, claim.getRewards().size(), claim.getId());
                toBeLore.add(string);
            }
        }

        ItemUtil.setLore(item, toBeLore);
        this.setIcon(item);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        Player player = event.getPlayer();

        if (claim != null && !used) {
            try {
                boolean status = claim.execute();
                if (!status) {
                    // Note: Most failure reasons already show a message to the user
                    // (e.g., inventory full). This catches other cases like cancelled events.
                    event.setWillClose(true);
                    return;
                }
                CorePlugin.getPlugin().getClaimRegistrar().removeClaim(claim);
                ItemStack item = this.getFinalIcon(player);
                item.setType(Mat.GREEN_STAINED_GLASS_PANE.toMaterial());
                List<String> lore = MoreObjects.firstNonNull(ItemUtil.getLore(item), new ArrayList<>());
                lore.add(settings.getClaimSuccessMessage());
                ItemUtil.setLore(item, lore);
                this.setIcon(item);
                event.setWillUpdate(true);
                used = true;
            } catch (IOException e) {
                CorePlugin.getPlugin()
                        .getLogger()
                        .log(java.util.logging.Level.SEVERE, "Failed to remove claim", e);
            }
        } else {
            event.setWillClose(true);
        }
    }
}
