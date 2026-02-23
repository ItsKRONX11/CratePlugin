package com.hazebyte.crate.cratereloaded.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RewardBean {
    private int slot;

    private String chanceRaw = "0.0";

    @NonNull
    private ItemStack displayItem;

    @NonNull
    private List<ItemStack> items = new ArrayList<>();

    @NonNull
    private List<String> commands = new ArrayList<>();

    @NonNull
    private List<String> permissions = new ArrayList<>();

    private boolean isUnique;

    private boolean isAlways;

    @NonNull
    private List<String> openMessage = new ArrayList<>();

    @NonNull
    private List<String> broadcastMessage = new ArrayList<>();
}
