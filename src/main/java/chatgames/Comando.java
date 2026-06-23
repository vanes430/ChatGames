package chatgames;

import Config.ConfigManager;
import Config.YamlConfig;
import Manager.GameManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comando {
   private final ChatGames plugin;

   public Comando(ChatGames instance) {
      this.plugin = instance;
   }

   public LiteralCommandNode<CommandSourceStack> register(Commands commands) {
      YamlConfig messages = this.plugin.configManager.messages;

      LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("chatgames")
         .requires(source -> source.getSender().hasPermission("chatgames.help") || source.getSender().hasPermission("chatgames.start.*"))
         .executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (!sender.hasPermission("chatgames.help")) {
               sender.sendMessage(ConfigManager.NOT_ENOUGH_PERMISSIONS);
               return 1;
            }
            for (String msg : messages.getStringList("help_command")) {
               sender.sendMessage(this.plugin.Color(msg));
            }
            return 1;
         });

      // top subcommand
      builder.then(Commands.literal("top")
         .requires(source -> source.getSender().hasPermission("chatgames.top"))
         .executes(ctx -> {
            this.plugin.gameManager.getTop(ctx.getSource().getSender());
            return 1;
         })
      );

      // toggle subcommand
      builder.then(Commands.literal("toggle")
         .requires(source -> source.getSender() instanceof Player && source.getSender().hasPermission("chatgames.toggle"))
         .executes(ctx -> {
            Player p = (Player) ctx.getSource().getSender();
            String value;
            if (!this.plugin.gameManager.gamesIsToggled(p)) {
               this.plugin.gameManager.toggleGames(p, true);
               value = "ON";
            } else {
               this.plugin.gameManager.toggleGames(p, false);
               value = "OFF";
            }
            p.sendMessage(this.plugin.Color(messages.getString("toggle_game").replaceAll("%state%", value)));
            return 1;
         })
      );

      // reload subcommand
      builder.then(Commands.literal("reload")
         .requires(source -> source.getSender().hasPermission("chatgames.reload"))
         .executes(ctx -> {
            this.plugin.reloadFiles();
            ctx.getSource().getSender().sendMessage(ConfigManager.CONFIG_RELOADED);
            return 1;
         })
      );

      // start subcommand (dynamic start)
      builder.then(Commands.literal("start")
         .requires(source -> source.getSender().hasPermission("chatgames.start.*") || 
                             this.plugin.games.stream().anyMatch(g -> source.getSender().hasPermission("chatgames.start." + g)))
         .then(Commands.argument("game", StringArgumentType.word())
            .suggests((ctx, suggestionsBuilder) -> {
               String input = suggestionsBuilder.getRemaining().toLowerCase();
               for (String game : this.plugin.games) {
                  if (ctx.getSource().getSender().hasPermission("chatgames.start." + game) || ctx.getSource().getSender().hasPermission("chatgames.start.*")) {
                     if (game.toLowerCase().startsWith(input)) {
                        suggestionsBuilder.suggest(game);
                     }
                  }
               }
               return suggestionsBuilder.buildFuture();
            })
            .executes(ctx -> {
               CommandSender sender = ctx.getSource().getSender();
               String game = StringArgumentType.getString(ctx, "game");
               this.startGameByCommand(sender, game);
               return 1;
            })
         )
      );

      return builder.build();
   }

   public void startGameByCommand(CommandSender sender, String game) {
      YamlConfig messages = this.plugin.configManager.messages;
      if (this.plugin.games.contains(game)) {
         if (!sender.hasPermission("chatgames.start." + game) && !sender.hasPermission("chatgames.start.*")) {
            sender.sendMessage(ConfigManager.NOT_ENOUGH_PERMISSIONS);
         } else {
            if (!this.startFromDisabledWorld(sender) && !sender.hasPermission("chatgames.start.*")) {
               sender.sendMessage(ConfigManager.CANNOT_START_DISABLED);
               return;
            }

            if (this.plugin.inGame && this.plugin.taskManager.timeExpiredTask != null) {
               this.plugin.taskManager.timeExpiredTask.cancel();
            }

            this.plugin.selectedGame = game;
            sender.sendMessage(this.plugin.Color(messages.getString("game_started").replaceAll("%game%", this.plugin.selectedGame)));
            this.plugin.gameManager.startGame(GameManager.GameType.valueOf(game.toUpperCase()));
         }
      } else {
         sender.sendMessage(ConfigManager.UNKNOWN_COMMAND);
      }
   }

   public boolean startFromDisabledWorld(CommandSender sender) {
      if (sender instanceof Player) {
         Player p = (Player)sender;

         for (String world : this.plugin.getConfig().getStringList("disabled-worlds")) {
            if (world.equalsIgnoreCase(p.getWorld().getName())) {
               return this.plugin.getConfig().getBoolean("allow_starting_from_disabled");
            }
         }

         return true;
      } else {
         return true;
      }
   }
}
