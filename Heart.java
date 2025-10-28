import java.awt.*;
import javax.swing.*;

public class Heart {
    private int x, y, speed;         // พิกัดและความเร็วของหัวใจ
    private int size = 60;           // ขนาดของรูปหัวใจ
    private boolean collected = false; // สถานะว่าถูกเก็บแล้วหรือยัง
    private Image heartImg;          // รูปภาพหัวใจ

    /** Constructor: กำหนดตำแหน่งเริ่มต้น ความเร็ว และโหลดรูปหัวใจ */
    public Heart(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;

        // ✅ โหลดรูปหัวใจ 
        heartImg = new ImageIcon("assets/ui/Potion.png").getImage();

    }

    /** อัปเดตตำแหน่งของหัวใจ (เคลื่อนไปทางซ้าย) */
    public void update() {
        x -= speed;
    }

    /** วาดหัวใจบนหน้าจอ (เฉพาะถ้ายังไม่ถูกเก็บ) */
    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(heartImg, x, y, size, size, null);
        }
    }

    /** กำหนดกรอบสี่เหลี่ยมของหัวใจไว้ตรวจจับการชน */
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /** ตรวจว่าเก็บหัวใจไปแล้วหรือยัง */
    public boolean isCollected() {
        return collected;
    }

    /** ตั้งสถานะว่า “เก็บแล้ว” */
    public void collect() {
        collected = true;
    }

    /** ตรวจว่าหัวใจหลุดออกจากจอหรือยัง */
    public boolean isOutOfScreen() {
        return (x + size < 0);
    }

    /** คืนค่าตำแหน่งแนวนอน (X) ของหัวใจ */
    public int getX() {
        return x;
    }
}
