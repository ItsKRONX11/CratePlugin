package com.hazebyte.crate.cratereloaded.crate.reward;

import com.google.common.base.Strings;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.api.crate.reward.RewardLine;
import com.hazebyte.crate.api.crate.reward.Tag;
import com.hazebyte.crate.api.util.Messages;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.error.ValidationException;
import com.hazebyte.crate.cratereloaded.util.ConfigConstants;
import com.hazebyte.crate.cratereloaded.util.StringUtils;
import com.hazebyte.crate.cratereloaded.util.item.ItemParser;
import com.hazebyte.crate.utils.NumberGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.inventory.ItemStack;

public class RewardParser {

    private final Reward reward;

    public RewardParser(Reward reward) {
        this.reward = reward;
        parse(this.reward);
    }

    public static Reward parse(Reward reward) {
        RewardLine line = reward.getLine();
        if (line == null || line.toString().equals("")) {
            throw new ValidationException(String.format("Found an empty reward line"));
        }

        if (reward.hasPostParsing()) {
            String postString = postParse(reward.getLine().getRewardString());
            if (postString != null) {
                line = new RewardLine(postString);
            }
        }

        for (Map.Entry<Tag, List<String>> current : line.parse().entrySet()) {
            Tag tag = current.getKey();

            if (tag == null) {
                continue;
            }

            List<String> values = current.getValue();
            if (values == null || values.size() == 0) {
                CorePlugin.getPlugin().getLogger().severe(String.format(Messages.ERROR_LINE, tag.getName()));
                continue;
            }

            switch (tag) {
                case COMMAND: {
                    reward.setCommands(values);
                    break;
                }

                case CHANCE: {
                    reward.setChanceRaw(values.get(0));
                    break;
                }

                case DISPLAY: {
                    ItemStack item = ItemParser.parse(values.get(0));
                    if (item == null) {
                        throw new ValidationException(String.format("Display tag is invalid for: [%s]", values.get(0)));
                    }
                    reward.setDisplayItem(item);
                    break;
                }

                case ITEM: {
                    List<ItemStack> items = new ArrayList<>();

                    for (String str : values) {
                        ItemStack item = ItemParser.parse(str);
                        if (item == null) {
                            throw new ValidationException(String.format("Item tag is invalid for: [%s]", str));
                        }
                        items.add(item);
                    }

                    reward.setItems(items);
                    break;
                }

                case PERMISSION: {
                    reward.setPermissions(values);
                    break;
                }

                case PREVENT_DUPLICATE: {
                    reward.setUnique(true);
                    break;
                }

                case ALWAYS: {
                    reward.setConstant(true);
                    break;
                }

                case BROADCAST:
                    reward.setBroadcastMessage(values);
                    break;
                case MESSAGE:
                    reward.setOpenMessage(values);
                    break;

                default: {
                    CorePlugin.getPlugin()
                            .getLogger()
                            .severe(String.format(Messages.ERROR_LINE_INFO, tag, ConfigConstants.LINK_REWARD_TAGS));
                }
            }
        }
        // TODO: refactor - this is a hack
        if (!reward.hasDisplayItem()) reward.setDisplayItem(null);
        return reward;
    }

    private static final Pattern splitPattern = Pattern.compile("[',;.]");

    private static String postParse(String string) {
        if (Strings.isNullOrEmpty(string)) {
            return null;
        }

        String separator = ":", end = "}";

        String randomSimilar = "{random-similar:", random = "{random:";
        int breaker = 0, max = 200;
        while (string.contains(randomSimilar) && (breaker++ < max)) {
            String[] values = postParse(string, randomSimilar, separator, end);
            string = postParseRandom(string, values[0], values[1], false);
        }

        while (string.contains(random) && (breaker++ < max)) {
            String[] values = postParse(string, random, separator, end);
            string = postParseRandom(string, values[0], values[1], true);
        }
        return string;
    }

    private static String postParseRandom(String string, String replace, String values, boolean once) {
        String[] split = splitPattern.split(values);
        if (split.length == 2) {
            try {
                int min = Integer.parseInt(split[0].trim());
                int max = Integer.parseInt(split[1].trim());
                if (min > max) min = max;
                int number = NumberGenerator.range(min, max);

                if (once) {
                    string = StringUtils.replaceOnce(string, replace, Integer.toString(number));
                } else {
                    string = string.replace(replace, Integer.toString(number));
                }
            } catch (Exception ignored) {
            }
        }
        return string;
    }

    /**
     * @param string Original line cr give {{random:-20,10}0} diamond_sword
     * @param key {random:
     * @param separator :
     * @param end }
     * @return An array [0] is the replacing key [1] is the values;
     */
    private static String[] postParse(String string, String key, String separator, String end) {
        String[] values = new String[2];

        int index, lastIndex;
        // Create another instance to do string modifications
        String substring = string;
        // Get the first index of the key so we can remove all prior useless information
        index = substring.indexOf(key);
        // Trim the string then find the first ending character
        substring = substring.substring(index);
        lastIndex = substring.indexOf(end);

        // This should result with the line we want to replace
        substring = substring.substring(0, lastIndex + 1);
        String replaced = substring;
        values[0] = replaced;

        // Find the first and last index so we can remove the key.
        index = substring.indexOf(separator) + 1; // Removes the key.
        lastIndex = substring.indexOf(end);
        // We should have only the values and a separator
        substring = substring.substring(index, lastIndex);
        values[1] = substring;

        return values;
    }
}
