package de.cuzim1tigaaa.smartsaddle.listeners;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.VersionMatcher;
import de.cuzim1tigaaa.smartsaddle.files.*;
import de.cuzim1tigaaa.smartsaddle.utils.SaddleUtils;
import de.cuzim1tigaaa.smartsaddle.utils.wrapper.EntityWrapper;
import de.cuzim1tigaaa.smartsaddle.utils.wrapper.WrapperUtils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class SaddleEvent implements Listener {

	private final WrapperUtils wrapperUtils;
	private final SaddleUtils saddleUtils;

	public SaddleEvent(SmartSaddle plugin) {
		this.wrapperUtils = plugin.getWrapperUtils();
		this.saddleUtils = plugin.getSaddleUtils();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSaddleRemove(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(Config.getConfig().getDisabledWorlds().contains(player.getWorld().getName()))
			return;

		Inventory inventory = event.getClickedInventory();
		if(inventory == null)
			return;

		InventoryHolder holder = inventory.getHolder();
		if(holder == null)
			return;

		EntityWrapper<? extends LivingEntity> targetWrapper = null;
		for(EntityWrapper<?> wrapper : wrapperUtils.getWrappers().values()) {
			if(wrapper.isHolderInstance(holder)) {
				targetWrapper = wrapper;
				break;
			}
		}

		if(targetWrapper == null)
			return;

		if(event.getRawSlot() != targetWrapper.getSaddleSlot(holder))
			return;

		LivingEntity entity = (LivingEntity) holder;
		if(!entity.getPassengers().contains(player))
			return;

		if(isInventoryFull(player)) {
			player.sendMessage(Config.getConfig().getMessage(Paths.MESSAGES_INVENTORY_FULL));
			return;
		}

		if(!player.hasPermission(Permissions.SADDLE_USE + entity.getType().name().toLowerCase()))
			return;

		event.setCancelled(true);
		if(event.isShiftClick())
			player.updateInventory();

		player.getInventory().addItem(saddleUtils.saveEntityToSaddle(entity));
		entity.removePassenger(player);
		entity.remove();
	}

	@EventHandler
	public void onHappyGhastShear(PlayerInteractAtEntityEvent event) {
		if(!VersionMatcher.isHappyGhastAvailable())
			return;

		Player player = event.getPlayer();
		if(Config.getConfig().getDisabledWorlds().contains(player.getWorld().getName()))
			return;

		if(!player.isSneaking() || player.getEquipment().getItemInMainHand().getType() != Material.SHEARS)
			return;

		Entity entity = event.getRightClicked();
		if(!(entity instanceof HappyGhast happyGhast))
			return;

		Material material = happyGhast.getEquipment().getItem(EquipmentSlot.BODY).getType();
		if(!material.name().endsWith("_HARNESS"))
			return;

		if(!player.hasPermission(Permissions.SADDLE_USE + entity.getType().name().toLowerCase()))
			return;

		player.getInventory().addItem(saddleUtils.saveEntityToSaddle(happyGhast));
		happyGhast.remove();
	}

	public boolean isInventoryFull(Player p) {
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

		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if(event.getClickedBlock() == null || event.getItem() == null)
			return;

		ItemStack item = event.getItem();
		ItemMeta meta = item.getItemMeta();

		if(!meta.getPersistentDataContainer().has(saddleUtils.getTypeKey()))
			if(!meta.getPersistentDataContainer().has(saddleUtils.getDataKey()))
				return;

		if(saddleUtils.spawnEntityFromSaddle(player.getUniqueId(), item, event.getClickedBlock().getLocation().add(0, 1, 0))) {
			event.setCancelled(true);
			player.getInventory().remove(item);
		}
	}
}