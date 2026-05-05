package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed, ePressed, enterPressed, escapePressed;
    public boolean mPressed, num1Pressed, num2Pressed, num3Pressed, num4Pressed;
    /** UC3 Use Phone — open phone overlay from gameplay. */
    public boolean pPressed;
    /** Leave / close context menus (e.g. Cafe Menu). */
    public boolean lPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if(code == KeyEvent.VK_E) {
            ePressed = true;
        }
        if(code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if(code == KeyEvent.VK_ESCAPE) {
            escapePressed = true;
        }
        if(code == KeyEvent.VK_M) {
            mPressed = true;
        }
        if(code == KeyEvent.VK_1) {
            num1Pressed = true;
        }
        if(code == KeyEvent.VK_2) {
            num2Pressed = true;
        }
        if(code == KeyEvent.VK_3) {
            num3Pressed = true;
        }
        if(code == KeyEvent.VK_4) {
            num4Pressed = true;
        }
        if (code == KeyEvent.VK_P) {
            pPressed = true;
        }
        if (code == KeyEvent.VK_L) {
            lPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if(code == KeyEvent.VK_E) {
            ePressed = false;
        }
        if(code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }
        if(code == KeyEvent.VK_ESCAPE) {
            escapePressed = false;
        }
        if(code == KeyEvent.VK_M) {
            mPressed = false;
        }
        if(code == KeyEvent.VK_1) {
            num1Pressed = false;
        }
        if(code == KeyEvent.VK_2) {
            num2Pressed = false;
        }
        if(code == KeyEvent.VK_3) {
            num3Pressed = false;
        }
        if(code == KeyEvent.VK_4) {
            num4Pressed = false;
        }
        if (code == KeyEvent.VK_P) {
            pPressed = false;
        }
        if (code == KeyEvent.VK_L) {
            lPressed = false;
        }
    }
}
