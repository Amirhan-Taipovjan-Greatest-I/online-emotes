package com.github.dima_dencep.mods.online_emotes;

import com.github.dima_dencep.mods.online_emotes.config.EmoteConfig;
import com.github.dima_dencep.mods.online_emotes.network.OnlineProxyImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnlineEmotes {
    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static final Logger logger = LogManager.getFormatterLogger(OnlineEmotes.MOD_ID);
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final String MOD_ID = "online_emotes";
    public static OnlineProxyImpl proxy;
    public static EmoteConfig config;

    public void onInitializeClient() {
        OnlineEmotes.config = AutoConfig.register(EmoteConfig.class, Toml4jConfigSerializer::new).getConfig();
        OnlineEmotes.proxy = new OnlineProxyImpl();

        EmotesProxyManager.registerProxyInstance(OnlineEmotes.proxy);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OnlineEmotes.proxy.disconnectNetty();
            OnlineEmotes.proxy.bootstrap.config().group().shutdownGracefully();
        }));
    }

    public static void sendMessage(boolean debug, Text title, Text description) {
        if (debug && !config.debug) return;

        if (EmotesExpectPlatform.isEssentialAvailable() && config.essentialIntegration) {
            EmotesExpectPlatform.sendEssentialMessage(title.getString(), description.getString());

            return;
        }

        try {
            client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, title, description));
        } catch (Throwable ignored) {
        }
    }
}
