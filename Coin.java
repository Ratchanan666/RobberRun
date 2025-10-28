import java.awt.*; // ใช้สำหรับวาดกราฟิก
import javax.swing.*; // ใช้โหลดรูปจากไฟล์

public class Coin {

    // พิกัดและความเร็วของเหรียญ
    private int x, y, speed;

    // ขนาดของเหรียญ
    private int width = 30, height = 30;

    // สถานะว่าเก็บเหรียญไปแล้วหรือยัง
    private boolean collected = false;

    // รูปภาพเหรียญ
    private Image coinImg;

    // Constructor: กำหนดตำแหน่งเริ่มต้นและโหลดรูปภาพ
    public Coin(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;

        // ✅ โหลดรูปเหรียญจากโฟลเดอร์ assets
        coinImg = new ImageIcon("assets/ui/coin.png").getImage();
    }

    // อัปเดตตำแหน่งเหรียญ (ให้เคลื่อนจากขวา → ซ้าย)
    public void update() {
        x -= speed;
    }

    // วาดเหรียญ (เฉพาะถ้ายังไม่ถูกเก็บ)
    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(coinImg, x, y, width, height, null);
        }
    }

    // ขอบสี่เหลี่ยมสำหรับตรวจชน
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // ตรวจว่าเหรียญถูกเก็บแล้วหรือยัง
    public boolean isCollected() {
        return collected;
    }

    // เปลี่ยนสถานะเป็น "ถูกเก็บแล้ว"
    public void collect() {
        collected = true;
    }

    // ตรวจว่าเหรียญหลุดออกจากจอหรือยัง
    public boolean isOutOfScreen() {
        return (x + width < 0);
    }

    // getter สำหรับตำแหน่ง X
    public int getX() {
        return x;
    }
}
