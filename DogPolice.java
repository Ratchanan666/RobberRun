import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * คลาส DogPolice
 * ศัตรูประเภทสุนัข วิ่งต่ำระดับพื้น ใช้ภาพเคลื่อนไหว (Animation) 5 เฟรม
 * สืบทอดจากคลาส Police
 */
public class DogPolice extends Police {
    private ArrayList<Image> runFrames = new ArrayList<>(); // เก็บภาพเคลื่อนไหวตอนวิ่ง
    private int frame = 0, frameDelay = 0;                  // ตัวนับเฟรมสำหรับอนิเมชัน

    /** Constructor: กำหนดตำแหน่งและโหลดภาพ */
    public DogPolice(int x, int speed) {
        super(x, speed);     // เรียก constructor ของ Police (คลาสแม่)
        this.width = 120;
        this.height = 120;

        // ✅ ปรับระดับให้อยู่ติดพื้น (ต่ำกว่าโจรเล็กน้อย)
        this.y = GamePanel.GROUND_Y - height - 30;

        // ✅ โหลดภาพสุนัข 5 เฟรม
        for (int i = 1; i <= 5; i++) {
            String path = String.format("assets/dog/dog_run_%03d.png", i);
            runFrames.add(new ImageIcon(path).getImage());
        }
    }

    /** อัปเดตตำแหน่งและเฟรมอนิเมชัน */
    @Override
    public void update() {
        x -= speed;           // เคลื่อนจากขวาไปซ้าย
        frameDelay++;         // หน่วงเวลาเปลี่ยนภาพ
        if (frameDelay >= 5) {
            frame = (frame + 1) % runFrames.size(); // เปลี่ยนภาพต่อเนื่อง
            frameDelay = 0;
        }
    }

    /** วาดสุนัขบนหน้าจอ */
    @Override
    public void draw(Graphics g) {
        Image img = runFrames.get(frame);
        g.drawImage(img, x, y, width, height, null);
    }

    /** Hitbox ใช้ตรวจจับการชนกับ Player */
    @Override
    public Rectangle getBounds() {
        // ✅ ทำให้ hitbox เล็กลงเล็กน้อยเพื่อให้ชนแม่นยำขึ้น
        int hitWidth = (int)(width * 0.6);
        int hitHeight = (int)(height * 0.8);
        int hitX = x + (width - hitWidth) / 2 + 15; // ขยับมาขวาเล็กน้อย
        int hitY = y + (height - hitHeight);

        return new Rectangle(hitX, hitY, hitWidth, hitHeight);
    }
}
