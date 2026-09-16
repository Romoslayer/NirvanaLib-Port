package com.clefal.nirvana_lib.network.packets;

import com.clefal.nirvana_lib.config.*;
import com.clefal.nirvana_lib.network.newtoolchain.C2SModPacket;
import lombok.Getter;
import com.clefal.nirvana_lib.NirvanaLibConstants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;

@Getter
public class C2SSendSyncingConfigPacket implements C2SModPacket<C2SSendSyncingConfigPacket> {
    private UUID player;
    private Map<String, ConfigValue<?>> map;


    public C2SSendSyncingConfigPacket(UUID player, Map<String, ConfigValue<?>> configs) {
        this.player = player;
        this.map = configs;

    }

    public C2SSendSyncingConfigPacket() {
    }



    @Override
    public void handleServer(ServerPlayer sender, C2SSendSyncingConfigPacket message, boolean isClient) {
        PersonalConfigData data = SyncingPersonalConfig.INSTANCE.getData(message.player);
        for (Map.Entry<String, ConfigValue<?>> e : message.map.entrySet()) {
            data.configMap.merge(e.getKey(), e.getValue(), (old, newOne) -> newOne);
        }
    }


    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(player);
        var tokens = new ArrayList<Byte>();
        for (Map.Entry<String, ConfigValue<?>> e : map.entrySet()) {
            tokens.add(e.getValue().getToken());
        }
        // MC 26.3 dropped FriendlyByteBuf's collection helpers; same wire format, written by hand.
        buf.writeVarInt(tokens.size());
        for (Byte token : tokens) {
            buf.writeByte(token);
        }

        for (Map.Entry<String, ConfigValue<?>> e : map.entrySet()) {
            buf.writeUtf(e.getKey());
            e.getValue().write(buf);
        }
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.player = buf.readUUID();
        int tokenCount = buf.readVarInt();
        List<Byte> bytes = new ArrayList<>(tokenCount);
        for (int i = 0; i < tokenCount; i++) {
            bytes.add(buf.readByte());
        }

        Map<String, ConfigValue<?>> map = new LinkedHashMap<>();

        for (Byte token : bytes) {
            String key = buf.readUtf();
            ConfigValue<?> value = null;
            switch (token) {
                case 0 -> value = new BooleanValue(buf.readBoolean());
                case 1 -> value = new IntegerValue(((Byte) buf.readByte()).intValue());
                case 2 -> {
                    int size = buf.readVarInt();
                    List<String> strings = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        strings.add(buf.readUtf());
                    }
                    value = new StringListValue(strings);
                }
            }
            if (value == null) throw new RuntimeException("invalidated token: " + token);
            map.put(key, value);
        }
        this.map = map;
    }

    @Override
    public Class<C2SSendSyncingConfigPacket> getSelfClass() {
        return C2SSendSyncingConfigPacket.class;
    }


}
