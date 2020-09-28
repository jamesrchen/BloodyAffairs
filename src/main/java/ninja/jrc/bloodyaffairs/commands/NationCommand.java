package ninja.jrc.bloodyaffairs.commands;

import ninja.jrc.bloodyaffairs.objects.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class NationCommand implements CommandExecutor {
    private final Logger logger;

    public NationCommand(Logger logger){
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            sender.sendMessage(Lang.INSUFFICIENT_ARGUMENTS.toStringWithPrefix());
        }
        return false;
    }
}
