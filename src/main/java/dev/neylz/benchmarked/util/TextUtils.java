package dev.neylz.benchmarked.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class TextUtils {

    public final static Text EMPTY_TEXT = Text.literal("");


    public static Text listOf(Text... texts) {
        MutableText ft = Text.literal("");
        for (Text text : texts) {
            ft.append(text);
        }

        return ft;
    }


    public static class DefaultStyle {
        public final static Style RESET = Style.EMPTY.withBold(false).withItalic(false).withUnderline(false).withStrikethrough(false).withObfuscated(false).withColor(0xFFFFFF).withInsertion("").withClickEvent(null).withHoverEvent(null);
        public final static Style BOLD = Style.EMPTY.withBold(true);
        public final static Style ITALIC = Style.EMPTY.withItalic(true);
        public final static Style UNDERLINED = Style.EMPTY.withUnderline(true);
        public final static Style STRIKETHROUGH = Style.EMPTY.withStrikethrough(true);
        public final static Style OBFUSCATED = Style.EMPTY.withObfuscated(true);


        public final static Style DARK_RED = Style.EMPTY.withColor(0xAA0000);
        public final static Style RED = Style.EMPTY.withColor(0xFF5555);
        public final static Style GOLD = Style.EMPTY.withColor(0xFFAA00);
        public final static Style YELLOW = Style.EMPTY.withColor(0xFFFF55);
        public final static Style DARK_GREEN = Style.EMPTY.withColor(0x00AA00);
        public final static Style GREEN = Style.EMPTY.withColor(0x55FF55);
        public final static Style AQUA = Style.EMPTY.withColor(0x55FFFF);
        public final static Style DARK_AQUA = Style.EMPTY.withColor(0x00AAAA);
        public final static Style DARK_BLUE = Style.EMPTY.withColor(0x0000AA);
        public final static Style BLUE = Style.EMPTY.withColor(0x5555FF);
        public final static Style LIGHT_PURPLE = Style.EMPTY.withColor(0xFF55FF);
        public final static Style DARK_PURPLE = Style.EMPTY.withColor(0xAA00AA);
        public final static Style WHITE = Style.EMPTY.withColor(0xFFFFFF);
        public final static Style GRAY = Style.EMPTY.withColor(0xAAAAAA);
        public final static Style DARK_GRAY = Style.EMPTY.withColor(0x555555);
        public final static Style BLACK = Style.EMPTY.withColor(0x000000);
    }


}
