package mcib3d.tapas.plugins.segmentation;

import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.Segment3DSpots;
import mcib3d.image3d.processing.FastFilters3D;

import java.util.HashMap;

public class SpotsSegProcess implements TapasProcessing {
    final static private String MIN_VOLUME = "minVolume";
    final static private String MAX_VOLUME = "maxVolume";
    final static private String SEEDS_THRESHOLD = "seedsThreshold";
    final static private String SEEDS_RADIUS = "seedsRadius";
    HashMap<String, String> parameters;

    public SpotsSegProcess() {
        parameters = new HashMap<>();
        setParameter(MIN_VOLUME, "1");
        setParameter(MAX_VOLUME, "1000000");
        setParameter(SEEDS_RADIUS, "2");
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
            case SEEDS_THRESHOLD:
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
        // segmentation
        Segment3DSpots seg = new Segment3DSpots(handler, seeds);
        seg.show = false;
        // set parameters
        seg.setSeedsThreshold(Integer.parseInt(parameters.get(SEEDS_THRESHOLD)));
        seg.setLocalThreshold(0);
        seg.setWatershed(false); // no watershed
        seg.setVolumeMin(Integer.parseInt(parameters.get(MIN_VOLUME)));
        seg.setVolumeMax(Integer.parseInt(parameters.get(MAX_VOLUME)));
        seg.setMethodLocal(Segment3DSpots.LOCAL_GAUSS);
        seg.setMethodSeg(Segment3DSpots.SEG_CLASSICAL);
        seg.setWatershed(true);
        seg.setGaussPc(1.2); // gaussian cut-off, coeff*sigma
        seg.setGaussMaxr(10); // radius to compute gaussian
        // segmentation
        seg.segmentAll();
        ImageHandler res = seg.getLabelImage();

        return res.getImagePlus();
    }

    @Override
    public String getName() {
        return "Spots segmentation";
    }

    @Override
    public String[] getParameters() {
        return new String[]{MIN_VOLUME, MAX_VOLUME, SEEDS_RADIUS, SEEDS_THRESHOLD};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
