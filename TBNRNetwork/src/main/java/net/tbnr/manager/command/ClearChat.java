/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.manager.command;

import net.tbnr.gearz.Gearz;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class to manage utility commands for TBNR
 *
 * <p>
 * Latest Change: Create
 * <p>
 *
 * @author jake
 * @since 3/30/2014
 */
public final class ClearChat implements TCommandHandler {
    @TCommand(
            name = "clearchat",
            usage = "/clearchat",
            permission = "gearz.clearchat.all",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus clearchat(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        for (int i = 0; i <= 200; i++) {
            silentBroadcast("", true);
        }
        silentBroadcast(ChatColor.DARK_AQUA + "+" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 60) + "+", false);
        silentBroadcast(ChatColor.DARK_AQUA + "\u25BA" + ChatColor.RESET + "" + ChatColor.BOLD + " The chat has been cleared by a staff member", false);
        silentBroadcast(ChatColor.DARK_AQUA + "+" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 60) + "+", false);
        sender.sendMessage(ChatColor.GREEN + "Chat cleared!");
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "clearmychat",
            usage = "/clearmychat",
            permission = "gearz.clearchat.own",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus clearmychat(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        for (int i = 0; i <= 200; i++) {
            sender.sendMessage("");
        }
        sender.sendMessage(ChatColor.GREEN + "Chat cleared!");
        return TCommandStatus.SUCCESSFUL;
    }

    private void silentBroadcast(String message, boolean bypassOPs) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if ((p.hasPermission("gearz.clearchat.bypass") || p.isOp()) && bypassOPs) continue;
            p.sendMessage(message);
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.handleCommandStatus(status, sender);
    }
}
