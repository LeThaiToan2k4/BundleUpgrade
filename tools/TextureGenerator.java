import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Procedural pixel-art generator for the Bundle Upgrade mod.
 *
 * <p>Run from the project root with JDK 21+ (single-file source launch, no compilation step):
 * <pre>  java tools/TextureGenerator.java</pre>
 *
 * <p>Produces, for every tier, the three sprites that the 1.21.11 bundle item model needs
 * ({@code <tier>_bundle.png}, {@code <tier>_bundle_open_back.png}, {@code <tier>_bundle_open_front.png})
 * plus the 128x128 mod icon.
 */
public final class TextureGenerator {

    private static final String ASSET_ROOT = "src/main/resources/assets/bundleupgrade";

    // Pixel legend shared by all templates:
    //  .  transparent      O  outline         D  dark shade     M  mid tone
    //  L  light tone       H  highlight       S  tie / band     G  clasp gem
    //  I  bag interior (open state only)

    private static final String[] CLOSED = {
            "................",
            ".....OOOOOO.....",
            "....OHLLLMDO....",
            ".....OMMMDO.....",
            "......OSSO......",
            "....OOSSSSOO....",
            "...OHLSGGSMDO...",
            "..OHLLLSSMMMDO..",
            "..OLHLMMMMMMDO..",
            ".OHLLMMMMMMMDDO.",
            ".OLLMMMMMMMMDDO.",
            ".OLMMMMMMMMMDDO.",
            ".OLMMMMMMMMDDDO.",
            "..OMMMMMMMDDDO..",
            "...OODDDDDDOO...",
            ".....OOOOOO.....",
    };

    private static final String[] OPEN_BACK = {
            "................",
            "................",
            "................",
            "...OOOOOOOOOO...",
            "..OHLLLLLLLMDO..",
            "..OIIIIIIIIIIO..",
            "..OIIIIIIIIIIO..",
            ".OIIIIIIIIIIIIO.",
            ".OIIIIIIIIIIIIO.",
            ".OIIIIIIIIIIIIO.",
            ".OIIIIIIIIIIIIO.",
            ".OIIIIIIIIIIIIO.",
            ".OIIIIIIIIIIIIO.",
            "..OIIIIIIIIIIO..",
            "...OIIIIIIIIO...",
            "....OOOOOOOO....",
    };

    private static final String[] OPEN_FRONT = {
            "................",
            "................",
            "................",
            "................",
            "................",
            "................",
            "................",
            ".OOOOOOOOOOOOOO.",
            ".OHLLLLLLLLLMDO.",
            ".OSSSSSGGSSSSSO.",
            ".OLLMMMMMMMMDDO.",
            ".OLHMMMMMMMMDDO.",
            ".OLMMMMMMMMDDDO.",
            "..OMMMMMMMDDDO..",
            "...OODDDDDDOO...",
            "....OOOOOOOO....",
    };

    /** outline, dark, mid, light, highlight, band, gem */
    private record Palette(int o, int d, int m, int l, int h, int s, int g) {
        int interior() {
            return shade(d, 0.38);
        }

        int color(char c) {
            return switch (c) {
                case 'O' -> o;
                case 'D' -> d;
                case 'M' -> m;
                case 'L' -> l;
                case 'H' -> h;
                case 'S' -> s;
                case 'G' -> g;
                case 'I' -> interior();
                default -> 0x00000000;
            };
        }
    }

    private static final Map<String, Palette> TIERS = new LinkedHashMap<>();

    static {
        TIERS.put("copper", new Palette(0x4A1F12, 0x8A3C22, 0xC15A36, 0xE77C56, 0xFCB08C, 0x3B2A20, 0x5AD6A0));
        TIERS.put("iron", new Palette(0x2E2E33, 0x6B6B72, 0xA8A8B0, 0xD8D8DC, 0xFFFFFF, 0x4A3426, 0x8C8C94));
        TIERS.put("gold", new Palette(0x5A3A00, 0xB07A10, 0xE8B923, 0xFBE15A, 0xFFFBC0, 0x6A3D1A, 0xFF5A5A));
        TIERS.put("emerald", new Palette(0x003A17, 0x0A7A34, 0x17B34E, 0x41F07A, 0xC3FFD8, 0x4A3426, 0xE8B923));
        TIERS.put("diamond", new Palette(0x0B3C3C, 0x1A8F8A, 0x2CCDC4, 0x6BF3E8, 0xE0FFFC, 0x2A2A40, 0xFFFFFF));
    }

    public static void main(String[] args) throws IOException {
        File itemDir = new File(ASSET_ROOT, "textures/item");
        if (!itemDir.isDirectory() && !itemDir.mkdirs()) {
            throw new IOException("Cannot create " + itemDir);
        }

        for (Map.Entry<String, Palette> tier : TIERS.entrySet()) {
            String base = tier.getKey() + "_bundle";
            Palette p = tier.getValue();
            write(render(CLOSED, p), new File(itemDir, base + ".png"));
            write(render(OPEN_BACK, p), new File(itemDir, base + "_open_back.png"));
            write(render(OPEN_FRONT, p), new File(itemDir, base + "_open_front.png"));
        }

        write(renderIcon(), new File(ASSET_ROOT, "icon.png"));
        System.out.println("Generated " + (TIERS.size() * 3) + " item textures + icon.png under " + ASSET_ROOT);
    }

    private static BufferedImage render(String[] template, Palette p) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 16; y++) {
            String row = template[y];
            if (row.length() != 16) {
                throw new IllegalStateException("Template row " + y + " is " + row.length() + " px wide");
            }
            for (int x = 0; x < 16; x++) {
                char c = row.charAt(x);
                img.setRGB(x, y, c == '.' ? 0 : 0xFF000000 | p.color(c));
            }
        }
        return img;
    }

    /** 128x128 icon: dark rounded tile, a five-tier ring, and a 6x scaled diamond bundle in the middle. */
    private static BufferedImage renderIcon() {
        final int size = 128;
        final int cell = 4; // background drawn on a 32x32 pixel-art grid
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Palette[] ring = TIERS.values().toArray(new Palette[0]);

        for (int cy = 0; cy < 32; cy++) {
            for (int cx = 0; cx < 32; cx++) {
                if (!insideRounded(cx, cy, 32, 5)) {
                    continue;
                }
                boolean border = !insideRounded(cx, cy, 32, 5, 2);
                int argb;
                if (border) {
                    // Walk the perimeter clockwise and split it into the five tier colours.
                    double angle = Math.atan2(cy - 15.5, cx - 15.5) + Math.PI; // 0..2PI
                    int idx = (int) (angle / (2 * Math.PI) * ring.length) % ring.length;
                    argb = 0xFF000000 | ((cx + cy) % 2 == 0 ? ring[idx].m() : ring[idx].l());
                } else {
                    // Vertical slate gradient with a subtle checker dither.
                    double t = cy / 31.0;
                    int top = 0x2A2F45, bottom = 0x12141F;
                    int base = lerp(top, bottom, t);
                    argb = 0xFF000000 | ((cx + cy) % 2 == 0 ? base : shade(base, 0.9));
                }
                fillCell(img, cx * cell, cy * cell, cell, argb);
            }
        }

        // Sparkles in tier colours on the background grid.
        int[][] sparkles = {{6, 7, 0}, {25, 6, 2}, {5, 24, 4}, {26, 25, 3}, {16, 4, 1}};
        for (int[] s : sparkles) {
            int c = 0xFF000000 | ring[s[2]].h();
            fillCell(img, s[0] * cell, s[1] * cell, cell, c);
            fillCell(img, (s[0] - 1) * cell, s[1] * cell, cell, 0xFF000000 | ring[s[2]].l());
            fillCell(img, (s[0] + 1) * cell, s[1] * cell, cell, 0xFF000000 | ring[s[2]].l());
            fillCell(img, s[0] * cell, (s[1] - 1) * cell, cell, 0xFF000000 | ring[s[2]].l());
            fillCell(img, s[0] * cell, (s[1] + 1) * cell, cell, 0xFF000000 | ring[s[2]].l());
        }

        // Centre sprite: diamond bundle, 6px per source pixel => 96px, with a 1-cell drop shadow.
        BufferedImage bundle = render(CLOSED, TIERS.get("diamond"));
        final int scale = 6;
        final int offset = (size - 16 * scale) / 2;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if ((bundle.getRGB(x, y) >>> 24) != 0) {
                    fillCell(img, offset + x * scale + 4, offset + y * scale + 4, scale, 0xAA05060A);
                }
            }
        }
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int c = bundle.getRGB(x, y);
                if ((c >>> 24) != 0) {
                    fillCell(img, offset + x * scale, offset + y * scale, scale, c);
                }
            }
        }
        return img;
    }

    private static boolean insideRounded(int x, int y, int n, int r) {
        return insideRounded(x, y, n, r, 0);
    }

    private static boolean insideRounded(int x, int y, int n, int r, int inset) {
        int min = inset, max = n - 1 - inset, rr = Math.max(0, r - inset);
        if (x < min || y < min || x > max || y > max) {
            return false;
        }
        int cx = Math.min(Math.max(x, min + rr), max - rr);
        int cy = Math.min(Math.max(y, min + rr), max - rr);
        double dx = x - cx, dy = y - cy;
        return dx * dx + dy * dy <= rr * rr + 0.5;
    }

    private static void fillCell(BufferedImage img, int px, int py, int s, int argb) {
        for (int y = py; y < py + s; y++) {
            for (int x = px; x < px + s; x++) {
                if (x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight()) {
                    img.setRGB(x, y, blend(img.getRGB(x, y), argb));
                }
            }
        }
    }

    private static int blend(int dst, int src) {
        int sa = src >>> 24;
        if (sa == 0xFF) return src;
        if (sa == 0) return dst;
        double a = sa / 255.0;
        int r = (int) (((src >> 16) & 0xFF) * a + ((dst >> 16) & 0xFF) * (1 - a));
        int g = (int) (((src >> 8) & 0xFF) * a + ((dst >> 8) & 0xFF) * (1 - a));
        int b = (int) ((src & 0xFF) * a + (dst & 0xFF) * (1 - a));
        int outA = Math.max(dst >>> 24, sa);
        return (outA << 24) | (r << 16) | (g << 8) | b;
    }

    private static int shade(int rgb, double f) {
        int r = (int) Math.round(((rgb >> 16) & 0xFF) * f);
        int g = (int) Math.round(((rgb >> 8) & 0xFF) * f);
        int b = (int) Math.round((rgb & 0xFF) * f);
        return (r << 16) | (g << 8) | b;
    }

    private static int lerp(int a, int b, double t) {
        int r = (int) Math.round(((a >> 16) & 0xFF) * (1 - t) + ((b >> 16) & 0xFF) * t);
        int g = (int) Math.round(((a >> 8) & 0xFF) * (1 - t) + ((b >> 8) & 0xFF) * t);
        int bl = (int) Math.round((a & 0xFF) * (1 - t) + (b & 0xFF) * t);
        return (r << 16) | (g << 8) | bl;
    }

    private static void write(BufferedImage img, File out) throws IOException {
        if (!ImageIO.write(img, "png", out)) {
            throw new IOException("No PNG writer available for " + out);
        }
    }
}
