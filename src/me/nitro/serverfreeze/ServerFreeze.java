package me.nitro.serverfreeze;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ServerFreeze extends JavaPlugin implements Listener {

    boolean frozen;

    public void onEnable() {
        frozen = false;
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.removePotionEffect(PotionEffectType.SLOW);
            p.removePotionEffect(PotionEffectType.JUMP);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (!sender.hasPermission("serverfreeze.freeze")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lServerFreeze: &7Insufficent Permission."));
                return false;
            }
            if (frozen) {
                frozen = false;
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    p.removePotionEffect(PotionEffectType.SLOW);
                    p.removePotionEffect(PotionEffectType.JUMP);
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                }
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lServerFreeze: &7The server has been &4UNFROZEN&7."));
            } else {
                frozen = true;
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!p.hasPermission("serverfreeze.bypass")) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 141));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 141));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 141));
                    }
                }
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lServerFreeze: &7The server has been &4FROZEN&7."));
            }
        }
        return false;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (frozen && !p.hasPermission("serverfreeze.freeze")) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 141));
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 141));
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 141));
        } else {
            p.removePotionEffect(PotionEffectType.SLOW);
            p.removePotionEffect(PotionEffectType.JUMP);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        p.removePotionEffect(PotionEffectType.SLOW);
        p.removePotionEffect(PotionEffectType.JUMP);
        p.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (frozen && !p.hasPermission("serverfreeze.bypass")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = (Player) e.getPlayer();
        if (frozen && !p.hasPermission("serverfreeze.bypass")) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lServerFreeze: &7You can not break blocks while the server is &4FROZEN&7."));
        }
    }
}
