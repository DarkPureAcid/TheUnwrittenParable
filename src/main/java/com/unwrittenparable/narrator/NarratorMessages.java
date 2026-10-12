package com.unwrittenparable.narrator;

import java.util.*;

public class NarratorMessages {
    private static final Random RANDOM = new Random();

    private static final Map<NarratorType, Map<String, List<String>>> MESSAGES = new HashMap<>();

    static {
        // Stanley
        Map<String, List<String>> stanley = new HashMap<>();
        stanley.put("block_break", Arrays.asList(
                "Stanley broke a block. How productive.",
                "Another block gone. Stanley's making progress... probably.",
                "Breaking blocks. That's what Stanley does best.",
                "The block didn't stand a chance against Stanley."
        ));
        stanley.put("biome_enter", Arrays.asList(
                "Stanley walked into a new biome. How adventurous.",
                "The scenery changed. Stanley didn't seem to notice.",
                "A new biome. Stanley wondered if this one would be different.",
                "The biome shifted. Stanley continued walking."
        ));
        stanley.put("death", Arrays.asList(
                "Stanley died. This was not part of the plan.",
                "And so Stanley's journey came to an abrupt end.",
                "Death found Stanley. Stanley did not appreciate this.",
                "The narrator watched Stanley die. The narrator felt nothing."
        ));
        stanley.put("combat", Arrays.asList(
                "Stanley was fighting for his life.",
                "The creature attacked. Stanley defended. It was chaos.",
                "Combat. Stanley was not prepared for this."
        ));
        stanley.put("idle", Arrays.asList(
                "Stanley stood still. The narrator waited.",
                "Nothing happened. The narrator grew impatient.",
                "Stanley wasn't doing anything. This was boring.",
                "The silence was deafening. Stanley did nothing."
        ));
        stanley.put("achievement", Arrays.asList(
                "Stanley achieved something. The narrator took notes.",
                "An achievement. Stanley was making progress after all.",
                "The narrator acknowledged Stanley's accomplishment. Briefly.",
                "Stanley earned an achievement. How industrious."
        ));
        MESSAGES.put(NarratorType.STANLEY, stanley);

        // Helpful Narrator
        Map<String, List<String>> helpful = new HashMap<>();
        helpful.put("block_break", Arrays.asList(
                "Good job! You broke a block. Keep going!",
                "That block was in your way. Problem solved!",
                "Mining is a noble profession. Keep at it!",
                "Every block broken is progress made!"
        ));
        helpful.put("biome_enter", Arrays.asList(
                "Oh, a new biome! Look around, there might be resources here!",
                "You've entered a new area. Explore carefully!",
                "New biome detected. Adventure awaits!",
                "The scenery changed! This could be exciting!"
        ));
        helpful.put("death", Arrays.asList(
                "Oh no! Don't worry, we'll try again!",
                "That didn't go as planned. Let's learn from this!",
                "Everyone dies sometimes. It's part of the journey!",
                "Unfortunate. But every failure is a lesson!"
        ));
        helpful.put("combat", Arrays.asList(
                "Fight bravely! You've got this!",
                "Combat time! Remember to dodge!",
                "Watch your health! Be careful!"
        ));
        helpful.put("idle", Arrays.asList(
                "Are you okay? You seem to be standing still.",
                "Take your time. No rush!",
                "Resting is important too!",
                "Whenever you're ready, adventure awaits!"
        ));
        helpful.put("achievement", Arrays.asList(
                "An achievement! You earned it!",
                "Well done! That's a great accomplishment!",
                "You did it! I'm so proud of you!",
                "Achievement unlocked! Keep up the great work!"
        ));
        MESSAGES.put(NarratorType.HELPFUL, helpful);

        // Sarcastic Narrator
        Map<String, List<String>> sarcastic = new HashMap<>();
        sarcastic.put("block_break", Arrays.asList(
                "Wow. You broke a block. Truly groundbreaking.",
                "Another block destroyed. Your legacy grows.",
                "Mining. How original.",
                "The block is gone. Are you happy now?"
        ));
        sarcastic.put("biome_enter", Arrays.asList(
                "Oh look, a new biome. I'm sure this one will be different.",
                "The scenery changed. Riveting.",
                "A new biome. Because the last one was boring, right?",
                "Welcome to somewhere else. It's all the same really."
        ));
        sarcastic.put("death", Arrays.asList(
                "And there it is. I was wondering when that would happen.",
                "Dead. Shocking. Truly unpredictable.",
                "You died. The narrator is not surprised.",
                "Death: the one thing you're consistent at."
        ));
        sarcastic.put("combat", Arrays.asList(
                "Oh, you're fighting. This should be entertaining.",
                "Combat. Let's see how this goes.",
                "A battle. How thrilling. I'll wait."
        ));
        sarcastic.put("idle", Arrays.asList(
                "Standing still. Bold strategy.",
                "Are you going to do something, or...?",
                "The narrator waits. And waits. And waits.",
                "Idle. Just like your ambitions."
        ));
        sarcastic.put("achievement", Arrays.asList(
                "An achievement. I suppose that's something.",
                "You got an achievement. Do you want a medal?",
                "Achievement unlocked. The narrator is underwhelmed.",
                "Congratulations on your achievement. Truly."
        ));
        MESSAGES.put(NarratorType.SARCASTIC, sarcastic);

        // Dark Narrator
        Map<String, List<String>> dark = new HashMap<>();
        dark.put("block_break", Arrays.asList(
                "The block crumbled. Like everything else.",
                "Destruction. It's all you know.",
                "Another piece of the world, gone forever.",
                "The earth weeps as you tear it apart."
        ));
        dark.put("biome_enter", Arrays.asList(
                "You've entered a place of shadows. Be wary.",
                "The biome shifted. Something watches here.",
                "New territory. The darkness grows.",
                "The land changed. It remembers you."
        ));
        dark.put("death", Arrays.asList(
                "Death comes for us all. Today it came for you.",
                "The end. As it was always going to be.",
                "You fell. The darkness is patient.",
                "Another soul claimed. The cycle continues."
        ));
        dark.put("combat", Arrays.asList(
                "Blood will be spilled. It is inevitable.",
                "The creatures hunger. They always do.",
                "Fight if you wish. It changes nothing."
        ));
        dark.put("idle", Arrays.asList(
                "Silence. The darkness gathers.",
                "You stand still. It watches.",
                "The stillness is deafening. Something approaches.",
                "Idle hands. The devil's workshop."
        ));
        dark.put("achievement", Arrays.asList(
                "An achievement. The darkness takes notice.",
                "You earned something. But at what cost?",
                "Achievement unlocked. The shadows grow longer.",
                "A accomplishment. The void acknowledges you."
        ));
        MESSAGES.put(NarratorType.DARK, dark);

        // Optimistic Narrator
        Map<String, List<String>> optimistic = new HashMap<>();
        optimistic.put("block_break", Arrays.asList(
                "You broke a block! That's the spirit!",
                "Mining away! What a beautiful day!",
                "Another block! You're doing amazing!",
                "Blocks are meant to be broken! Keep going!"
        ));
        optimistic.put("biome_enter", Arrays.asList(
                "A new biome! Isn't nature wonderful?!",
                "Look at this beautiful place! Everything is amazing!",
                "New adventures! New possibilities! Life is good!",
                "The world is full of wonders! Explore them all!"
        ));
        optimistic.put("death", Arrays.asList(
                "You died! But hey, at least you tried!",
                "Death is just a new beginning! How exciting!",
                "Oops! Well, nobody's perfect! Let's go again!",
                "You fell! But the important thing is you had fun!"
        ));
        optimistic.put("combat", Arrays.asList(
                "A battle! What an exciting opportunity!",
                "Fight with joy in your heart!",
                "Combat! Every moment is a gift!"
        ));
        optimistic.put("idle", Arrays.asList(
                "Resting! Good for you! Self-care is important!",
                "Taking a moment! How peaceful!",
                "Standing still! Enjoying the view!",
                "Life is beautiful! Even when you're doing nothing!"
        ));
        optimistic.put("achievement", Arrays.asList(
                "AN ACHIEVEMENT! THIS IS THE BEST DAY EVER!",
                "YOU DID IT! I KNEW YOU COULD!",
                "ACHIEVEMENT UNLOCKED! YOU'RE AMAZING!",
                "WOW! AN ACHIEVEMENT! I'M SO HAPPY FOR YOU!"
        ));
        MESSAGES.put(NarratorType.OPTIMISTIC, optimistic);

        // Narcissistic Narrator
        Map<String, List<String>> narcissistic = new HashMap<>();
        narcissistic.put("block_break", Arrays.asList(
                "I told you to break that block. You're welcome.",
                "Another block broken, thanks to my guidance.",
                "You broke a block. I deserve credit for this.",
                "Mining. I would've done it better, but still."
        ));
        narcissistic.put("biome_enter", Arrays.asList(
                "I chose this biome for you. You should be grateful.",
                "A new biome. I planned this all along.",
                "Welcome to my masterpiece. Look around.",
                "The biome changed. I made that happen."
        ));
        narcissistic.put("death", Arrays.asList(
                "You died. I would never.",
                "Death? How embarrassing. I'm immortal.",
                "You fell. I could've prevented that. I chose not to.",
                "Dead. The narrator remains superior."
        ));
        narcissistic.put("combat", Arrays.asList(
                "Fight well. I'm watching. Taking notes.",
                "Combat. I could win this in my sleep.",
                "Show me what you've learned from me.",
                "Battle. I've seen better. But try."
        ));
        narcissistic.put("idle", Arrays.asList(
                "Standing still. Waiting for my next brilliant instruction.",
                "Idle. Even I need a break from being perfect.",
                "Nothing happens. The narrator is still the best.",
                "Waiting. I have all the time in the world."
        ));
        narcissistic.put("achievement", Arrays.asList(
                "An achievement. I taught you everything you know.",
                "You earned something. I deserve the credit.",
                "Achievement unlocked. My guidance paid off.",
                "Congratulations to me for your achievement."
        ));
        MESSAGES.put(NarratorType.NARCISSISTIC, narcissistic);
    }

    public static String getMessage(NarratorType type, String category) {
        Map<String, List<String>> narratorMessages = MESSAGES.get(type);
        if (narratorMessages == null) return "";

        List<String> messages = narratorMessages.get(category);
        if (messages == null || messages.isEmpty()) return "";

        return messages.get(RANDOM.nextInt(messages.size()));
    }

    public static String getMultiplayerMessage(String category, String narratorName) {
        List<String> messages = new ArrayList<>();

        switch (category) {
            case "block_break":
                messages.add(narratorName + " watches you mine.");
                messages.add(narratorName + " approves of your mining.");
                messages.add(narratorName + " notes your progress.");
                messages.add(narratorName + " observes silently.");
                break;
            case "biome_enter":
                messages.add(narratorName + " knows this place.");
                messages.add(narratorName + " has been here before.");
                messages.add(narratorName + " watches from the shadows.");
                messages.add(narratorName + " follows you.");
                break;
            case "death":
                messages.add(narratorName + " is not surprised.");
                messages.add(narratorName + " saw that coming.");
                messages.add(narratorName + " does not mourn.");
                messages.add(narratorName + " watches you fall.");
                break;
            case "combat":
                messages.add(narratorName + " watches the battle.");
                messages.add(narratorName + " is entertained.");
                messages.add(narratorName + " waits patiently.");
                break;
            case "idle":
                messages.add(narratorName + " is waiting.");
                messages.add(narratorName + " grows impatient.");
                messages.add(narratorName + " watches you do nothing.");
                messages.add(narratorName + " observes the silence.");
                break;
            case "achievement":
                messages.add(narratorName + " acknowledges your achievement.");
                messages.add(narratorName + " watched you succeed.");
                messages.add(narratorName + " notes your progress.");
                messages.add(narratorName + " sees your accomplishment.");
                break;
            default:
                messages.add(narratorName + " speaks.");
                break;
        }

        return messages.get(RANDOM.nextInt(messages.size()));
    }
}