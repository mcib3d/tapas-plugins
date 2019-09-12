package mcib3d.tapas.plugins.segmentation;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.geom.Object3DVoxels;
import mcib3d.geom.Voxel3D;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.ImageLabeller;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class HysteresisProcess implements TapasProcessing {
    final static private String THRESHOLD_MIN = "minValue";
    final static private String THRESHOLD_MAX = "maxValue";
    final static private String LABELLING = "labeling";
    final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public HysteresisProcess() {
        parameters = new HashMap<>();
        setParameter(LABELLING, "no");
        setParameter(USERS, "-");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case THRESHOLD_MIN: // test value
                parameters.put(id, value);
                return true;
            case THRESHOLD_MAX:
                parameters.put(id, value);
                return true;
            case LABELLING:
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
        // get value for min
        String minS = getParameter(THRESHOLD_MIN);
        String key = TapasBatchProcess.getKey(minS, info, users);
        if (key == null) {
            IJ.log("No key " + minS);
            return null;
        }
        int thresholdMin = Integer.parseInt(key);
        // get value for max
        String maxS = getParameter(THRESHOLD_MAX);
        key = TapasBatchProcess.getKey(maxS, info, users);
        if (key == null) {
            IJ.log("No key " + maxS);
            return null;
        }
        int thresholdMax = Integer.parseInt(key);
        IJ.log("Hysteresis thresholding with values " + thresholdMin + " and " + thresholdMax);
        ImagePlus plus = hysteresis(input, thresholdMin, thresholdMax);
        if (input.getCalibration() != null) plus.setCalibration(input.getCalibration());

        return plus;
    }

    private ImagePlus hysteresis(ImagePlus image, double lowval, double highval) {
        int HIGH = 255;
        int LOW = 128;
        // first threshold the image
        ImageInt img = ImageInt.wrap(image);
        ImageByte multi = new ImageByte("Multi", img.sizeX, img.sizeY, img.sizeZ);
        for (int z = 0; z < img.sizeZ; z++) {
            for (int xy = 0; xy < img.sizeXY; xy++) {
                if (img.getPixel(xy, z) > highval) {
                    multi.setPixel(xy, z, HIGH);
                } else if (img.getPixel(xy, z) > lowval) {
                    multi.setPixel(xy, z, LOW);
                }
            }
        }

        ImageHandler thresholded = multi.thresholdAboveInclusive(LOW);
        ImageLabeller labeller = new ImageLabeller();
        ArrayList<Object3DVoxels> objects = labeller.getObjects(thresholded);
        boolean label = getParameter(LABELLING).equalsIgnoreCase("yes");
        ImageHandler hyst = new ImageByte("Hyst_" + image.getTitle(), multi.sizeX, multi.sizeY, multi.sizeZ);
        hyst.setScale(img);
        int val = 1;
        for (Object3DVoxels object3DVoxels : objects) {
            if (hasOneVoxelValueRange(object3DVoxels, multi, HIGH, HIGH)) {
                if (label) object3DVoxels.draw(hyst, val++);
                else object3DVoxels.draw(hyst, 255);
            }
        }

        return hyst.getImagePlus();
    }

    private boolean hasOneVoxelValueRange(Object3DVoxels object3DVoxels, ImageHandler handler, int t0, int t1) {
        LinkedList<Voxel3D> list = object3DVoxels.getVoxels();
        for (Voxel3D vox : list) {
            float pix = handler.getPixel(vox);
            if ((pix >= t0) && (pix <= t1)) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "Hysteresis thresholding";
    }

    @Override
    public String[] getParameters() {
        return new String[]{THRESHOLD_MIN, THRESHOLD_MAX, LABELLING, USERS};
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
