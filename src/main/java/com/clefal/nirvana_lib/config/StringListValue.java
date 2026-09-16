package com.clefal.nirvana_lib.config;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class StringListValue extends ConfigValue<List<String>>{
    public StringListValue(List<String> value) {
        super(value);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        // MC 26.3 dropped FriendlyByteBuf's collection helpers; write the same wire format
        // (varint length prefix + elements) by hand so every target compiles.
        buf.writeVarInt(value.size());
        for (String s : value) {
            buf.writeUtf(s);
        }
    }

    @Override
    public byte getToken() {
        return ConfigTokens.STRINGLIST;
    }

    @Override
    public List<String> getDefault() {
        return List.of();
    }
}
