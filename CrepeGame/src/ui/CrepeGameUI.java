package ui;

import game.GameManager;
import game.GameTimer;
import game.Order;
import game.OrderFactory;
import observer.GameObserver;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import crepe.ToppingData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CrepeGameUI extends JFrame implements GameObserver {
    private JLabel orderLabel, resultLabel, timerLabel, profitLabel, livesLabel, selectedLabel;
    private JButton submitBtn, restartBtn, startBtn, howToPlayBtn, menuBtn, clearBtn;
    private java.util.List<JButton> toppingButtons = new ArrayList<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JPanel toppingGridPanel;
    private PlatePanel platePanel;

    private ArrayList<String> playerToppings = new ArrayList<>();
    private Order currentOrder;

    private GameManager gm;
    private GameTimer gameTimer = new GameTimer();

    private final java.util.List<File> assetsDirs = new ArrayList<>();
    private final Map<String, ImageIcon> buttonImageCache = new HashMap<>();
    private BufferedImage menuBackground;
    private BufferedImage gameBackground;
    private BufferedImage plateImage;

    public CrepeGameUI() {
        gm = GameManager.getInstance();
        gm.addObserver(this);

        setupAssetDirectories();
        loadStaticImages();

        setTitle("Crepe Maker Game");
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createMenuPanel();
        createGamePanel();

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        setContentPane(mainPanel);

        cardLayout.show(mainPanel, "MENU");
    }

    private void createMenuPanel() {
        menuPanel = new BackgroundPanel(menuBackground, new Color(255, 248, 240, 225));
        menuPanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 300));

        JLabel titleLabel = new JLabel("Crepe Maker Game", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel subLabel = new JLabel("Build the exact order before time runs out", SwingConstants.CENTER);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        startBtn = new JButton("Start Game");
        howToPlayBtn = new JButton("How To Play");
        styleMenuButton(startBtn);
        styleMenuButton(howToPlayBtn);

        startBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "GAME");
            startGame();
        });

        howToPlayBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "How To Play:\n\n"
                        + "1. Read the order at the top.\n"
                        + "2. Click toppings in the exact same order.\n"
                        + "3. Watch the plate in the middle to check what you picked.\n"
                        + "4. Press Submit when you are done.\n"
                        + "5. Wrong order or time out = lose 1 life.\n\n",
                "How To Play",
                JOptionPane.INFORMATION_MESSAGE
        ));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(30));
        card.add(startBtn);
        card.add(Box.createVerticalStrut(15));
        card.add(howToPlayBtn);

        menuPanel.add(card);
    }

    private void createGamePanel() {
        gamePanel = new BackgroundPanel(gameBackground, new Color(255, 250, 245, 215));
        gamePanel.setLayout(new BorderLayout(15, 15));
        gamePanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setOpaque(true);
        topPanel.setBackground(new Color(255, 255, 255, 210));
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        orderLabel = new JLabel("Order: ");
        timerLabel = new JLabel("Time: ");
        profitLabel = new JLabel("Profit: ฿0");
        livesLabel = new JLabel("Lives: 3");

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        orderLabel.setFont(labelFont);
        timerLabel.setFont(labelFont);
        profitLabel.setFont(labelFont);
        livesLabel.setFont(labelFont);

        topPanel.add(orderLabel);
        topPanel.add(timerLabel);
        topPanel.add(profitLabel);
        topPanel.add(livesLabel);

        gamePanel.add(topPanel, BorderLayout.NORTH);

        int total = ToppingData.getToppings().size();
        int cols = 4; // try 4 or 5 depending on how many toppings
        int rows = (int) Math.ceil(total / (double) cols);

        toppingGridPanel = new JPanel(new GridLayout(rows, cols, 10, 10));
        toppingGridPanel.setOpaque(false);

        for (String topping : ToppingData.getToppings()) {
            JButton btn = new JButton(topping, loadButtonIcon(topping));
            btn.setPreferredSize(new Dimension(130, 90));
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setOpaque(true);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setIconTextGap(8);
            btn.addActionListener(e -> addTopping(topping));
            toppingButtons.add(btn);
            toppingGridPanel.add(btn);
        }
    
        platePanel = new PlatePanel();
        platePanel.setPreferredSize(new Dimension(420, 420));
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Toppings"));
        leftPanel.add(toppingGridPanel, BorderLayout.CENTER);
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(platePanel, BorderLayout.CENTER);
        gamePanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        rightPanel.setOpaque(true);
        rightPanel.setBackground(new Color(255, 255, 255, 210));
        rightPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        submitBtn = new JButton("Submit");
        clearBtn = new JButton("Clear");
        restartBtn = new JButton("Restart Game");
        menuBtn = new JButton("Main Menu");
        restartBtn.setVisible(false);

        submitBtn.addActionListener(e -> checkOrder());
        clearBtn.addActionListener(e -> clearSelection());
        restartBtn.addActionListener(e -> restartGame());
        menuBtn.addActionListener(e -> goToMenu());

        rightPanel.add(submitBtn);
        rightPanel.add(clearBtn);
        rightPanel.add(restartBtn);
        rightPanel.add(menuBtn);

        gamePanel.add(rightPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setOpaque(true);
        bottomPanel.setBackground(new Color(255, 255, 255, 210));
        bottomPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        resultLabel = new JLabel(" ");
        selectedLabel = new JLabel("Selected: ");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectedLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        bottomPanel.add(resultLabel);
        bottomPanel.add(selectedLabel);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleMenuButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 50));
        button.setPreferredSize(new Dimension(240, 50));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
    }

    private void startGame() {
        gm.reset();
        restartBtn.setVisible(false);
        enableButtons();
        updateLabels();
        generateNewOrder();
    }

    private void restartGame() {
        restartBtn.setVisible(false);
        startGame();
    }

    private void goToMenu() {
        gameTimer.stop();
        playerToppings.clear();
        platePanel.repaint();
        cardLayout.show(mainPanel, "MENU");
    }

    private void clearSelection() {
        playerToppings.clear();
        selectedLabel.setText("Selected: ");
        resultLabel.setText("Selection cleared.");
        platePanel.repaint();
    }

    private void addTopping(String topping) {
        if (playerToppings.size() >= currentOrder.getToppings().length) {
            resultLabel.setText("Too many toppings!");
            return;
        }

        playerToppings.add(topping);
        selectedLabel.setText("Selected: " + String.join(", ", playerToppings));
        resultLabel.setText(" ");
        platePanel.repaint();
    }

    private void generateNewOrder() {
        enableButtons();
        gameTimer.stop();

        currentOrder = OrderFactory.createOrder(gm.getRound());
        playerToppings.clear();

        int expected = gm.calculateCrepeCost(currentOrder.getToppings());
        orderLabel.setText("Order: " + currentOrder + " (฿" + expected + ")");
        resultLabel.setText("Make this crepe!");
        selectedLabel.setText("Selected: ");

        int startTime = Math.max(8, 15 - gm.getRound());

        timerLabel.setText("Time: " + startTime);

        gameTimer.start(
            startTime,

            // what happens every second
            e -> {
                timerLabel.setText("Time: " + gameTimer.getTimeLeft());
                platePanel.repaint();
            },

            // what happens if the timer reaches 0
            e -> {
                loseLife("Time's up! ❌");
            }
        );
    }

    private void checkOrder() {
        disableButtons();
        gameTimer.stop();

        String[] required = currentOrder.getToppings();

        System.out.println("Required: " + Arrays.toString(required));
        System.out.println("Player: " + playerToppings);

        if (playerToppings.size() != required.length) {
            loseLife("Wrong number of toppings!");
            return;
        }

        for (int i = 0; i < required.length; i++) {
            if (!playerToppings.get(i).equals(required[i])) {
                loseLife("Wrong toppings or order!");
                return;
            }
        }

        int earned = gm.calculateProfit(currentOrder, gameTimer.getTimeLeft());
        gm.nextRound();

        resultLabel.setText("Correct! +฿" + earned + " 🎉");
        new Timer(500, e -> {
            ((Timer) e.getSource()).stop();
            generateNewOrder();
        }).start();
    }

    private void loseLife(String msg) {
        gm.loseLife();
        resultLabel.setText(msg);
        if (gm.getLives() <= 0) {
            gameOver();
        } else {
            generateNewOrder();
        }
    }

    private void gameOver() {
        gameTimer.stop();

        resultLabel.setText("GAME OVER 💀 Final Profit: ฿" + gm.getProfit());
        disableButtons();
        restartBtn.setVisible(true);
    }

    private void updateLabels() {
        profitLabel.setText("Profit: ฿" + gm.getProfit());
        livesLabel.setText("Lives: " + gm.getLives());
    }

    private void disableButtons() {
        for (JButton b : toppingButtons) b.setEnabled(false);
        submitBtn.setEnabled(false);
        clearBtn.setEnabled(false);
    }

    private void enableButtons() {
        for (JButton b : toppingButtons) b.setEnabled(true);
        submitBtn.setEnabled(true);
        clearBtn.setEnabled(true);
    }

    @Override
    public void onGameStateChange() {
        SwingUtilities.invokeLater(this::updateLabels);
    }


    private void setupAssetDirectories() {
        String[] possiblePaths = {
                "assets",
                "./assets",
                "../assets",
                "../../assets",
                "CrepeGame/assets",
                "./CrepeGame/assets",
                "src/assets"
        };

        for (String path : possiblePaths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory() && !assetsDirs.contains(dir)) {
                assetsDirs.add(dir);
            }
        }
    }

    private void loadStaticImages() {
        menuBackground = tryLoadRawImage("menu_background");
        gameBackground = tryLoadRawImage("game_background");
        plateImage = tryLoadRawImage("plate");
    }

    private ImageIcon loadButtonIcon(String topping) {
        if (buttonImageCache.containsKey(topping)) {
            return buttonImageCache.get(topping);
        }

        BufferedImage raw = tryLoadRawImage(normalizeName(topping));
        ImageIcon icon;
        if (raw != null) {
            Image scaled = raw.getScaledInstance(84, 64, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } else {
            icon = new ImageIcon(createTextPlaceholderImage(topping, 84, 64));
        }

        buttonImageCache.put(topping, icon);
        return icon;
    }

    private BufferedImage tryLoadRawImage(String baseName) {
        String[] exts = {"png", "jpg", "jpeg", "webp"};
        String normalizedBase = normalizeFileKey(baseName);

        for (File dir : assetsDirs) {
            for (String ext : exts) {
                File directFile = new File(dir, baseName + "." + ext);
                if (directFile.exists()) {
                    try {
                        return ImageIO.read(directFile);
                    } catch (IOException ignored) {
                    }
                }
            }

            File[] files = dir.listFiles();
            if (files == null) continue;

            for (File file : files) {
                if (!file.isFile()) continue;
                String fileName = file.getName().toLowerCase();
                boolean supported = false;
                for (String ext : exts) {
                    if (fileName.endsWith("." + ext)) {
                        supported = true;
                        break;
                    }
                }
                if (!supported) continue;

                String justName = file.getName().replaceFirst("\\.[^.]+$", "");
                String normalizedFile = normalizeFileKey(justName);

                if (normalizedFile.equals(normalizedBase)
                        || normalizedFile.contains(normalizedBase)
                        || normalizedBase.contains(normalizedFile)) {
                    try {
                        return ImageIO.read(file);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return null;
    }

    private String normalizeName(String topping) {
        return topping.toLowerCase()
                .replace("&", "and")
                .replace("'", "")
                .replace("-", "_")
                .replace(" ", "_");
    }

    private String normalizeFileKey(String value) {
        return value.toLowerCase()
                .replace("&", "and")
                .replaceAll("[^a-z0-9]", "");
    }

    private BufferedImage createTextPlaceholderImage(String text, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(245, 228, 214));
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        g2.setColor(new Color(155, 99, 65));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        drawCenteredString(g2, text, new Rectangle(6, 6, w - 12, h - 12));
        g2.dispose();
        return img;
    }

    private void drawCenteredString(Graphics2D g2, String text, Rectangle rect) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        java.util.List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String test = current.length() == 0 ? word : current + " " + word;
            if (fm.stringWidth(test) > rect.width && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(test);
            }
        }
        if (current.length() > 0) lines.add(current.toString());

        int lineHeight = fm.getHeight();
        int totalHeight = lines.size() * lineHeight;
        int y = rect.y + (rect.height - totalHeight) / 2 + fm.getAscent();

        for (String line : lines) {
            int x = rect.x + (rect.width - fm.stringWidth(line)) / 2;
            g2.drawString(line, x, y);
            y += lineHeight;
        }
    }

    private class BackgroundPanel extends JPanel {
        private final BufferedImage bgImage;
        private final Color overlay;

        BackgroundPanel(BufferedImage bgImage, Color overlay) {
            this.bgImage = bgImage;
            this.overlay = overlay;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (bgImage != null) {
                g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 240, 228), getWidth(), getHeight(), new Color(255, 221, 204));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setColor(overlay);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private class PlatePanel extends JPanel {
        PlatePanel() {
            setOpaque(false);
            setBorder(BorderFactory.createTitledBorder("Your Crepe Plate"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            if (plateImage != null) {
                g2.drawImage(plateImage, centerX - 150, centerY - 120, 300, 240, null);
            } else {
                g2.setColor(new Color(240, 240, 240));
                g2.fillOval(centerX - 150, centerY - 95, 300, 190);
                g2.setColor(new Color(205, 205, 205));
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(centerX - 150, centerY - 95, 300, 190);
            }

            g2.setColor(new Color(244, 210, 150));
            int[] xPoints = {centerX - 60, centerX + 80, centerX - 10};
            int[] yPoints = {centerY - 20, centerY + 10, centerY + 90};
            g2.fillPolygon(xPoints, yPoints, 3);
            g2.setColor(new Color(196, 155, 102));
            g2.drawPolygon(xPoints, yPoints, 3);

            if (playerToppings.isEmpty()) {
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g2.drawString("Selected toppings will appear here", centerX - 115, centerY + 130);
            } else {
                int startX = centerX - 120;
                int startY = centerY - 60;
                int col = 0;
                int row = 0;
                for (String topping : playerToppings) {
                    BufferedImage img = tryLoadRawImage(normalizeName(topping));
                    int x = startX + col * 95;
                    int y = startY + row * 90;
                    if (img != null) {
                        g2.drawImage(img, x, y, 74, 54, null);
                    } else {
                        g2.drawImage(createTextPlaceholderImage(topping, 74, 54), x, y, null);
                    }
                    col++;
                    if (col == 3) {
                        col = 0;
                        row++;
                    }
                }
            }
            g2.dispose();
        }
    }
}
