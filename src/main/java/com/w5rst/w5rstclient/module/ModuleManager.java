package com.w5rst.w5rstclient.module;

import com.w5rst.w5rstclient.module.impl.BuildRandom;
import com.w5rst.w5rstclient.module.impl.InstantWither;
import com.w5rst.w5rstclient.module.impl.NoFall;

import java.util.ArrayList;

public class ModuleManager {
    private final ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();
    }

    public void initialize() {
        // add mod
        addModule(new BuildRandom("BuildRandom", 0));
        addModule(new InstantWither("InstantWither", 0));
        addModule(new NoFall("NoFall", 0));
    }

    private void addModule(Module module) {
        modules.add(module);
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public Module getClazzString(String name) {
        try {
            for (Module feature : getModules()) {
                if (feature.getName().toLowerCase().replaceAll(" ", "").equals(name.toLowerCase())) {
                    return feature;
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return null;
    }
}
