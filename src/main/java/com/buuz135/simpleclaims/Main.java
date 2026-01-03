package com.buuz135.simpleclaims;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.SimpleClaimProtectCommand;
import com.buuz135.simpleclaims.commands.SimpleClaimsPartyCommand;
import com.buuz135.simpleclaims.config.SimpleClaimsConfig;
import com.buuz135.simpleclaims.map.SimpleClaimsChunkWorldMap;
import com.buuz135.simpleclaims.map.SimpleClaimsWorldMapProvider;
import com.buuz135.simpleclaims.systems.events.BreakBlockEventSystem;
import com.buuz135.simpleclaims.systems.events.InteractEventSystem;
import com.buuz135.simpleclaims.systems.events.PlaceBlockEventSystem;
import com.buuz135.simpleclaims.systems.tick.TitleTickingSystem;

import com.buuz135.simpleclaims.systems.tick.WorldMapUpdateTickingSystem;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Main extends JavaPlugin {

    public static Config<SimpleClaimsConfig> CONFIG;
    public static HashMap<String, World> WORLDS = new HashMap<>();

    public Main(@NonNullDecl JavaPluginInit init) {
        super(init);
        CONFIG = this.withConfig("SimpleClaims", SimpleClaimsConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();

        this.getEntityStoreRegistry().registerSystem(new BreakBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new PlaceBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new InteractEventSystem());
        this.getEntityStoreRegistry().registerSystem(new TitleTickingSystem());
        this.getChunkStoreRegistry().registerSystem(new WorldMapUpdateTickingSystem());
        this.getCommandRegistry().registerCommand(new SimpleClaimProtectCommand());
        this.getCommandRegistry().registerCommand(new SimpleClaimsPartyCommand());

        IWorldMapProvider.CODEC.register(SimpleClaimsWorldMapProvider.ID, SimpleClaimsWorldMapProvider.class, SimpleClaimsWorldMapProvider.CODEC);

        ClaimManager.getInstance();

        this.getEventRegistry().registerGlobal(AddWorldEvent.class, (event) -> {
            WORLDS.put(event.getWorld().getName(), event.getWorld());

            if (CONFIG.get().isForceSimpleClaimsChunkWorldMap() && !event.getWorld().getWorldConfig().isDeleteOnRemove()) event.getWorld().getWorldConfig().setWorldMapProvider(new SimpleClaimsWorldMapProvider());
        });

        this.getEventRegistry().registerGlobal(RemoveWorldEvent.class, (event) -> {
            WORLDS.remove(event.getWorld().getName());
        });

        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, (event) -> {
            var player = event.getHolder().getComponent(Player.getComponentType());
            ClaimManager.getInstance().getPlayerNameTracker().setPlayerName(player.getUuid(), player.getDisplayName());
            ClaimManager.getInstance().markDirty();
        });

        this.getEventRegistry().register(EventPriority.EARLY, PlayerMouseButtonEvent.class, (event) -> {
            System.out.println("PlayerMouseButtonEvent");
            System.out.println(event.getItemInHand());
            System.out.println(event.getMouseButton());
            event.setCancelled(true);
        });
    }

}