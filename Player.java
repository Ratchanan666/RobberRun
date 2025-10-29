import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class Player {

    private int x, y;                         
    private final int BASE_WIDTH = 100;       
    private final int BASE_HEIGHT = 150;      
    private final int CROUCH_HEIGHT = 110;    
    private int width = BASE_WIDTH;      
    private int height = BASE_HEIGHT;      
    private int velocityY = 0;                // ความเร็วแกน Y
    private final int gravity = 1;            // แรงโน้มถ่วง (ค่าบวก = ดึงลง)
    private int groundY;                      // ระดับพื้น (พิกัด Y ของ "พื้น")
    private boolean jumping = false;   
    private boolean crouching = false;      
    private String state = "idle";
    private ArrayList<Image> runFrames = new ArrayList<>(); // เฟรมวิ่ง 6 เฟรม
    private Image idleImg, jumpImg, crouchImg, deadImg;
    private int frame = 0, frameDelay = 0;    
    private double scale = 1.0;
    public void setScale(double s) { this.scale = s; }
                   
    public Player(int x, int groundY) {
        this.x = x;
        this.groundY = groundY;
        this.height = BASE_HEIGHT;
        this.y = groundY - height; 

        for (int i = 1; i <= 6; i++) {
            String path = String.format("assets/Robber/run/1_terrorist_1_Run_%03d.png", i);
            runFrames.add(new ImageIcon(path).getImage());

        }
        idleImg = new ImageIcon("assets/Robber/idle/1_terrorist_1_Idle_001.png").getImage();
        jumpImg = new ImageIcon("assets/Robber/jump/1_terrorist_1_Jump_001.png").getImage();
        crouchImg = new ImageIcon("assets/Robber/crouch/1_terrorist_1_Crouch_001.png").getImage();
        deadImg = new ImageIcon("assets/Robber/dead/1_terrorist_1_Dead_001.png").getImage();
        state = "run";
    }

    public void update() {
        y += velocityY;
        if (jumping) velocityY += gravity;

        //ถึงพื้น
        int groundLevel = groundY - height;
        if (y >= groundLevel) {
            y = groundLevel;
            if (jumping) {
                jumping = false;
                if (!crouching && !state.equals("dead")) state = "run";
            }
        }
        if (state.equals("run")) {
            frameDelay++;
            if (frameDelay >= 5) {
                frame = (frame + 1) % runFrames.size();
                frameDelay = 0;
            }
        }
    }

    public void jump() {
        if (!jumping && !state.equals("dead")) {
            jumping = true;
            velocityY = -23;
            state = "jump";
            y = groundY - height;
        }
    }

    public void crouch() {
        if (!jumping && !state.equals("dead")) {
            crouching = true;
            state = "crouch";
            height = CROUCH_HEIGHT-10;
            width  = (int) (BASE_WIDTH * 1.8);
            y = groundY - height;
        }
    }

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

    /* วาดตัวละครตาม state  */
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

        // (ภาพ jump มักเล็กกว่า run)
        if (GamePanel.speedBoostActive && state.equals("jump")) {
            scaledWidth *= 1.1;
            scaledHeight *= 1.2;
        }
        // วาดให้เท้าอยู่ระดับเดิม
        int drawY = y - (scaledHeight - height);
        g.drawImage(imgToDraw, x, drawY, scaledWidth, scaledHeight, null);
    }

    /* hitbox */
    public Rectangle getBounds() {
        double scaleFactor = (GamePanel.speedBoostActive ? 1.8 : 1.0);

        // ปรับขนาด hitbox ให้พอดีตัว
        int hitWidth  = (int) (width  * 0.65 * scaleFactor);
        int hitHeight = (int) (height * 1.05 * scaleFactor);

        int offsetY = (int) (height * (scaleFactor - 1) + 8); // เลื่อนกล่องชนขึ้นข้างบน
        int hitX = x + (int) ((width * scaleFactor - hitWidth) / 2);//เลื่อนกล่องชนให้อยู่ ตรงกลางลำตัว
        int hitY = y - offsetY;//ขยับขึ้นจากตำแหน่งเดิมนิดหน่อย

        return new Rectangle(hitX, hitY, hitWidth, hitHeight);
    }
}
