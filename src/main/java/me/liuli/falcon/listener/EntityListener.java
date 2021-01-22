package me.liuli.falcon.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.combat.CriticalsCheck;
import me.liuli.falcon.check.combat.KillauraCheck;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class EntityListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        boolean shouldFlag=false;
        if(event.getEntity() instanceof Player){
            Player player=(Player) event.getEntity();
            if(AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
                CheckCache.get(player).fakePlayer.doSwing(player);
            }
        }
        if(event.getDamager() instanceof Player){
            Player player=(Player) event.getDamager();
            if(AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
                CheckCache.get(player).fakePlayer.showDamage(player);
                FakePlayerManager.playerHurt(player);
            }
            if(AnticheatManager.canCheckPlayer(player,CheckType.KILLAURA)){
                CheckResult checkResult=KillauraCheck.checkAngle(player,event);
                if(checkResult.failed()){
                    shouldFlag=AnticheatManager.addVL(CheckCache.get(player), CheckType.KILLAURA,checkResult);
                }
                checkResult=KillauraCheck.checkReach(player,event.getEntity());
                if(checkResult.failed()){
                    shouldFlag=AnticheatManager.addVL(CheckCache.get(player), CheckType.KILLAURA,checkResult);
                }
            }
            if(AnticheatManager.canCheckPlayer(player,CheckType.CRITICALS)){
                CheckResult checkResult=CriticalsCheck.doDamageEvent(event);
                if(checkResult.failed()){
                    shouldFlag=AnticheatManager.addVL(CheckCache.get(player), CheckType.CRITICALS,checkResult);
                }
            }
            if(AnticheatManager.canCheckPlayer(player,CheckType.KA_NOSWING)){
                KillauraCheck.addSwingCheckTimer(player);
            }
        }
        if(shouldFlag&&Configuration.flag){
            event.setCancelled();
        }
    }
}
