package com.hazebyte.crate.cratereloaded.listener.crate;

import com.hazebyte.crate.api.crate.BlockCrateRegistrar;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.CrateAction;
import com.hazebyte.crate.api.crate.CrateRegistrar;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.api.event.CrateInteractEvent;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LeftClickListener implements Listener {

    private final CrateRegistrar crateHandler;
    private final BlockCrateRegistrar blockHandler;

    public LeftClickListener() {
        crateHandler = CorePlugin.getPlugin().getCrateRegistrar();
        blockHandler = CorePlugin.getPlugin().getBlockCrateRegistrar();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreview(CrateInteractEvent event) {
        if (event.isCancelled() || (event.getAction() != CrateAction.PREVIEW)) {
            return;
        }

        Crate crate = event.getCrate();
        Player player = event.getPlayer();
        List<Crate> crates = blockHandler.getCrates(event.getLocation());

        if (crates != null && !crates.isEmpty()) {
            if (crate.isPreviewable()) {
                crateHandler.previewAll(crates, player);
                blockHandler
                        .getFirstCrate(event.getLocation())
                        .runEffect(event.getLocation(), Category.INSPECT, player);
            }
        }
    }
}
