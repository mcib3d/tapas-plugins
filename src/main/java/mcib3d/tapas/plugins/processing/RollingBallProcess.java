package mcib3d.tapas.plugins.processing;

import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.process.ImageProcessor;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class RollingBallProcess implements TapasProcessing {
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
        ImageProcessor processor = input.getProcessor();
        double rad = Double.parseDouble(parameters.get(RADIUS));
        boolean createBackground = false;
        boolean ligthBackground = false;
        if (getParameter(DARK_BACKGROUND).equalsIgnoreCase("no")) ligthBackground = true;
        boolean useParaboloid = false;
        boolean doPresmooth = true;
        boolean correctCorners = true;
        BackgroundSubtracter backgroundSubtracter = new BackgroundSubtracter();
        backgroundSubtracter.rollingBallBackground(processor, rad, createBackground, ligthBackground, useParaboloid, doPresmooth, correctCorners);
        ImagePlus copy = input.duplicate();
        copy.setProcessor(processor);

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
