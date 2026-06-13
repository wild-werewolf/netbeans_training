package matrixlink;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main extends JPanel {
    public static final int DATA_COLS = 64;
    public static final int DATA_ROWS = 40;
    public static final int PALETTE_SIZE = 16;

    private static final int FRAME_MS = 90;
    private static final int MIN_CELL_PX = 6;
    private static final int SAFE_MARGIN_PX = 28;
    private static final String MESSAGE = "Hello from the Java matrix art encoder.";

    private static final Color BACKGROUND = new Color(8, 11, 16);
    private static final Color GRID_LINE = new Color(5, 8, 12);
    private static final Color BLOCK_SHADOW = new Color(14, 19, 27);

    private static final Color[] PALETTE = {
        new Color(0x1B1F3A),
        new Color(0x274060),
        new Color(0x355C7D),
        new Color(0x2A9D8F),
        new Color(0x70C1B3),
        new Color(0xF4D35E),
        new Color(0xEE964B),
        new Color(0xF95738),
        new Color(0xC44569),
        new Color(0x6D597A),
        new Color(0xB56576),
        new Color(0xE56B6F),
        new Color(0x84A59D),
        new Color(0xF6BD60),
        new Color(0x43AA8B),
        new Color(0x577590)
    };

    private final byte[] payload;
    private final String sourceLabel;
    private final Timer timer;
    private int tick;

    public Main(byte[] payload, String sourceLabel) {
        this.payload = payload.clone();
        this.sourceLabel = sourceLabel;
        this.timer = new Timer(FRAME_MS, event -> {
            tick++;
            repaint();
        });
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(1280, 820));
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1 && "--self-test".equals(args[0])) {
            runSelfTest();
            return;
        }

        byte[] payload = payloadFromArgs(args);
        String source = args.length == 0 ? "built-in message" : Path.of(args[0]).getFileName().toString();
        SwingUtilities.invokeLater(() -> new Main(payload, source).showWindow());
    }

    static byte[] payloadFromArgs(String[] args) throws IOException {
        if (args.length == 0) {
            return MESSAGE.getBytes(StandardCharsets.UTF_8);
        }
        return Files.readAllBytes(Path.of(args[0]));
    }

    private static void runSelfTest() {
        byte[] payload = "diagonal wave payload".getBytes(StandardCharsets.UTF_8);
        byte[] changedPayload = "diagonal wave payloae".getBytes(StandardCharsets.UTF_8);

        int[] first = buildSymbols(payload, 3);
        int[] repeat = buildSymbols(payload, 3);
        int[] animated = buildSymbols(payload, 4);
        int[] changed = buildSymbols(changedPayload, 3);

        assert first.length == DATA_COLS * DATA_ROWS : "symbol count must match the data grid";
        assert Arrays.equals(first, repeat) : "same payload and tick must be deterministic";
        assert !Arrays.equals(first, animated) : "different ticks must animate the matrix";
        assert !Arrays.equals(first, changed) : "different payloads must change the art";

        for (int symbol : first) {
            assert symbol >= 0 && symbol < PALETTE_SIZE : "symbol must fit the palette: " + symbol;
        }

        System.out.println("Main self-test passed");
    }

    public static int[] buildSymbols(byte[] payload, int tick) {
        if (payload == null) {
            throw new IllegalArgumentException("payload must not be null");
        }

        long seed = seedFromPayload(payload);
        long[] fingerprint = fingerprintFromPayload(payload, seed);
        int[] symbols = new int[DATA_COLS * DATA_ROWS];

        for (int y = 0; y < DATA_ROWS; y++) {
            for (int x = 0; x < DATA_COLS; x++) {
                int index = y * DATA_COLS + x;
                int b0 = fingerprintSample(fingerprint, seed, index);
                int b1 = fingerprintSample(fingerprint, seed ^ 0xA24BAED4963EE407L, index * 17 + 41);
                int wave = (int) Math.floor((Math.sin((x + y * 1.7 + tick) * 0.18) + 1.0) * 7.5);

                long value = seed;
                value ^= (long) b0 * 0x9E3779B97F4A7C15L;
                value += (long) b1 * 0xC2B2AE3D27D4EB4FL;
                value ^= (long) wave * 0x165667B19E3779F9L;
                value += (long) x * 0x85EBCA77C2B2AE63L;
                value ^= (long) y * 0x27D4EB2F165667C5L;
                value += (long) tick * 0x94D049BB133111EBL;
                symbols[index] = (int) (mix64(value) & (PALETTE_SIZE - 1));
            }
        }

        return symbols;
    }

    private static long seedFromPayload(byte[] payload) {
        long a = 0x9E3779B97F4A7C15L ^ payload.length;
        long b = 0xC2B2AE3D27D4EB4FL + payload.length * 0x165667B19E3779F9L;

        for (int i = 0; i < payload.length; i++) {
            long v = (payload[i] & 0xFFL) + (long) i * 0x100000001B3L;
            a = mix64(a ^ v);
            b = Long.rotateLeft(b + v * 0x9E3779B97F4A7C15L, 17) ^ a;
        }

        return mix64(a ^ Long.rotateLeft(b, 31));
    }

    private static long[] fingerprintFromPayload(byte[] payload, long seed) {
        long[] buckets = new long[PALETTE_SIZE];

        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = mix64(seed + i * 0x9E3779B97F4A7C15L);
        }

        for (int i = 0; i < payload.length; i++) {
            long value = (payload[i] & 0xFFL) + (long) i * 0x100000001B3L;
            int bucket = (int) (mix64(seed ^ value ^ ((long) i * 0x632BE59BD9B4E019L)) & (PALETTE_SIZE - 1));
            buckets[bucket] = mix64(buckets[bucket] ^ value ^ Long.rotateLeft(seed, i & 63));
        }

        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = mix64(buckets[i] ^ payload.length ^ ((long) i * 0xC2B2AE3D27D4EB4FL));
        }

        return buckets;
    }

    private static int fingerprintSample(long[] fingerprint, long seed, int index) {
        int bucket = (int) (mix64(seed + index * 0x632BE59BD9B4E019L) & (PALETTE_SIZE - 1));
        long value = fingerprint[bucket] ^ seed ^ ((long) index * 0xC2B2AE3D27D4EB4FL);
        return (int) (mix64(value) & 0xFF);
    }

    private static long mix64(long value) {
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        value ^= value >>> 33;
        return value;
    }

    private void showWindow() {
        JFrame frame = new JFrame("Matrix Art - " + sourceLabel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int cell = Math.max(MIN_CELL_PX, Math.min(
                (getWidth() - SAFE_MARGIN_PX * 2) / DATA_COLS,
                (getHeight() - SAFE_MARGIN_PX * 2) / DATA_ROWS));
        int blockWidth = DATA_COLS * cell;
        int blockHeight = DATA_ROWS * cell;
        int x0 = (getWidth() - blockWidth) / 2;
        int y0 = (getHeight() - blockHeight) / 2;
        int gap = Math.max(1, cell / 14);

        g.setColor(BLOCK_SHADOW);
        g.fillRect(x0 - gap * 8, y0 - gap * 8, blockWidth + gap * 16, blockHeight + gap * 16);
        g.setColor(GRID_LINE);
        g.fillRect(x0, y0, blockWidth, blockHeight);

        int[] symbols = buildSymbols(payload, tick);
        for (int row = 0; row < DATA_ROWS; row++) {
            for (int col = 0; col < DATA_COLS; col++) {
                int cellX = x0 + col * cell;
                int cellY = y0 + row * cell;
                int color = symbols[row * DATA_COLS + col];
                g.setColor(PALETTE[color]);
                g.fillRect(cellX + gap, cellY + gap, cell - gap, cell - gap);
            }
        }

        g.dispose();
    }
}
