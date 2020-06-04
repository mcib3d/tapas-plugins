package mcib3d.tapas.IJ.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.HashMap;

public class ThresholdPercentileProcess implements TapasProcessingIJ {
    final static private String PERCENT = "percentile";
    //final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public ThresholdPercentileProcess() {
        parameters = new HashMap<>();
        setParameter(PERCENT, "0.01");
        //setParameter(USERS, "-");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case PERCENT:
                double pc = Double.parseDouble(value);
                if ((Double.isNaN(pc)) || (pc < 0) || (pc > 1)) {
                    IJ.log("Percentile value should be between 0 and 1");
                    return false;
                }
                parameters.put(id, value);
                return true;
            //case USERS:
            //    parameters.put(id, value);
            //    return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImageHandler img = ImageHandler.wrap(input);
        double thld = img.getPercentile(Double.parseDouble(getParameter(PERCENT)), null);
        IJ.log("Thresholding with value : "+thld);
        ImageByte thresholded = img.thresholdAboveExclusive((float) thld);
        thresholded.setScale(img);

        return thresholded.getImagePlus();
    }

    @Override
    public String getName() {
        return "Threshold with percentile of bright pixels";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PERCENT};// USERS
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
