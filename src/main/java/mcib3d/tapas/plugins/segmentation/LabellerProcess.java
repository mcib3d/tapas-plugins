package mcib3d.tapas.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageLabeller;

import java.util.HashMap;

public class LabellerProcess implements TapasProcessing {
    final static public String MIN_VOLUME = "minVolume";
    final static public String MAX_VOLUME = "maxVolume";
    final static public String UNIT = "unit";
    HashMap<String, String> parameters;

    public LabellerProcess() {
        parameters = new HashMap<>();
        setParameter(MIN_VOLUME, "0");
        setParameter(MAX_VOLUME, "-1");// no maximum limit
        setParameter(UNIT, "no");
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
            case UNIT:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImageHandler img = ImageHandler.wrap(input);
        ImageLabeller labeller = new ImageLabeller();
        // get min max volumes
        double minP = getParameterDouble(MIN_VOLUME);
        double maxP = getParameterDouble(MAX_VOLUME);
        // check if unit, convert in pixels
        int min, max;
        if (getParameter(UNIT).trim().equalsIgnoreCase("yes")) {
            IJ.log("Image is calibrated : " + img.getScaleXY() + " " + img.getScaleZ());
            double volInv = 1.0 / (img.getScaleXY() * img.getScaleXY() * img.getScaleZ());
            min = (int) Math.round(minP * volInv);
            if (maxP < 0) max = Integer.MAX_VALUE;
            else max = (int) Math.round(maxP * volInv);
        } else {
            min = (int) minP;
            if (maxP < 0) max = Integer.MAX_VALUE;
            else max = (int) (maxP);
        }
        // log
        if (maxP < 0) {
            IJ.log("Labelling with size min " + min + " voxels");
        } else {
            IJ.log("Labelling with size from " + min + " to " + max + " voxels");
        }
        // run labelling
        labeller.setMinSize(min);
        labeller.setMaxsize(max);


        return labeller.getLabels(img).getImagePlus();
    }

    @Override
    public String getName() {
        return "Labelling";
    }

    @Override
    public String[] getParameters() {
        return new String[]{MIN_VOLUME, MAX_VOLUME, UNIT};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }

    private double getParameterDouble(String id) {
        return Double.parseDouble(getParameter(id));
    }

}
