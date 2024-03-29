package net.kigawa.spigot.pluginutil.game;

import net.kigawa.kutil.kutil.Kutil;
import net.kigawa.kutil.kutil.interfaces.HasEnd;
import net.kigawa.spigot.pluginutil.PluginBase;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class GameManagerBase<D extends GameDataBase, G extends GameBase> implements HasEnd, Listener {
    private final List<D> dataList;
    private final List<G> gameList = new ArrayList<>();
    private final PluginBase plugin;

    public GameManagerBase(PluginBase plugin) {
        dataList = plugin.getRecorder().loadAll(getDataClass(), getName());
        this.plugin = plugin;

        plugin.addHasEnd(this);
        plugin.registerEvents(this);
    }

    public abstract String getName();

    public abstract D newData();

    public abstract G newGame(D data);

    public abstract Class<D> getDataClass();

    public abstract Class<G> getGameClass();

    public void execGame(Kutil.Process<G> process) {
        Kutil.executeIterable(getGameList(), process);
    }

    public void save(D data) {
        plugin.getRecorder().save(data, getName());
    }

    public void end() {
        for (G game : new ArrayList<>(gameList)) {
            if (game == null) continue;
            game.end();
        }
    }

    public String end(String name) {
        G game = getGame(name);
        if (game == null) return "game is not started";
        return game.end();
    }

    public void removeGame(G game) {
        gameList.remove(game);
    }

    public String remove(String name) {
        D data = getData(name);
        if (data == null) return name + " is not exit";
        for (G game : gameList) {
            if (game.contain(data)) {
                game.end();
                break;
            }
        }
        dataList.remove(data);
        plugin.getRecorder().remove(data, getName());
        return name + "is removed";
    }

    public String create(String name, String world) {
        PluginBase.logger.info("create game...");
        D data = getData(name);
        if (data != null) return PluginBase.logger.infoPass(name + " is exit");
        data = newData();
        data.setName(name);
        data.setWorld(world);
        getDataList().add(data);
        save(data);
        return PluginBase.logger.infoPass(name + " is created");
    }

    public D getData(String name) {
        for (D data : dataList) {
            if (data.getName() == null) continue;
            if (data.getName().equals(name)) {
                return data;
            }
        }
        return null;
    }

    public G getGame(String name) {
        for (G gameBase : gameList) {
            if (gameBase.equals(name)) {
                return gameBase;
            }
        }
        D data = getData(name);
        if (data == null) return null;
        var game = newGame(data);
        gameList.add(game);
        return game;
    }

    public List<G> getGameList() {
        return gameList;
    }

    public List<D> getDataList() {
        return dataList;
    }

    public PluginBase getPlugin() {
        return plugin;
    }
}
