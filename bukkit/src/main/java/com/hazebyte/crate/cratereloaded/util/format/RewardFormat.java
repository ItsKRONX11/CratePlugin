package com.hazebyte.crate.cratereloaded.util.format;

import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.model.RewardImpl;
import com.hazebyte.crate.cratereloaded.util.MoreObjects;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RewardFormat extends Format {

    private final DecimalFormat format;

    public RewardFormat(@NotNull String message) {
        super(message);
        format = new DecimalFormat(CorePlugin.getPlugin().getSettings().getDecimalFormat());
    }

    @Override
    public String format(Object object) {
        return format(null, object);
    }

    public String format(Player player, Object object) {
        if (object instanceof Reward) {
            return format(player, (Reward) object);
        }
        if (object instanceof List) {
            return format(player, (List) object);
        }
        return message;
    }

    public String format(Player player, @NotNull Reward reward) {
        if (!(reward instanceof RewardImpl)) { // It is a legacy reward.
            return message;
        }

        RewardImpl abstractReward = (RewardImpl) reward;
        if (reward.hasDisplayItem()) {
            message = CustomFormat.format(message, abstractReward.getModel().getDisplayItem());
        } else if (reward.hasItems()) {
            message = CustomFormat.format(message, abstractReward.getItemsNonFormatted());
        }

        if (reward.getParent() != null) {
            if (player != null) {
                double totalChance = reward.getParent().getRewards().stream()
                        .filter(r -> r.hasPermission(player))
                        .map(r -> r.getChance(player))
                        .reduce(0.0, Double::sum);
                double chance = (reward.getChance(player) / totalChance) * 100;
                String chanceString = String.format("%s%%", format.format(chance));
                message = message.replace("{chance}", chanceString)
                        .replace("{raw-chance}", Double.toString(chance));
            } else {
                message = message.replace("{chance}", "Player Needed")
                        .replace("{raw-chance}", "Player Needed");
            }
            CrateFormat crateFormat = new CrateFormat(message);
            message = crateFormat.format(reward.getParent());
        }
        return message;
    }

    public String format(Player player, List<Reward> rewards) {
        Reward firstReward = MoreObjects.firstNonNull(rewards);

        List<ItemStack> displayItems = new ArrayList<>();
        for (Reward reward : rewards) {
            displayItems.add(reward.getDisplayItem(player));
        }

        message = CustomFormat.format(message, displayItems);
        message = format(player, firstReward);
        return message;
    }
}
