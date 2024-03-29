package net.kigawa.spigot.pluginutil;

import net.kigawa.kutil.kutil.KutilFile;
import net.kigawa.kutil.kutil.interfaces.HasEnd;
import net.kigawa.kutil.log.log.Logger;
import net.kigawa.spigot.pluginutil.command.CommandManager;
import net.kigawa.spigot.pluginutil.command.CommandParent;
import net.kigawa.spigot.pluginutil.message.Messenger;
import net.kigawa.spigot.pluginutil.player.User;
import net.kigawa.spigot.pluginutil.player.UserManager;
import net.kigawa.spigot.pluginutil.recorder.Recorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class PluginBase extends JavaPlugin implements Listener, CommandParent {
    public static boolean debug;
    public static boolean useDB;
    public static boolean log;
    public static Logger logger;
    private final List<HasEnd> hasEnds = new ArrayList<>();
    private Recorder recorder;
    private Messenger messenger;
    private UserManager userManager;

    public PluginBase() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("debug", false);
        config.addDefault("log", true);

        addConfigDefault(config);
        config.options().copyDefaults(true);
        this.saveConfig();

        debug = config.getBoolean("debug");
        useDB = config.getBoolean("useDB");
        log = config.getBoolean("log");

        Level level = Level.INFO;
        if (debug) level = Level.FINEST;
        File logDir = null;
        if (log) logDir = KutilFile.getFile(getDataFolder(), "logs");

        logger = new Logger(getName(), getLogger(), level, logDir);
        logger.enable();
    }

    public abstract void addConfigDefault(FileConfiguration config);

    public abstract void enable();

    public abstract void disable();

    public abstract void load();

    /**
     * @deprecated
     */
    public void onStart() {
    }


    @Override
    public void onLoad() {
        logger.info("loading...");


        load();
    }

    @Override
    public void onEnable() {

        CommandManager.enable(this);

        logger.info("enable " + getName());

        recorder = new Recorder(this);
        messenger = new Messenger(this);
        userManager = new UserManager(this);

        registerEvents(this);

        onStart();
        enable();
    }


    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        User.playerJoinEvent(event, this);
    }

    @EventHandler
    public void leaveEvent(PlayerQuitEvent event) {
        User.playerQuitEvent(event);
    }

    @Override
    public void onDisable() {
        logger.info("disable " + getName());
        for (HasEnd hasEnd : hasEnds) {
            hasEnd.end();
        }
        User.onDisable(this);

        disable();
        logger.disable();
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * @deprecated
     */
    public void logger(String message) {
        if (debug) {
            this.getLogger().info(message);
        }
    }

    /**
     * @deprecated
     */
    public void logger(int message) {
        if (debug) {
            this.getLogger().info(Integer.toString(message));
        }
    }

    /**
     * @deprecated
     */
    public void logger(boolean message) {
        logger(String.valueOf(message));
    }

    /**
     * @deprecated
     */
    public void logger(double message) {
        logger(Double.toString(message));
    }

    public void addHasEnd(HasEnd hasEnd) {
        hasEnds.add(hasEnd);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public CommandParent getCommandParent() {
        return null;
    }
}
