package com.buuz135.simpleclaims.map;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapLoadException;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;

public class SimpleClaimsWorldMapProvider implements IWorldMapProvider {
    public static final String ID = "SimpleClaims";
    public static final BuilderCodec<SimpleClaimsWorldMapProvider> CODEC = BuilderCodec.builder(SimpleClaimsWorldMapProvider.class, SimpleClaimsWorldMapProvider::new).build();
    @Override
    public IWorldMap getGenerator(World world) throws WorldMapLoadException {
        return SimpleClaimsChunkWorldMap.INSTANCE;
    }
}
