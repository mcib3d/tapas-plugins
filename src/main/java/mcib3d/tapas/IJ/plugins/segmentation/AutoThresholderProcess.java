package mcib3d.tapas.IJ.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.AutoThresholder;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageStats;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AutoThresholderProcess implements TapasProcessingIJ {
    public static Map<String, AutoThresholder.Method> methods = Collections.unmodifiableMap(new HashMap<String, AutoThresholder.Method>() {{
        put("isodata", AutoThresholder.Method.IsoData);
        put("otsu", AutoThresholder.Method.Otsu);
        //put("MINIMUM", AutoThresholder.Method.Minimum);
        //put("MINERROR", AutoThresholder.Method.MinError);
        //put("MOMENTS", AutoThresholder.Method.Moments);
        put("intermodes", AutoThresholder.Method.Intermodes);
        put("yen", AutoThresholder.Method.Yen);
        put("triangle", AutoThresholder.Method.Triangle);
        put("mean", AutoThresholder.Method.Mean);
        //put("RENYIENTROPY", AutoThresholder.Method.RenyiEntropy);
        //put("SHANBHAG", AutoThresholder.Method.Shanbhag);
        //put("TRIANGLE", AutoThresholder.Method.Triangle);
        //put("YEN", AutoThresholder.Method.Yen);
        put("huang", AutoThresholder.Method.Huang);
        //put("PERCENTILE", AutoThresholder.Method.Percentile);
        //put("MAXENTROPY", AutoThresholder.Method.MaxEntropy);
        put("ij_isodata", AutoThresholder.Method.IJ_IsoData);
    }});

    final static private String THRESHOLD_METHOD = "method";
    final static private String DARK_BACKGROUND = "dark";

    ImageInfo info;
    HashMap<String, String> parameters;

    public AutoThresholderProcess() {
        parameters = new HashMap<>();
        setParameter(DARK_BACKGROUND, "yes");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case THRESHOLD_METHOD: // test value
                parameters.put(id, value);
                return true;
            case DARK_BACKGROUND: // test value
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String thresholdS = parameters.get(THRESHOLD_METHOD);
        // compute histogram
        ImageHandler imageHandler = ImageHandler.wrap(input);
        ImageStats stat = imageHandler.getImageStats(null);
        int[] histo = stat.getHisto256();
        double binSize = stat.getHisto256BinSize();
        double min = stat.getMin();

        AutoThresholder at = new AutoThresholder();
        double thld = at.getThreshold(methods.get(thresholdS.toLowerCase()), histo);
        float threshold;
        if (input.getBitDepth() > 8)
            threshold = (float) (thld * binSize + min);
        else
            threshold = (float) thld;
        IJ.log("Thresholding with value " + threshold);

        ImageHandler temp;
        if (getParameter(DARK_BACKGROUND).equalsIgnoreCase("yes"))
            temp = imageHandler.thresholdAboveExclusive(threshold);
        else
            temp = imageHandler.thresholdRangeInclusive(0, threshold);
        // calibration
        ImagePlus res = temp.getImagePlus();
        Calibration cal = input.getCalibration();
        if (cal != null) res.setCalibration(cal);

        return res;
    }


    @Override
    public String getName() {
        return "Auto Thresholding";
    }

    @Override
    public String[] getParameters() {
        return new String[]{THRESHOLD_METHOD};
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
