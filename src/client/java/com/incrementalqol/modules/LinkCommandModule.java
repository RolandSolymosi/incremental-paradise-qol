package com.incrementalqol.modules;

import com.incrementalqol.common.utils.TextUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class LinkCommandModule implements ClientModInitializer {

    private static final String WIKI_LINK = "https://incrementalprisons.wiki.gg";
    private static final String ANNOUCNMENT_DISCORD_LINK = "https://discord.gg/DGZMpPjpWK";

    @Override
    public void onInitializeClient() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("links").executes(context -> {
                        sendCommunityLinks();
                        return 0;
                    })
            );
        });
    }

    private void sendCommunityLinks() {
        Text communityLinks =
                Text.literal("§bFind below some community links that you find useful!\n")
                    .append(Text.literal("\n§bThe server wiki currently WIP:\n"))
                    .append(TextUtils.textLink("§e" + WIKI_LINK, WIKI_LINK,"§eView Wiki Here"))
                    .append(Text.literal("\n\n§bThe community announcement server that pings you on events:\n"))
                    .append(TextUtils.textLink("§e" + ANNOUCNMENT_DISCORD_LINK, ANNOUCNMENT_DISCORD_LINK,"§eJoin Here"));

        MinecraftClient.getInstance().player.sendMessage(communityLinks, false);
    }

}
