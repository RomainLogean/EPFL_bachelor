/*
 *	Author:      Romain Logean
 *	Date:        19 nov. 2020
 */



package ch.epfl.cs107.play.game.tutos;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.tutos.Tuto2Behavior.Tuto2CellType;
import ch.epfl.cs107.play.window.Window;

public class Tuto2Behavior extends AreaBehavior{

	public Tuto2Behavior(Window window, String name) {
		super(window, name);
		int height = getHeight();
		int width = getWidth();
		for(int y =0;y<height;++y) {
			for(int x=0;x<width;++x){
				Tuto2CellType cellType = Tuto2CellType.toType(getRGB(height-1-y, x));
				setCell(x,y,new Tuto2Cell(x,y,cellType));
			}
		}
	}
	
	public enum Tuto2CellType {	
		NULL(0, false), 
		WALL(-16777216, false), 
		IMPASSABLE(-8750470, false), 
		INTERACT(-256, true), 
		DOOR(-195580, true), 
		WALKABLE(-1, true),;
		
		public static Tuto2CellType toType(int type) {
			for(Tuto2CellType aType : Tuto2CellType.values()) {
				if(aType.type==type) {
					return aType;
				}
			}
			//en cas de nouvelle couleur si on veut connaitre son numéro
			System.out.println(type);
			return NULL;
		}
		
		final int type;
		final boolean isWalkable;
		
		Tuto2CellType(int type, boolean isWalkable){ 
			this.type = type;
			this.isWalkable = isWalkable; 
		}
	}
	
	public class Tuto2Cell extends AreaBehavior.Cell {
	
		private Tuto2CellType type;
		
		public  Tuto2Cell(int x, int y, Tuto2CellType type){
			super(x, y);
			this.type = type;
		}
		
	
		@Override
		public boolean isCellInteractable() {
			return true;
		}
	
		@Override
		public boolean isViewInteractable() {
			return false;
		}
	
		@Override
		public void acceptInteraction(AreaInteractionVisitor v) {		
		}
	
		@Override
		protected boolean canLeave(Interactable entity) {
			return true;
		}
	
		@Override
		protected boolean canEnter(Interactable entity) {
			return type.isWalkable;
		}
	}
}











