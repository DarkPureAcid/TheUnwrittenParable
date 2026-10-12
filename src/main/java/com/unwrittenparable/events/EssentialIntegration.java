package com.unwrittenparable.events;

import com.unwrittenparable.TheUnwrittenParable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class EssentialIntegration {
    private static final Random RANDOM = new Random();
    private static Boolean essentialAvailable = null;

    public static boolean isEssentialAvailable() {
        if (essentialAvailable == null) {
            try {
                Class.forName("net.essentialmc.EssentialMod");
                essentialAvailable = true;
                TheUnwrittenParable.LOGGER.info("Essential mod detected!");
            } catch (ClassNotFoundException e) {
                essentialAvailable = false;
                TheUnwrittenParable.LOGGER.info("Essential mod not found. Friends list integration disabled.");
            }
        }
        return essentialAvailable;
    }

    public static List<String> getFriendsList() {
        List<String> friends = new ArrayList<>();

        if (!isEssentialAvailable()) return friends;

        try {
            Class<?> apiClass = Class.forName("net.essentialmc.api.EssentialAPI");
            Method getInstance = apiClass.getMethod("getInstance");
            Object api = getInstance.invoke(null);

            Method getFriends = api.getClass().getMethod("getFriends");
            Object friendsList = getFriends.invoke(api);

            if (friendsList instanceof List<?>) {
                for (Object friend : (List<?>) friendsList) {
                    Method getName = friend.getClass().getMethod("getName");
                    String name = (String) getName.invoke(friend);
                    friends.add(name);
                }
            }
        } catch (Exception e) {
            TheUnwrittenParable.LOGGER.debug("Failed to get Essential friends list", e);
        }

        return friends;
    }

    public static String getRandomFriend() {
        List<String> friends = getFriendsList();
        if (friends.isEmpty()) {
            return null;
        }
        return friends.get(RANDOM.nextInt(friends.size()));
    }
}