package com.hazebyte.crate.cratereloaded.util.format;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.model.CrateV2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomFormat {

    private CustomFormat() {}

    private static boolean isTypeOfList(Object object, Class target) {
        List list;
        return (object instanceof List && ((list = (List) object).size() > 0) && target.isInstance(list.get(0)));
    }

    public static String format(String message, Object... arguments) {
        if (message == null) {
            return null;
        }
        String formatted = message;
        Format format = new DefaultFormat(formatted);
        formatted = format.format(null);
        Player player = null;
        for (Object object : arguments) {
            if (object instanceof Player) {
                player = (Player) object;
                break;
            }
        }

        for (Object object : arguments) {
            if (object == null) {
                continue;
            } else if (object instanceof CrateV2 || isTypeOfList(object, CrateV2.class)) {
                format = new CrateV2Format(formatted);
            } else if (object instanceof Crate || isTypeOfList(object, Crate.class)) {
                format = new CrateFormat(formatted);
            } else if (object instanceof ItemStack || isTypeOfList(object, ItemStack.class)) {
                format = new ItemFormat(formatted);
            } else if ((object instanceof OfflinePlayer)) {
                format = new PlayerFormat(formatted);
            } else if (object instanceof Reward || isTypeOfList(object, Reward.class)) {
                formatted = new RewardFormat(formatted).format(player, object);
                continue;
            } else if (object instanceof Double) {
                format = new DoubleFormat(formatted);
            } else if (object instanceof Integer) {
                format = new IntegerFormat(formatted);
            } else if (object instanceof Long) {
                format = new DateFormat(formatted);
            }
            formatted = format.format(object);
        }
        return formatted;
    }

    public static List<String> format(List<String> messages, Object... arguments) {
        if (messages == null) return null;
        for (int i = 0; i < messages.size(); i++) {
            messages.set(i, format(messages.get(i), arguments));
        }
        return messages;
    }
}
