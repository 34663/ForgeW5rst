package com.w5rst.w5rstclient.module.impl;

import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.module.Module;
import net.minecraft.util.text.ITextComponent;

public class AutoSign extends Module {
    private ITextComponent[] signText;

    public AutoSign(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.signText = null;
    }

    @Override
    public void onEvent(Event event) {
    }

    public ITextComponent[] getSignText() {
        return this.signText;
    }

    public void setSignText(ITextComponent[] signText) {
        if (isEnabled() && this.signText == null) {
            this.signText = signText;
        }
    }
}
