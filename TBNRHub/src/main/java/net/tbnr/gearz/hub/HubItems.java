package net.tbnr.gearz.hub;

import net.tbnr.gearz.hub.items.warpstar.WarpStarConfig;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by rigor789 on 2013.12.21.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class HubItems implements Listener {

    private final ArrayList<HubItem> items;

    private final WarpStarConfig warpStarConfig = null;
	/**
	 * Creates a new HubItems instance
	 * @param itemPackage ~ the package where all the items are
	 */
    public HubItems(String itemPackage) {
        items = new ArrayList<>();
	    Reflections hubItemsReflection = new Reflections(itemPackage);

	    Set<Class<? extends HubItem>> hubItems = hubItemsReflection.getSubTypesOf(HubItem.class);

	    for(Class<? extends HubItem> hubItem: hubItems) {
		    HubItemMeta itemMeta = hubItem.getAnnotation(HubItemMeta.class);
            if (itemMeta == null) continue;
		    if(itemMeta.hidden()) continue;
		    if(TBNRHub.getInstance().getConfig().getBoolean("hub-items." + itemMeta.key()+ ".isEnabled", false)) {
			    try {
				    HubItem item = hubItem.newInstance();
				    items.add(item);
			    } catch (InstantiationException | IllegalAccessException e) {
				    e.printStackTrace();
			    }
		    }
	    }

    }

    public void refreshWarpStar() {
        warpStarConfig.refresh();
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPlayerJoin(TPlayerJoinEvent event) {
		Player player = event.getPlayer().getPlayer();
	    ItemStack itemStack;
	    ItemStack itemInSlot;
	    for (HubItem item : items) {
		    if (!shouldAdd(player, item.getItems())) continue;
            /*if(item.getItems().get(0).getType() == Material.ANVIL) {
                if(!event.getPlayer().hasPermission("gearz.serverselector")) continue;
                event.getPlayer().getInventory().addItem(item.getItems().get(0));
            }*/

		    HubItemMeta itemMeta = item.getClass().getAnnotation(HubItemMeta.class);
		    if (itemMeta == null) continue;
		    if(itemMeta.hidden()) continue;

		    if(player.hasPermission(itemMeta.permission()) ||
				    itemMeta.permission().isEmpty()) {
			    itemStack = item.getItems().get(0);
			    itemInSlot = player.getInventory().getItem(itemMeta.slot());
			    if(itemInSlot != null && itemInSlot.getType() != Material.AIR && itemMeta.slot() == -1) {
				    player.getInventory().addItem(itemStack);
				    continue;
			    }
			    player.getInventory().setItem(itemMeta.slot(), itemStack);
		    }
	    }


    }

    private boolean shouldAdd(Player player, List<ItemStack> item) {
	    for(ItemStack i: item) {
		    if(player.getInventory().contains(i)) return false;
	    }
	    return true;
    }
}
