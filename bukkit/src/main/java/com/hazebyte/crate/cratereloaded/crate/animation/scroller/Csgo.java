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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Csgo will use four stages. (Percent of Display / Tick Speed) = (50% / 2), (25% / 4), (15% / 5),
 * (10% / 10)
 *
 * <p>Number of items = ticks / speed
 */
public class Csgo extends BaseScroller {
    public Csgo(Crate crate, PluginSettingComponent settings) {
        super(crate, settings);
    }

    public Csgo(Crate crate, int length, PluginSettingComponent settings) {
        super(crate, length, settings);
    }

    @Override
    public AnimationTask task(Inventory inventory, Player player, List<Reward> rewards, Location location) {
        return new CsgoTask(this, inventory, player, rewards, location);
    }

    @Override
    public AnimationTask task(AnimationTask task) {
        return new CsgoTask(task);
    }

    protected void initialize(Inventory inventory, Player player) {
        List<Reward> rewards = getRewards(player, 6);
        for (int i = 0; i < rewards.size(); i++) {
            inventory.setItem(10 + i, rewards.get(i).getDisplayItem(player));
        }

        ItemStack topDisplay = settings.getCSGOAnimationTopRowItem();
        ItemStack botDisplay = settings.getCSGOAnimationBottomRowItem();

        inventory.setItem(4, topDisplay);
        inventory.setItem(22, botDisplay);

        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f);
    }

    public class CsgoTask extends AnimationTask {
        public CsgoTask(
                BaseScroller parent, Inventory inventory, Player player, List<Reward> rewards, Location location) {
            super(parent, inventory, player, length, location);
            this.timeLapsed = 0;
            this.rewards = rewards;

            initialize(inventory, player);
        }

        public CsgoTask(AnimationTask previous) {
            super(previous);
        }

        @Override
        public void run() {
            if (parent.isDisabled(player)) {
                return;
            }

            sync();

            if (shouldStop(numberOfPrizes)) {
                onEnd(player, location, rewards.get(iterations - 3), inventory);
                return;
            }

            update(player, inventory, rewards);
        }

        @Override
        public void update(Player player, Inventory inventory, List<Reward> rewards) {
            task(this).runTaskLater(CorePlugin.getPlugin(), speed);
            crate.runEffect(location, Category.ANIMATION, player);

            shift(inventory);

            Reward reward = rewards.get(iterations);
            ItemStack display = reward.getDisplayItem(player);
            inventory.setItem(10, display);
            fillEmptySlots(inventory);
        }

        private void shift(Inventory inventory) {
            for (int i = 16; i >= 10; i--) {
                inventory.setItem(i, inventory.getItem(i - 1));
            }
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_PLACE, 1.0f, 1.0f);
        }
    }
}
