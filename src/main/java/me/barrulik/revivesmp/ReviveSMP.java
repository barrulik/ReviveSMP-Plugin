package me.barrulik.revivesmp;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.*;

public final class ReviveSMP extends JavaPlugin implements Listener {
    ReviveSMP instance;
    @Override
    public void onEnable() {
        instance = this;
        File dir = new File("plugins/config");
        if (!dir.exists()){
            dir.mkdir();
        }


        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);

        System.out.println("revivesmp by Barrulik");

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws IOException {
        Player dead = event.getEntity();
        Entity killer_ = event.getEntity().getKiller();
        setLives(dead, getLives(dead)-1);
        if (killer_ instanceof Player) {
            Player killer = ((Player) killer_).getPlayer();
            setLives(killer, getLives(killer)+1);
        }
    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent e) throws IOException {
        Player p = e.getPlayer();
        if (getLives(p) <= 0){
            p.kickPlayer(ChatColor.AQUA + "Since you have lost all your lives, you cant join the server");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player p = event.getPlayer();
        if (!p.hasPlayedBefore())
            setLives(p, (int)(Math.random()*13+8));
        else {
            File f = new File("plugins/config/" + p.getUniqueId()+".txt");
            if (!f.exists())
                setLives(p, (int)(Math.random()*13+8));
            if (getLives(p) == 0) {
                p.kickPlayer(ChatColor.AQUA + "Since you have lost all your lives, you cant join the server");
                getServer().broadcastMessage(ChatColor.RED + p.getName() + " Tried to join even though he is dead");
            }
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reset")) {
            try {
                generateEmptyConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getServer().broadcastMessage( ChatColor.RED + "Lives has been reset successfully");
        }

        if (command.getName().equalsIgnoreCase("lives")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                try {
                    p.sendMessage(ChatColor.AQUA + "You have " + getLives(p) + " remaining lives");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void generateEmptyConfig() throws IOException {
        File dir = new File("plugins/config");
        for(File file: dir.listFiles())
            file.delete();
        for (Player p : getServer().getOnlinePlayers()){
            setLives(p, (int)(Math.random()*13+8));
        }
    }

    public void setLives(Player p, int lives) throws IOException {
        FileWriter writer = new FileWriter("plugins/config/"+p.getUniqueId()+".txt", false);
        writer.write(""+lives);
        writer.close();
        if (lives <= 0){
            getServer().broadcastMessage( ChatColor.RED + p.getName()+" Just ran out of lives");
        }
    }

    public int getLives(Player p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("plugins/config/" + p.getUniqueId()+".txt"))) {
            String text = br.readLine(); // first line only
            return Integer.parseInt(text);
        } catch (Error err){
            return -1;
        }
    }
}

