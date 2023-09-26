package pp.muza.universe.extensions.detector;

import org.junit.jupiter.api.Test;
import pp.muza.universe.body.Body;

class BodyGraphTest {

    @Test
    void addBodyPair() {

        BodyGraph bodyGraph = new BodyGraph();
        Body body1 = new Body(0,0,0,0,0,0,null);
        Body body2 = new Body(1,0,0,0,0,0,null);
        Body body3 = new Body(2,0,0,0,0,0,null);
        Body body4 = new Body(3,0,0,0,0,0,null);
        Body body5 = new Body(4,0,0,0,0,0,null);
        Body body6 = new Body(5,0,0,0,0,0,null);

        bodyGraph.addBodyPair(body1, body2);
        bodyGraph.addBodyPair(body3, body4);
        bodyGraph.addBodyPair(body1, body3);
        bodyGraph.addBodyPair(body5, body6);
        bodyGraph.addBodyPair(body3, body2);
    }
}