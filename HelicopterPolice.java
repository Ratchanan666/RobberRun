import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class HelicopterPolice extends Police {
    private ArrayList<Image> frames = new ArrayList<>(); // เก็บภาพเฟรมเคลื่อนไหว
    private int frame = 0, frameDelay = 0;               // ควบคุมความเร็วการเปลี่ยนเฟรม
    private int prevX;                                   // เก็บตำแหน่งก่อนหน้า ใช้ตรวจชนข้ามเฟรม

    /** Constructor: โหลดภาพและตั้งค่าตำแหน่งเริ่มต้น */
    public HelicopterPolice(int x, int speed) {
        super(x, speed);

        // ✅ โหลดภาพอนิเมชัน 4 เฟรม
        for (int i = 1; i <= 4; i++) {
            String path = "assets/helicopter/helicopter_run_" + String.format("%03d", i) + ".png";

            frames.add(new ImageIcon(path).getImage());
        }

        this.width = 350;
        this.height = 100;
        this.y = GamePanel.GROUND_Y - 135; // ให้ลอยเหนือพื้น
        this.prevX = x;                    // เก็บตำแหน่งเริ่มต้นไว้ใช้ในครั้งถัดไป
    }

    /** อัปเดตการเคลื่อนไหวและอนิเมชัน */
    @Override
    public void update() {
        prevX = x;       // เก็บตำแหน่งก่อนหน้าไว้ใช้ตรวจการชน
        x -= speed;      // เคลื่อนจากขวาไปซ้าย

        // 🔁 อัปเดตเฟรมอนิเมชัน (เปลี่ยนภาพทุก 6 tick)
        frameDelay++;
        if (frameDelay >= 6) {
            frame = (frame + 1) % frames.size();
            frameDelay = 0;
        }
    }

    /** วาดเฮลิคอปเตอร์บนหน้าจอ (หันเข้าหาผู้เล่น) */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Image currentFrame = frames.get(frame);

        // ✅ พลิกภาพแนวนอน (flip) เพื่อให้เฮลิคอปเตอร์หันเข้าหาโจร
        g2d.drawImage(currentFrame, x + width, y - height, -width, height, null);
    }

    /** กำหนด hitbox สำหรับตรวจการชน */
    @Override
    public Rectangle getBounds() {
        // ✅ กำหนด hitbox ให้อยู่กลางลำเครื่อง (แคบกว่าภาพจริงเพื่อความสมจริง)
        int hitWidth = (int) (width * 0.5);
        int hitHeight = (int) (height * 0.6);
        int hitX = x + (width - hitWidth) / 2;
        int hitY = y - height + (height - hitHeight) / 2;

        return new Rectangle(hitX, hitY, hitWidth, hitHeight);
    }

    /** ✅ ตรวจว่าฮ. บินผ่าน player ในเฟรมนี้หรือไม่ (ใช้ใน checkCollisionSmooth) */
    public boolean passedThrough(Rectangle playerRect) {
        Rectangle now = getBounds();
        Rectangle before = new Rectangle(
            prevX + (width - (int)(width * 0.8)) / 2,
            now.y,
            (int)(width * 0.8),
            (int)(height * 0.6)
        );

        // ถ้าเฟรมก่อนอยู่ขวา แล้วเฟรมนี้อยู่ซ้าย และเส้นทางผ่าน player → ถือว่าชน
        return (before.x > playerRect.x + playerRect.width && now.x < playerRect.x);
    }

    /** Getter ใช้ดึงค่าตำแหน่งก่อนหน้า (prevX) */
    public int getPrevX() {
        return prevX;
    }

    /** Getter ใช้ดึงค่าความกว้าง (width) */
    public int getWidth() {
        return width;
    }
}
