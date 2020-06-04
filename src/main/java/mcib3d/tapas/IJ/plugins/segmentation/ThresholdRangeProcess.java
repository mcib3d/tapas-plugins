package mcib3d.tapas.IJ.plugins.segmentation;

import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.core.TapasBatchUtils;

import java.util.HashMap;

public class ThresholdRangeProcess implements TapasProcessingIJ {
    final static private String THRESHOLD_MIN = "minValue";
    final static private String THRESHOLD_MAX = "maxValue";
    final static private String INCLUSIVE = "inclusive";
    final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public ThresholdRangeProcess() {
        parameters = new HashMap<>();
        setParameter(INCLUSIVE, "yes");
        setParameter(USERS, "-");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case THRESHOLD_MIN: // test value
                parameters.put(id, value);
                return true;
            case THRESHOLD_MAX:
                parameters.put(id, value);
                return true;
            case INCLUSIVE:
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
        String users = parameters.get(USERS);
        float thresholdMin = 0;
        float thresholdMax = 0;
        String thresholdS = parameters.get(THRESHOLD_MIN);
        thresholdMin = Float.parseFloat(TapasBatchUtils.getKey(thresholdS, info, users));
        thresholdS = parameters.get(THRESHOLD_MAX);
        thresholdMax = Float.parseFloat(TapasBatchUtils.getKey(thresholdS, info, users));
        if (getParameter(INCLUSIVE).equalsIgnoreCase("yes")) {
            return ImageHandler.wrap(input).thresholdRangeInclusive(thresholdMin, thresholdMax).getImagePlus();
        } else {
            return ImageHandler.wrap(input).thresholdRangeExclusive(thresholdMin, thresholdMax).getImagePlus();
        }
    }


    @Override
    public String getName() {
        return "Thresholding with range";
    }

    @Override
    public String[] getParameters() {
        return new String[]{THRESHOLD_MIN, THRESHOLD_MAX, INCLUSIVE};
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
