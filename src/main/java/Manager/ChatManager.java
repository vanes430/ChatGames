package Manager;

import Config.YamlConfig;
import chatgames.ChatGames;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager implements Listener {
   private final ChatGames plugin;

   public ChatManager(ChatGames instance) {
      this.plugin = instance;
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void chat(AsyncPlayerChatEvent event) {
      final Player player = event.getPlayer();
      String message = event.getMessage();
      if (this.plugin.inGame && this.plugin.selected != null) {
         final String game = this.plugin.selectedGame;
         if (this.plugin.gameManager.worldEnabled(player)
            && this.plugin.gameManager.gamesIsToggled(player)
            && this.plugin.textEqualsWord(game, message, this.plugin.selected)) {
            event.setCancelled(true);
            this.plugin.gameManager.stopTimer();
            YamlConfig rewardConfig = this.plugin.configManager.rewards;
            YamlConfig messages = this.plugin.configManager.messages;
            Runnable runnable = new Runnable() {
               private List<String> rewards = ChatManager.this.plugin.rewardManager.loadGameRewards(game, player);

               @Override
               public void run() {
                  ChatManager.this.plugin.rewardManager.executeCommands(this.rewards, player);
                  this.rewards.clear();
               }
            };
            Bukkit.getGlobalRegionScheduler().run(this.plugin, (task) -> runnable.run());
            int newPoints = this.plugin.gameManager.getPoints(player) + 1;
            this.plugin.gameManager.updateTop(player.getName(), this.plugin.gameManager.getPoints(player), newPoints);
            this.plugin.gameManager.setPoints(player, newPoints);

            for (Player p : Bukkit.getOnlinePlayers()) {
               if (this.plugin.gameManager.worldEnabled(p)) {
                  this.plugin
                     .gameManager
                     .sendWinnerMessage(
                        p,
                        messages.getStringList(game + ".correct_message"),
                        "%player%",
                        player.getName(),
                        "%display_name%",
                        player.getDisplayName(),
                        "%correct_answer%",
                        this.plugin.selected,
                        "%time%",
                        "" + this.plugin.gameManager.getElapsedTime(),
                        "%equation%",
                        this.plugin.equation
                     );
               }
            }

            this.plugin.inGame = false;
            this.plugin.selected = null;
            if (this.plugin.taskManager.timeExpiredTask != null) {
               this.plugin.taskManager.timeExpiredTask.cancel();
            }
            this.plugin.selectedGame = null;
         }
      }
   }
}
