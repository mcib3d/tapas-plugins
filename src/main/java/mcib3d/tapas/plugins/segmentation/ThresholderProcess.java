package mcib3d.tapas.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.image3d.ImageHandler;

import java.util.HashMap;

public class ThresholderProcess implements TapasProcessing {
    final static private String THRESHOLD_VALUE = "value";
    final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public ThresholderProcess() {
        parameters = new HashMap<>();
        setParameter(THRESHOLD_VALUE, "128");
        setParameter(USERS, "-");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case THRESHOLD_VALUE: // test value
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
        int threshold = 0;
        // if not value, look for key in core
        String thresholdS = parameters.get(THRESHOLD_VALUE);
        threshold = Integer.parseInt(TapasBatchProcess.getKey(thresholdS, info, getParameter(USERS)));
        IJ.log("Thresholding with value " + threshold);
        ImagePlus res = ImageHandler.wrap(input).thresholdAboveExclusive(threshold).getImagePlus();
        res.setCalibration(input.getCalibration());

        return res;
    }


    @Override
    public String getName() {
        return "Thresholding";
    }

    @Override
    public String[] getParameters() {
        return new String[]{THRESHOLD_VALUE, USERS};
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
