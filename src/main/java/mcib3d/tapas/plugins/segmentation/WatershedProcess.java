package mcib3d.tapas.plugins.segmentation;

import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.processing.FastFilters3D;
import mcib3d.image3d.regionGrowing.Watershed3D;
import mcib3d.utils.Logger.IJLog;

import java.util.HashMap;

public class WatershedProcess implements TapasProcessing {
    final static public String SEEDS_THRESHOLD = "seedsThreshold";
    final static public String SIGNAL_THRESHOLD = "signalThreshold";
    final static public String SEEDS_RADIUS = "seedsRadius";
    HashMap<String, String> parameters;

    public WatershedProcess() {
        parameters = new HashMap<>();
        setParameter(SEEDS_RADIUS,"2");
        setParameter(SEEDS_THRESHOLD,"0");
        setParameter(SIGNAL_THRESHOLD,"0");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case SEEDS_THRESHOLD:
                parameters.put(id, value);
                return true;
            case SIGNAL_THRESHOLD:
                parameters.put(id, value);
                return true;
            case SEEDS_RADIUS:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // compute seeds
        ImageHandler handler = ImageHandler.wrap(input);
        float radius = Float.parseFloat(parameters.get(SEEDS_RADIUS));
        ImageHandler seeds = FastFilters3D.filterImage(handler, FastFilters3D.MAXLOCAL, radius, radius, radius, 0, false);
        // watershed
        double signal = Double.parseDouble(parameters.get(SIGNAL_THRESHOLD));
        int seed = Integer.parseInt(parameters.get(SEEDS_THRESHOLD));
        Watershed3D watershed3D = new Watershed3D(handler, seeds, signal, seed);
        watershed3D.setAnim(false);
        watershed3D.setLog(new IJLog());
        ImageHandler water=watershed3D.getWatershedImage3D();
        water.setScale(handler);

        return water.getImagePlus();
    }

    @Override
    public String getName() {
        return "Watershed segmentation";
    }

    @Override
    public String[] getParameters() {
        return new String[]{SEEDS_THRESHOLD, SEEDS_RADIUS, SIGNAL_THRESHOLD};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
