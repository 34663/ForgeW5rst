package com.w5rst.w5rstclient.config;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.module.Module;
import com.w5rst.w5rstclient.utilities.IMC;

import java.io.*;
import java.util.ArrayList;

public class SaveLoad {
    private File directory;
    private File data;

    public SaveLoad() {
        directory = new File(IMC.mc.gameDir, "ForgeWurst");
        if (!directory.exists()) {
            if (directory.mkdir()) {
                W5rst.WriteLine("mkdir ForgeWurst successful");
            }
        }

        data = new File(directory, "Data.txt");
        if (!data.exists()) {
            try {
                if (data.createNewFile()) {
                    W5rst.WriteLine("Create data.txt successful");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile() {
        ArrayList<String> toSaves = new ArrayList<>();
        W5rst.getModuleManager().getModules().forEach(module -> toSaves.add(String.format("Module:%s:%s", module.getName(), module.getKeyCode())));

        PrintWriter pw;
        try {
            pw = new PrintWriter(this.data);
            toSaves.forEach(pw::println);
            pw.close();
            W5rst.WriteLine("Saved Data");
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
            W5rst.WriteLine(e.getMessage());
        }

        for (String line : lines) {
            String[] split = line.split(":");
            if (line.startsWith("Module:")) {
                Module module = W5rst.getModuleManager().getClazzString(split[1]);
                if (module != null) {
                    module.setKeyCode(Integer.parseInt(split[2]));
                }
            }
        }

        W5rst.WriteLine("Loaded Data");
    }
}
