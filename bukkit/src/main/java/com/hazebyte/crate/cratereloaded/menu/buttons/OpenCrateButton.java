package com.hazebyte.crate.cratereloaded.menu.buttons;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.util.Messenger;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.crate.CrateHandler;
import com.hazebyte.crate.cratereloaded.menu.Button;
import com.hazebyte.crate.cratereloaded.menu.ItemClickEvent;
import com.hazebyte.crate.cratereloaded.util.PlayerUtil;
import com.hazebyte.crate.cratereloaded.util.format.CustomFormat;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpenCrateButton extends Button {
    private Crate crate;
    private Location location;

    public OpenCrateButton(ItemStack item) {
        super(item);
    }

    public void setAttributes(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Crate) {
                crate = (Crate) object;
            } else if (object instanceof Location) {
                location = (Location) object;
            }
        }
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        Player player = event.getPlayer();
        ItemStack item = PlayerUtil.getItemInHand(player);
        if (CrateHandler.getInstance().isCrate(item) && crate.is(item)) {
            CorePlugin.getPlugin().getCrateRegistrar().open(crate, player, location);
        } else {
            Messenger.tell(player, CustomFormat.format(CorePlugin.getPlugin().getMessage("core.invalid_crate"), player, crate));
        }
        event.setWillClose(true);
    }
}
