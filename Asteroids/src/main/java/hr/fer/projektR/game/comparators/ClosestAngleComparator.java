package hr.fer.projektR.game.comparators;

import java.util.Comparator;
import hr.fer.projektR.game.GameObject;
import hr.fer.projektR.game.Player;
import hr.fer.projektR.game.Vector2D;

public class ClosestAngleComparator implements Comparator<GameObject> {
    private Player player;

    public ClosestAngleComparator(Player player) {
        super();
        this.player = player;
    }

    @Override
    public int compare(GameObject o1, GameObject o2) {
        Vector2D lineDirection1 = Vector2D.subVector2d(o1.getPos(), player.getPos()).unit();
        Vector2D lineDirection2 = Vector2D.subVector2d(o2.getPos(), player.getPos()).unit();

        double angle1 = Math.atan2(lineDirection1.y, lineDirection1.x);
        double angle2 = Math.atan2(lineDirection2.y, lineDirection2.x);

        double angleDistance1 = player.getOrient() - angle1;
        double angleDistance2 = player.getOrient() - angle2;

        if (angleDistance1 < 0) {
            angleDistance1 += 2 * Math.PI;
        }
        if (angleDistance2 < 0) {
            angleDistance2 += 2 * Math.PI;
        }
        if (angleDistance1 >= 2 * Math.PI) {
            angleDistance1 -= 2 * Math.PI;
        }
        if (angleDistance2 >= 2 * Math.PI) {
            angleDistance2 -= 2 * Math.PI;
        }

        if (angleDistance1 >= Math.PI) {
            angleDistance1 = 2 * Math.PI - angleDistance1;
        }
        if (angleDistance2 >= Math.PI) {
            angleDistance2 = 2 * Math.PI - angleDistance2;
        }
        
        return (int) (16 * angleDistance1 - 16 * angleDistance2);
    }
    
}
