package com.clefal.nirvana_lib.utils;


import com.clefal.nirvana_lib.NirvanaLibConstants;

//? <26.2 {
import com.clefal.nirvana_lib.network.newtoolchain.ModPacket;
import com.clefal.nirvana_lib.network.newtoolchain.Side;
import commonnetwork.api.Dispatcher;
import commonnetwork.api.Network;
//?}
import lombok.experimental.UtilityClass;


//? !legacy && <26.2 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
//?}

import java.util.ArrayList;
//? <26.2 {
import java.util.function.Supplier;
//?}

//? <26.2 {
import net.minecraft.network.FriendlyByteBuf;
//?}

import net.minecraft.resources.ResourceLocation;

//? <26.2 {
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//?}



@UtilityClass
public class NetworkUtils {

    // mysticdrew's common-networking library has no build published for 26.2 fabric yet,
    // and its 1.21.11 jar is intermediary-mapped so it can't resolve under 26.2's no-remap Loom flow.
    //? <26.2 {
    public <T> void sendToClient(T msg, ServerPlayer player) {
        Dispatcher.sendToClient(msg, player);
    }

    public <T> void sendToClients(T msg, Iterable<ServerPlayer> playerList) {
        playerList.forEach(x -> sendToClient(msg, x));
    }

    public <T> void sendToServer(T msg) {
        Dispatcher.sendToServer(msg);
    }

    public <T> void sendToAllClients(T msg, MinecraftServer level){
        sendToClients(msg, level.getPlayerList().getPlayers());
    }

    public <MSG extends ModPacket<MSG>> void registerPacket(Supplier<MSG> supplier) {
        Class<MSG> selfClass = supplier.get().getSelfClass();
        //? !legacy {
        var codec = StreamCodec.of((buf, msg) -> {
            msg.write(buf);

        }, (StreamDecoder<FriendlyByteBuf, MSG>) buf -> {
            MSG msg = supplier.get();
            msg.read(buf);
            return msg;
        });
        Network.registerPacket(supplier.get().type(), selfClass, codec, x -> x.message().handle(x.sender(), x.message(), Side.fromCM(x.side())));
        //?} else {
        /*Network.registerPacket(classToResourceLocation(selfClass), selfClass, (ModPacket::write), buf -> {
            MSG msg = supplier.get();
            msg.read(buf);
            return msg;
        }, x -> x.message().handle(x.sender(), x.message(), Side.fromCM(x.side())));

        *///?}
    }
    //?}


    public static ResourceLocation classToResourceLocation(Class<?> clas) {
        String name = clas.getSimpleName().toLowerCase();
        String result;
        ArrayList<Character> characters = new ArrayList<>();
        for (char c : name.toCharArray()) {
            characters.add(c);
        }
        result = characters.stream()
                .filter(NetworkUtils::validPathChar)
                .collect(StringBuilder::new, (StringBuilder::append), StringBuilder::append).toString();


        return NirvanaLibConstants.id(result.toLowerCase());
    }

    public static boolean validPathChar(char pathChar) {
        return pathChar == '_' || pathChar == '-' || pathChar >= 'a' && pathChar <= 'z' || pathChar >= '0' && pathChar <= '9' || pathChar == '/' || pathChar == '.';
    }

}
