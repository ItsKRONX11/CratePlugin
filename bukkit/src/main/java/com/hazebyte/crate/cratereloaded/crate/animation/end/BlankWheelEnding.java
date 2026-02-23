package com.hazebyte.crate.cratereloaded.crate.animation.end;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.crate.animation.AnimationTask;
import com.hazebyte.crate.cratereloaded.crate.animation.BaseScroller;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BlankWheelEnding extends BlankEnding {
    public BlankWheelEnding(Crate crate, PluginSettingComponent settings) {
        super(crate, settings);
    }

    public BlankWheelEnding(Crate crate, int length, PluginSettingComponent settings) {
        super(crate, length, settings);
    }

    @Override
    public void startClosing(Player player, Location location, Reward reward, Inventory inventory) {
        BlankWheelEndingTask task = new BlankWheelEndingTask(
                this, inventory, player, this.length, location, Collections.singletonList(reward));
        task.runTaskLater(CorePlugin.getPlugin(), this.length);
    }

    @Override
    public void startClosing(Player player, Location location, List<Reward> rewards, Inventory inventory) {
        BlankWheelEndingTask task = new BlankWheelEndingTask(this, inventory, player, this.length, location, rewards);
        task.runTaskLater(CorePlugin.getPlugin(), this.length);
    }

    @Override
    public AnimationTask task(AnimationTask task) {
        return new BlankWheelEndingTask((BlankWheelEndingTask) task);
    }

    private class BlankWheelEndingTask extends AnimationTask {

        public BlankWheelEndingTask(
                BaseScroller parent,
                Inventory inventory,
                Player player,
                int length,
                Location location,
                List<Reward> rewards) {
            super(parent, inventory, player, length, location);
            this.rewards = rewards;
        }

        public BlankWheelEndingTask(BlankWheelEndingTask previous) {
            super(previous);
        }

        public void focus(Inventory inventory) {
            ItemStack[] contents = inventory.getContents();
            ItemStack glass = getDisplay();
            for (int i = 0; i < contents.length; i++) {
                if (i == 22) {
                    contents[i] = rewards.get(0).getDisplayItem(player);
                } else {
                    contents[i] = glass;
                }
            }
            inventory.setContents(contents);
        }

        @Override
        public void run() {
            this.focus(inventory);
            onReward(player, location, this.rewards);
        }

        @Override
        public void update(Player player, Inventory inventory, List<Reward> rewards) {}
    }
}
