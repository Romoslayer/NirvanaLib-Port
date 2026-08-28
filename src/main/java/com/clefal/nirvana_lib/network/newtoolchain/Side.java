package com.clefal.nirvana_lib.network.newtoolchain;

import org.jetbrains.annotations.NotNull;

public enum Side
{
    /**
     * CLIENT is the client side.
     */
    CLIENT,
    /**
     * SERVER can be dedicated server, or logical server in singleplayer.
     */
    SERVER;

    /**
     * Gets the opposite side.
     *
     * @return - The opposite side
     */
    public Side opposite()
    {
        if (CLIENT.equals(this))
        {
            return SERVER;
        }
        return CLIENT;
    }

    //? <26.2 {
    public static Side fromCM(@NotNull commonnetwork.networking.data.Side side){
        return switch (side){
            case CLIENT -> Side.CLIENT;
            case SERVER -> Side.SERVER;
        };
    }
    //?}
}
