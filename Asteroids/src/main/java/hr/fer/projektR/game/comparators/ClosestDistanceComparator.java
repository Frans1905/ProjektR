package hr.fer.projektR.game.comparators;

import java.util.Comparator;

import hr.fer.projektR.game.GameObject;
import hr.fer.projektR.game.Player;
import hr.fer.projektR.game.Vector2D;

public class ClosestDistanceComparator implements Comparator<GameObject> {
    private Player player;

    public ClosestDistanceComparator(Player player) {
        super();
        this.player = player;
    }

    @Override
    public int compare(GameObject o1, GameObject o2) {
        double distance1 = player.gameDistance(o1);
        double distance2 = player.gameDistance(o2);

        return (int) (distance1 - distance2);
    }
}
