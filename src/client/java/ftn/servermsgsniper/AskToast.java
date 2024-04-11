package ftn.servermsgsniper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.Util.getMeasuringTimeMs;

@Environment(EnvType.CLIENT)
public class AskToast implements Toast {
    public static final int PROGRESS_BAR_WIDTH = 154;
    public static final int PROGRESS_BAR_HEIGHT = 1;
    public static final int PROGRESS_BAR_X = 3;
    public static final int PROGRESS_BAR_Y = 28;
    private static final Text title = MutableText.of(PlainTextContent.of("Run event? (y/n)"));
    private static final Identifier TEXTURE = new Identifier("toast/advancement");
    @Nullable
    private final Text description;
    private Toast.Visibility visibility = Visibility.SHOW;
    private final float initTime = getMeasuringTimeMs();
    private final ClickEvent event;
    public boolean run = false;

    public AskToast(ClickEvent event) {
        this.event = event;
        this.description = MutableText.of(PlainTextContent.of(event.getValue()));
    }

    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        if(run)
            return Visibility.HIDE;
        if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 'Y')){
            CustomClickEventHandler.process(event);
            run = true;
            return Visibility.HIDE;
        }
        if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 'N')){
            run = true;
            return Visibility.HIDE;
        }

        context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(manager.getClient().textRenderer, this.title, 8, 7, -(0x1010F0), false);
        context.drawText(manager.getClient().textRenderer, this.description, 8, 18, -(0x101010), false);
        context.fill(3, 28, 157, 29, -1);

        float f = (getMeasuringTimeMs() - initTime) / 3000;
        if (f > 1)
            this.hide();

        context.fill(3, 28, (int)(3.0F + 154.0F * f), 29, -16755456);

        return this.visibility;
    }

    public void hide() {
        this.visibility = Visibility.HIDE;
    }

}
