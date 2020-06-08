package mcib3d.tapas.IJ.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.processing.FastFilters3D;

import java.util.HashMap;

public class FastFilters3DProcess implements TapasProcessingIJ {
    public final static String RADIUSXY = "radxy";
    public final static String RADIUSZ = "radz";
    public final static String FILTER = "filter";
    HashMap<String, String> parameters;

    public FastFilters3DProcess() {
        parameters = new HashMap<>();
        setParameter(RADIUSXY, "2");
        setParameter(RADIUSZ, "0");
        setParameter(FILTER, "median");
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
            case FILTER:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        float rx = getParameterFloat(RADIUSXY);
        float ry = rx;
        float rz = getParameterFloat(RADIUSZ);
        String filterS = getParameter(FILTER).toLowerCase();
        int filter = FastFilters3D.MEDIAN;
        switch (filterS) {
            case "median":
                filter = FastFilters3D.MEDIAN;
                break;
            case "mean":
                filter = FastFilters3D.MEAN;
                break;
            case "tophat":
                filter = FastFilters3D.TOPHAT;
                break;
            case "open":
                filter = FastFilters3D.OPENGRAY;
                break;
            case "close":
                filter = FastFilters3D.CLOSEGRAY;
                break;
            case "min":
                filter = FastFilters3D.MIN;
                break;
            case "max":
                filter = FastFilters3D.MAX;
                break;
        }
        IJ.log("Filtering with filter " + filterS + " and radii " + rx + "-" + ry + "-" + rz);

        return FastFilters3D.filterImage(ImageHandler.wrap(input), filter, rx, ry, rz, 0, false).getImagePlus();
    }

    @Override
    public String getName() {
        return "Filtering 2D/3D";
    }

    @Override
    public String[] getParameters() {
        return new String[]{RADIUSXY, RADIUSZ, FILTER};
    }


    public String getParameter(String id) {
        return parameters.get(id);
    }

    private float getParameterFloat(String id) {
        return Float.parseFloat(getParameter(id));
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }


}
