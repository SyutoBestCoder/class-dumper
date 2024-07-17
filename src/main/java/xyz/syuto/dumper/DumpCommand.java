package xyz.syuto.dumper;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class DumpCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "dump";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dump";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ClassDumperMod.dumpClasses();
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§dD§7] §aDumping classes!"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
