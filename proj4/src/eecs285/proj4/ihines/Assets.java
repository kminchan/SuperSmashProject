package eecs285.proj4.ihines;

import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import eecs285.proj4.util.Sprite;

public class Assets {
	private static boolean texturesInitialized = false;
	private static boolean soundsInitialized = false;
	private static boolean fontsInitialized = false;
	
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private static HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();
	private static HashMap<String, TrueTypeFont> fonts = new HashMap<String, TrueTypeFont>();
	
	public static void InitializeTextures(){
		if(!texturesInitialized){
			try {
				Texture texture; 
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("Button.png"));
				textures.put("button", texture);
				
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("Menu_Selector.png"));
				textures.put("select", texture);
				
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("Sky.png"));
				textures.put("start_menu_background", texture);
				textures.put("title_screen_background", texture);
				textures.put("about_screen_background", texture);
				textures.put("how_to_play_screen_background", texture);
				
				sprites.put("example_sprite", new Sprite(texture, 0.0f, 0.1f, 0.0f, 0.1f));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			
			texturesInitialized = true;
		}
	}
	
	public static void InitializeFonts(){
		if(!fontsInitialized){
			Font awtFont = new Font("Times New Roman", Font.BOLD, 48);
			TrueTypeFont font = new TrueTypeFont(awtFont, false);
			fonts.put("times_bold", font);
			fonts.put("title", font);
			
			fontsInitialized = true;
		}
	}
	
	// TODO: figure out how to use sounds (Online)
	public static void InitializeSounds(){
		if(!soundsInitialized){
		
			soundsInitialized = true;
		}
	}

	public static Texture GetTexture(String label){
		return textures.get(label);
	}
	
	public static Sprite GetSprite(String label){
		return sprites.get(label);
	}
	
	public static TrueTypeFont GetFont(String label){
		return fonts.get(label);
	}
	
	private Assets(){}
}