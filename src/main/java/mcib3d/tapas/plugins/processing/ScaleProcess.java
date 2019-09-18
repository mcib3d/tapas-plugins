package mcib3d.tapas.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.image3d.ImageHandler;
import omero.gateway.model.ImageData;

import java.util.HashMap;

public class ScaleProcess implements TapasProcessing {
    private final static String SCALEX = "scalex";
    private final static String SCALEY = "scaley";
    private final static String SCALEZ = "scalez";
    private final static String NORMALISE = "normalise";

    HashMap<String, String> parameters;
    ImageInfo info;

    public ScaleProcess() {
        parameters = new HashMap<>();
        parameters.put(SCALEX, "1");
        parameters.put(SCALEY, "1");
        parameters.put(SCALEZ, "1");
        parameters.put(NORMALISE, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case SCALEX:
                parameters.put(id, value);
                return true;
            case SCALEY:
                parameters.put(id, value);
                return true;
            case SCALEZ:
                parameters.put(id, value);
                return true;
            case NORMALISE:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        float scaleX = getParameterFloat(SCALEX);
        float scaleY = getParameterFloat(SCALEY);
        float scaleZ = getParameterFloat(SCALEZ);
        // check normalisation
        if (parameters.get(NORMALISE).equalsIgnoreCase("yes")) {
            IJ.log("normalising z scale");
            try {
                String project = info.getProject();
                String dataset = info.getDataset();
                String name = info.getName();
                OmeroConnect connect = new OmeroConnect();
                connect.connect();
                ImageData imageData = connect.findOneImage(project, dataset, name, true);
                if (imageData == null) {
                    IJ.log("Cannot find " + project + " / " + dataset + " / " + name);
                    return null;
                }
                double[] pixSize = connect.getResolutionImage(imageData);
                scaleZ = (float) ((pixSize[2] * scaleX) / pixSize[0]);
                connect.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IJ.log("Scaling with " + scaleX + ", " + scaleY + ", " + scaleZ);
        int newSizeX = Math.round(input.getWidth() * scaleX);
        int newSizeY = Math.round(input.getHeight() * scaleY);
        int newSizeZ = Math.round(input.getNSlices() * scaleZ);
        if ((scaleX == 1) && (scaleY == 1) && (scaleZ == 1))
            return input.duplicate();

        return ImageHandler.wrap(input).resample(newSizeX, newSizeY, newSizeZ, ImageProcessor.BICUBIC).getImagePlus();
    }

    @Override
    public String getName() {
        return "Scaling";
    }

    @Override
    public String[] getParameters() {
        return new String[]{SCALEX, SCALEY, SCALEZ, NORMALISE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

    private float getParameterFloat(String id) {
        return Float.parseFloat(getParameter(id));
    }
}
