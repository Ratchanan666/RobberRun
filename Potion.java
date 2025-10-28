import java.awt.*;
import javax.swing.*;

public class Potion {
    private int x, y, speed;          // พิกัดและความเร็วในการเคลื่อนที่
    private int width = 63, height = 63; // ขนาดของยา
    private boolean collected = false;   // สถานะว่าเก็บไปแล้วหรือยัง
    private Image img;                   // รูปภาพของยา

    /** Constructor: กำหนดตำแหน่ง, ความเร็ว และโหลดรูปภาพ */
    public Potion(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;

        // ✅ โหลดรูปภาพยาเพิ่มความเร็ว (ปรับ path เป็นสัมพัทธ์ถ้าใช้ในเครื่องอื่น)
        img = new ImageIcon("assets/ui/Potionspeed.png").getImage();

    }

    /** อัปเดตตำแหน่งของยาให้เคลื่อนไปทางซ้าย (เหมือนวัตถุอื่นในเกม) */
    public void update() {
        x -= speed;
    }

    /** วาดยาเพิ่มความเร็วบนหน้าจอ (ถ้ายังไม่ถูกเก็บ) */
    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(img, x, y, width, height, null);
        }
    }

    /** คืนค่าสี่เหลี่ยม hitbox สำหรับตรวจจับการชนกับ Player */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** ตั้งสถานะว่า "ถูกเก็บแล้ว" */
    public void collect() {
        collected = true;
    }

    /** ตรวจว่ายาเก็บไปแล้วหรือยัง */
    public boolean isCollected() {
        return collected;
    }

    /** คืนค่าพิกัดแนวนอน (ใช้ใน GamePanel เพื่อลบออกเมื่อหลุดจอ) */
    public int getX() {
        return x;
    }
}
