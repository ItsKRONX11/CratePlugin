package com.hazebyte.crate.cratereloaded.component.impl;

import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.component.RewardServiceComponent;
import com.hazebyte.crate.cratereloaded.model.RewardV2;
import me.clip.placeholderapi.PlaceholderAPI;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class RewardServiceComponentImpl implements RewardServiceComponent {

    private final Random random = new Random();

    @Override
    public List<Reward> createPrizePool(@NonNull List<Reward> rewards, @NonNull List<Predicate<Reward>> rules) {
        return applyPredicateOnList(rewards, rules);
    }

    @Override
    public Reward generatePrize(Player player, @NonNull List<Reward> prizePool) {
        double total = 0;
        for (Reward reward : prizePool) {
            String parsed = PlaceholderAPI.setPlaceholders(player, reward.getChanceRaw());
            try {
                double weight = Double.parseDouble(parsed);
                if (weight > 0) total += weight;
            } catch (NumberFormatException ignored) {
            }
        }

        if (total <= 0) return null;

        double value = random.nextDouble() * total;
        double cumulative = 0;

        for (Reward reward : prizePool) {
            String parsed = PlaceholderAPI.setPlaceholders(player, reward.getChanceRaw());
            try {
                double weight = Double.parseDouble(parsed);
                if (weight <= 0) continue;
                cumulative += weight;
                if (value < cumulative) return reward;
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    @Override
    public List<RewardV2> createPrizePoolV2(@NonNull List<RewardV2> rewards, @NonNull List<Predicate<RewardV2>> rules) {
        return applyPredicateOnList(rewards, rules);
    }

    @Override
    public RewardV2 generatePrizeV2(Player player, @NonNull List<RewardV2> rewards) {
        double total = 0;
        for (RewardV2 reward : rewards) {
            String parsed = PlaceholderAPI.setPlaceholders(player, reward.getChanceRaw());
            try {
                double weight = Double.parseDouble(parsed);
                if (weight > 0) total += weight;
            } catch (NumberFormatException ignored) {
            }
        }

        if (total <= 0) return null;

        double value = random.nextDouble() * total;
        double cumulative = 0;

        for (RewardV2 reward : rewards) {
            String parsed = PlaceholderAPI.setPlaceholders(player, reward.getChanceRaw());
            try {
                double weight = Double.parseDouble(parsed);
                if (weight <= 0) continue;
                cumulative += weight;
                if (value < cumulative) return reward;
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    private <T> List<T> applyPredicateOnList(List<T> list, List<Predicate<T>> rules) {
        return list.stream().filter(item -> allPredicateMatch(item, rules)).collect(Collectors.toList());
    }

    private <T> boolean allPredicateMatch(T item, List<Predicate<T>> predicates) {
        return predicates.stream().allMatch(predicate -> predicate.test(item));
    }
}