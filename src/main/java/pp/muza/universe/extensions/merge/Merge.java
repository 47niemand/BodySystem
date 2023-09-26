package pp.muza.universe.extensions.merge;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.CollisionDetectorExtension;
import pp.muza.universe.extensions.CollisionPair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Merge extends BaseSystemExtension implements CollisionDetectorExtension.SetDetectorExtension {

    int currentStep = -1;
    private CollisionDetectorExtension collisionDetector;


    @Override
    protected void doApply(double dt) {

        // todo: check all paris, if they are moving towards each other, and if they are in collision, merge them

        Collection<CollisionPair> pairs = new ArrayList<>(collisionDetector.getCollisionPairs());

        for (Iterator<CollisionPair> iterator = pairs.iterator(); iterator.hasNext(); ) {
            CollisionPair pair = iterator.next();
            if (pair.isInCollision() && pair.getModuleDistance() < -Math.min(pair.body1.r * pair.body1.r, pair.body2.r * pair.body2.r)
            ) {
                Body body1 = pair.body1;
                Body body2 = pair.body2;
                if (body1.tag == null || body2.tag == null) {
                    continue;
                }

                system.removeBody(body2);
                system.removeBody(body1);
                // mix colors of the bodies proportionally to their masses
                Color body1Color = (Color) body1.tag;
                Color body2Color = (Color) body2.tag;
                double color1Ratio = body1.r / (body1.r + body2.r);
                double color2Ratio = body2.r / (body1.r + body2.r);
                Color c = new Color(
                        (int) (body1Color.getRed() * color1Ratio + body2Color.getRed() * color2Ratio),
                        (int) (body1Color.getGreen() * color1Ratio + body2Color.getGreen() * color2Ratio),
                        (int) (body1Color.getBlue() * color1Ratio + body2Color.getBlue() * color2Ratio));
                // new mass
                double m = body1.m + body2.m;
                // get area of the two circles
                double r = Math.sqrt(body1.r * body1.r + body2.r * body2.r);
                Complex newPosition = body1.r > body2.r ? body1.position : body2.position; // position of the bigger body
                Complex newVelocity;

                if (body1.r > body2.r) {
                    // calculate new velocity proportionally to the mass
                    newVelocity = Complex.scale(body1.velocity, body1.m / m);
                } else {
                    newVelocity = Complex.scale(body2.velocity, body2.m / m);
                }
                Body body = new Body(newPosition, newVelocity, m, r, c);
                system.addBody(body);
                body1.tag = null;
                body2.tag = null;
                iterator.remove();


            }
        }
    }


    @Override
    public void sync() {

    }

    @Override
    public void setCollisionDetector(CollisionDetectorExtension collisionDetector) {
        this.collisionDetector = collisionDetector;
    }
}
