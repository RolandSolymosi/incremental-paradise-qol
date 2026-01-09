package com.incrementalclient.featues;

import com.incrementalclient.services.ChatHandler;
import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.common.utils.TextUtils;
import net.minecraft.text.Text;

public class LinksCommand {
    private static final String WIKI_LINK = "https://incrementalprisons.wiki.gg";
    private static final String ANNOUCNMENT_DISCORD_LINK = "https://discord.gg/DGZMpPjpWK";

    public LinksCommand(CommandHandler commandHandler, ChatHandler chatHandler){
        Text communityLinks =
                Text.literal("§bFind below some community links that you find useful!\n")
                        .append(Text.literal("\n§bThe server wiki currently WIP:\n"))
                        .append(TextUtils.textLink("§e" + WIKI_LINK, WIKI_LINK,"§eView Wiki Here"))
                        .append(Text.literal("\n\n§bThe community announcement server that pings you on events:\n"))
                        .append(TextUtils.textLink("§e" + ANNOUCNMENT_DISCORD_LINK, ANNOUCNMENT_DISCORD_LINK,"§eJoin Here"));

        commandHandler.register(new CommandHandler.CommandRegistration("links", () -> {
            chatHandler.sendChatMessage(communityLinks);
        }));
    }
}
