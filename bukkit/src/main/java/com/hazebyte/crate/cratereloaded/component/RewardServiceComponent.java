package com.hazebyte.crate.cratereloaded.component;

import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.model.RewardV2;
import java.util.List;
import java.util.function.Predicate;
import lombok.NonNull;
import org.bukkit.entity.Player;

public interface RewardServiceComponent {

    List<Reward> createPrizePool(@NonNull List<Reward> rewards, @NonNull List<Predicate<Reward>> rules);

    Reward generatePrize(Player player, @NonNull List<Reward> rewards);

    List<RewardV2> createPrizePoolV2(@NonNull List<RewardV2> rewards, @NonNull List<Predicate<RewardV2>> rules);

    RewardV2 generatePrizeV2(Player player, @NonNull List<RewardV2> rewards);
}
