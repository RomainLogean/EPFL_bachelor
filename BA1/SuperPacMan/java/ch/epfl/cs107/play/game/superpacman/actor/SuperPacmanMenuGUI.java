/*
 *	Author:      Romain Logean
 *	Date:        15 déc. 2020
 */

package ch.epfl.cs107.play.game.superpacman.actor;

import java.awt.Color;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class SuperPacmanMenuGUI extends ImageGraphics implements Graphics{
	private static  boolean gameOver=false;
	private static  boolean gamePause=false;
	private SuperPacmanPlayer player;
	private String deathMessage = null;
	
	public SuperPacmanMenuGUI(String name, float width, float height, SuperPacmanPlayer player) {
		super(name, width, height);
		this.player=player;
	}
	
	public static void gameOver() {
		gameOver=true;
	}
	
	public static void gamePause() {
		gamePause = true;
	}
	public static void gameResume() {
		gamePause=false;
	}
	@Override
	public void draw(Canvas canvas) {
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));
	
		if(gameOver) {
			TextGraphics gameOver = new TextGraphics("GAME OVER - "+player.getScoreString(), 0.9f, Color.RED, Color.BLACK, 0.05f, true, false, anchor.add(new Vector(width/2-6f, height/2+0.5f)));
			int randomInt = RandomGenerator.getInstance().nextInt(4);
			
			if(deathMessage==null) {
				switch (randomInt) {
					case 0 : {
						deathMessage="Stay determined.";
						break;
					}
					case 1 : {
						deathMessage="Don't lose hope.";
						break;
					}
					case 2 : {
						deathMessage="Try again!";
						break;
					}
					default : {
						deathMessage="Keep your determination.";
						break;
					}
				}
			}
			TextGraphics gameOver1 = new TextGraphics(deathMessage, 0.5f, Color.WHITE, Color.BLACK, 0f, false, true, anchor.add(new Vector(width/2-2.5f, height/2-1f)));
			gameOver.draw(canvas);
			gameOver1.draw(canvas);
		}
		if(gamePause) {
			TextGraphics gamePause = new TextGraphics("- PAUSE - ", 1f, Color.WHITE, Color.BLACK, 0.05f, true, false, anchor.add(new Vector(width/2-2.5f, height/2+1f)));
			TextGraphics gamePause1 = new TextGraphics("press enter te resume ", 0.75f, Color.WHITE, Color.BLACK, 0f, false, true, anchor.add(new Vector(width/2-4f, height/2-1f)));
			gamePause.draw(canvas);
			gamePause1.draw(canvas);
		}
	}
}
