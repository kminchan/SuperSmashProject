package eecs285.proj4.game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputKeyboard extends Input {
	public InputKeyboard(){
		analogStick = false;
	}

	public void getInput(){
		primaryAttack = (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_PERIOD));
		secondaryAttack = (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_SLASH));
		jump = (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_SEMICOLON))
			|| (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_APOSTROPHE));
	
		xAxis = ((Keyboard.isKeyDown(Keyboard.KEY_D)? 1.0f: 0.0f) - (Keyboard.isKeyDown(Keyboard.KEY_A)? 1.0f: 0.0f));
		yAxis = ((Keyboard.isKeyDown(Keyboard.KEY_S)? 1.0f: 0.0f) - (Keyboard.isKeyDown(Keyboard.KEY_W)? 1.0f: 0.0f));
	}
}