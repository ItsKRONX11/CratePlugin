package com.hazebyte.crate.cratereloaded.crate;

import com.hazebyte.crate.api.crate.AnimationType;
import com.hazebyte.crate.api.crate.CrateType;
import com.hazebyte.crate.api.crate.EndAnimationType;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.model.CrateImpl;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Allows you to create a crate with different types of specification.
 *
 * @author William
 */
public class CrateBuilder {

    private CrateImpl crate;
    private final String name;
    private String displayName;
    private ItemStack displayItem;
    private CrateType type;
    private AnimationType animation;
    private EndAnimationType endAnimation;

    private boolean confirmation;
    private ItemStack acceptButton;
    private ItemStack declineButton;

    // Crate
    private ItemStack crateItem;

    // Economy
    private double cost;
    private boolean buyEnabled;

    // Holographic
    private List<String> holographicText;

    // Message
    private List<String> open;
    private List<String> broadcast;

    // Preview
    private boolean previewEnabled = true;
    private int rows = 0;

    // Reward Options
    private int minRewards;
    private int maxRewards;
    private List<Reward> rewards;

    /**
     * Initializes the builder with the crate crateName.
     *
     * @param name
     */
    public CrateBuilder(String name) {
        this.name = name;
    }

    public CrateBuilder setType(CrateType type) {
        this.type = type;
        return this;
    }

    public CrateBuilder setConfirmation(boolean bool) {
        this.confirmation = bool;
        return this;
    }

    public CrateBuilder setAcceptButton(ItemStack item) {
        this.acceptButton = item;
        return this;
    }

    public CrateBuilder setDeclineButton(ItemStack item) {
        this.declineButton = item;
        return this;
    }

    public CrateBuilder setEndAnimation(EndAnimationType animation) {
        this.endAnimation = animation;
        return this;
    }

    public CrateBuilder setAnimation(AnimationType animation) {
        this.animation = animation;
        return this;
    }

    public CrateBuilder setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        return this;
    }

    public CrateBuilder setDisplayName(String name) {
        if (name == null || name.equals("")) name = this.name;
        this.displayName = name;
        return this;
    }

    public CrateBuilder setCrateItem(ItemStack item) {
        this.crateItem = item;
        return this;
    }

    public CrateBuilder setCost(double cost) {
        this.cost = cost;
        return this;
    }

    public CrateBuilder setBuyable(boolean bool) {
        this.buyEnabled = bool;
        return this;
    }

    public CrateBuilder setHolographicText(List<String> text) {
        this.holographicText = text;
        return this;
    }

    public CrateBuilder setOpen(List<String> message) {
        this.open = message;
        return this;
    }

    public CrateBuilder setBroadcast(List<String> message) {
        this.broadcast = message;
        return this;
    }

    public CrateBuilder setPreviewable(boolean bool) {
        this.previewEnabled = bool;
        return this;
    }

    public CrateBuilder setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public CrateBuilder setMinRewards(int minRewards) {
        this.minRewards = minRewards;
        return this;
    }

    public CrateBuilder setMaxRewards(int maxRewards) {
        this.maxRewards = maxRewards;
        return this;
    }

    public CrateBuilder setRewards(List<Reward> rewards) {
        this.rewards = rewards;
        return this;
    }

    /**
     * Builds the crate.
     *
     * @return
     */
    public CrateImpl build() {
        crate = new CrateImpl(name, type);
        crate.setAnimationType(animation);
        crate.setEndAnimationType(endAnimation);
        crate.setDisplayName(displayName);
        crate.setDisplayItem(displayItem);
        crate.setConfirmationToggle(confirmation);
        crate.setAcceptButton(acceptButton);
        crate.setDeclineButton(declineButton);
        crate.setItem(crateItem);
        crate.setCost(cost);
        crate.setBuyable(buyEnabled);
        crate.setHolographicText(holographicText);
        crate.setOpenMessage(open);
        crate.setBroadcastMessage(broadcast);
        crate.setPreviewable(previewEnabled);
        crate.setPreviewRows(rows);
        crate.setMinimumRewards(minRewards);
        crate.setMaximumRewards(maxRewards);
        if (rewards != null) {
            for (Reward reward : rewards) {
                crate.addReward(reward);
            }
        }
        return crate;
    }
}
