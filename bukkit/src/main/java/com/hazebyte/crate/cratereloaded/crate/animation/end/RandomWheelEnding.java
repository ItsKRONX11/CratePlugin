package com.hazebyte.crate.cratereloaded.crate.animation.end;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.crate.animation.AnimationTask;
import com.hazebyte.crate.cratereloaded.crate.animation.BaseScroller;
import com.hazebyte.crate.cratereloaded.crate.animation.Ending;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RandomWheelEnding extends Ending {
    public RandomWheelEnding(Crate crate, PluginSettingComponent settings) {
        super(crate, settings);
        this.length = 60;
    }

    public RandomWheelEnding(Crate crate, int length, PluginSettingComponent settings) {
        super(crate, length, settings);
    }

    @Override
    public void startClosing(Player player, Location location, Reward reward, Inventory inventory) {
        RandomEndingTask task =
                new RandomEndingTask(this, inventory, player, this.length, Collections.singletonList(reward), location);
        task.run();
    }

    @Override
    public void startClosing(Player player, Location location, List<Reward> rewards, Inventory inventory) {
        RandomEndingTask task = new RandomEndingTask(this, inventory, player, this.length, rewards, location);
        task.run();
    }

    @Override
    public AnimationTask task(Inventory inventory, Player player, List<Reward> rewards, Location location) {
        return new RandomEndingTask(this, inventory, player, length, rewards, location);
    }

    @Override
    public AnimationTask task(AnimationTask task) {
        return new RandomEndingTask(task);
    }

    private class RandomEndingTask extends AnimationTask {

        public RandomEndingTask(
                BaseScroller parent,
                Inventory inventory,
                Player player,
                int length,
                List<Reward> rewards,
                Location location) {
            super(parent, inventory, player, length, location);
            this.rewards = rewards;
            phase(inventory);
        }

        public RandomEndingTask(AnimationTask previous) {
            super(previous);
        }

        private void phase(Inventory inventory) {
            ItemStack[] contents = inventory.getContents();
            ItemStack single = getDisplay();
            for (int i = 0; i < contents.length; i++) {
                if (i == 22) {
                    contents[i] = rewards.get(0).getDisplayItem(player);
                } else {
                    contents[i] = single;
                }
            }
            inventory.setContents(contents);
        }

        @Override
        public void run() {
            this.sync();

            if (shouldStop(numberOfPrizes)) {
                phase(inventory);
                onReward(player, location, this.rewards);
                return;
            }

            task(this).runTaskLater(CorePlugin.getPlugin(), speed);
            update(player, inventory, rewards);
        }

        @Override
        public void update(Player player, Inventory inventory, List<Reward> rewards) {
            fillEmptySlots(inventory);
        }
    }
}
