package interrrp.lastdeath;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public final class Feedback {
    public static void info(ServerPlayerEntity player, String message) {
        sendColored(player, message, 0x3838FA);
    }

    public static void error(ServerPlayerEntity player, String message) {
        sendColored(player, message, 0xFA3838);
    }

    private static void sendColored(ServerPlayerEntity player, String message, int color) {
        var textColor = TextColor.fromRgb(color);
        var style = Style.EMPTY.withColor(textColor);
        var text = Text.literal(message).setStyle(style);
        player.sendMessage(text);
    }
}
