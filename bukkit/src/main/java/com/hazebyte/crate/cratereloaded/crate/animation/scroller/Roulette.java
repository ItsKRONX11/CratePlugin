package com.hazebyte.crate.cratereloaded.crate.animation.scroller;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.crate.animation.AnimationTask;
import com.hazebyte.crate.cratereloaded.crate.animation.BaseScroller;
import com.hazebyte.crate.cratereloaded.util.InventoryConstants;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Roulette will use three stages. (Percent of Display / Tick Speed) = (50% / 1), (30% / 2), (20% /
 * 4)
 *
 * <p>Percentage of display (s to tick) (pd(time)): a = (.5)(time)(tick), b = (.3)(time)(tick), c =
 * (.2)(time)(tick) Number of Items = ticks / speed i.e. 5 seconds = ((.5 * 5 * 20) / 1) = 50, ((.3
 * * 5 * 20) / 2) = 15, ((.2 * 5 * 20) / 4) = 5
 */
public class Roulette extends BaseScroller {
    public Roulette(Crate crate, PluginSettingComponent settings) {
        super(crate, settings);
    }

    public Roulette(Crate crate, int length, PluginSettingComponent settings) {
        super(crate, length, settings);
    }

    @Override
    public AnimationTask task(Inventory inventory, Player player, List<Reward> rewards, Location location) {
        return new RouletteTask(this, inventory, player, rewards, location);
    }

    @Override
    public AnimationTask task(AnimationTask task) {
        return new RouletteTask(task);
    }

    public class RouletteTask extends AnimationTask {
        public RouletteTask(
                BaseScroller parent, Inventory inventory, Player player, List<Reward> rewards, Location location) {
            super(parent, inventory, player, length, location);
            this.timeLapsed = 0;
            this.rewards = rewards;
        }

        public RouletteTask(AnimationTask previous) {
            super(previous);
        }

        @Override
        public void run() {
            if (parent.isDisabled(player)) {
                return;
            }

            sync();

            if (shouldStop(numberOfPrizes)) {
                onEnd(player, location, rewards.get(iterations), inventory);
                return;
            }

            task(this).runTaskLater(CorePlugin.getPlugin(), speed);

            update(player, inventory, rewards);
        }

        @Override
        public void update(Player player, Inventory inventory, List<Reward> rewards) {
            crate.runEffect(location, Category.ANIMATION, player);
            Reward reward = rewards.get(iterations);
            ItemStack display = reward.getDisplayItem(player);
            inventory.setItem(InventoryConstants.CENTER_SLOT_THREE_ROWS, display);
            fillEmptySlots(inventory);
        }
    }
}
