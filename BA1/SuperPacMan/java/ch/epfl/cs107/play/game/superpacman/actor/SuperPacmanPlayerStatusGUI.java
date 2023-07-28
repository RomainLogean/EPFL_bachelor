/*
 *	Author:      Romain Logean
 *	Date:        5 déc. 2020
 */



package ch.epfl.cs107.play.game.superpacman.actor;

import java.awt.Color;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
class SuperPacmanPlayerStatusGUI extends ImageGraphics implements Graphics{
	private SuperPacmanPlayer player;

	protected SuperPacmanPlayerStatusGUI(String name, float width, float height, SuperPacmanPlayer player) {
		super(name, width, height);
		this.player=player;
	}
	
	@Override
	public void draw(Canvas canvas) {
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));
		
		for(int n=0;n<SuperPacmanPlayer.PV_MAX;++n) {
			int m = (n >= player.getPv()) ? 64 : 0;
			
				ImageGraphics life = new ImageGraphics(ResourcePath.getSprite("superpacman/lifeDisplay"), 1.f, 1.f, new RegionOfInterest(m, 0, 64, 64),
				anchor.add(new Vector(n, height - 1.375f)), 1, getDepth());
				life.draw(canvas);
		}
		TextGraphics score = new TextGraphics(player.getScoreString(), 0.7f, Color.YELLOW, Color.BLACK, 0.05f, true, false, anchor.add(new Vector(SuperPacmanPlayer.PV_MAX + 0.5f, height - 1.1875f)));
		score.draw(canvas);
	}
}
