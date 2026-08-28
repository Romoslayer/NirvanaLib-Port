package com.clefal.nirvana_lib.client.render.batch;

import com.mojang.blaze3d.vertex.VertexConsumer;
//? !new_pipeline {
import net.minecraft.client.renderer.MultiBufferSource;
//?}

public interface IBufferInfo {

    default void upload(VertexConsumer consumer) {

    }
    //? !new_pipeline {
    default void upload(MultiBufferSource bufferSource) {

    }
    //?}
}
