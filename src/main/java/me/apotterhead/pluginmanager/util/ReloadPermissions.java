// APotterhead
// 02072023-16072023

package me.apotterhead.pluginmanager.util;

import me.apotterhead.pluginmanager.PluginManager;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;

public class ReloadPermissions {

    public enum ReloadType {
        PLAYER,
        GROUP,
        PERMISSION
    }

    public static @Nullable Component reload( ReloadType reloadType, String target, PluginManager plugin  ) {
        return null;
    }
}
