package eecs285.proj4.game.screens;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import eecs285.proj4.game.GameAssets;
import eecs285.proj4.game.Battle;
import eecs285.proj4.game.BattleInfo;
import eecs285.proj4.game.Game;
import eecs285.proj4.game.SelectableObject;
import eecs285.proj4.game.screens.ScreenMenu;
import eecs285.proj4.util.Window;
import eecs285.proj4.util.Render;

public class ScreenLevelSelect extends ScreenMenu {
	private Texture background;
	private TrueTypeFont titleFont;
	
	private BattleInfo battleInfo;
	
	public ScreenLevelSelect(BattleInfo battleInfo){
		super(new Window(0.0f, 100.0f, 0.0f, 100.0f));
		
		ArrayList<SelectableObject> menuItems = new ArrayList<SelectableObject>();
		menuItems.add(new LevelItem(25.0f, 75.0f, 10.0f, 25.0f, "Mario World", "level_mario"));
		menuItems.add(new LevelItem(25.0f, 75.0f, 30.0f, 45.0f, "Retro Action", "level_sonic"));
		menuItems.add(new LevelItem(25.0f, 75.0f, 50.0f, 65.0f, "Ice Bridge", "level_spyro"));
		menuItems.add(new LevelItem(25.0f, 75.0f, 70.0f, 85.0f, "Balloons!!!", "level_rayman"));
	
		initMenuItems(menuItems);
		
		this.battleInfo = battleInfo;
		
		background = GameAssets.GetTexture("level_screen_background");
		titleFont = GameAssets.GetFont("title");
	}

	public void onActivate(){
		super.onActivate();
		Game.setMusic(GameAssets.GetMusic("music_menu"));
	}
	
	public void render(double delta){
		Render.renderBackground(background);
		
		Render.render(titleFont, window, "Level Select", 
				window.getCenterX(), window.getTop() + 1.0f, 
				window.getSizeY()/16.0f, 0.5f, 0.0f, Color.red);
		
		super.render(delta);
	}
	
	// Menu items:
	class LevelItem extends SelectableObject{
		Texture highLight;
		TrueTypeFont font;
		String visibleName;
		String key;
		
		public LevelItem(float left, float right, float top, float bottom, String visibleName, String key) {
			super(left, right, top, bottom);
			highLight = GameAssets.GetTexture("select");
			font = GameAssets.GetFont("times_bold");
			
			this.visibleName = visibleName;
			this.key = key;
		}

		public void step(double delta){
			if(currentState == State.BEGIN_MOUSE_PRESS){
				battleInfo.setLevel(key);
				Game.popGameState();
				Game.pushGameState(new Battle(battleInfo));
			}
		}

		public void render(double delta){
			if(currentState != State.NONE){
				Render.render(highLight, this);
			}
			
			Render.render(font, window, visibleName, getCenterX(), getCenterY(), getSizeY()*0.5f, 0.5f, 0.5f, Color.blue);
		}
	}
}