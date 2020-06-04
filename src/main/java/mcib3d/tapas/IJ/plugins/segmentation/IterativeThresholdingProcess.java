package mcib3d.tapas.IJ.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.image3d.IterativeThresholding.TrackThreshold;
import mcib3d.tapas.core.TapasBatchUtils;

import java.util.HashMap;

public class IterativeThresholdingProcess implements TapasProcessingIJ {
    final static private String MIN_VOLUME = "minVolume";
    final static private String MAX_VOLUME = "maxVolume";
    final static private String TH_MIN = "minThreshold";
    final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public IterativeThresholdingProcess() {
        parameters = new HashMap<>();
        setParameter(MIN_VOLUME, "100");
        setParameter(MAX_VOLUME, "-1");
        setParameter(TH_MIN, "0");
        setParameter(USERS, "-");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case MIN_VOLUME:
                parameters.put(id, value);
                return true;
            case MAX_VOLUME:
                parameters.put(id, value);
                return true;
            case TH_MIN:
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
        // check threshold, any key ?
        // if not value, look for key in core
        int thmin = 0;
        String thresholdS = parameters.get(TH_MIN);
        thmin = Integer.parseInt(TapasBatchUtils.getKey(thresholdS, info, users));
        //other parameters
        int volMin = Integer.parseInt(parameters.get(MIN_VOLUME));
        int volMax = Integer.parseInt(parameters.get(MAX_VOLUME));
        if (volMax < 0) volMax = Integer.MAX_VALUE;
        int minCont = 0;
        int step = 1;

        TrackThreshold TT = new TrackThreshold(volMin, volMax, minCont, step, step, thmin);
        TT.setMethodThreshold(TrackThreshold.THRESHOLD_METHOD_STEP);
        TT.setCriteriaMethod(TrackThreshold.CRITERIA_METHOD_MIN_ELONGATION);
        ImagePlus res = TT.segmentBest(input, true);
        if (res == null) {
            // no objects found, create blank image
            IJ.log("No objects found.");
            res = input.duplicate();
            ImageStack stack = res.getImageStack();
            for (int s = 1; s <= res.getNSlices(); s++)
                stack.getProcessor(s).set(0);

        }
        if ((res != null) && (input.getCalibration() != null)) res.setCalibration(input.getCalibration());

        return res;
    }

    @Override
    public String getName() {
        return "Iterative thresholding";
    }

    @Override
    public String[] getParameters() {
        return new String[]{MIN_VOLUME, MAX_VOLUME, TH_MIN};
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
