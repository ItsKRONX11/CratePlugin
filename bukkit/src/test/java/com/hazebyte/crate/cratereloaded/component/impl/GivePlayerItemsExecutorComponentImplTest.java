package com.hazebyte.crate.cratereloaded.component.impl;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.hazebyte.crate.BukkitTest;
import com.hazebyte.crate.constants.ItemConstants;
import com.hazebyte.crate.cratereloaded.model.GiveItemExecutorResult;
import com.hazebyte.crate.test.PlayerMockData;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GivePlayerItemsExecutorComponentImplTest extends BukkitTest {

    private GivePlayerItemsComponentImpl executor = new GivePlayerItemsComponentImpl(plugin, plugin.getSettings());

    private static ItemStack oneItem = new ItemStack(Material.STONE, 1);
    private static ItemStack oneInventory = new ItemStack(Material.STONE, 64 * ItemConstants.SLOTS_IN_INVENTORY);
    private static ItemStack oneInventoryPlusOne =
            new ItemStack(Material.STONE, 64 * ItemConstants.SLOTS_IN_INVENTORY + 1);

    // test contents with minecart stack

    @ParameterizedTest
    @MethodSource("provideArgsForItemExecutorResult")
    public void execute_returns_correctItemExecutorResult(
            List<ItemStack> items, Set<GiveItemExecutorResult> expected, PlayerMock playerMock) {
        Set<GiveItemExecutorResult> results = executor.giveItems(items, playerMock);

        Assertions.assertEquals(expected, results);
    }

    private static Stream<Arguments> provideArgsForItemExecutorResult() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(oneItem),
                        EnumSet.of(GiveItemExecutorResult.PUT_INTO_PLAYER_INVENTORY),
                        server.addPlayer()),
                Arguments.of(
                        Arrays.asList(oneInventory),
                        EnumSet.of(GiveItemExecutorResult.PUT_INTO_PLAYER_INVENTORY),
                        server.addPlayer()),
                Arguments.of(
                        Arrays.asList(oneInventoryPlusOne),
                        EnumSet.of(
                                GiveItemExecutorResult.PUT_INTO_PLAYER_INVENTORY,
                                GiveItemExecutorResult.PUT_INTO_PLAYER_CLAIM),
                        server.addPlayer()),
                Arguments.of(
                        Arrays.asList(oneItem),
                        EnumSet.of(GiveItemExecutorResult.PUT_INTO_PLAYER_CLAIM),
                        PlayerMockData.createPlayerWithFullInventory(server)));
    }
}
