package com.clefal.nirvana_lib.client.render.batch;



import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.gui.GuiGraphics;
//? new_pipeline {
/*import com.clefal.nirvana_lib.mixin.GuiGraphicsAccessor;
*///?}
//? !new_pipeline {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import org.joml.Matrix4f;
//? if <=1.21.1 {
import net.minecraft.util.FastColor;

//?} else {

//?}
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;

public class VertexContainer {

    //? new_pipeline {
    /*public Multimap<ResourceLocation, ITextureBufferInfo> map = Multimaps.newListMultimap(new TreeMap<>(), ArrayList::new);
    *///? } else {
    public HashMultimap<ResourceLocation, ITextureBufferInfo> map = HashMultimap.create(10, 100);
    //?}
    public List<IFillBufferInfo> fillBufferInfos = new ArrayList<>();
    public List<DrawStringBufferInfo> strings = new ArrayList<>();

    public VertexContainer(){
    }

    public VertexContainer(int expectedKeys, int expectedValuesPerKey){
        this.map = HashMultimap.create(expectedKeys, expectedValuesPerKey);
    }

    public void putBliz(ResourceLocation resourceLocation, TextureBufferInfo bufferInfo){
        putBliz(resourceLocation, ((ITextureBufferInfo) bufferInfo));
    }

    public void putBliz(ResourceLocation resourceLocation, ITextureBufferInfo bufferInfo){
        map.put(resourceLocation, bufferInfo);
    }

    public void putBlitNineSliced(ResourceLocation atlasLocation, int x, int y, int width, int height, int sliceSize, int uOffset, int vOffset, int textureX, int textureY, Matrix4f matrix4f) {
        putBlitNineSliced(atlasLocation, x, y, width, height, sliceSize, sliceSize, sliceSize, sliceSize, uOffset, vOffset, textureX, textureY, matrix4f);
    }

    public void putBlitNineSliced(ResourceLocation atlasLocation, int x, int y, int width, int height, int sliceWidth, int sliceHeight, int uWidth, int vHeight, int textureX, int textureY, Matrix4f matrix4f) {
        putBlitNineSliced(atlasLocation, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, textureX, textureY, matrix4f);
    }
    
    public void putBlitNineSliced(ResourceLocation atlasLocation, int x, int y, int width, int height, int leftSliceWidth, int topSliceHeight, int rightSliceWidth, int bottomSliceHeight, int uWidth, int vHeight, int textureX, int textureY, Matrix4f matrix4f){
        leftSliceWidth = Math.min(leftSliceWidth, width / 2);
        rightSliceWidth = Math.min(rightSliceWidth, width / 2);
        topSliceHeight = Math.min(topSliceHeight, height / 2);
        bottomSliceHeight = Math.min(bottomSliceHeight, height / 2);
        if (width == uWidth && height == vHeight) {
            putBliz(atlasLocation, TextureBufferInfo.of(x, y, textureX, textureY, width, height, matrix4f));
        } else if (height == vHeight) {
            putBliz(atlasLocation, TextureBufferInfo.of(x, y, textureX, textureY, leftSliceWidth, height, matrix4f));
            putBlizRepeating(atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, height, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, vHeight, matrix4f);
            putBliz(atlasLocation, TextureBufferInfo.of(x + width - rightSliceWidth, y, textureX + uWidth - rightSliceWidth, textureY, rightSliceWidth, height, matrix4f));
        } else if (width == uWidth) {
            putBliz(atlasLocation, TextureBufferInfo.of(x, y, textureX, textureY, width, topSliceHeight, matrix4f));
            putBlizRepeating(atlasLocation, x, y + topSliceHeight, width, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, uWidth, vHeight - bottomSliceHeight - topSliceHeight, matrix4f);
            putBliz(atlasLocation, TextureBufferInfo.of(x, y + height - bottomSliceHeight, textureX, textureY + vHeight - bottomSliceHeight, width, bottomSliceHeight, matrix4f));
        } else {
            putBliz(atlasLocation, TextureBufferInfo.of(x, y, textureX, textureY, leftSliceWidth, topSliceHeight, matrix4f));
            putBlizRepeating(atlasLocation, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, topSliceHeight, textureX + leftSliceWidth, textureY, uWidth - rightSliceWidth - leftSliceWidth, topSliceHeight, matrix4f);
            putBliz(atlasLocation, TextureBufferInfo.of(x + width - rightSliceWidth, y, textureX + uWidth - rightSliceWidth, textureY, rightSliceWidth, topSliceHeight, matrix4f));
            putBliz(atlasLocation, TextureBufferInfo.of(x, y + height - bottomSliceHeight, textureX, textureY + vHeight - bottomSliceHeight, leftSliceWidth, bottomSliceHeight, matrix4f));
            putBlizRepeating(atlasLocation, x + leftSliceWidth, y + height - bottomSliceHeight, width - rightSliceWidth - leftSliceWidth, bottomSliceHeight, textureX + leftSliceWidth, textureY + vHeight - bottomSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, bottomSliceHeight, matrix4f);
            putBliz(atlasLocation, TextureBufferInfo.of(x + width - rightSliceWidth, y + height - bottomSliceHeight, textureX + uWidth - rightSliceWidth, textureY + vHeight - bottomSliceHeight, rightSliceWidth, bottomSliceHeight, matrix4f));
            putBlizRepeating(atlasLocation, x, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX, textureY + topSliceHeight, leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight, matrix4f);
            putBlizRepeating(atlasLocation, x + leftSliceWidth, y + topSliceHeight, width - rightSliceWidth - leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + leftSliceWidth, textureY + topSliceHeight, uWidth - rightSliceWidth - leftSliceWidth, vHeight - bottomSliceHeight - topSliceHeight, matrix4f);
            putBlizRepeating(atlasLocation, x + width - rightSliceWidth, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, textureX + uWidth - rightSliceWidth, textureY + topSliceHeight, rightSliceWidth, vHeight - bottomSliceHeight - topSliceHeight, matrix4f);
        }
    }

    public void putBlizRepeating(ResourceLocation atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset, int sourceWidth, int sourceHeight, Matrix4f matrix4f) {
        putBlizRepeating(atlasLocation, x, y, width, height, uOffset, vOffset, sourceWidth, sourceHeight, 256, 256, matrix4f);
    }
    public void putBlizRepeating(ResourceLocation atlasLocation, int x, int y, int width, int height, int uOffset, int vOffset, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight, Matrix4f matrix4f){
        int i = x;

        int j;
        for(IntIterator intiterator = slices(width, sourceWidth); intiterator.hasNext(); i += j) {
            j = intiterator.nextInt();
            int k = (sourceWidth - j) / 2;
            int l = y;

            int i1;
            for(IntIterator intiterator1 = slices(height, sourceHeight); intiterator1.hasNext(); l += i1) {
                i1 = intiterator1.nextInt();
                int j1 = (sourceHeight - i1) / 2;
                putBliz(atlasLocation, TextureBufferInfo.of(i, l, uOffset + k, vOffset + j1, j, i1, textureWidth, textureHeight, matrix4f));
            }
        }
    }

    private static IntIterator slices(int target, int total) {
        int i = Mth.positiveCeilDiv(target, total);
        return new Divisor(target, i);
    }

    public void putFill(IFillBufferInfo fillBufferInfo){
        fillBufferInfos.add(fillBufferInfo);
    }

    public void putString(DrawStringBufferInfo s){
        strings.add(s);
    }

    private void refresh(){
        this.map = HashMultimap.create(10, 100);
        this.fillBufferInfos = new ArrayList<>();
        this.strings.clear();
    }
    //? !new_pipeline {
    public void draw(MultiBufferSource guiGraphics, Function<ResourceLocation, RenderType> renderTypeFunction){
        RenderSystem.enableDepthTest();

        for (var entry : this.map.asMap().entrySet()) {
            ResourceLocation key = entry.getKey();
            RenderType statusRenderType = renderTypeFunction.apply(key);
            VertexConsumer buffer = guiGraphics.getBuffer(statusRenderType);

            for (var bufferInfo : entry.getValue()) {
                bufferInfo.upload(buffer);
            }

        }
        VertexConsumer buffer = guiGraphics.getBuffer(RenderType.gui());
        if (!fillBufferInfos.isEmpty()){
            for (IFillBufferInfo fillBufferInfo : fillBufferInfos) {
                fillBufferInfo.upload(buffer);
            }
        }
        if (!strings.isEmpty()){
            for (DrawStringBufferInfo string : strings) {
                string.upload(guiGraphics);
            }
        }

        VertexConsumer end = guiGraphics.getBuffer(RenderType.gui());
        int color;

        //? if <=1.21.1 {
         color = FastColor.ARGB32.color(0, 0, 0, 0);
         //?} else {
            /*color = ARGB.color(0, 0, 0, 0);
        *///?}

        //? if >1.20.1 {
        
        end.addVertex(0, 0, 0).setColor(color);
        end.addVertex(0, 0, 0).setColor(color);
        end.addVertex(0, 0, 0).setColor(color);
        end.addVertex(0, 0, 0).setColor(color);
        //?} else {
        /*end.vertex(0, 0, 0).color(color).endVertex();
        end.vertex(0, 0, 0).color(color).endVertex();
        end.vertex(0, 0, 0).color(color).endVertex();
        end.vertex(0, 0, 0).color(color).endVertex();
        *///?}
        RenderSystem.disableDepthTest();
        refresh();
    }
    //?} else {
    /*public void draw(GuiGraphics guiGraphics){

        for (var entry : this.map.asMap().entrySet()) {
            ResourceLocation key = entry.getKey();

            for (var bufferInfo : entry.getValue()) {
                if (bufferInfo instanceof TextureBufferInfo textureBufferInfo){
                    BufferInfoRenderState renderState = textureBufferInfo.toRenderState(key, guiGraphics);
                    //~ if > 1.21.11 'submitGuiElement' -> 'addGuiElement'
                    ((GuiGraphicsAccessor) guiGraphics).getGuiRenderState().submitGuiElement(renderState);
                }
            }

        }
        if (!fillBufferInfos.isEmpty()){
            for (IFillBufferInfo fillBufferInfo : fillBufferInfos) {
                //~ if > 1.21.11 'submitGuiElement' -> 'addGuiElement'
                ((GuiGraphicsAccessor) guiGraphics).getGuiRenderState().submitGuiElement(fillBufferInfo.toRenderState(guiGraphics));
            }
        }
        if (!strings.isEmpty()){
            for (DrawStringBufferInfo string : strings) {
                //~ if > 1.21.11 'submitText' -> 'addText'
                ((GuiGraphicsAccessor) guiGraphics).getGuiRenderState().submitText(string.toRenderState(guiGraphics));
            }
        }

        refresh();
    }
    *///?}

}