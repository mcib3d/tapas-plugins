package mcib3d.tapas.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.HashMap;

public class CropZProcess implements TapasProcessing {
    final static private String ZMIN = "zMin";
    final static private String ZMAX = "zMax";
    final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public CropZProcess() {
        parameters = new HashMap<>();
        setParameter(USERS, "-");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case ZMIN: // test value
                parameters.put(id, value);
                return true;
            case ZMAX:
                parameters.put(id, value);
                return true;
            case USERS:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        if (input.getNSlices() <= 1) return input.duplicate();
        // get parameters
        String users = parameters.get(USERS);
        // get value for min
        String minS = getParameter(ZMIN);
        String key = TapasBatchProcess.getKey(minS, info, getParameter(USERS));
        if (key == null) {
            IJ.log("No key " + minS);
            return null;
        }
        int zmin = Integer.parseInt(key);
        // get value for max
        String maxS = getParameter(ZMAX);
        key = TapasBatchProcess.getKey(maxS, info, getParameter(USERS));
        if (key == null) {
            IJ.log("No key " + maxS);
            return null;
        }
        int zmax = Integer.parseInt(key);
        IJ.log("Cropping in Z with values " + zmin + " and " + zmax);
        // get the stack
        ImagePlus plus = input.duplicate();
        ImageStack stack = plus.getStack();
        // remove last slices
        int ns = stack.getSize();
        for (int s = zmax + 1; s < ns; s++) {
            stack.deleteLastSlice();
        }
        // remove first slices
        for (int s = 0; s < zmin; s++) {
            stack.deleteSlice(1);
        }

        return plus;
    }

    @Override
    public String getName() {
        return "Crop in Z";
    }

    @Override
    public String[] getParameters() {
        return new String[]{ZMIN, ZMAX, USERS};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
