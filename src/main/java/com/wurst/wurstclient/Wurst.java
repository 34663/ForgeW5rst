package com.wurst.wurstclient;

import com.wurst.wurstclient.command.Command;
import com.wurst.wurstclient.command.CommandManager;
import com.wurst.wurstclient.config.SaveLoad;
import com.wurst.wurstclient.module.ModuleManager;
import com.wurst.wurstclient.utilities.IMC;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@Mod(modid = "wurst", name = "Wurst Client", version = "0.1")
public class Wurst implements IMC {
    public static Logger logger;
    private static ModuleManager moduleManager;
    private static CommandManager commandManager;
    private static SaveLoad saveLoad;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("[ForgeWurst] Initializing...");
        MinecraftForge.EVENT_BUS.register(this);
        (moduleManager = new ModuleManager()).initialize();
        (commandManager = new CommandManager()).initialize();
        (saveLoad = new SaveLoad()).loadFile();
    }

    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent e) {
        if (mc.player != null && mc.world != null && moduleManager != null) {
            try {
                if (Keyboard.isCreated()) {
                    if (Keyboard.getEventKeyState()) {
                        int k = Keyboard.getEventKey();
                        if (k != 0) {
                            moduleManager.getModules().forEach(module -> {
                                if (k == module.getKeyCode()) {
                                    module.toggle();
                                }
                            });
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void chatEvent(ClientChatEvent event) {
        if (mc.player != null && mc.world != null && moduleManager != null) {
            String message = event.getMessage();

            if (message.startsWith("-")) {
                String[] commandBits = message.substring("-".length()).split(" ");
                String commandName = commandBits[0];
                Command command = CommandManager.getCommand(commandName);
                if (command != null) {
                    if (commandBits.length > 1) {
                        String[] commandArguments = Arrays.<String>copyOfRange(commandBits, 1, commandBits.length);
                        command.fire(commandArguments);
                    } else {
                        command.fire(null);
                    }
                }

                event.setCanceled(true);
            }
        }
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static SaveLoad getSaveLoad() {
        return saveLoad;
    }

    public static Logger getLogger() {
        return logger;
    }
}
