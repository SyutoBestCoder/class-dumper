package xyz.syuto.dumper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class PrintLoaders extends CommandBase {

    @Override
    public String getCommandName() {
        return "loaders";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/loaders";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        GetLoaders.printClassLoaders();
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§dD§7] §aLoaders sent to console!"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
