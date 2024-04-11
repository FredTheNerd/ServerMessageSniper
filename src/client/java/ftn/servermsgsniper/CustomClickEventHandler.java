package ftn.servermsgsniper;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;

import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import static org.apache.logging.log4j.core.net.UrlConnectionFactory.ALLOWED_PROTOCOLS;

public class CustomClickEventHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger("server-message-sniper");

    public static void process(ClickEvent clickEvent){
        if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            if (!(Boolean) MinecraftClient.getInstance().options.getChatLinks().getValue()) {
                return;
            }

            try {
                URI uRI;
                uRI = new URI(clickEvent.getValue());
                String string = uRI.getScheme();
                if (string == null) {
                    throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
                }

                if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
                    throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
                }

                Util.getOperatingSystem().open(uRI);

            } catch (URISyntaxException var5) {
                LOGGER.error("Can't open url for {}", clickEvent, var5);
            }
        } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND || clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
            String string2 = SharedConstants.stripInvalidChars(clickEvent.getValue());
            if (string2.startsWith("/")) {
                assert MinecraftClient.getInstance().player != null;
                if (!MinecraftClient.getInstance().player.networkHandler.sendCommand(string2.substring(1))) {
                    LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", string2);
                }
            } else {
                ClientPlayNetworkHandler x = MinecraftClient.getInstance().getNetworkHandler();
                if(x != null){
                    x.sendChatMessage(string2);
                }
            }
        } else if (clickEvent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
            MinecraftClient.getInstance().keyboard.setClipboard(clickEvent.getValue());
        } else {
            LOGGER.error("Don't know how to handle {}", clickEvent);
        }
    }
}
