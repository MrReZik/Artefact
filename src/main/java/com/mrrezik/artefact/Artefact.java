package com.mrrezik.artefact;

import com.mrrezik.artefact.commands.ArtefactCommand;
import com.mrrezik.artefact.listeners.ArtefactBlockListener;
import com.mrrezik.artefact.managers.ArtefactManager;
import com.mrrezik.artefact.managers.MenuManager;
import com.mrrezik.artefact.listeners.MenuListener;
import com.mrrezik.artefact.tasks.ArtefactGrowthTask;
import net.milkbowl.vault.economy.Economy; // НОВЫЙ ИМПОРТ
import org.black_ixx.playerpoints.PlayerPoints; // НОВЫЙ ИМПОРТ
import org.black_ixx.playerpoints.PlayerPointsAPI; // НОВЫЙ ИМПОРТ
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin; // НОВЫЙ ИМПОРТ
import org.bukkit.plugin.RegisteredServiceProvider; // НОВЫЙ ИМПОРТ
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class Artefact extends JavaPlugin {

    private static Artefact instance;
    private ArtefactManager artefactManager;
    private MenuManager menuManager;
    private BukkitTask growthTask;

    // НОВЫЕ ПОЛЯ для API
    private PlayerPointsAPI playerPointsAPI;
    private Economy vaultEconomyAPI;

    @Override
    public void onEnable() {
        instance = this;

        // Загрузка и сохранение конфигов
        this.loadConfiguration();

        // Настройка зависимостей (Vault & PlayerPoints)
        if (!setupDependencies()) {
            this.getLogger().severe("*************************************");
            this.getLogger().severe("Ни PlayerPoints, ни Vault не найдены!");
            this.getLogger().severe("Плагин отключается, так как обмен невозможен.");
            this.getLogger().severe("*************************************");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.artefactManager = new ArtefactManager(this);
        this.menuManager = new MenuManager(this);

        // Регистрация команд
        ArtefactCommand commandExecutor = new ArtefactCommand(this);
        this.getCommand("artefact").setExecutor(commandExecutor);
        this.getCommand("artefact").setTabCompleter(commandExecutor);

        // Регистрация слушателей
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ArtefactBlockListener(this), this);

        // Запуск задачи роста цен
        this.startGrowthTask();

        this.getLogger().info("Плагин Artefacts успешно запущен!");
    }

    @Override
    public void onDisable() {
        if (growthTask != null && !growthTask.isCancelled()) {
            growthTask.cancel();
        }
        this.getLogger().info("Плагин Artefacts выключен.");
    }

    // Загрузка/перезагрузка всех конфигов
    public void loadConfiguration() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.saveDefaultMenuConfig();
    }

    // Метод для перезагрузки всего плагина
    public void reloadPlugin() {
        if (growthTask != null && !growthTask.isCancelled()) {
            growthTask.cancel();
        }

        this.loadConfiguration();

        // Перезагружаем API
        setupDependencies();

        this.getArtefactManager().loadArtefacts();
        this.getMenuManager().loadMenu();

        this.startGrowthTask();
    }

    // Запуск задачи роста цен
    public void startGrowthTask() {
        if (!getConfig().getBoolean("artefact-growth.enabled", false)) {
            return;
        }
        long intervalMinutes = getConfig().getLong("artefact-growth.interval-minutes", 60);
        long intervalTicks = intervalMinutes * 60 * 20;
        if (intervalTicks <= 0) intervalTicks = 72000L;

        double valueIncrease = getConfig().getDouble("artefact-growth.value-increase", 0.1);
        double maxValue = getConfig().getDouble("artefact-growth.max-value", -1.0);

        this.growthTask = new ArtefactGrowthTask(this, valueIncrease, maxValue)
                .runTaskTimerAsynchronously(this, intervalTicks, intervalTicks);

        this.getLogger().info("Задача роста ценности артефактов запущена с интервалом " + intervalMinutes + " мин.");
    }

    private void saveDefaultMenuConfig() {
        File menuFile = new File(this.getDataFolder(), "menu.yml");
        if (!menuFile.exists()) {
            this.saveResource("menu.yml", false);
        }
    }

    // ИЗМЕНЕНО: Настройка ОБОИХ API
    private boolean setupDependencies() {
        // 1. PlayerPoints
        Plugin ppPlugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if (ppPlugin != null) {
            this.playerPointsAPI = ((PlayerPoints) ppPlugin).getAPI();
            this.getLogger().info("PlayerPoints API успешно подключен.");
        } else {
            this.playerPointsAPI = null;
            this.getLogger().warning("PlayerPoints не найден. Обмен на очки будет недоступен.");
        }

        // 2. Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                this.vaultEconomyAPI = null;
                this.getLogger().warning("Vault найден, но не удалось подключить Economy. Обмен на монеты будет недоступен.");
            } else {
                this.vaultEconomyAPI = rsp.getProvider();
                this.getLogger().info("Vault Economy API успешно подключен.");
            }
        } else {
            this.vaultEconomyAPI = null;
            this.getLogger().warning("Vault не найден. Обмен на монеты будет недоступен.");
        }

        // Плагин может работать, только если есть ХОТЯ БЫ ОДНА система
        return playerPointsAPI != null || vaultEconomyAPI != null;
    }

    // Геттеры
    public static Artefact getInstance() {
        return instance;
    }

    public ArtefactManager getArtefactManager() {
        return artefactManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    // НОВЫЕ ГЕТТЕРЫ API
    public PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    public Economy getVaultEconomyAPI() {
        return vaultEconomyAPI;
    }

    // УДАЛЕН: Метод color() (теперь в ChatUtil)
}