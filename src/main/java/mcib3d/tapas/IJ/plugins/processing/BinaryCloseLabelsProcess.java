package mcib3d.tapas.IJ.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.processing.BinaryMorpho;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class BinaryCloseLabelsProcess implements TapasProcessingIJ {
    public final static String RADIUSXY = "radxy";
    public final static String RADIUSZ = "radz";

    HashMap<String, String> parameters;

    public BinaryCloseLabelsProcess() {
        parameters = new HashMap<>();
        setParameter(RADIUSXY, "5");
        setParameter(RADIUSZ, "0");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case RADIUSXY:
                parameters.put(id, value);
                return true;
            case RADIUSZ:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus imagePlus) {
        // get parameters
        float rx = getParameterFloat(RADIUSXY);
        float ry = rx;
        float rz = getParameterFloat(RADIUSZ);
        // does not work yet with 32-bits images
        if (imagePlus.getBitDepth() > 16) {
            IJ.log("Does not work with 32-bits images");
        }
        // convert imagePlus to imageHandler then do closeLabels
        ImageInt handler = ImageInt.wrap(imagePlus);
        ImageInt result = BinaryMorpho.binaryCloseMultilabel(handler, rx, rz);

        return result.getImagePlus();
    }

    @Override
    public String getName() {
        return "Binary Closing for labelled images";
    }

    @Override
    public String[] getParameters() {
        return new String[]{RADIUSXY, RADIUSZ};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    private float getParameterFloat(String id) {
        return Float.parseFloat(getParameter(id));
    }

    @Override
    public void setCurrentImage(ImageInfo imageInfo) {
    }
}
