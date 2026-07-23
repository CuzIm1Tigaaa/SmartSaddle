package de.cuzim1tigaaa.smartsaddle.listeners;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.VersionMatcher;
import de.cuzim1tigaaa.smartsaddle.files.*;
import de.cuzim1tigaaa.smartsaddle.utils.SaddleUtils;
import de.cuzim1tigaaa.smartsaddle.utils.wrapper.EntityWrapper;
import de.cuzim1tigaaa.smartsaddle.utils.wrapper.WrapperUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

	@EventHandler(ignoreCancelled = true)
	public void onSaddleRemove(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player player))
			return;

		if(Config.getConfig().getDisabledWorlds().contains(player.getWorld().getName()))
			return;

		Inventory inventory = event.getClickedInventory();
		if(inventory == null)
			return;

		ItemStack saddle = event.getCurrentItem();
		if(saddle == null || saddle.getType() != Material.SADDLE)
			return;

		InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof LivingEntity entity))
			return;

		EntityWrapper<? extends LivingEntity> targetWrapper = wrapperUtils.getWrappers().values().stream()
				.filter(wrapper -> wrapper.isHolderInstance(holder))
				.findFirst()
				.orElse(null);

		if(targetWrapper == null || event.getRawSlot() != targetWrapper.getSaddleSlot(holder))
			return;

		if(!entity.getPassengers().contains(player))
			return;

		String permission = Permissions.SADDLE_USE + entity.getType().name().toLowerCase();
		if(!player.hasPermission(permission))
			return;

		if(isInventoryFull(player)) {
			player.sendMessage(Config.getConfig().getMessage(Paths.MESSAGES_INVENTORY_FULL));
			return;
		}

		event.setCancelled(true);
		player.getInventory().addItem(saddleUtils.saveEntityToSaddle(entity));
		entity.removePassenger(player);
		entity.remove();

		if(event.isShiftClick())
			player.updateInventory();
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

		if(event.getClickedBlock() == null || !event.hasItem())
			return;

		ItemStack item = event.getItem();
		ItemMeta meta = item.getItemMeta();

		boolean hasTypeKey = meta.getPersistentDataContainer().has(saddleUtils.getTypeKey());
		boolean hasDataKey = meta.getPersistentDataContainer().has(saddleUtils.getDataKey());

		if(!(hasDataKey && hasTypeKey))
			return;

		Location spawnLocation = event.getClickedBlock().getLocation().add(0, 1, 0);
		if(saddleUtils.spawnEntityFromSaddle(player.getUniqueId(), item, spawnLocation)) {
			event.setCancelled(true);
			player.getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
		}
	}
}