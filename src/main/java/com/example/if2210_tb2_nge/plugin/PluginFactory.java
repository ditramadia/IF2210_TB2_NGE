package com.example.if2210_tb2_nge.plugin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PluginFactory {
    @Getter
    @Setter
    private static List<BasePlugin> basePlugins;


    public static void addPlugin(BasePlugin basePluginClass) {
        basePlugins.add(basePluginClass);
    }

    public static void removePlugin(String pluginClassName) {
        for (BasePlugin basePlugin : basePlugins) {
            if (basePlugin.getClass().getName().equals(pluginClassName)) {
                basePlugins.remove(basePlugin);
                break;
            }
        }
    }

    public void removeAllPlugin() {
        basePlugins.clear();
    }

    private void runAll() {
        for (BasePlugin basePlugin : basePlugins) {
            basePlugin.run();
        }
    }


}
