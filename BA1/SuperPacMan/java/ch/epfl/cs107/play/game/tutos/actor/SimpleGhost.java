package ch.epfl.cs107.play.game.tutos.actor;

import java.awt.Color;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.math.Vector;



public class SimpleGhost extends Entity{
	private float hp;
	private TextGraphics hpText;
	private Sprite texture;
	
	
	
	public SimpleGhost(Vector position, String spriteName) {
		super(position);
		hp = 10;
		
		texture = new Sprite(spriteName, 1, 1.f, this);
		
		hpText = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
		hpText.setParent(this);
		hpText.setAnchor(new Vector(-0.3f, 0.1f));

	}
	
	
	public void moveUp() {
		setCurrentPosition(getPosition().add(0.f, 0.15f));
	}
	
	public void moveDown() {
		setCurrentPosition(getPosition().add(0.f, -0.15f));
	}
	
	public void moveLeft() {
		setCurrentPosition(getPosition().add(-0.15f, 0.f));
	}
	
	public void moveRight() {
		setCurrentPosition(getPosition().add(0.15f, 0.f));
	}

	public boolean isWeak() {
		if(hp<=0) {
			return true;
		}
		return false;
		
	}
	public void strengthen() {
		hp = 10;
	}

	@Override
	public void draw(Canvas canvas) {
		hpText.draw(canvas);
		texture.draw(canvas);
    }
	@Override
	public void update(float deltaTime) {
		
		if(!isWeak()) {
			hp -= deltaTime;
			hpText.setText(Integer.toString((int)hp));
		}
		else {
			hp=0;
		}
	}
}
