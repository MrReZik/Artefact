package com.mrrezik.artefact.listeners;

import com.mrrezik.artefact.Artefact;
import com.mrrezik.artefact.util.ChatUtil; // ИЗМЕНЕНО
import com.mrrezik.artefact.util.NBTUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ArtefactBlockListener implements Listener {

    private final Artefact plugin;

    public ArtefactBlockListener(Artefact plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();

        if (NBTUtil.isArtefact(itemInHand)) {
            event.setCancelled(true);

            // ИЗМЕНЕНО: Загружаем сообщение из конфига
            String message = plugin.getConfig().getString("block_place_denied", "&cВы не можете ставить артефакты!");
            player.sendMessage(ChatUtil.color(message));
        }
    }
}