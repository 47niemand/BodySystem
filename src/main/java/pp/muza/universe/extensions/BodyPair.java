package pp.muza.universe.extensions;

import pp.muza.universe.body.Body;

public class BodyPair {
    public final Body body1;
    public final Body body2;

    public BodyPair(Body body1, Body body2) {
        if (body1.hashCode() > body2.hashCode()) {
            this.body1 = body1;
            this.body2 = body2;
        } else {
            this.body1 = body2;
            this.body2 = body1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BodyPair) {
            BodyPair other = (BodyPair) obj;
            return (body1 == other.body1 && body2 == other.body2) || (body1 == other.body2 && body2 == other.body1);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Math.max(body1.hashCode(), body2.hashCode());
        result = 31 * result + Math.min(body1.hashCode(), body2.hashCode());
        return result;
    }
}
