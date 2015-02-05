package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Bbent_000 on 12/25/2014.
 */
public class GUI {
    public static BitmapFont font;
    public static Texture defaultTexture = new Texture("background.png");

    private static BitmapFont defaultFont = new BitmapFont();
    private static Texture defaultNormalButton = new Texture("defaultButton_normal.png");
    private static Texture defaultMousedButton = new Texture("defaultButton_moused.png");
    private static Texture defaultClickedButton = new Texture("defaultButton_clicked.png");

    private static ButtonStyle defaultButtonStyle = new ButtonStyle();

    private static boolean clicked = false;

    static{
        font = defaultFont;
    }

    public static void Texture(Rectangle rect, Texture texture, SpriteBatch batch){
        batch.draw(texture, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public static void Texture(float x, float y, float width, float height, Texture texture, SpriteBatch batch){
        batch.draw(texture, x, y, width, height);
    }

    public static void Text(String text, SpriteBatch batch, float x, float y){
        font.draw(batch, text, x, y);
    }

    public static boolean Button(Rectangle rect, SpriteBatch batch){
        return GUI.Button(rect, null, "", batch);
    }

    public static boolean Button(Rectangle rect, String text, SpriteBatch batch){


        return GUI.Button(rect, null, text, batch);
    }

    public static boolean Button(Rectangle rect, ButtonStyle style, String text, SpriteBatch batch){
        boolean clicked = false;

        if(style == null)
            style = defaultButtonStyle;

        Texture currTexture = style.normal;

        if(rect.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())){
            boolean down = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            if(down && !GUI.clicked){
                currTexture = style.clicked;
                GUI.clicked = clicked = true;
            }else if(down && GUI.clicked){
                currTexture = style.moused;
                clicked = false;
            }else {
                currTexture = style.moused;
                GUI.clicked = false;
            }
        }else
            GUI.clicked = false;

        batch.draw(currTexture, rect.x, rect.y, rect.getWidth(), rect.getHeight());
        BitmapFont.TextBounds bounds = font.getBounds(text);                                //Get the bounds of the text
        font.draw(batch, text, rect.getX() + rect.getWidth()/2 - bounds.width/2, rect.getY() + rect.getHeight()/2 + bounds.height/2); //Draw the text

        return clicked;
    }

    public static void Label(String text, SpriteBatch batch, float x, float y, boolean centered){
        if(centered){
            BitmapFont.TextBounds bounds = font.getBounds(text);
            font.draw(batch, text, x - bounds.width/2, y + bounds.height/2);
        }else{
            font.draw(batch, text, x, y);
        }
    }

    public static void Label(String text, SpriteBatch batch, Rectangle rect, boolean centered){
        GUI.Label(text, batch, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2, centered);
    }

    public static String TextBox(String text, SpriteBatch batch, float x, float y){
        return "";
    }

    public static void ResetFont(){
        GUI.font = defaultFont;
        GUI.font.setColor(Color.WHITE);
    }

    public static class ButtonStyle{
        public Texture normal = GUI.defaultNormalButton;
        public Texture moused = GUI.defaultMousedButton;
        public Texture clicked = GUI.defaultClickedButton;

        public ButtonStyle(){

        }
    }
}
