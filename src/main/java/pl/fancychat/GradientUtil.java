package pl.fancychat;

/** Gradient hex (#RRGGBB) -> legacy §x§R§R§G§G§B§B (MC 1.16+). */
final class GradientUtil {

    private GradientUtil() {
    }

    static String applyGradient(String text, String hexStart, String hexMid, String hexEnd) {
        int[] start = hexToRgb(hexStart);
        int[] mid = hexToRgb(hexMid);
        int[] end = hexToRgb(hexEnd);
        int len = text.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                sb.append(c);
                continue;
            }
            double t = len == 1 ? 0.0 : (double) i / (len - 1);
            int[] a = t <= 0.5 ? start : mid;
            int[] b = t <= 0.5 ? mid : end;
            double local = t <= 0.5 ? t * 2.0 : (t - 0.5) * 2.0;
            int r = (int) Math.round(a[0] + (b[0] - a[0]) * local);
            int g = (int) Math.round(a[1] + (b[1] - a[1]) * local);
            int bl = (int) Math.round(a[2] + (b[2] - a[2]) * local);
            sb.append(rgbToLegacy(r, g, bl)).append(c);
        }
        return sb.toString();
    }

    static boolean isHexColor(String s) {
        return s != null && s.matches("^#[0-9a-fA-F]{6}$");
    }

    private static int[] hexToRgb(String hex) {
        return new int[]{
                Integer.parseInt(hex.substring(1, 3), 16),
                Integer.parseInt(hex.substring(3, 5), 16),
                Integer.parseInt(hex.substring(5, 7), 16)
        };
    }

    private static String rgbToLegacy(int r, int g, int b) {
        String hex = String.format("%02X%02X%02X", r, g, b);
        StringBuilder sb = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            sb.append('§').append(c);
        }
        return sb.toString();
    }
}
