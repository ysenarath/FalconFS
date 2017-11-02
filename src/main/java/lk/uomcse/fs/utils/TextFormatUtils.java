package lk.uomcse.fs.utils;

public class TextFormatUtils {
    /**
     * Converts value to ranked textual representation
     *
     * @param value a value to convert (<10)
     * @return 1=first, 2=second, 3=third, ... >10 or 0=undefined
     */
    public static String toRankedText(int value) {
        switch (value) {
            case 1:
                return "first";
            case 2:
                return "second";
            case 3:
                return "third";
            case 4:
                return "forth";
            case 5:
                return "fifth";
            case 6:
                return "sixth";
            case 7:
                return "seventh";
            case 8:
                return "eighth";
            case 9:
                return "ninth";
            default:
                return "undefined";
        }
    }
}
