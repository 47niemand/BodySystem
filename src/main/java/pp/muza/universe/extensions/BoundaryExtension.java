package pp.muza.universe.extensions;

/**
 * A BoundaryExtension is an extension that can resize the universe boundary.
 */
public interface BoundaryExtension extends SystemExtension {

    void resize(int x1, int y1, int width, int height);

}
