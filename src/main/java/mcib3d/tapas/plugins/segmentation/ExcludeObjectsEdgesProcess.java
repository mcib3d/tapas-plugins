package mcib3d.tapas.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class ExcludeObjectsEdgesProcess implements TapasProcessing {
    private static final String TOUCHZ = "excludeZ";

    HashMap<String, String> parameters;

    public ExcludeObjectsEdgesProcess() {
        parameters = new HashMap<>();
        parameters.put(TOUCHZ, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case TOUCHZ:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // touchZ
        boolean touchZ = getParameter(TOUCHZ).equalsIgnoreCase("yes");
        // population
        ImageHandler img = ImageHandler.wrap(input.duplicate());
        Objects3DPopulation population = new Objects3DPopulation(img);
        int del = 0;
        for (Object3D object3D : population.getObjectsList()) {
            if (object3D.touchBorders(img, touchZ)) {
                object3D.draw(img, 0);
                del++;
            }
        }
        IJ.log(del + " objects removed touching edges");

        return img.getImagePlus();
    }

    @Override
    public String getName() {
        return "Delete objects on edges";
    }

    @Override
    public String[] getParameters() {
        return new String[]{TOUCHZ};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
