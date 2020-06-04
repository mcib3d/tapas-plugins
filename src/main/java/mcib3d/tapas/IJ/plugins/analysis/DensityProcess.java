package mcib3d.tapas.IJ.plugins.analysis;

import ij.ImagePlus;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.processing.Density3D;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class DensityProcess implements TapasProcessingIJ {
    private static final String NEIGHBOURS = "neighbours";
    private static final String RADIUS = "radius";

    HashMap<String, String> parameters;
    private ImageInfo info;

    public DensityProcess() {
        parameters = new HashMap<>(2);
        parameters.put(NEIGHBOURS, "10");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case NEIGHBOURS:
                parameters.put(id, value);
                return true;
            case RADIUS:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        //parameters
        int neigh = Integer.parseInt(getParameter(NEIGHBOURS));
        double sigma = Double.parseDouble(getParameter(RADIUS));
        // density, multi threaded
        Density3D density3D = new Density3D(neigh, sigma);
        ImageHandler res = density3D.computeDensity(ImageHandler.wrap(input), true);

        return res.getImagePlus();
    }

    @Override
    public String getName() {
        return "Density computation";
    }

    @Override
    public String[] getParameters() {
        return new String[]{NEIGHBOURS, RADIUS};
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
