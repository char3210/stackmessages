package me.char321.stackmessages.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public class ChatMixin {
    private Text prevMessage;
    private boolean repeated = false;
    private int count = 1;

    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", cancellable = true)
    private void onMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo info) {
        if(refresh) return;
        System.out.println(message.getClass());

        List<ChatHudLine<Text>> messages = ((ChatAccessor)MinecraftClient.getInstance().inGameHud.getChatHud()).getMessages();
//		List<ChatHudLine<OrderedText>> visible = ((MessageAccessor)MinecraftClient.getInstance().inGameHud.getChatHud()).getVisibleMessages();

//		for(ChatHudLine<Text> asdf : messages) {
//			System.out.println(asdf.getText().getString());
//		}
//		System.out.println(visible.get(0).getText());

        if(message.equals(prevMessage)) {
            info.cancel();
            messages.remove(0);

            //update string
            String add = " §7(" + ++count + ")";
            MutableText copy = prevMessage.shallowCopy();
//            if(prevMessage instanceof MutableText) {
                copy.append(add);
//            }
//            prevMessage.append(add);
            messages.set(0, new ChatHudLine<>(timestamp, copy, messageId));
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
        } else {
            prevMessage = message;
            count = 1;
        }
    }
}
