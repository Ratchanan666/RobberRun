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
    }

    /** อัปเดตการเคลื่อนไหวและอนิเมชัน */
    @Override
    public void update() {
        x -= speed;      // เคลื่อนจากขวาไปซ้าย

        //อัปเดตเฟรมอนิเมชัน
        frameDelay++;
        if (frameDelay >= 6) {
            frame = (frame + 1) % frames.size();
            frameDelay = 0;
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Image currentFrame = frames.get(frame);

        // พลิกภาพแนวนอน (flip) 
        g2d.drawImage(currentFrame, x + width, y - height, -width, height, null);
    }

    /** กำหนด hitbox สำหรับตรวจการชน */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y - height, width, height);
    }
}
