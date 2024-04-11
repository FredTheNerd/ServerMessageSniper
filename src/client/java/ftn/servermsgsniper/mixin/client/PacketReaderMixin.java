package ftn.servermsgsniper.mixin.client;

import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static ftn.servermsgsniper.PacketReaderHelper.processClickEvents;

@Mixin(MessageHandler.class)
public class PacketReaderMixin {

	@ModifyVariable(method = "onGameMessage", at = @At("HEAD"), argsOnly = true)
	private Text injected(Text a) {
		MutableText b = (MutableText) a;
		for(Text t: processClickEvents(a)){
			b.append(" ");
			b.append(t);
		}
		return b;
	}

}