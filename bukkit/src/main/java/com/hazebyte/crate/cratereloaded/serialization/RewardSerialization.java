package com.hazebyte.crate.cratereloaded.serialization;

import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.crate.reward.Tag;
import com.hazebyte.crate.cratereloaded.model.RewardImpl;
import com.hazebyte.crate.cratereloaded.util.ClassUtil;
import com.hazebyte.crate.cratereloaded.util.item.ItemParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;

public class RewardSerialization {

    public static Map<String, Object> serializeToMap(Reward reward) {
        Objects.requireNonNull(reward);

        Map<String, Object> serialized = new HashMap<>();
        RewardImpl impl = (RewardImpl) reward;
        serialized.put(Tag.CHANCE.getName(), impl.getChanceRaw());
        serialized.put(Tag.DISPLAY.getName(), impl.getModel().getDisplayItem());
        serialized.put(Tag.ITEM.getName(), impl.getItems());
        serialized.put(Tag.COMMAND.getName(), impl.getCommands());
        serialized.put(Tag.PERMISSION.getName(), impl.getPermissions());
        serialized.put(Tag.PREVENT_DUPLICATE.getName(), impl.isUnique());
        serialized.put(Tag.ALWAYS.getName(), impl.isConstant());
        serialized.put(Tag.MESSAGE.getName(), impl.getOpenMessage());
        serialized.put(Tag.BROADCAST.getName(), impl.getBroadcastMessage());
        return serialized;
    }

    public static String serializeToString(Reward reward) {
        Map<String, Object> map = serializeToMap(reward);
        List<String> rewardTags = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            if (value instanceof String || value instanceof Integer || value instanceof Double) {
                rewardTags.add(wrapOf(entry.getKey(), String.valueOf(value)));
            } else if (value instanceof Boolean) {
                if ((Boolean) value) {
                    rewardTags.add(wrapOf(entry.getKey(), null));
                }
            } else if (ClassUtil.isInstanceOf(value, ItemStack.class)) {
                insert(rewardTags, entry.getKey(), value, ItemParser::serialize);
            } else if (ClassUtil.isInstanceOf(value, String.class)) {
                insert(rewardTags, entry.getKey(), value, String::valueOf);
            } else if (!(value instanceof List)) {
                throw new IllegalArgumentException(String.format(
                        "Unable to parse out key [%s] for class [%s]",
                        entry.getKey(), value.getClass().getName()));
            }
        }
        return rewardTags.stream().collect(Collectors.joining(", "));
    }

    private static <T, K> void insert(List<String> builder, String key, T itemOrList, Function<K, String> function) {
        if (itemOrList instanceof Collection) {
            insertList(builder, key, (Collection<K>) itemOrList, function);
        } else {
            insertElement(builder, key, (K) itemOrList, function);
        }
    }

    private static <T> void insertList(
            List<String> builder, String key, Collection<T> collection, Function<T, String> function) {
        for (T item : collection) {
            insertElement(builder, key, item, function);
        }
    }

    private static <T> void insertElement(List<String> builder, String key, T element, Function<T, String> function) {
        builder.add(wrapOf(key, function.apply(element)));
    }

    private static String wrapOf(String key, String value) {
        return new StringBuilder()
                .append(key)
                .append(":(")
                .append(value != null ? value : "")
                .append(")")
                .toString();
    }
}
