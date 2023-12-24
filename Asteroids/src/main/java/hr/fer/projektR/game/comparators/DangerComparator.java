package hr.fer.projektR.game.comparators;

import java.util.Comparator;

import hr.fer.projektR.game.GameObject;
import hr.fer.projektR.game.Player;
import hr.fer.projektR.game.Vector2D;

public class DangerComparator implements Comparator<GameObject> {
	private Player player;
	public DangerComparator(Player player) {
		this.player = player;
	}
	@Override
	public int compare(GameObject o1, GameObject o2) {
		double distance1 = player.gameDistance(o1);
		Vector2D relative1 = Vector2D.subVector2d(player.getPos(), o1.getPos());
		Vector2D relativeSpeed1 = Vector2D.subVector2d(o1.getSpeed(), player.getSpeed());
		if (relative1.abs() != distance1) {
			relative1 = relative1.unit().scale(-distance1);
		}
		double danger1 = Vector2D.dotProduct(relativeSpeed1, relative1)/(relativeSpeed1.abs()*distance1*distance1);

		
		double distance2 = player.gameDistance(o2);
        Vector2D relative2 = Vector2D.subVector2d(player.getPos(), o2.getPos());
        Vector2D relativeSpeed2 = Vector2D.subVector2d(o2.getSpeed(), player.getSpeed());
        if (relative2.abs() != distance2) {
        	relative2 = relative2.unit().scale(-distance2);
        }
        double danger2 = Vector2D.dotProduct(relativeSpeed2, relative2)/(relativeSpeed2.abs()*distance2*distance2);
        
        if (danger1 - danger2 > 0) return 1;
        if (danger1 - danger2 < 0) return -1;
		return 0;
	}

}
