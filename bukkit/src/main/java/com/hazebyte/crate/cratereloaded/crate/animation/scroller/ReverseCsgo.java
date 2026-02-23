package com.hazebyte.crate.cratereloaded.crate.animation.scroller;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.crate.animation.AnimationTask;
import com.hazebyte.crate.cratereloaded.crate.animation.BaseScroller;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Csgo will use four stages. (Percent of Display / Tick Speed) = (50% / 2), (25% / 4), (15% / 5),
 * (10% / 10)
 *
 * <p>Number of items = ticks / speed
 */
public class ReverseCsgo extends Csgo {

    public ReverseCsgo(Crate crate, PluginSettingComponent settings) {
        super(crate, settings);
    }

    public ReverseCsgo(Crate crate, int length, PluginSettingComponent settings) {
        super(crate, length, settings);
    }

    @Override
    public AnimationTask task(Inventory inventory, Player player, List<Reward> rewards, Location location) {
        return new ReverseCsgoTask(this, inventory, player, rewards, location);
    }

    @Override
    public AnimationTask task(AnimationTask task) {
        return new ReverseCsgoTask(task);
    }

    public class ReverseCsgoTask extends CsgoTask {
        public ReverseCsgoTask(
                BaseScroller parent, Inventory inventory, Player player, List<Reward> rewards, Location location) {
            super(parent, inventory, player, rewards, location);
        }

        public ReverseCsgoTask(AnimationTask previous) {
            super(previous);
        }

        @Override
        public void update(Player player, Inventory inventory, List<Reward> rewards) {
            task(this).runTaskLater(CorePlugin.getPlugin(), speed);
            crate.runEffect(location, Category.ANIMATION, player);

            shift(inventory);

            Reward reward = rewards.get(iterations);
            ItemStack display = reward.getDisplayItem(player);
            inventory.setItem(16, display);
            fillEmptySlots(inventory);
        }

        private void shift(Inventory inventory) {
            for (int i = 10; i < 16; i++) {
                inventory.setItem(i, inventory.getItem(i + 1));
            }
        }
    }
}
