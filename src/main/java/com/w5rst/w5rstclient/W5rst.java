package com.w5rst.w5rstclient;

import com.w5rst.w5rstclient.event.EventManager;
import com.w5rst.w5rstclient.command.Command;
import com.w5rst.w5rstclient.command.CommandManager;
import com.w5rst.w5rstclient.config.SaveLoad;
import com.w5rst.w5rstclient.module.ModuleManager;
import com.w5rst.w5rstclient.utilities.IMC;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
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

@Mod(modid = "w5rst", name = "W5rst Client", version = "0.1")
public class W5rst implements IMC {
    private static Logger logger;
    private static EventManager eventManager;
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
        (eventManager = new EventManager()).initialize();
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

    public static void WriteLine(Object message) {
        logger.info("[ForgeW5rst] " + message);
    }

    public static void WriteChat(Object message) {
        mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("\2477[\2476ForgeW5rst\2477] \247f" + message.toString()));
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static SaveLoad getSaveLoad() {
        return saveLoad;
    }

    public Logger getLogger() {
        return logger;
    }
}
