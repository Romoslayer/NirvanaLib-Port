package com.clefal.nirvana_lib;

import com.clefal.nirvana_lib.network.packets.C2SSendSyncingConfigPacket;
import com.clefal.nirvana_lib.utils.DevUtils;
import com.clefal.nirvana_lib.utils.NetworkUtils;

public class NirvanaLibCommon {

    public static void init() {
        DevUtils.announceDevEnabled();
        registerClientPackets();
        registerServerPackets();
    }

    private static void registerClientPackets() {
    }

    private static void registerServerPackets() {
        // mysticdrew's common-networking library has no build published for 26.2 fabric yet,
        // and its 1.21.11 jar is intermediary-mapped so it can't be resolved under 26.2's no-remap Loom flow.
        //? <26.2
        NetworkUtils.registerPacket(C2SSendSyncingConfigPacket::new);
    }

}
