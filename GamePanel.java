import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * คลาส GamePanel
 * หน้าที่: พื้นที่หลักของเกม ควบคุมการวาดภาพ การชน การสร้างศัตรู และการควบคุมคีย์บอร์ด
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // 🔹 ขนาดจอเกม
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int GROUND_Y = HEIGHT - 80;

    // 🔹 สถานะ Speed Boost
    public static boolean speedBoostActive = false;

    // 🔹 ตัวแปรหลักของเกม
    private Timer timer;
    private Player player;
    private ArrayList<Police> policeList;
    private ArrayList<Heart> heartList;
    private ArrayList<Coin> coinList;
    private ArrayList<Potion> potionList;

    // 🔹 คะแนนและสถานะเกม
    private int score = 0;
    private int coinCount = 0;
    private boolean bonusActive = false;
    private int bonusTimer = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private Random rand = new Random();

    // 🔹 ระบบชีวิต
    private int life = 1;
    private final int MAX_LIFE = 3;

    // 🔹 ระบบความยาก
    private int difficultyTimer = 0;
    private int difficultyLevel = 1;

    // 🔹 พื้นหลัง
    private Image bgImg;
    private int bgX1 = 0, bgX2;
    private int bgSpeed = 8;

    // 🔹 ตัวจับเวลา Speed Boost และคะแนนสูงสุด
    private int speedBoostTimer = 0;
    private int highScore = 0;

    // 🔹 Constructor เริ่มเกม
    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setDoubleBuffered(true);

        // โหลดภาพพื้นหลัง
        bgImg = new ImageIcon("assets/bg.jpg").getImage();
        bgX2 = bgImg.getWidth(null);

        // สร้างอ็อบเจกต์เริ่มต้น
        player = new Player(100, HEIGHT - 100);
        policeList = new ArrayList<>();
        heartList = new ArrayList<>();
        coinList = new ArrayList<>();
        potionList = new ArrayList<>();

        timer = new Timer(20, this); // 20ms ต่อเฟรม
        timer.start();
    }

    /** วาดภาพทั้งหมดบนหน้าจอ */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดพื้นหลัง
        g.drawImage(bgImg, bgX1, 0, WIDTH, HEIGHT, null);
        g.drawImage(bgImg, bgX2, 0, WIDTH, HEIGHT, null);

        // วาดวัตถุในเกม
        player.draw(g);
        for (Police p : policeList) p.draw(g);
        for (Heart h : heartList) h.draw(g);
        for (Coin c : coinList) c.draw(g);
        for (Potion p : potionList) p.draw(g);

        // UI แสดงข้อมูล
        drawGameUI(g);

        // หน้าจอจบเกม
        if (gameOver) drawGameOver(g);

        // หน้าจอเริ่มเกม
        if (!gameStarted && !gameOver) drawStartScreen(g);
    }

    /** อัปเดตทุกเฟรม (Timer เด้งทุก 20ms) */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameStarted) { repaint(); return; }
        if (gameOver) { repaint(); return; }

        // พื้นหลังเลื่อน
        scrollBackground();

        // อัปเดตผู้เล่น
        player.update();

        // สุ่มสร้างวัตถุ
        spawnObjects();

        // อัปเดตวัตถุทั้งหมด
        updatePolice();
        updateCoins();
        updateHearts();
        updatePotions();

        // ตรวจจับเวลา Boost / Bonus
        handleBoostTimers();

        // เพิ่มคะแนนและระดับความยาก
        updateScoreAndDifficulty();

        repaint();
    }

    /** ✅ พื้นหลังเลื่อนต่อเนื่อง */
    private void scrollBackground() {
        bgX1 -= bgSpeed;
        bgX2 -= bgSpeed;
        if (bgX1 + bgImg.getWidth(null) <= 0) bgX1 = bgX2 + bgImg.getWidth(null);
        if (bgX2 + bgImg.getWidth(null) <= 0) bgX2 = bgX1 + bgImg.getWidth(null);
    }

    /** ✅ สุ่มสร้างศัตรูและไอเท็ม */
    private void spawnObjects() {
        // ศัตรู
        if (rand.nextInt(100) < 1 &&
            (policeList.isEmpty() || policeList.get(policeList.size() - 1).getX() < WIDTH - 600)) {

            int speed = getPoliceSpeed(difficultyLevel);
            int type = rand.nextInt(10);
            if (type < 6) policeList.add(new NormalPolice(WIDTH, speed));
            else if (type < 8) policeList.add(new DogPolice(WIDTH, speed));
            else policeList.add(new HelicopterPolice(WIDTH, speed));
        }

        // เหรียญ (เป็นแถว)
        if (rand.nextInt(120) == 0) {
            int gap = 45, baseY = GROUND_Y - 110;
            for (int i = 0; i < 5; i++)
                coinList.add(new Coin(WIDTH + i * gap, baseY, 6));
        }

        // หัวใจ
        if (rand.nextInt(400) == 0)
            heartList.add(new Heart(WIDTH, rand.nextInt(200) + 350, 6));

        // ยาเพิ่มความเร็ว
        if (rand.nextInt(500) == 0)
            potionList.add(new Potion(WIDTH, GROUND_Y - 120, 6));
    }

    /** ✅ อัปเดตศัตรู */
    private void updatePolice() {
        Iterator<Police> iter = policeList.iterator();
        while (iter.hasNext()) {
            Police p = iter.next();
            p.update();

            if (p.getX() < -100) { iter.remove(); continue; }

            if (p instanceof HelicopterPolice && player.isCrouching()) continue;

            if (!speedBoostActive && checkCollisionSmooth(player, p)) {
                life--;
                iter.remove();
                if (life <= 0) handleGameOver();
            }
        }
    }

    /** ✅ อัปเดตเหรียญ */
    private void updateCoins() {
        Iterator<Coin> it = coinList.iterator();
        while (it.hasNext()) {
            Coin c = it.next();
            c.update();
            if (c.getX() < -50) { it.remove(); continue; }

            if (player.getBounds().intersects(c.getBounds())) {
                coinCount++;
                score += bonusActive ? 100 : 50;
                it.remove();
                if (coinCount % 10 == 0) {
                    bonusActive = true;
                    bonusTimer = 500;
                }
            }
        }
    }

    /** ✅ อัปเดตหัวใจ */
    private void updateHearts() {
        Iterator<Heart> it = heartList.iterator();
        while (it.hasNext()) {
            Heart h = it.next();
            h.update();
            if (h.getX() < -50) { it.remove(); continue; }
            if (player.getBounds().intersects(h.getBounds())) {
                if (life < MAX_LIFE) life++;
                it.remove();
            }
        }
    }

    /** ✅ อัปเดต Potion เพิ่มความเร็ว */
    private void updatePotions() {
        Iterator<Potion> it = potionList.iterator();
        while (it.hasNext()) {
            Potion p = it.next();
            p.update();
            if (p.getX() < -50) { it.remove(); continue; }

            if (player.getBounds().intersects(p.getBounds())) {
                it.remove();
                if (speedBoostActive) speedBoostTimer += 500;
                else {
                    speedBoostActive = true;
                    speedBoostTimer = 500;
                    bgSpeed += 4;
                    player.setScale(1.8);
                }
            }
        }
    }

    /** ✅ ตรวจชน */
    private boolean checkCollisionSmooth(Player player, Police police) {
        Rectangle a = player.getBounds(), b = police.getBounds();
        if (a.intersects(b)) return true;
        if (police instanceof HelicopterPolice h) {
            int prevX = h.getPrevX();
            return (prevX > a.x && h.getX() < a.x + a.width);
        }
        return false;
    }

    /** ✅ จัดการเวลา Boost และ Bonus */
    private void handleBoostTimers() {
        if (speedBoostActive && --speedBoostTimer <= 0) {
            speedBoostActive = false;
            bgSpeed -= 4;
            player.setScale(1.0);
        }
        if (bonusActive && --bonusTimer <= 0)
            bonusActive = false;
    }

    /** ✅ เพิ่มคะแนนและความยาก */
    private void updateScoreAndDifficulty() {
        score += bonusActive ? 2 : 1;
        if (++difficultyTimer % 1000 == 0) {
            difficultyLevel++;
            for (Police p : policeList) p.increaseSpeed(2);
        }
    }

    /** ✅ คำนวณความเร็วตำรวจตามเลเวล */
    private int getPoliceSpeed(int level) { return 8 + 2 * level; }

    /** ✅ การควบคุมด้วยคีย์บอร์ด */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (!gameStarted && code == KeyEvent.VK_SPACE) { gameStarted = true; return; }
        if (code == KeyEvent.VK_SPACE) player.jump();
        if (code == KeyEvent.VK_DOWN) player.crouch();
        if (code == KeyEvent.VK_R && gameOver) resetGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) player.standUp();
    }

    @Override public void keyTyped(KeyEvent e) {}

    /** ✅ รีเซ็ตเกม */
    private void resetGame() {
        player = new Player(100, GROUND_Y - 20);
        policeList.clear();
        heartList.clear();
        coinList.clear();
        potionList.clear();
        score = 0; coinCount = 0; life = 1;
        gameOver = false; difficultyTimer = 0; difficultyLevel = 1;
        bgSpeed = 8;
        timer.start();
        gameStarted = true;
    }

    /** ✅ จัดการ Game Over */
    private void handleGameOver() {
        gameOver = true;
        player.setState("dead");
        timer.stop();
        if (score > highScore) highScore = score;
    }

    /** ✅ วาด UI (คะแนน, เหรียญ, โบนัส) */
    private void drawGameUI(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tahoma", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("Coins: " + coinCount, 20, 60);
        g.drawString("Level: " + difficultyLevel, 20, 90);
        g.drawString("High Score: " + highScore, 1050, 30);

        if (speedBoostActive) {
            int sec = speedBoostTimer / 50;
            g.setColor(Color.CYAN);
            g.drawString("Speed Boost: " + sec + "s", 1050, 60);
        }
        if (bonusActive) {
            int sec = bonusTimer / 50;
            g.setColor(Color.ORANGE);
            g.drawString("Score x2: " + sec + "s", 1050, 90);
        }

        Image heartIcon = new ImageIcon("assets/ui/heart.png").getImage();
        for (int i = 0; i < life; i++)
            g.drawImage(heartIcon, 20 + (i * 35), 120, 25, 25, null);
    }

    /** ✅ วาดหน้าจอเริ่มเกม */
    private void drawStartScreen(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        String title = "ROBBER RUN";
        g2d.setFont(new Font("Impact", Font.BOLD, 100));
        int w = g2d.getFontMetrics().stringWidth(title);
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, (WIDTH - w) / 2, HEIGHT / 2 - 100);

        g2d.setFont(new Font("Impact", Font.BOLD, 36));
        String press = "PRESS SPACE TO START";
        int pw = g2d.getFontMetrics().stringWidth(press);
        g2d.drawString(press, (WIDTH - pw) / 2, HEIGHT / 2 + 80);
    }

    /** ✅ วาดหน้าจอ Game Over */
    private void drawGameOver(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        String over = "GAME OVER";
        g2d.setFont(new Font("Impact", Font.BOLD, 90));
        int w = g2d.getFontMetrics().stringWidth(over);
        g2d.setColor(Color.WHITE);
        g2d.drawString(over, (WIDTH - w) / 2, HEIGHT / 2 - 46);

        g2d.setFont(new Font("Impact", Font.BOLD, 36));
        String restart = "PRESS R TO RESTART";
        int rw = g2d.getFontMetrics().stringWidth(restart);
        g2d.drawString(restart, (WIDTH - rw) / 2, HEIGHT / 2 + 70);
    }
}
