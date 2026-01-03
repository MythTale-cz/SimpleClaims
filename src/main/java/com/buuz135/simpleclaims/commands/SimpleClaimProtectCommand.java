package com.buuz135.simpleclaims.commands;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.subcommand.chunk.ClaimChunkCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.UnclaimChunkCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.op.OpClaimChunkCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.op.OpUnclaimChunkCommand;
import com.buuz135.simpleclaims.gui.ChunkInfoGui;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class SimpleClaimProtectCommand extends AbstractAsyncCommand {

    public SimpleClaimProtectCommand() {
        super("simpleclaims", "Opens the chunk claim gui");
        this.addAliases("sc", "sc-chunks", "scc");
        this.setPermissionGroup(GameMode.Adventure);

        this.addSubCommand(new ClaimChunkCommand());
        this.addSubCommand(new UnclaimChunkCommand());

        this.addSubCommand(new OpClaimChunkCommand());
        this.addSubCommand(new OpUnclaimChunkCommand());
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    if (!ClaimManager.getInstance().canClaimInDimension(world)) {
                        player.sendMessage(CommandMessages.CANT_CLAIM_IN_THIS_DIMENSION);
                        return;
                    }
                    var party = ClaimManager.getInstance().getPartyFromPlayer(player);
                    if (party == null) {
                        party = ClaimManager.getInstance().createParty(player);
                        player.sendMessage(CommandMessages.PARTY_CREATED);
                    }
                    PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRefComponent != null) {
                        player.getPageManager().openCustomPage(ref, store, new ChunkInfoGui(playerRefComponent, player.getWorld().getName(), ChunkUtil.chunkCoordinate(player.getPosition().getX()), ChunkUtil.chunkCoordinate(player.getPosition().getZ()) ));
                    }
                }, world);
            } else {
                commandContext.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return CompletableFuture.completedFuture(null);
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}
