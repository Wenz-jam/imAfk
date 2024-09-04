package org.wenzjam.imafk;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Imafk implements ModInitializer {
    private static final String AFK_TEAM_NAME = "AFK";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, listener) -> {
            dispatcher.register(CommandManager.literal("afk").executes(this::swithPlayerAfkState));
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity) {
                removeAfkStateOnPLayerJoin((ServerPlayerEntity) entity);
            }
        });
    }

    private void removeAfkStateOnPLayerJoin(ServerPlayerEntity player) {
        ServerScoreboard scoreboard = player.getServer().getScoreboard();
        Team afkTeam = scoreboard.getTeam(AFK_TEAM_NAME);
        if (afkTeam == null) {
            return;
        }
        if (afkTeam.getPlayerList().contains(player.getNameForScoreboard())) {
            scoreboard.removeScoreHolderFromTeam(player.getNameForScoreboard(), afkTeam);
        }
    }

    private int swithPlayerAfkState(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerScoreboard scoreboard = player.getServer().getScoreboard();
        Team afkTeam = scoreboard.getTeam(AFK_TEAM_NAME);
        if (afkTeam == null) {
            return 0;
        }
        var isAfk = afkTeam.getPlayerList().contains(player.getNameForScoreboard());
        if (isAfk)
            scoreboard.removeScoreHolderFromTeam(player.getNameForScoreboard(), afkTeam);
        else
            scoreboard.addScoreHolderToTeam(player.getNameForScoreboard(), afkTeam);
        return 0;
    }
}
