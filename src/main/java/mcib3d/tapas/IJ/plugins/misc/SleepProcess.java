package mcib3d.tapas.IJ.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class SleepProcess implements TapasProcessingIJ {
    private static final String TIME = "time";

    HashMap<String, String> parameters;

    public SleepProcess() {
        parameters = new HashMap<>();
        setParameter(TIME, "10");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case TIME:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        double sec = Double.parseDouble(getParameter(TIME));
        try {
            IJ.log("Sleeping " + sec + " seconds");
            Thread.sleep((long) (sec * 1000));
        } catch (InterruptedException e) {
            IJ.log("Pb sleep " + e);
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Just sleep (sec)";
    }

    @Override
    public String[] getParameters() {
        return new String[]{TIME};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }
}
