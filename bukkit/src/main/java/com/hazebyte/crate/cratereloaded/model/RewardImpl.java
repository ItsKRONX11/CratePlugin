package com.hazebyte.crate.cratereloaded.model;

import com.google.common.base.Strings;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.crate.reward.RewardLine;
import com.hazebyte.crate.api.result.RewardExecutorResult;
import com.hazebyte.crate.api.util.ItemBuilder;
import com.hazebyte.crate.api.util.Messenger;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.crate.reward.RewardParser;
import com.hazebyte.crate.cratereloaded.serialization.RewardSerialization;
import com.hazebyte.crate.cratereloaded.util.MoreObjects;
import com.hazebyte.crate.cratereloaded.util.format.CustomFormat;
import com.hazebyte.crate.cratereloaded.util.format.ItemFormatter;
import com.hazebyte.util.Mat;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardImpl implements Reward {

    private RewardBean model;
    private RewardParser parser;
    private RewardLine line;
    private Crate crate;
    private BiFunction<Reward, Player, Set<RewardExecutorResult>> executor = (reward, player) ->
            CorePlugin.getJavaPluginComponent().getOpenCrateComponent().executeReward(player, reward);

    public RewardImpl() {
        model = new RewardBean();
    }

    public RewardImpl(String line) {
        this.model = new RewardBean();
        this.line = new RewardLine(line);
        this.parser = new RewardParser(this);
    }

    // This is being used for getDisplayItem which calls ItemFormatter -> CustomFormat -> RewardFormat
    // -> getDisplayItem
    // There will be a stackoverflow so we will need to grab the vanilla item by default.
    public RewardBean getModel() {
        return model;
    }

    @Override
    public String serialize() {
        return this.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RewardImpl that = (RewardImpl) o;
        return this.model.equals(that.model);
    }

    @Override
    public int hashCode() {
        return this.model.hashCode();
    }

    @Override
    public String toString() {
        return RewardSerialization.serializeToString(this);
    }

    @Override
    public Crate getParent() {
        return this.crate;
    }

    @Override
    public void setParent(Crate crate) {
        this.crate = crate;
    }

    @Override
    public RewardLine getLine() {
        if (line == null) {
            line = new RewardLine("");
        }
        return line;
    }

    @Override
    public boolean hasPermission(Player player) {
        for (String permission : model.getPermissions()) {
            boolean negated = permission.startsWith("!");
            String node = negated ? permission.substring(1) : permission;

            if (negated) {
                if (player.hasPermission(node)) return false;
            } else {
                if (!player.hasPermission(node)) return false;
            }
        }
        return true;
    }

    @Override
    public List<String> getPermissions() {
        return model.getPermissions();
    }

    @Override
    public void setPermissions(List<String> permissions) {
        if (permissions == null) {
            model.setPermissions(Collections.emptyList());
        } else {
            model.setPermissions(permissions);
        }
    }

    @Override
    public int getSlot() {
        return model.getSlot();
    }

    @Override
    public void setSlot(int slot) {
        model.setSlot(slot);
    }

    @Override
    public double getChance(Player player) {
        String changeRaw = getChanceRaw();
        String parsed = PlaceholderAPI.setPlaceholders(player, changeRaw);
        double d = Double.parseDouble(parsed);
        return d;
    }

    @Override
    public String getChanceRaw() {
        return model.getChanceRaw();
    }

    @Override
    public void setChanceRaw(String chance) {
        model.setChanceRaw(chance);
    }

    @Override
    public ItemStack getDisplayItem(Player player) {
        Objects.requireNonNull(model.getDisplayItem());
        ItemStack cloned = model.getDisplayItem().clone(); // defensive copying
        ItemFormatter.format(cloned, player, this);
        return cloned;
    }

    public ItemStack getDisplayItem() {
        Objects.requireNonNull(model.getDisplayItem());
        // defensive copying
        return model.getDisplayItem().clone();
    }

    @Override
    public void setDisplayItem(ItemStack item) {
        // todo: move verification logic out
        if (item == null) {
            item = MoreObjects.firstNonNull(getItems());
        }
        if (item == null) {
            String lineDetails = this.line != null
                    ? this.line
                            .toString()
                            .substring(0, Math.min(40, this.line.toString().length()))
                    : "Line does not exist";
            item = new ItemBuilder(Mat.RED_WOOL.toMaterial())
                    .displayName("No display item set.")
                    .lore(lineDetails)
                    .asItemStack();
        }
        model.setDisplayItem(item.clone());
    }

    @Override
    public boolean hasDisplayItem() {
        return model.getDisplayItem() != null;
    }

    @Override
    public List<ItemStack> getItems() {
        return model.getItems().stream()
                .map(item -> ItemFormatter.format(item.clone(), this))
                .collect(Collectors.toList());
    }

    public List<ItemStack> getItemsNonFormatted() {
        return model.getItems();
    }

    @Override
    public List<ItemStack> getItems(Player player) {
        return getItems().stream()
                .map(item -> ItemFormatter.format(item.clone(), player))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasItems() {
        return !MoreObjects.isNullOrEmpty(this.model.getItems());
    }

    @Override
    public void setItems(List<ItemStack> items) {
        model.setItems(items);
    }

    @Override
    public List<String> getCommands() {
        return model.getCommands().stream()
                .map(cmd -> CustomFormat.format(cmd, crate, this))
                .collect(Collectors.toList());
    }

    public List<String> getCommands(Player player) {
        return this.getCommands().stream()
                .map(cmd -> CustomFormat.format(cmd, player, this, crate))
                .map(cmd -> PlaceholderAPI.setPlaceholders(player, cmd))
                .collect(Collectors.toList());
    }

    @Override
    public void setCommands(List<String> commands) {
        model.setCommands(commands);
    }

    @Override
    public List<String> getBroadcastMessage() {
        return model.getBroadcastMessage();
    }

    @Override
    public List<String> getOpenMessage() {
        return model.getOpenMessage();
    }

    @Override
    public void setBroadcastMessage(List<String> broadcastMessage) {
        model.setBroadcastMessage(broadcastMessage);
    }

    @Override
    public void setOpenMessage(List<String> openMessage) {
        model.setOpenMessage(openMessage);
    }

    @Override
    public boolean hasPostParsing() {
        return this.getLine().getRewardString().contains("{random:")
                || this.getLine().getRewardString().contains("{random-similar:");
    }

    @Override
    public Reward copy() {
        try {
            RewardImpl reward = new RewardImpl(this.getLine().toString());
            reward.setParent(getParent());
            return reward;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isConstant() {
        return model.isAlways();
    }

    @Override
    public void setConstant(boolean bool) {
        model.setAlways(bool);
    }

    @Override
    public boolean isUnique() {
        return model.isUnique();
    }

    @Override
    public void setUnique(boolean bool) {
        model.setUnique(bool);
    }

    public void runMessage(Player player) {
        this.getBroadcastMessage().stream()
                .filter(s -> !Strings.isNullOrEmpty(s))
                .map(s -> CustomFormat.format(s, player, this, crate))
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                .forEach(Messenger::broadcast);

        this.getOpenMessage().stream()
                .filter(s -> !Strings.isNullOrEmpty(s))
                .map(s -> CustomFormat.format(s, player, this, crate))
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                .forEach(s -> Messenger.tell(player, s));
    }

    @Override
    public Set<RewardExecutorResult> execute(Player player) {
        return executor.apply(this, player);
    }

    @Override
    public void setExecutor(BiFunction<Reward, Player, Set<RewardExecutorResult>> executor) {
        this.executor = executor;
    }
}
