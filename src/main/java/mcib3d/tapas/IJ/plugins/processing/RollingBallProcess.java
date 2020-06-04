package mcib3d.tapas.IJ.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.BackgroundSubtracter;
import ij.process.ImageProcessor;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class RollingBallProcess implements TapasProcessingIJ {
    final static public String RADIUS = "radius";
    final static private String DARK_BACKGROUND = "dark";
    HashMap<String, String> parameters;

    public RollingBallProcess() {
        parameters = new HashMap<>();
        setParameter(RADIUS, "50");
        setParameter(DARK_BACKGROUND, "yes");
    }


    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case RADIUS:
                parameters.put(id, value);
                return true;
            case DARK_BACKGROUND:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        double rad = Double.parseDouble(parameters.get(RADIUS));
        boolean createBackground = false;
        boolean ligthBackground = false;
        if (getParameter(DARK_BACKGROUND).equalsIgnoreCase("no")) ligthBackground = true;
        boolean useParaboloid = false;
        boolean doPresmooth = true;
        boolean correctCorners = true;
        BackgroundSubtracter backgroundSubtracter = new BackgroundSubtracter();
        if (input.getNChannels() > 1) {
            IJ.log("Cannot proces multi-channels images");
            return null;
        }
        // TODO for all slices
        ImagePlus copy = input.duplicate();
        ImageStack stack = copy.getStack();
        for (int s = 1; s <= input.getStackSize(); s++) {
            ImageProcessor processor = stack.getProcessor(s);
            backgroundSubtracter.rollingBallBackground(processor, rad, createBackground, ligthBackground, useParaboloid, doPresmooth, correctCorners);
        }

        return copy;
    }

    @Override
    public String getName() {
        return "Rolling Ball background subtraction";
    }

    @Override
    public String[] getParameters() {
        return new String[]{RADIUS, DARK_BACKGROUND};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
