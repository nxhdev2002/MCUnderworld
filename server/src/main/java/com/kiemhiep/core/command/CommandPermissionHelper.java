package com.kiemhiep.core.command;

import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Method;

/**
 * Permission check that works with both Yarn (hasPermissionLevel) and official Mojang mappings
 * (method name may differ). Requires at least the given level (0=all, 2=op, 4=admin).
 */
public final class CommandPermissionHelper {

    private static final Method HAS_LEVEL_METHOD;
    private static final Method GET_LEVEL_METHOD;

    static {
        Method hasLevel = null;
        Method getLevel = null;
        for (Method m : CommandSourceStack.class.getMethods()) {
            if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == int.class
                && m.getReturnType() == boolean.class) {
                hasLevel = m;
                break;
            }
        }
        if (hasLevel == null) {
            for (Method m : CommandSourceStack.class.getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType() == int.class) {
                    getLevel = m;
                    break;
                }
            }
        }
        HAS_LEVEL_METHOD = hasLevel;
        GET_LEVEL_METHOD = getLevel;
    }

    /**
     * Returns true if the source has at least the given permission level (e.g. 2 for OP).
     * Works with hasPermissionLevel(int) or getPermissionLevel() depending on mappings.
     * If no permission method is found, returns false (deny by default).
     */
    public static boolean hasPermissionLevel(CommandSourceStack source, int requiredLevel) {
        try {
            if (HAS_LEVEL_METHOD != null) {
                return (Boolean) HAS_LEVEL_METHOD.invoke(source, requiredLevel);
            }
            if (GET_LEVEL_METHOD != null) {
                int level = (Integer) GET_LEVEL_METHOD.invoke(source);
                return level >= requiredLevel;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private CommandPermissionHelper() {}
}
