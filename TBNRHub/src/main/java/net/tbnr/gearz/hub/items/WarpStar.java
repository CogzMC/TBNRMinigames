package net.tbnr.gearz.hub.items;

import net.tbnr.gearz.hub.TBNRHub;
import net.tbnr.gearz.hub.annotations.HubItem;
import net.tbnr.gearz.hub.annotations.HubItemMeta;
import net.tbnr.gearz.hub.items.warpstar.WarpStarConfig;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.inventory.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rigor789 on 2013.12.21.
 *
 * Purpose Of File: To provide a warp star
 *
 * Latest Change: Added hub item meta
 */
@HubItemMeta(
        key = "warpstar",
		slot = 0
)
public class WarpStar extends HubItem {

    private InventoryGUI inventoryGUI;

    public WarpStar() {
        super(true);

        final WarpStarConfig config = new WarpStarConfig();

        this.inventoryGUI = new InventoryGUI(config.getWarps(), "Warp Menu", new InventoryGUI.InventoryGUICallback() {
            @Override
            public void onItemSelect(InventoryGUI gui, InventoryGUI.InventoryGUIItem item, Player player) {
                inventoryGUI.close(player);
                try {
                    player.teleport(config.getLocation(item.getName()));
                } catch (Exception e) {
                    return;
                }
                player.sendMessage(TBNRHub.getInstance().getFormat("warped-to", true, new String[]{"<prefix>", TBNRHub.getInstance().getChatPrefix()}, new String[]{"<warp>", item.getName()}));
                GearzPlayer.playerFromPlayer(player).getTPlayer().playSound(Sound.ENDERMAN_TELEPORT);
                GearzPlayer.playerFromPlayer(player).getTPlayer().playSound(Sound.CHICKEN_EGG_POP);
            }

            @Override
            public void onGUIOpen(InventoryGUI gui, Player player) {}
            @Override
            public void onGUIClose(InventoryGUI gui, Player player) {}
        });
    }

    @Override
    public List<ItemStack> getItems() {
	    List<ItemStack> items = new ArrayList<>();
        ItemStack star = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = star.getItemMeta();
        meta.setDisplayName(getProperty("name"));
        star.setItemMeta(meta);
	    items.add(star);
        return items;
    }

    @Override
    public void rightClicked(Player player) {
        inventoryGUI.open(player);
    }

    @Override
    public void leftClicked(Player player) {
        rightClicked(player);
    }
}
