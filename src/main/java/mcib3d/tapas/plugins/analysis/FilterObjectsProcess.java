package mcib3d.tapas.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;

import java.util.HashMap;
import java.util.Set;

public class FilterObjectsProcess implements TapasProcessing {
    private static final String MIN = "minValue";
    private static final String MAX = "maxValue";
    private static final String DESCRIPTOR = "descriptor";

    HashMap<String, String> parameters;

    public FilterObjectsProcess() {
        parameters = new HashMap<>();
        parameters.put(MIN, "0");
        parameters.put(MAX, "1");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case MIN:
                parameters.put(id, value);
                return true;
            case MAX:
                parameters.put(id, value);
                return true;
            case DESCRIPTOR:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // measurements
        String descriptor = parameters.get(DESCRIPTOR);
        double min = Double.parseDouble(parameters.get(MIN));
        double max = Double.parseDouble(parameters.get(MAX));
        // population
        ImageHandler handler = ImageHandler.wrap(input.duplicate());
        Objects3DPopulation population = new Objects3DPopulation(handler);
        int del = 0;
        for (Object3D object3D : population.getObjectsList()) {
            Double value = getMeasurement(object3D, descriptor);
            object3D.getAreaPixels();
            if ((Double.isNaN(value)) || (value < min) || (value > max)) {
                object3D.draw(handler, 0);
                del++;
            }

        }
        IJ.log(del + " objects removed using filter on " + descriptor + " between " + min + " and " + max);

        return handler.getImagePlus();
    }

    private double getMeasurement(Object3D object3D, String descriptor) {
        double value = Double.NaN;
        switch (descriptor) {
            case "volume":
                value = object3D.getVolumeUnit();
                break;
            case "compactness":
                value = object3D.getCompactness();
                break;
            case "elongation":
                value = object3D.getMainElongation();
                break;
            case "compactnessDiscrete":
                value = object3D.getObject3DVoxels().getDiscreteCompactness();
                break;
        }

        return value;
    }


    @Override
    public String getName() {
        return "Filter objects based on measurements";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DESCRIPTOR, MIN, MAX};
    }

    public Set<String> getParametersName(String id) {
        return parameters.keySet();
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }
}
