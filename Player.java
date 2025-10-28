import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class Player {

    private int x, y;                         // พิกัดซ้ายบนของตัวละคร
    private final int BASE_WIDTH = 100;       // ความกว้างพื้นฐานตอนยืน
    private final int BASE_HEIGHT = 150;      // ความสูงพื้นฐานตอนยืน
    private final int CROUCH_HEIGHT = 110;    // ความสูงตอนหมอบ
    private int width = BASE_WIDTH;           // ความกว้างปัจจุบัน (เปลี่ยนได้เมื่อหมอบ/กระโดด)
    private int height = BASE_HEIGHT;         // ความสูงปัจจุบัน
    private int velocityY = 0;                // ความเร็วแกน Y
    private final int gravity = 1;            // แรงโน้มถ่วง (ค่าบวก = ดึงลง)
    private int groundY;                      // ระดับพื้น (พิกัด Y ของ "พื้น")
    private boolean jumping = false;          // กำลังกระโดดอยู่หรือไม่
    private boolean crouching = false;        // กำลังหมอบอยู่หรือไม่
    private String state = "idle";
    private ArrayList<Image> runFrames = new ArrayList<>(); // เฟรมวิ่ง 6 เฟรม
    private Image idleImg, jumpImg, crouchImg, deadImg;     // ภาพเดี่ยวของแต่ละสถานะ
    private int frame = 0, frameDelay = 0;                  // คุมความเร็วเฟรมตอนวิ่ง
    private double scale = 1.0;
    public void setScale(double s) { this.scale = s; }

    /** Constructor: กำหนดตำแหน่ง/พื้น และโหลดภาพทั้งหมด */
    public Player(int x, int groundY) {
        this.x = x;
        this.groundY = groundY;
        this.height = BASE_HEIGHT;
        this.y = groundY - height; // ให้ "เท้า" อยู่บนพื้นพอดี

        // โหลดภาพ run (6 เฟรม)
        for (int i = 1; i <= 6; i++) {
            String path = String.format("assets/Robber/run/1_terrorist_1_Run_%03d.png", i);
            runFrames.add(new ImageIcon(path).getImage());

        }
        // โหลดภาพสถานะเดี่ยว
        idleImg   = new ImageIcon("D:/RobberRun/assets/Robber/idle/1_terrorist_1_Idle_001.png").getImage();
        jumpImg   = new ImageIcon("D:/RobberRun/assets/Robber/jump/1_terrorist_1_Jump_001.png").getImage();
        crouchImg = new ImageIcon("D:/RobberRun/assets/Robber/crouch/1_terrorist_1_Crouch_001.png").getImage();
        deadImg   = new ImageIcon("D:/RobberRun/assets/Robber/dead/1_terrorist_1_Dead_001.png").getImage();
        state = "run";
    }

    /** อัปเดตฟิสิกส์/สถานะ และอนิเมชันวิ่ง */
    public void update() {
        y += velocityY;
        if (jumping) velocityY += gravity;

        // ชนพื้น?
        int groundLevel = groundY - height;
        if (y >= groundLevel) {
            y = groundLevel;
            if (jumping) {
                jumping = false;
                if (!crouching && !state.equals("dead")) state = "run";
            }
        }

        // เดิน/วิ่ง: เลื่อนเฟรมอนิเมชัน
        if (state.equals("run")) {
            frameDelay++;
            if (frameDelay >= 5) {
                frame = (frame + 1) % runFrames.size();
                frameDelay = 0;
            }
        }
    }

    /** กระโดด (เริ่มได้เมื่อไม่กำลังกระโดดและไม่ตาย) */
    public void jump() {
        if (!jumping && !state.equals("dead")) {
            jumping = true;
            velocityY = -23;
            state = "jump";
            y = groundY - height;
        }
    }

    /** หมอบ (เฉพาะตอนอยู่บนพื้นและยังไม่ตาย) */
    public void crouch() {
        if (!jumping && !state.equals("dead")) {
            crouching = true;
            state = "crouch";

            // ลดความสูง และเพิ่มความกว้างเล็กน้อย (บาลานซ์ภาพ)
            height = CROUCH_HEIGHT;
            width  = (int) (BASE_WIDTH * 1.8);

            // ย้ายตัวลงมาให้เท้าชิดพื้น
            y = groundY - height;
        }
    }

    /** เลิกหมอบ → กลับสู่ run */
    public void standUp() {
        if (crouching) {
            crouching = false;
            state = "run";

            width  = BASE_WIDTH;
            height = BASE_HEIGHT;
            y = groundY - height;
        }
    }

    public void setState(String s) { state = s; }
    public boolean isCrouching() { return crouching; }

    /** วาดตัวละครตาม state ปัจจุบัน + รองรับสเกลตอนบูสต์ */
    public void draw(Graphics g) {
        Image imgToDraw;
        switch (state) {
            case "run":   imgToDraw = runFrames.get(frame % runFrames.size()); break;
            case "jump":  imgToDraw = jumpImg;   break;
            case "crouch":imgToDraw = crouchImg; break;
            case "dead":  imgToDraw = deadImg;   break;
            default:      imgToDraw = idleImg;
        }

        // คำนวณขนาดหลังสเกล และเลื่อนรูปขึ้นเท่าที่โตขึ้น เพื่อให้ "เท้า" อยู่ระดับเดิม
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);

        // ✅ ถ้ากำลังกระโดดและมีบูสต์ ให้ขยายเพิ่มอีกนิด (ภาพ jump มักเล็กกว่า run)
        if (GamePanel.speedBoostActive && state.equals("jump")) {
            scaledWidth *= 1.1;
            scaledHeight *= 1.2;
        }

        // วาดให้เท้าอยู่ระดับเดิม
        int drawY = y - (scaledHeight - height);


        g.drawImage(imgToDraw, x, drawY, scaledWidth, scaledHeight, null);
    }

    /** hitbox ปรับตามขนาด/สเกลจริง ขณะบูสต์จะกว้างขึ้น (อ่านค่า static จาก GamePanel) */
    public Rectangle getBounds() {
        double scaleFactor = (GamePanel.speedBoostActive ? 1.8 : 1.0);

        // ปรับขนาด hitbox ให้พอดีตัว (อมนิดหน่อยเพื่อการเล่นที่แฟร์)
        int hitWidth  = (int) (width  * 0.65 * scaleFactor);
        int hitHeight = (int) (height * 1.05 * scaleFactor);

        // ขยับ Y ขึ้นเล็กน้อยให้ครอบหัว; X จูนให้อยู่กลางตัว
        int offsetY = (int) (height * (scaleFactor - 1) + 8);
        int hitX = x + (int) ((width * scaleFactor - hitWidth) / 2);
        int hitY = y - offsetY;

        return new Rectangle(hitX, hitY, hitWidth, hitHeight);
    }
}
