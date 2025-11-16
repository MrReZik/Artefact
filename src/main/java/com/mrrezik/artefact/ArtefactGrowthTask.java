package com.mrrezik.artefact.tasks;

import com.mrrezik.artefact.Artefact;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ArtefactGrowthTask extends BukkitRunnable {

    private final Artefact plugin;
    private final double valueIncrease;
    private final double maxValue;

    public ArtefactGrowthTask(Artefact plugin, double valueIncrease, double maxValue) {
        this.plugin = plugin;
        this.valueIncrease = valueIncrease;
        this.maxValue = maxValue;
    }

    @Override
    public void run() {
        // Эта задача асинхронная. Мы не можем напрямую изменять инвентарь.
        // Мы должны запланировать синхронную задачу для каждого игрока.

        for (Player player : Bukkit.getOnlinePlayers()) {
            // Проверяем, онлайн ли еще игрок
            if (player == null || !player.isOnline()) {
                continue;
            }

            // Запускаем синхронную подзадачу для изменения инвентаря
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) return; // Еще раз проверка

                    ItemStack[] contents = player.getInventory().getContents();
                    for (int i = 0; i < contents.length; i++) {
                        ItemStack item = contents[i];
                        if (item == null) continue;

                        // Используем менеджер для обновления предмета
                        // Он сам проверит NBT, ID и обновит лор
                        plugin.getArtefactManager().updateGrowingArtefact(item, valueIncrease, maxValue);
                    }
                    // Важно: Обновляем инвентарь после цикла
                    player.getInventory().setContents(contents);
                }
            }.runTask(plugin);
        }
    }
}