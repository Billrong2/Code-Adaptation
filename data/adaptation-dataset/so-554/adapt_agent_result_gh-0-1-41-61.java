@Override
public float[] getNormalizedComponents(Object pixel, float[] normComponents, int normOffset) {
    int numComponents = getNumComponents();

    if (normComponents == null || normComponents.length < numComponents + normOffset) {
        normComponents = new float[numComponents + normOffset];
    }

    switch (transferType) {
        case java.awt.image.DataBuffer.TYPE_INT: {
            int[] ipixel = (int[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                long maxComponentValue = (1L << getComponentSize(c)) - 1L;
                normComponents[nc] = ipixel[c] / (float) maxComponentValue;
            }
            break;
        }
        default:
            throw new UnsupportedOperationException(
                "This method has not been implemented for transferType " + transferType);
    }

    return normComponents;
}