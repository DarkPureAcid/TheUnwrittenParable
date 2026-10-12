package com.unwrittenparable.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EssentialIntegration {
    private static final Random RANDOM = new Random();
    private static boolean essentialAvailable = false;
    private static Object essentialApi = null;

    static {
        try {
            Class<?> essentialClass = Class.forName("net.essentialmc.EssentialMod");
            essentialAvailable = true;
        } catch (ClassNotFoundException e) {
            essentialAvailable = false;
        }
    }

    public static boolean isEssentialAvailable() {
        return essentialAvailable;
    }

    public static List<String> getFriendsList() {
        List<String> friends = new ArrayList<>();

        if (!essentialAvailable) return friends;

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