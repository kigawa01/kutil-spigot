package net.kigawa.spigot.pluginutil.command;

import net.kigawa.spigot.pluginutil.PluginBase;

public abstract class Subcommand extends Command {
    public Subcommand(PluginBase pluginBase, CommandParent commandParent) {
        super(pluginBase, commandParent);
    }
}
