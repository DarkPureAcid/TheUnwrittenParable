package com.unwrittenparable.narrator;

import java.util.*;

public class NarratorMessages {
    private static final Random RANDOM = new Random();

    private static final Map<NarratorType, Map<String, List<String>>> MESSAGES = new HashMap<>();

    static {
        // Stanley
        Map<String, List<String>> stanley = new HashMap<>();
        stanley.put("block_break", Arrays.asList(
                "narrator.stanley.block_break.1",
                "narrator.stanley.block_break.2",
                "narrator.stanley.block_break.3",
                "narrator.stanley.block_break.4"
        ));
        stanley.put("biome_enter", Arrays.asList(
                "narrator.stanley.biome_enter.1",
                "narrator.stanley.biome_enter.2",
                "narrator.stanley.biome_enter.3",
                "narrator.stanley.biome_enter.4"
        ));
        stanley.put("death", Arrays.asList(
                "narrator.stanley.death.1",
                "narrator.stanley.death.2",
                "narrator.stanley.death.3",
                "narrator.stanley.death.4"
        ));
        stanley.put("combat", Arrays.asList(
                "narrator.stanley.combat.1",
                "narrator.stanley.combat.2",
                "narrator.stanley.combat.3"
        ));
        stanley.put("idle", Arrays.asList(
                "narrator.stanley.idle.1",
                "narrator.stanley.idle.2",
                "narrator.stanley.idle.3",
                "narrator.stanley.idle.4"
        ));
        stanley.put("achievement", Arrays.asList(
                "narrator.stanley.achievement.1",
                "narrator.stanley.achievement.2",
                "narrator.stanley.achievement.3",
                "narrator.stanley.achievement.4"
        ));
        MESSAGES.put(NarratorType.STANLEY, stanley);

        // Nigel (Helpful)
        Map<String, List<String>> nigel = new HashMap<>();
        nigel.put("block_break", Arrays.asList(
                "narrator.nigel.block_break.1",
                "narrator.nigel.block_break.2",
                "narrator.nigel.block_break.3",
                "narrator.nigel.block_break.4"
        ));
        nigel.put("biome_enter", Arrays.asList(
                "narrator.nigel.biome_enter.1",
                "narrator.nigel.biome_enter.2",
                "narrator.nigel.biome_enter.3",
                "narrator.nigel.biome_enter.4"
        ));
        nigel.put("death", Arrays.asList(
                "narrator.nigel.death.1",
                "narrator.nigel.death.2",
                "narrator.nigel.death.3",
                "narrator.nigel.death.4"
        ));
        nigel.put("combat", Arrays.asList(
                "narrator.nigel.combat.1",
                "narrator.nigel.combat.2",
                "narrator.nigel.combat.3"
        ));
        nigel.put("idle", Arrays.asList(
                "narrator.nigel.idle.1",
                "narrator.nigel.idle.2",
                "narrator.nigel.idle.3",
                "narrator.nigel.idle.4"
        ));
        nigel.put("achievement", Arrays.asList(
                "narrator.nigel.achievement.1",
                "narrator.nigel.achievement.2",
                "narrator.nigel.achievement.3",
                "narrator.nigel.achievement.4"
        ));
        MESSAGES.put(NarratorType.HELPFUL, nigel);

        // Malcolm (Sarcastic)
        Map<String, List<String>> malcolm = new HashMap<>();
        malcolm.put("block_break", Arrays.asList(
                "narrator.malcolm.block_break.1",
                "narrator.malcolm.block_break.2",
                "narrator.malcolm.block_break.3",
                "narrator.malcolm.block_break.4"
        ));
        malcolm.put("biome_enter", Arrays.asList(
                "narrator.malcolm.biome_enter.1",
                "narrator.malcolm.biome_enter.2",
                "narrator.malcolm.biome_enter.3",
                "narrator.malcolm.biome_enter.4"
        ));
        malcolm.put("death", Arrays.asList(
                "narrator.malcolm.death.1",
                "narrator.malcolm.death.2",
                "narrator.malcolm.death.3",
                "narrator.malcolm.death.4"
        ));
        malcolm.put("combat", Arrays.asList(
                "narrator.malcolm.combat.1",
                "narrator.malcolm.combat.2",
                "narrator.malcolm.combat.3"
        ));
        malcolm.put("idle", Arrays.asList(
                "narrator.malcolm.idle.1",
                "narrator.malcolm.idle.2",
                "narrator.malcolm.idle.3",
                "narrator.malcolm.idle.4"
        ));
        malcolm.put("achievement", Arrays.asList(
                "narrator.malcolm.achievement.1",
                "narrator.malcolm.achievement.2",
                "narrator.malcolm.achievement.3",
                "narrator.malcolm.achievement.4"
        ));
        MESSAGES.put(NarratorType.SARCASTIC, malcolm);

        // Edgar (Dark)
        Map<String, List<String>> edgar = new HashMap<>();
        edgar.put("block_break", Arrays.asList(
                "narrator.edgar.block_break.1",
                "narrator.edgar.block_break.2",
                "narrator.edgar.block_break.3",
                "narrator.edgar.block_break.4"
        ));
        edgar.put("biome_enter", Arrays.asList(
                "narrator.edgar.biome_enter.1",
                "narrator.edgar.biome_enter.2",
                "narrator.edgar.biome_enter.3",
                "narrator.edgar.biome_enter.4"
        ));
        edgar.put("death", Arrays.asList(
                "narrator.edgar.death.1",
                "narrator.edgar.death.2",
                "narrator.edgar.death.3",
                "narrator.edgar.death.4"
        ));
        edgar.put("combat", Arrays.asList(
                "narrator.edgar.combat.1",
                "narrator.edgar.combat.2",
                "narrator.edgar.combat.3"
        ));
        edgar.put("idle", Arrays.asList(
                "narrator.edgar.idle.1",
                "narrator.edgar.idle.2",
                "narrator.edgar.idle.3",
                "narrator.edgar.idle.4"
        ));
        edgar.put("achievement", Arrays.asList(
                "narrator.edgar.achievement.1",
                "narrator.edgar.achievement.2",
                "narrator.edgar.achievement.3",
                "narrator.edgar.achievement.4"
        ));
        MESSAGES.put(NarratorType.DARK, edgar);

        // Chester (Optimistic)
        Map<String, List<String>> chester = new HashMap<>();
        chester.put("block_break", Arrays.asList(
                "narrator.chester.block_break.1",
                "narrator.chester.block_break.2",
                "narrator.chester.block_break.3",
                "narrator.chester.block_break.4"
        ));
        chester.put("biome_enter", Arrays.asList(
                "narrator.chester.biome_enter.1",
                "narrator.chester.biome_enter.2",
                "narrator.chester.biome_enter.3",
                "narrator.chester.biome_enter.4"
        ));
        chester.put("death", Arrays.asList(
                "narrator.chester.death.1",
                "narrator.chester.death.2",
                "narrator.chester.death.3",
                "narrator.chester.death.4"
        ));
        chester.put("combat", Arrays.asList(
                "narrator.chester.combat.1",
                "narrator.chester.combat.2",
                "narrator.chester.combat.3"
        ));
        chester.put("idle", Arrays.asList(
                "narrator.chester.idle.1",
                "narrator.chester.idle.2",
                "narrator.chester.idle.3",
                "narrator.chester.idle.4"
        ));
        chester.put("achievement", Arrays.asList(
                "narrator.chester.achievement.1",
                "narrator.chester.achievement.2",
                "narrator.chester.achievement.3",
                "narrator.chester.achievement.4"
        ));
        MESSAGES.put(NarratorType.OPTIMISTIC, chester);

        // Reginald (Narcissistic)
        Map<String, List<String>> reginald = new HashMap<>();
        reginald.put("block_break", Arrays.asList(
                "narrator.reginald.block_break.1",
                "narrator.reginald.block_break.2",
                "narrator.reginald.block_break.3",
                "narrator.reginald.block_break.4"
        ));
        reginald.put("biome_enter", Arrays.asList(
                "narrator.reginald.biome_enter.1",
                "narrator.reginald.biome_enter.2",
                "narrator.reginald.biome_enter.3",
                "narrator.reginald.biome_enter.4"
        ));
        reginald.put("death", Arrays.asList(
                "narrator.reginald.death.1",
                "narrator.reginald.death.2",
                "narrator.reginald.death.3",
                "narrator.reginald.death.4"
        ));
        reginald.put("combat", Arrays.asList(
                "narrator.reginald.combat.1",
                "narrator.reginald.combat.2",
                "narrator.reginald.combat.3",
                "narrator.reginald.combat.4"
        ));
        reginald.put("idle", Arrays.asList(
                "narrator.reginald.idle.1",
                "narrator.reginald.idle.2",
                "narrator.reginald.idle.3",
                "narrator.reginald.idle.4"
        ));
        reginald.put("achievement", Arrays.asList(
                "narrator.reginald.achievement.1",
                "narrator.reginald.achievement.2",
                "narrator.reginald.achievement.3",
                "narrator.reginald.achievement.4"
        ));
        MESSAGES.put(NarratorType.NARCISSISTIC, reginald);
    }

    public static String getMessageKey(NarratorType type, String category) {
        Map<String, List<String>> narratorMessages = MESSAGES.get(type);
        if (narratorMessages == null) return "";

        List<String> messages = narratorMessages.get(category);
        if (messages == null || messages.isEmpty()) return "";

        return messages.get(RANDOM.nextInt(messages.size()));
    }

    public static String getMultiplayerMessageKey(String category, String narratorName) {
        List<String> messages = new ArrayList<>();

        switch (category) {
            case "block_break":
                messages.add("narrator." + narratorName.toLowerCase() + ".block_break.1");
                messages.add("narrator." + narratorName.toLowerCase() + ".block_break.2");
                messages.add("narrator." + narratorName.toLowerCase() + ".block_break.3");
                messages.add("narrator." + narratorName.toLowerCase() + ".block_break.4");
                break;
            case "biome_enter":
                messages.add("narrator." + narratorName.toLowerCase() + ".biome_enter.1");
                messages.add("narrator." + narratorName.toLowerCase() + ".biome_enter.2");
                messages.add("narrator." + narratorName.toLowerCase() + ".biome_enter.3");
                messages.add("narrator." + narratorName.toLowerCase() + ".biome_enter.4");
                break;
            case "death":
                messages.add("narrator." + narratorName.toLowerCase() + ".death.1");
                messages.add("narrator." + narratorName.toLowerCase() + ".death.2");
                messages.add("narrator." + narratorName.toLowerCase() + ".death.3");
                messages.add("narrator." + narratorName.toLowerCase() + ".death.4");
                break;
            case "combat":
                messages.add("narrator." + narratorName.toLowerCase() + ".combat.1");
                messages.add("narrator." + narratorName.toLowerCase() + ".combat.2");
                messages.add("narrator." + narratorName.toLowerCase() + ".combat.3");
                break;
            case "idle":
                messages.add("narrator." + narratorName.toLowerCase() + ".idle.1");
                messages.add("narrator." + narratorName.toLowerCase() + ".idle.2");
                messages.add("narrator." + narratorName.toLowerCase() + ".idle.3");
                messages.add("narrator." + narratorName.toLowerCase() + ".idle.4");
                break;
            case "achievement":
                messages.add("narrator." + narratorName.toLowerCase() + ".achievement.1");
                messages.add("narrator." + narratorName.toLowerCase() + ".achievement.2");
                messages.add("narrator." + narratorName.toLowerCase() + ".achievement.3");
                messages.add("narrator." + narratorName.toLowerCase() + ".achievement.4");
                break;
            default:
                messages.add("narrator." + narratorName.toLowerCase() + ".idle.1");
                break;
        }

        return messages.get(RANDOM.nextInt(messages.size()));
    }

    public static String getMessage(NarratorType type, String category) {
        return getMessageKey(type, category);
    }

    public static String getMultiplayerMessage(String category, String narratorName) {
        return getMultiplayerMessageKey(category, narratorName);
    }
}