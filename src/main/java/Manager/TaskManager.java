package Manager;

import chatgames.ChatGames;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class TaskManager {
   private final ChatGames plugin;
   public ScheduledTask timeExpiredTask;

   public TaskManager(ChatGames instance) {
      this.plugin = instance;
   }

   public void restartTask() {
      if (this.plugin.mainTask != null) {
         this.plugin.mainTask.cancel();
      }
      this.initTask();
   }

   public ScheduledTask timeExpiredTask() {
      if (this.timeExpiredTask != null) {
         this.timeExpiredTask.cancel();
      }
      this.timeExpiredTask = Bukkit.getGlobalRegionScheduler().runDelayed(this.plugin, (task) -> {
         if (this.plugin.inGame && this.plugin.selected != null) {
            this.plugin.inGame = false;
            this.plugin.gameManager.sendTimeExpiredToWorlds(this.plugin.selected, this.plugin.selectedGame);
            this.plugin.selected = null;
            this.plugin.selectedGame = null;
            this.plugin.gameManager.stopTimer();
         }
      }, this.plugin.getConfig().getInt("timeToGuess_seconds") * 20L);
      return this.timeExpiredTask;
   }

   public void initTask() {
      long delay = this.plugin.getConfig().getInt("time_minutes") * 20 * 60L;
      long period = this.plugin.getConfig().getInt("time_minutes") * 20 * 60L;
      this.plugin.mainTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(this.plugin, (task) -> {
         synchronized (this.plugin.games) {
            if (this.checkPlayersOnline()) {
               this.plugin.selectedGame = this.plugin.games.get(ThreadLocalRandom.current().nextInt(this.plugin.games.size()));
               this.plugin.gameManager.startGame(GameManager.GameType.valueOf(this.plugin.selectedGame.toUpperCase()));
            }
         }
      }, delay, period);
   }

   public boolean checkPlayersOnline() {
      int vanished = 0;

      for (Player player : Bukkit.getOnlinePlayers()) {
         for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
               vanished++;
            }
         }
      }

      return Bukkit.getOnlinePlayers().size() - vanished >= this.plugin.getConfig().getInt("min_players_online");
   }
}
