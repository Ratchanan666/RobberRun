import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class NormalPolice extends Police {
    private ArrayList<Image> runFrames = new ArrayList<>(); // เก็บภาพเฟรมแอนิเมชัน
    private int frame = 0, frameDelay = 0;                  // ใช้ควบคุมความเร็วในการเปลี่ยนเฟรม

    /** Constructor: โหลดภาพและกำหนดค่าพื้นฐาน */
    public NormalPolice(int x, int speed) {
        super(x, speed); 
        this.width = 120;
        this.height = 130;
        this.y = GamePanel.GROUND_Y - height - 25; // วางให้ยืนบนพื้น
        for (int i = 1; i <= 6; i++) {
            String path = String.format("assets/police/police_run_%03d.png", i);
            runFrames.add(new ImageIcon(path).getImage());
        }
    }
    /** อัปเดตตำแหน่งและแอนิเมชันของตำรวจ */
    @Override
    public void update() {
        x -= speed; 
        frameDelay++; // หน่วงเวลาเปลี่ยนภาพ
        if (frameDelay >= 5) {
            frame = (frame + 1) % runFrames.size(); // เปลี่ยนภาพต่อเนื่อง
            frameDelay = 0;
        }
    }
    @Override
    public void draw(Graphics g) {
        Image img = runFrames.get(frame);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(
            img, x + width,  y, -width, height,null);
    }
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
