/*
 *	Author:      Romain Logean
 *	Date:        27 nov. 2020
 */



package ch.epfl.cs107.play.game.superpacman.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.AreaGraph;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.superpacman.actor.Blinky;
import ch.epfl.cs107.play.game.superpacman.actor.Bonus;
import ch.epfl.cs107.play.game.superpacman.actor.Cherry;
import ch.epfl.cs107.play.game.superpacman.actor.Diamond;
import ch.epfl.cs107.play.game.superpacman.actor.Inky;
import ch.epfl.cs107.play.game.superpacman.actor.InvisibleWall;
import ch.epfl.cs107.play.game.superpacman.actor.Pinky;
import ch.epfl.cs107.play.game.superpacman.actor.Wall;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class SuperPacmanBehavior extends AreaBehavior {
	public enum SuperPacmanCellType{
		NONE(0), // never used as real content 
		WALL(-16777216), //black 
		FREE_WITH_DIAMOND(-1), //white 
		FREE_WITH_BLINKY(-65536), //red 
		FREE_WITH_PINKY(-157237), //pink 
		FREE_WITH_INKY(-16724737), //cyan 
		FREE_WITH_CHERRY(-36752), //light red 
		FREE_WITH_BONUS(-16478723), //light blue 
		FREE_EMPTY(-6118750); // sort of gray

		final int type;

		SuperPacmanCellType(int type){
			this.type = type;
		}

		public static SuperPacmanCellType toType(int type){
			for(SuperPacmanCellType ict : SuperPacmanCellType.values()){
				if(ict.type == type)
					return ict;
			}
			return NONE;
		}
	}
	
	AreaGraph areaGraph;

	public SuperPacmanBehavior(Window window, String name) {
		super(window, name);
		int height = getHeight();
		int width = getWidth();
		for(int y = 0; y < height; y++) {
			for (int x = 0; x < width ; x++) {
				SuperPacmanCellType type = SuperPacmanCellType.toType(getRGB(height-1-y, x));
				setCell(x,y, new SuperPacmanCell(x,y,type));
			}
		}
		areaGraph=new AreaGraph();
	}
	
	protected void registerActors(Area area) {
		int height = getHeight();
		int width = getWidth();
		for(int y = 0; y < height; y++) {
			for (int x = 0; x < width ; x++) {
				if(!(area instanceof Temple || area  instanceof RouteTemple)) {
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.WALL) {
						boolean neighborhood[][]= new boolean[3][3];
						for(int j=0;j<=2;++j) {
							for(int i=0;i<=2;++i) {
								if(x+(i-1)<0 || x+(i-1) >= width || height-1-y+(j-1)<0 || height-1-y+(j-1) >= height) {
									neighborhood[i][j]=false;
								}
								else {
									SuperPacmanCellType neighbor = SuperPacmanCellType.toType(getRGB(height-1-y+(j-1), x+(i-1)));
									if(neighbor==SuperPacmanCellType.WALL){
										neighborhood[i][j]=true;
									}
									else {
										neighborhood[i][j]=false;
									}
								}							
							}
						}
						area.registerActor(new Wall(area, new DiscreteCoordinates(x,y), neighborhood));
					}
					else {
						boolean hasUpEdge =(y < height-1 && ((SuperPacmanCell)getCell(x,y+1)).type != SuperPacmanCellType.WALL) ? true : false ;
						boolean hasDownEdge = (y > 0 && ((SuperPacmanCell)getCell(x,y-1)).type != SuperPacmanCellType.WALL) ? true : false ;
						boolean hasLeftEdge = (x > 0 && ((SuperPacmanCell)getCell(x-1,y)).type != SuperPacmanCellType.WALL) ? true : false ;
						boolean hasRightEdge = (x < width-1 && ((SuperPacmanCell)getCell(x+1,y)).type != SuperPacmanCellType.WALL)? true : false ;
						areaGraph.addNode(new DiscreteCoordinates(x,y), hasLeftEdge, hasUpEdge, hasRightEdge, hasDownEdge);
					}
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.FREE_WITH_DIAMOND){
						area.registerActor(new Diamond(area,null,new DiscreteCoordinates(x,y)));
					}
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.FREE_WITH_BONUS){
						area.registerActor(new Bonus(area,null,new DiscreteCoordinates(x,y)));
					}
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.FREE_WITH_CHERRY){
						area.registerActor(new Cherry(area,null, new DiscreteCoordinates(x,y)));
					}
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.FREE_WITH_BLINKY){
						area.registerActor(new Blinky(area, Orientation.UP, new DiscreteCoordinates(x,y)));
					}
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.FREE_WITH_INKY){
						area.registerActor(new Inky(area, Orientation.UP, new DiscreteCoordinates(x,y)));
					}
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.FREE_WITH_PINKY){
						area.registerActor(new Pinky(area, Orientation.UP, new DiscreteCoordinates(x,y)));
					}
				}
				else {
					if(((SuperPacmanCell)getCell(x,y)).type == SuperPacmanCellType.WALL){
						area.registerActor(new InvisibleWall(area, Orientation.UP, new DiscreteCoordinates(x,y)));
					}
				}
			}
		}
	}
	
	public AreaGraph getGraph() {
		return areaGraph;
	}

	public class SuperPacmanCell extends AreaBehavior.Cell{
		private final SuperPacmanCellType type;

		protected SuperPacmanCell(int x, int y,SuperPacmanCellType type) {
			super(x, y);
			this.type = type;
		}

		@Override
		public boolean isCellInteractable() {
			return false;
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
			 if(this.hasNonTraversableContent()) {
				 return false;
			 }
			 return true;
		}
		
	}
}
