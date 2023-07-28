/*
 *	Author:      Romain Logean
 *	Date:        24 nov. 2020
 */

package ch.epfl.cs107.play.game.tutos.actor;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class GhostPlayer extends MovableAreaEntity{
	private float hp;
	private TextGraphics hpText;
	private Sprite texture;
	/// Animation duration in frame number
	private final static int ANIMATION_DURATION = 8;
		
	
	public GhostPlayer(Area owner, Orientation orientation, DiscreteCoordinates position, String spriteName) {
		super(owner, orientation, position);
		hp = 10;
		
		texture = new Sprite(spriteName, 1, 1.f, this);
		
		hpText = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
		hpText.setParent(this);
		hpText.setAnchor(new Vector(-0.3f, 0.1f));
		resetMotion();
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
		
		Keyboard keyboard= getOwnerArea().getKeyboard();
		moveOrientation(Orientation.UP,keyboard.get(Keyboard.UP));
		moveOrientation(Orientation.DOWN,keyboard.get(Keyboard.DOWN));
		moveOrientation(Orientation.LEFT,keyboard.get(Keyboard.LEFT));
		moveOrientation(Orientation.RIGHT,keyboard.get(Keyboard.RIGHT));
		
		super.update(deltaTime);
	}
	
	public void moveOrientation(Orientation orientation, Button bouton) {
		if(bouton.isDown()) {
			if(this.getOrientation()==orientation) 
				move(ANIMATION_DURATION);
			else {
				this.orientate(orientation);
			}
		}
		
	}

	public boolean isWeak() {
		if(hp<=0) {
			return true;
		}
		return false;
		
	}
	public void strengthen() {
		hp = 50;
	}
	
	public void enterArea(Area area, DiscreteCoordinates position) {
		area.registerActor(this);
		area.setViewCandidate(this);
		setOwnerArea(area);
		setCurrentPosition(position.toVector());
		resetMotion();	
	}
	
	public void leaveArea(){
        getOwnerArea().unregisterActor(this); //??????????
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Canvas canvas) {
		texture.draw(canvas);	
		hpText.draw(canvas);
		
	}

}
