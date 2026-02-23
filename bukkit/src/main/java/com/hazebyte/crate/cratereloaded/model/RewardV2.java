package com.hazebyte.crate.cratereloaded.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.hazebyte.crate.cratereloaded.util.format.ItemFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class RewardV2 {
    @NonNull
    @Builder.Default
    Optional<ItemStack> displayItem = Optional.empty();

    @Builder.Default
    String chanceRaw = "0.0";

    @NonNull
    @Builder.Default
    List<ItemStack> items = Collections.emptyList();

    @NonNull
    @Builder.Default
    List<String> commands = Collections.emptyList();

    @NonNull
    @Builder.Default
    List<String> exclusivePermissions = Collections.emptyList();

    @NonNull
    @Builder.Default
    List<String> inclusivePermissions = Collections.emptyList();

    @NonNull
    @Builder.Default
    List<String> broadcastMessage = Collections.emptyList();

    @NonNull
    @Builder.Default
    List<String> openMessage = Collections.emptyList();

    @Builder.Default
    boolean constant = false;

    @Builder.Default
    boolean unique = false;

    public boolean hasPermission(Player player) {
        for (String permission : exclusivePermissions) {
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

    public double getChance(Player player) {
        return Double.parseDouble(PlaceholderAPI.setPlaceholders(player, getChanceRaw()));
    }

    public Optional<ItemStack> getDisplayItem(Player player) {
        Objects.requireNonNull(displayItem);
        return displayItem.map(i -> {
            ItemStack cloned = i.clone(); // defensive copying
            ItemFormatter.format(cloned, player, this);
            return cloned;
        });
    }

}
