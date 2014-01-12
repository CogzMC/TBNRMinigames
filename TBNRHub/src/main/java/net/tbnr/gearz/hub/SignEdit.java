package net.tbnr.gearz.hub;

import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

/**
 * Created by rigor789 on 2014.01.10..
 */
public class SignEdit implements Listener, TCommandHandler {

    private HashMap<String, Sign> players;
    private String name;

    public SignEdit(){
        this.players = new HashMap<>();
        this.name = ChatColor.AQUA + "The magic SIGN!!!!!";
    }

    @EventHandler
    public void onSignPlace(BlockPlaceEvent event){
        if(!event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.name)) return;
        if(!(event.getBlockPlaced().getState() instanceof Sign)) return;
        if(!(event.getBlockAgainst().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getBlockAgainst().getState();
        Sign gui = (Sign) event.getBlockPlaced().getState();
        for(int i = 0; i < sign.getLines().length; i++){
            gui.setLine(i, sign.getLine(i));
        }
        this.players.put(event.getPlayer().getName(), sign);
    }

    @EventHandler
    public void onSignEdit(SignChangeEvent event){
        if(!this.players.containsKey(event.getPlayer().getName())) return;
        Sign sign = players.get(event.getPlayer());
        for(int i = 0; i < event.getLines().length; i++){
            sign.setLine(i, event.getLine(i));
        }
        this.players.remove(event.getPlayer().getName());
    }

    @TCommand(
            name = "magicsign",
            usage = "/magicsign",
            permission = "gearz.magicsign",
            senders = { TCommandSender.Player }
    )
    public TCommandStatus magicsign(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        Player player = (Player) sender;
        ItemStack magic = new ItemStack(Material.SIGN);
        ItemMeta itemMeta = magic.getItemMeta();
        itemMeta.setDisplayName(name);
        player.getInventory().addItem(magic);
        sender.sendMessage(ChatColor.GREEN + "Sign Given!");
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        TBNRHub.handleCommandStatus(status, sender);
    }
}
