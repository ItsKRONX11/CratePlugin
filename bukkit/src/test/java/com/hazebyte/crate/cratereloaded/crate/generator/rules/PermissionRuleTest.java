package com.hazebyte.crate.cratereloaded.crate.generator.rules;

import com.hazebyte.crate.api.crate.reward.Reward;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PermissionRuleTest {

    @Test
    public void verifyRewardNotAllowedIfPermissionExists() {
        Player player = Mockito.mock(Player.class);
        Reward reward = Mockito.mock(Reward.class);
        Mockito.when(reward.hasPermission(player)).thenReturn(true);
        PermissionRule rule = new PermissionRule(player);
        Assertions.assertFalse(rule.test(reward));
    }

    @Test
    public void verifyRewardAllowedIfPermissionDoesNotExist() {
        Player player = Mockito.mock(Player.class);
        Reward reward = Mockito.mock(Reward.class);
        Mockito.when(reward.hasPermission(player)).thenReturn(false);
        PermissionRule rule = new PermissionRule(player);
        Assertions.assertTrue(rule.test(reward));
    }
}
