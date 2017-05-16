/* MIT License
Copyright (c) 2017 SimplePlugins

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

Written by Matthew Hogan <matt@matthogan.co.uk>, May 2017
*/
package co.uk.matthogan.simpleplugins.simplekills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>Simple and lightweight Spigot plugin that runs command(s)
 * when a player kills another player</p>
 *
 * @author Matthew Hogan
 */
public class SimpleKillsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        EntityDamageByEntityEvent lastDamage = (EntityDamageByEntityEvent)
                event.getEntity().getLastDamageCause();

        // NOTE: Make sure that if the damaging entity is an arrow it
        // NOTE: was shot by a player
        if (lastDamage.getDamager().getType() == EntityType.ARROW &&
                !(((Arrow) lastDamage.getDamager()).getShooter() instanceof Player))
        {
            return;
        }

        Player attacker;

        if (lastDamage.getDamager().getType() == EntityType.ARROW) {
            attacker = (Player) ((Arrow) lastDamage.getDamager()).getShooter();
        } else if (lastDamage.getDamager().getType() == EntityType.PLAYER){
            attacker = (Player) lastDamage.getDamager();
        } else {
            return;
        }

        // NOTE: Permission check
        if (!attacker.hasPermission("simplekills.receive")) {
            return;
        }

        Player victim = event.getEntity();

        // NOTE: Dispatch the commands
        for (String command : this.getConfig().getStringList("run-commands")) {
            command = command.replace("%attacker%", attacker.getName())
                    .replace("%victim%", victim.getName());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}