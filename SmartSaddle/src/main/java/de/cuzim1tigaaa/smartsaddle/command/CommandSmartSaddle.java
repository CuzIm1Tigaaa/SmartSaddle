package de.cuzim1tigaaa.smartsaddle.command;

import de.cuzim1tigaaa.smartsaddle.SmartSaddle;
import de.cuzim1tigaaa.smartsaddle.files.*;
import de.cuzim1tigaaa.smartsaddle.utils.SaddleUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CommandSmartSaddle implements CommandExecutor, TabCompleter {

	private final SmartSaddle plugin;
	private final SaddleUtils saddleUtils;

	public CommandSmartSaddle(SmartSaddle plugin) {
		this.plugin = plugin;
		this.saddleUtils = plugin.getSaddleUtils();
		plugin.getCommand("smartsaddle").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission(Permissions.SADDLE_COMMAND)) {
			sender.sendMessage(Config.getConfig().getMessage(Paths.MESSAGES_PERMISSION));
			return true;
		}

		if(args.length > 0 && sender instanceof Player player) {
			switch(args[0]) {
				case "data" -> {
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item.getType().isAir())
						return true;

					ItemMeta meta = item.getItemMeta();
					if(!meta.getPersistentDataContainer().has(saddleUtils.getDataKey()))
						return true;

					String data = meta.getPersistentDataContainer().get(saddleUtils.getDataKey(), PersistentDataType.STRING);
					player.sendMessage(data);
					return true;
				}
			}
		}

		sender.sendMessage(Config.getConfig().getMessage(Paths.MESSAGES_RELOADED));
		plugin.reload();
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return List.of();
	}
}