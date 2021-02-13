package com.wurst.wurstclient.config;

import com.wurst.wurstclient.Wurst;
import com.wurst.wurstclient.module.Module;
import com.wurst.wurstclient.utilities.IMC;

import java.io.*;
import java.util.ArrayList;

public class SaveLoad {
    private File directory;
    private File data;

    public SaveLoad() {
        directory = new File(IMC.mc.gameDir, "ForgeWurst");
        if (!directory.exists()) {
            if (directory.mkdir()) {
                Wurst.getLogger().info("[ForgeWurst] mkdir ForgeWurst successful");
            }
        }

        data = new File(directory, "Data.txt");
        if (!data.exists()) {
            try {
                if (data.createNewFile()) {
                    Wurst.getLogger().info("[ForgeWurst] Create data.txt successful");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile() {
        ArrayList<String> toSaves = new ArrayList<>();
        Wurst.getModuleManager().getModules().forEach(module -> toSaves.add(String.format("Module:%s:%s", module.getName(), module.getKeyCode())));

        PrintWriter pw;
        try {
            pw = new PrintWriter(this.data);
            toSaves.forEach(pw::println);
            pw.close();
            Wurst.getLogger().info("[ForgeWurst] Saved Data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadFile() {
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.data));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String line : lines) {
            String[] split = line.split(":");
            if (line.startsWith("Module:")) {
                Module module = Wurst.getModuleManager().getClazzString(split[1]);
                if (module != null) {
                    module.setKeyCode(Integer.parseInt(split[2]));
                }
            }
        }

        Wurst.getLogger().info("[ForgeWurst] Loaded Data");
    }
}
