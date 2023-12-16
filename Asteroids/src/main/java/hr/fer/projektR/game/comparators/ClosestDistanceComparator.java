package hr.fer.projektR.game.comparators;

import java.util.Comparator;

import hr.fer.projektR.game.GameObject;
import hr.fer.projektR.game.Vector2D;

public class ClosestDistanceComparator implements Comparator<GameObject> {
    private Vector2D playerPos;

    public ClosestDistanceComparator(Vector2D playerPos) {
        super();
        this.playerPos = playerPos;
    }

    @Override
    public int compare(GameObject o1, GameObject o2) {
        double distance1 = Vector2D.subVector2d(o1.getPos(), playerPos).abs();
        double distance2 = Vector2D.subVector2d(o2.getPos(), playerPos).abs();

        return (int) (distance1 - distance2);
    }
}
