import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
public class DogPolice extends Police {
    private ArrayList<Image> runFrames = new ArrayList<>(); // เก็บภาพเคลื่อนไหวตอนวิ่ง
    private int frame = 0, frameDelay = 0;                  // ตัวนับเฟรมสำหรับอนิเมชัน
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

    /** อัปเดตตำแหน่งและเฟรม*/
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

    /** Hitbox*/
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

}
