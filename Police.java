import java.awt.*;

public abstract class Police {
    protected int x, y;         // พิกัดของศัตรู
    protected int width, height; // ขนาดของภาพ
    protected int speed;        // ความเร็วในการเคลื่อนที่ (พิกัด X)

    /** Constructor: กำหนดค่าตำแหน่งเริ่มต้นและความเร็ว */
    public Police(int x, int speed) {
        this.x = x;
        this.speed = speed;
    }

    /** เมธอด abstract (ให้คลาสลูก override เอง) */
    public abstract void update();          // อัปเดตการเคลื่อนไหว
    public abstract void draw(Graphics g);  // วาดภาพศัตรู

    /** ✅ hitbox สำหรับตรวจจับการชน (ใช้กับ Player) */
    public Rectangle getBounds() {
        // hitbox เริ่มจากตำแหน่ง x, y โดยเลื่อนขึ้นจากพื้นเล็กน้อย (ประมาณ 120px)
        return new Rectangle(x, y - height + 120, width, height);
    }

    /** คืนค่าพิกัด X ปัจจุบันของศัตรู */
    public int getX() { 
        return x; 
    }

    /** เพิ่มความเร็ว (ใช้ตอนเลเวลเพิ่มใน GamePanel) */
    public void increaseSpeed(int add) {
        speed += add;
    }
}
