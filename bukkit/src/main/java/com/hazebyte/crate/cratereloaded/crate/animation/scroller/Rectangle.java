package com.hazebyte.crate.cratereloaded.crate.animation.scroller;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.api.util.ItemBuilder;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.crate.animation.AnimationTask;
import com.hazebyte.crate.cratereloaded.crate.animation.BaseScroller;
import com.hazebyte.crate.cratereloaded.menu.Size;
import com.hazebyte.crate.utils.NumberGenerator;
import com.hazebyte.util.Mat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Wheels speed derives off getRouletteLength.
 *
 * <p>It has a original spinner, background. The getWheelLength animation generates 18 rewards
 * around [10-16, 19, 25, 28, 24, 37-43]
 */
public class Rectangle extends BaseScroller {
    protected ItemStack spinner;
    protected List<Integer> scrollPath;

    public Rectangle(Crate crate, PluginSettingComponent settings) {
        super(crate, settings);
    }

    public Rectangle(Crate crate, int length, PluginSettingComponent settings) {
        super(crate, length, settings);
    }

    @Override
    public void setDefault() {
        super.setDefault();
        this.size = Size.SIX_LINE;
        this.spinner = ItemBuilder.of(Mat.GREEN_STAINED_GLASS_PANE.toItemStack())
                .displayName(" ")
                .asItemStack();
        numberOfPrizes = 18;

        defineScrollPath();
        //        Messenger.debug(String.format("%s A: %d, B: %d, C: %d, D: %d, Displays: %d",
        // crate.getCrateName(), minA, minB, minC, minD, numberOfPrizes));
    }

    protected void defineScrollPath() {
        if (this.scrollPath == null) {
            this.scrollPath = new ArrayList<>();
        } else {
            this.scrollPath.clear();
        }

        Integer[] numberPath = {13, 14, 15, 16, 25, 34, 43, 42, 41, 40, 39, 38, 37, 28, 19, 10, 11, 12};
        this.scrollPath.addAll(Arrays.asList(numberPath));
    }

    @Override
    public AnimationTask task(Inventory inventory, Player player, List<Reward> rewards, Location location) {
        return new WheelTask(this, inventory, player, rewards, location);
    }

    @Override
    public AnimationTask task(AnimationTask task) {
        return new WheelTask((WheelTask) task);
    }

    public class WheelTask extends AnimationTask {
        private final Map<Integer, Reward> pathMapping;
        private int spinnerLocation;
        private final long randomAddition;

        public WheelTask(
                BaseScroller parent, Inventory inventory, Player player, List<Reward> rewards, Location location) {
            super(parent, inventory, player, length, location);
            this.timeLapsed = 0;
            this.rewards = rewards;

            pathMapping = new HashMap<>();
            spinnerLocation = 13;
            randomAddition = NumberGenerator.range(2, 64);
            // DO NOT USE TOTAL TICKS. PRECALCULATIONS IN SPEEDS MESS IT UP.
            this.timeLapsed -= randomAddition;
            fillEmptySlots(inventory);
            moveWheel(inventory, rewards);
        }

        public WheelTask(WheelTask previous) {
            super(previous);
            this.pathMapping = previous.pathMapping;
            this.spinnerLocation = previous.spinnerLocation;
            this.randomAddition = previous.randomAddition;
        }

        protected Inventory fillEmptySlots(Inventory inventory) {
            ItemStack[] contents = inventory.getContents();
            ItemStack item = getDisplay();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] == null) {
                    contents[i] = item;
                }
            }
            inventory.setContents(contents);
            return inventory;
        }

        public void moveWheel(Inventory inventory, List<Reward> rewards) {
            ItemStack[] contents = inventory.getContents();
            // Set the Wheel
            int counter = 0;
            for (int current : scrollPath) {
                contents[current] = rewards.get(counter).getDisplayItem(player);
                pathMapping.put(current, rewards.get(counter));
                counter++;
            }

            // Set the Spinner
            contents[spinnerLocation] = spinner;
            inventory.setContents(contents);
        }

        @Override
        public void run() {
            if (parent.isDisabled(player)) {
                return;
            }

            sync();

            if (shouldStop(numberOfPrizes)) {
                int index = scrollPath.indexOf(spinnerLocation);
                Reward reward = rewards.get(index);
                onEnd(player, location, reward, inventory);
                return;
            }

            update(player, inventory, rewards);
            task(this).runTaskLater(CorePlugin.getPlugin(), speed);
        }

        @Override
        public void update(Player player, Inventory inventory, List<Reward> rewards) {
            // Effects
            crate.runEffect(location, Category.ANIMATION, player);

            int index = scrollPath.indexOf(spinnerLocation);

            // If it reaches the max index, reset to start
            if ((index + 1) >= scrollPath.size()) {
                index = -1;
            }

            int nextSpinnerLocation = scrollPath.get(index + 1);

            inventory.setItem(spinnerLocation, pathMapping.get(spinnerLocation).getDisplayItem(player));
            inventory.setItem(nextSpinnerLocation, spinner);

            spinnerLocation = nextSpinnerLocation;
        }

        @Override
        public boolean shouldStop(int numberOfPrizes) {
            if ((timeLapsed) >= totalTicks) {
                return true;
            }
            this.iterations++;
            return false;
        }
    }
}
