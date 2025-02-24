package de.cuzim1tigaaa.smartsaddle.listeners;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.files.*;
import de.cuzim1tigaaa.smartsaddle.utils.SaddleUtils;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

public class SaddleEvent implements Listener {

	private final SaddleUtils saddleUtils;

	public SaddleEvent(SmartSaddle plugin) {
		this.saddleUtils = new SaddleUtils(plugin);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSaddleRemove(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(Config.getConfig().getDisabledWorlds().contains(player.getWorld().getName()))
			return;

		if(!(event.getInventory() instanceof AbstractHorseInventory horseInv))
			return;

		if(event.getCurrentItem() != null) {
			ItemStack item = event.getCurrentItem();
			if(item.getType() != Material.SADDLE)
				return;

			if(!item.getItemMeta().getPersistentDataContainer().has(saddleUtils.getHorseDataKey(), PersistentDataType.STRING))
				return;

			event.setCancelled(true);
			return;
		}

		if(event.getRawSlot() != 0 || horseInv.getSaddle() == null)
			return;

		if(!(horseInv.getHolder() instanceof AbstractHorse horse))
			return;

		if(!horse.getPassengers().contains(player))
			return;

		if(inventoryIsFull(player)) {
			player.sendMessage(Config.getConfig().getMessage(Paths.MESSAGES_INVENTORY_FULL));
			return;
		}

		switch(horse.getType()) {
			case HORSE, SKELETON_HORSE, ZOMBIE_HORSE, DONKEY, MULE, CAMEL -> {
				if(!player.hasPermission(Permissions.SADDLE_USE + horse.getType().name().toLowerCase()))
					return;
			}
		}

		event.setCancelled(true);
		if(event.isShiftClick())
			player.updateInventory();

		player.getInventory().addItem(saddleUtils.saveHorseToSaddle(horse));
		horse.removePassenger(player);
		horse.remove();
	}

	public boolean inventoryIsFull(Player p) {
		Inventory pInv = p.getInventory();
		boolean invIsFull = true;

		for(int i = 0; i < 36; i++)
			if(pInv.getItem(i) == null) {
				invIsFull = false;
				break;
			}

		return invIsFull;
	}

	@EventHandler
	public void onSaddlePlace(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(Config.getConfig().getDisabledWorlds().contains(player.getWorld().getName()))
			return;

		if(event.getClickedBlock() == null || event.getItem() == null)
			return;

		ItemStack item = event.getItem();
		if(item.getType() != Material.SADDLE)
			return;

		if(saddleUtils.spawnHorseFromSaddle(item, event.getClickedBlock().getLocation().add(0, 1, 0))) {
			event.setCancelled(true);
			player.getInventory().remove(item);
		}
	}
}