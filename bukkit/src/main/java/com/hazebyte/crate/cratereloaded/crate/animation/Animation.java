package com.hazebyte.crate.cratereloaded.crate.animation;

import com.hazebyte.crate.api.crate.AnimationType;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.effect.Category;
import com.hazebyte.crate.api.exception.AnimationException;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.component.PluginSettingComponent;
import com.hazebyte.crate.cratereloaded.menu.Size;
import com.hazebyte.crate.cratereloaded.model.CrateImpl;
import com.hazebyte.crate.cratereloaded.util.PlayerUtil;
import com.hazebyte.crate.cratereloaded.util.item.ItemUtil;
import com.hazebyte.crate.utils.NumberGenerator;
import com.hazebyte.util.Mat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/** Created by willi on 3/10/2017. */
public abstract class Animation {

    protected static final List<String> players = new ArrayList<>();
    private static final List<Mat> panes = new ArrayList<>();
    protected CrateImpl crate;
    protected AnimationType type;
    protected int length;
    protected Size size;
    protected int numberOfPrizes;
    private final ItemStack item;
    protected Ending ending;
    protected boolean isDisabled = false; // this is not per animation. this is a instance

    static {
        for (Mat mat : Mat.values()) {
            if (mat.name().contains("LIGHT_GRAY_STAINED_GLASS")) {
                continue;
            }
            if (mat.name().contains("STAINED_GLASS_PANE")) {
                panes.add(mat);
            }
        }
    }

    public Animation(Crate crate, PluginSettingComponent settings) {
        this.crate = (CrateImpl) crate;
        this.length = 160;
        this.item = settings.getCrateAnimationShuffleDisplay();
        this.size = Size.THREE_LINE;
        players.clear();
        AnimationMenuListener.getInstance().register(CorePlugin.getPlugin());
        checkTicks();
        setDefault();
    }

    public Animation(Crate crate, int length, PluginSettingComponent settings) {
        this.crate = (CrateImpl) crate;
        this.length = length;
        this.item = settings.getCrateAnimationShuffleDisplay();
        this.size = Size.THREE_LINE;
        players.clear();
        AnimationMenuListener.getInstance().register(CorePlugin.getPlugin());
        checkTicks();
        setDefault();
    }

    public Animation(Crate crate, int length, Size size, PluginSettingComponent settings) {
        this.crate = (CrateImpl) crate;
        this.length = length;
        this.item = settings.getCrateAnimationShuffleDisplay();
        this.size = size;
        players.clear();
        AnimationMenuListener.getInstance().register(CorePlugin.getPlugin());
        checkTicks();
        setDefault();
    }

    public void setEnding(Ending ending) {
        this.ending = ending;
    }

    public abstract void setDefault();

    protected void checkTicks() {
        if (this.length < 60) {
            this.length = 60;
            CorePlugin.getPlugin()
                    .getLogger()
                    .info(String.format(
                            "Crate %s must have an animation length greater than %d sec.",
                            crate.getCrateName(), length / 20));
        }
        CorePlugin.getPlugin()
                .getLogger()
                .finer(String.format("Crate %s, Ticks: %s", crate.getCrateName(), this.length));
    }

    public Inventory open(Player player, Location location) {
        String UUID = player.getUniqueId().toString();
        if (players.contains(UUID)) {
            return null;
        }
        players.add(UUID);
        return openCrate(player, location);
    }

    protected abstract Inventory openCrate(Player player, Location location);

    public void closeCrate(Player player, Location location, Reward reward, int time) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Crate crate = reward.getParent();
                crate.onRewards(
                        player,
                        Collections.singletonList(reward),
                        location,
                        (e) -> crate.runEffect(location, Category.END, player));
                removePlayerFromOpening(player);
                PlayerUtil.closeInventoryLater(player, 1);
            }
        }.runTaskLater(CorePlugin.getPlugin(), time);
    }

    protected List<Reward> getRewards(Player player) {
        return getRewards(player, (numberOfPrizes + 1));
    }

    protected List<Reward> getRewards(Player player, int amount) {
        return CorePlugin.getJavaPluginComponent()
                .getGenerateCratePrizeComponent()
                .generateCratePrize(crate, player, amount, true, false);
    }

    protected ItemStack getDisplay() {
        ItemStack current = item.clone();
        Mat mat = Mat.from(item.getType());
        if (mat.isGlassPane()) {
            mat = panes.get(NumberGenerator.range(0, panes.size() - 1));
            current = mat.toItemStack();
            ItemUtil.setNameAndLore(current, ItemUtil.getName(item), ItemUtil.getLore(item));
        }
        return current;
    }

    protected boolean isDisplay(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        Mat mat = Mat.from(item);
        if (mat == null) {
            return false;
        }
        return mat.isGlassPane();
    }

    protected Inventory fillEmptySlots(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null || isDisplay(contents[i])) {
                contents[i] = getDisplay();
            }
        }
        inventory.setContents(contents);
        return inventory;
    }

    protected void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String UUID = player.getUniqueId().toString();
        long delay = 1;
        if (players.contains(UUID)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.openInventory(event.getInventory());
                }
            }.runTaskLater(CorePlugin.getPlugin(), delay);
        }
    }

    public abstract void onDisable(List<Reward> rewards, List<Reward> constant, Player player);

    public abstract void onEnd(Player player, Location location, Reward reward, Inventory inventory);

    public abstract void onEnd(Player player, Location location, List<Reward> reward, Inventory inventory);

    public void onReward(Player player, Location location, Reward reward) {
        closeCrate(player, location, reward, 50);
    }

    public void onReward(Player player, Location location, List<Reward> rewards) {
        rewards.forEach(reward -> onReward(player, location, reward));
    }

    public abstract AnimationTask task(Inventory inventory, Player player, List<Reward> rewards, Location location);

    public abstract AnimationTask task(AnimationTask task) throws AnimationException;

    public boolean has(Player player) {
        return (players.contains(player.getUniqueId().toString()));
    }

    public int getLength() {
        return length;
    }

    public boolean isDisabled(Player player) {
        return !players.contains(player.getUniqueId().toString());
    }

    public void removePlayerFromOpening(Player player) {
        players.remove(player.getUniqueId().toString());
    }

    public Ending getEnding() {
        return this.ending;
    }
}
