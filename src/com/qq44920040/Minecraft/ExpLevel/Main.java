package com.qq44920040.Minecraft.ExpLevel;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class Main extends JavaPlugin implements Listener {
    private HashMap<Integer,Integer> LevelInfo = new HashMap<>();
    private String Msg;
    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!file.exists()){
            saveDefaultConfig();
        }
        ReloadConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        super.onEnable();
    }

    @EventHandler
    public void PlayerJoinGame(PlayerJoinEvent event){
        String playername = event.getPlayer().getName();
        if (!getConfig().isSet("Level."+playername)){
            getConfig().set("Level."+playername+".Exp",0);
            saveConfig();
        }
    }

    @EventHandler
    public void PlayergetExp(PlayerExpChangeEvent event){
        String playername =event.getPlayer().getName();
        System.out.println(event.getPlayer().getExp());
        if (getConfig().isSet("Level."+playername)){
            try{
                int LevelUpExp =LevelInfo.get(event.getPlayer().getLevel());
                int LastLevelUpExp =LevelInfo.get(event.getPlayer().getLevel()-1)==null?0:LevelInfo.get(event.getPlayer().getLevel()-1);
                int nowexp = getConfig().getInt("Level."+playername+".Exp");
                event.getPlayer().sendMessage(Msg.replace("{nowExp}",String.valueOf(nowexp)).replace("{ToExp}",String.valueOf(LevelUpExp)));
                nowexp += event.getAmount();
                if (nowexp<=LevelUpExp){
                    event.getPlayer().setExp(((float) (nowexp-LastLevelUpExp)/(LevelUpExp-LastLevelUpExp)));
                }else {
                    event.getPlayer().setLevel(event.getPlayer().getLevel()+1);
                    event.getPlayer().setExp(0);
                }
                getConfig().set("Level."+playername+".Exp",nowexp);
                saveConfig();
            }catch (Exception e){

            }
        }
        event.setAmount(0);
    }


    private void ReloadConfig() {
        Set<String> mines = getConfig().getConfigurationSection("ConfigLevel").getKeys(false);
        for (String temp : mines) {
            LevelInfo.put(Integer.parseInt(temp), getConfig().getInt("ConfigLevel."+temp+".Exp"));
        }
        Msg = getConfig().getString("Msg");
    }

    @Override
    public void onDisable() {

        saveConfig();
        super.onDisable();
    }
}
