package net.tbnr.gearz.hh;

import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.effects.EnderBar;
import net.tbnr.gearz.game.GameCountdown;
import net.tbnr.gearz.game.GameCountdownHandler;
import net.tbnr.gearz.game.GameMeta;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.player.TPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@GameMeta(
        longName = "Head Hunter",
        shortName = "HH",
        version = "1.0",
        description = "In HeadHunter, the objective is simple, kill everyone. Once you accomplish this task, " +
                "they will drop their heads, and you need to capture them! When you capture a head, you earn one level of Sharpness" +
                ", and for every five heads you earn one level of Speed. These points count for nothing though, " +
                "you must bank your skulls using the diamond for them to count for points. Choosing when to bank" +
                "your skulls is the way to get good at this game!",
        key = "headhunter",
        minPlayers = 6,
        maxPlayers = 48,
        mainColor = ChatColor.DARK_AQUA,
        secondaryColor = ChatColor.DARK_RED)
public final class HeadHunterGame extends GearzGame implements GameCountdownHandler {
    private HeadHunterArena hhArena;
    private HashMap<GearzPlayer, Integer> pointsAwarded;

    /**
     * New game in this arena
     *
     * @param players The players in this game
     * @param arena   The Arena that the game is in.
     * @param plugin  The plugin that handles this Game.
     * @param meta    The meta of the game.
     */
    public HeadHunterGame(List<GearzPlayer> players, Arena arena, GearzPlugin plugin, GameMeta meta, Integer id) {
        super(players, arena, plugin, meta, id);
        if (!(arena instanceof HeadHunterArena)) throw new RuntimeException("Invalid game class");
        this.hhArena = (HeadHunterArena) arena;
        this.pointsAwarded = new HashMap<>();
    }

    @Override
    protected void gameStarting() {
        for (GearzPlayer player : getPlayers()) {
            this.pointsAwarded.put(player, 0);
        }
        updateScoreboard();
        GameCountdown countdown = new GameCountdown(600, this, this);
        countdown.start();
    }

    @Override
    protected void gameEnding() {
        GearzPlayer[] players = getTop(Math.min(8, getPlayers().size()));
        displayWinners(players);
    }

    private GearzPlayer[] getTop(int l) {
        List<GearzPlayer> sortedPoints = getSortedPoints();
        GearzPlayer[] players = new GearzPlayer[l];
        for (int x = 0; x < l; x++) {
            players[x] = sortedPoints.get(x);
        }
        return players;
    }

    @Override
    protected boolean canBuild(GearzPlayer player) {
        return false;
    }

    @Override
    protected boolean canPvP(GearzPlayer attacker, GearzPlayer target) {
        return true;
    }

    @Override
    protected boolean canUse(GearzPlayer player) {
        ItemStack itemInHand = player.getPlayer().getItemInHand();
        if (itemInHand.getType() == Material.DIAMOND) {
            int i = killsInInventory(player);
            if (i == 0) return true;
            for (ItemStack itemStack : player.getPlayer().getInventory()) {
                if (itemStack == null) continue;
                if (itemStack.getType() != Material.SKULL_ITEM) continue;
                player.getPlayer().getInventory().removeItem(itemStack);
            }
            player.getTPlayer().playSound(Sound.BLAZE_BREATH, 20);
            player.getPlayer().playNote(player.getPlayer().getLocation(), Instrument.BASS_DRUM, Note.sharp(1, Note.Tone.D));
            this.pointsAwarded.put(player, this.pointsAwarded.containsKey(player) ? this.pointsAwarded.get(player) + i : i);
            addGPoints(player, i * (i < 5 ? 2 : 5));
            updateScoreboard();
            updatePlayerSword(player);
            player.getPlayer().updateInventory();
        }
        return true;
    }

    @Override
    protected boolean canBreak(GearzPlayer player, Block block) {
        return false;
    }

    @Override
    protected boolean canPlace(GearzPlayer player, Block block) {
        return false;
    }

    @Override
    protected boolean canMove(GearzPlayer player) {
        return true;
    }

    @Override
    protected boolean canDrawBow(GearzPlayer player) {
        return true;
    }

    @Override
    protected void playerKilled(GearzPlayer dead, LivingEntity killer) {

    }

    @Override
    protected void playerKilled(GearzPlayer dead, GearzPlayer killer) {
        addGPoints(killer, Math.max(1, killsInInventory(dead)) * 2);
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) stack.getItemMeta();
        itemMeta.setOwner(dead.getPlayer().getName());
        stack.setItemMeta(itemMeta);
        if (killsInInventory(dead) >= 5) stack.setAmount(2);
        dead.getPlayer().getWorld().dropItemNaturally(dead.getPlayer().getLocation(), stack);
        killer.getPlayer().playNote(killer.getPlayer().getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
    }

    @Override
    protected void mobKilled(LivingEntity killed, GearzPlayer killer) {
    }

    @Override
    protected boolean canDropItem(GearzPlayer player, Item itemToDrop) {
        return false;
    }

    @Override
    protected Location playerRespawn(GearzPlayer player) {
        return getArena().pointToLocation(this.hhArena.spawnPoints.random());
    }

    @Override
    protected boolean canPlayerRespawn(GearzPlayer player) {
        return true;
    }

    @Override
    protected int xpForPlaying() {
        return 200;
    }

    @Override
    protected void activatePlayer(GearzPlayer player) {
        player.getTPlayer().giveItem(Material.STONE_AXE);
        player.getTPlayer().giveItem(Material.DIAMOND, 1, (short) 0, getPluginFormat("formats.diamond-title", false), new String[0], 9);
    }

    @Override
    protected boolean allowHunger(GearzPlayer player) {
        return false;
    }

    @Override
    public boolean canPickup(final GearzPlayer player, Item item) {
        if (item.getItemStack().getType() != Material.SKULL_ITEM) {
            item.remove();
            return false;
        }
        Player player1 = player.getPlayer();
        player1.playNote(player1.getLocation(), Instrument.BASS_DRUM, Note.natural(1, Note.Tone.D));
        player.getTPlayer().playSound(Sound.BLAZE_DEATH, 20);
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                updatePlayerSword(player);
            }
        }, 1L);
        return true;
    }

    @Override
    public void removePlayerFromGame(GearzPlayer player) {
        if (this.pointsAwarded.containsKey(player)) this.pointsAwarded.remove(player);
        updateScoreboard();
    }

    private int killsInInventory(GearzPlayer player) {
        int damage = 0;
        for (ItemStack s : player.getPlayer().getInventory()) {
            if (s == null) continue;
            if (s.getType().equals(Material.SKULL_ITEM)) damage += s.getAmount();
        }
        return damage;
    }

    private void updateScoreboard() {
        for (GearzPlayer player : getPlayers()) {
            if (!player.isValid()) return;
            TPlayer tPlayer = player.getTPlayer();
            tPlayer.setScoreboardSideTitle(getPluginFormat("formats.scoreboard-title", false));
            for (Map.Entry<GearzPlayer, Integer> gearzPlayerIntegerEntry : this.pointsAwarded.entrySet()) {
                tPlayer.setScoreBoardSide(gearzPlayerIntegerEntry.getKey().getUsername(), gearzPlayerIntegerEntry.getValue());
            }
        }
    }

    private void updatePlayerSword(GearzPlayer player) {
        if (!player.isValid()) return;
        int i = killsInInventory(player);
        ItemStack item = player.getPlayer().getInventory().getItem(0);
        item.removeEnchantment(Enchantment.DAMAGE_ALL);

        //test if player has most skulls and that the player has points
        if (player.equals(getMostSkulls()) && this.killsInInventory(player) != 0)
            player.getPlayer().getInventory().setHelmet(new ItemStack(Material.GOLD_BLOCK, 1));
        if (player.equals(getMostPoints()) && this.pointsAwarded.get(player) != 0)
            player.getPlayer().getInventory().setHelmet(new ItemStack(Material.DIAMOND_BLOCK, 1));

        if (i != 0) {
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, (int) Math.round(Math.sqrt(5 * i) - 1));
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
            if (i % 4 == 0) {
                int absorbtionLevel = player.getTPlayer().getCurrentPotionLevel(PotionEffectType.HEALTH_BOOST);
                player.getTPlayer().addInfinitePotionEffect(PotionEffectType.HEALTH_BOOST, absorbtionLevel == -1 ? 1 : absorbtionLevel + 1);
            }
        }
        player.getPlayer().getInventory().setItem(0, item);
    }

    @Override
    protected boolean useEnderBar(GearzPlayer player) {
        return false;
    }

    @Override
    public void onCountdownStart(Integer max, GameCountdown countdown) {
        updateEnderBar(max, max);
    }

    @Override
    public void onCountdownChange(Integer seconds, Integer max, GameCountdown countdown) {
        updateEnderBar(seconds, max);
    }

    @Override
    public void onCountdownComplete(GameCountdown countdown) {
        for (GearzPlayer player : allPlayers()) {
            EnderBar.remove(player);
        }
        GearzPlayer max = getMostPoints();
        broadcast(getPluginFormat("formats.winner", true, new String[]{"<player>", max.getUsername()}));
        addGPoints(max, 250);
        addWin(max);
        max.getTPlayer().playSound(Sound.ENDERDRAGON_GROWL);
        getArena().getWorld().strikeLightningEffect(max.getPlayer().getLocation());
        finishGame();
    }

    @Override
    public boolean allowInventoryChange() {
        return false;
    }

    /**
     * returns the player with most skulls in inventory
     *
     * @return
     */
    private GearzPlayer getMostSkulls() {
        //cache players
        GearzPlayer[] cPlayers = getPlayers().toArray(new GearzPlayer[getPlayers().size()]);

        //person with most skulls
        GearzPlayer most = cPlayers[0];

        //start at 1 to miss out the first 1
        for (int i = 1, l = cPlayers.length; i < l; i++) {
            if (killsInInventory(cPlayers[i]) > killsInInventory(most)) most = cPlayers[i];
        }

        return most;
    }

    /**
     * returns players with most points
     *
     * @return
     */
    private GearzPlayer getMostPoints() {
        //cache players
        GearzPlayer[] cPlayers = getPlayers().toArray(new GearzPlayer[getPlayers().size()]);

        //person with most points
        GearzPlayer most = cPlayers[0];

        //start at 1 to miss out the first 1
        for (int i = 1, l = cPlayers.length; i < l; i++) {
            if (this.pointsAwarded.get(cPlayers[i]) > this.pointsAwarded.get(most)) most = cPlayers[i];
        }

        return most;
    }

    private void updateEnderBar(Integer seconds, Integer max) {
        for (GearzPlayer player : allPlayers()) {
            if (!player.isValid()) return;
            EnderBar.setTextFor(player, getPluginFormat("formats.time-remaining", false, new String[]{"<timespec>", formatInt(seconds)}));
            EnderBar.setHealthPercent(player, (float) seconds / (float) max);
        }
    }


    private String formatInt(Integer integer) {
        if (integer < 60) return String.format("%02d", integer);
        else return String.format("%02d:%02d", (integer / 60), (integer % 60));
    }

    private List<GearzPlayer> getSortedPoints() {
        List<GearzPlayer> playersSorted = new ArrayList<>(this.pointsAwarded.keySet());
        final HashMap<GearzPlayer, Integer> pointsCopy = new HashMap(this.pointsAwarded);
        Collections.sort(playersSorted, new Comparator<GearzPlayer>() {
            @Override
            public int compare(GearzPlayer o1, GearzPlayer o2) {
                return pointsCopy.get(o1) - pointsCopy.get(o2);
            }
        });
        return playersSorted;
    }
}
