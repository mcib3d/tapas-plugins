 package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.*;
import omero.gateway.model.ImageData;

import java.util.HashMap;

public class InputProcessBinning implements TapasProcessingIJ {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String IMAGE = "image";
    private static final String CHANNEL = "channel";
    private static final String FRAME = "frame";
    // binning
    private static final String BINXY = "binningXY";
    private static final String BINZ = "binningZ";

    ImageInfo info;
    HashMap<String, String> parameters;

    public InputProcessBinning() {
        info = new ImageInfo();
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(IMAGE, "?image?");
        setParameter(CHANNEL, "?channel?");
        setParameter(FRAME, "?frame?");
        setParameter(BINXY, "1");
        setParameter(BINZ, "1");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case PROJECT:
                parameters.put(id, value);
                return true;
            case DATASET:
                parameters.put(id, value);
                return true;
            case "name": // deprecated
            case IMAGE:
                parameters.put(id, value);
                return true;
            case CHANNEL:
                parameters.put(id, value);
                return true;
            case FRAME:
                parameters.put(id, value);
                return true;
            case BINXY:
                parameters.put(id, value);
                return true;
            case BINZ:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = getParameter(IMAGE);
        String project = getParameter(PROJECT);
        String dataset = getParameter(DATASET);
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        ImageHandler output = null;

        // core input
        int c = TapasBatchUtils.analyseChannelFrameName(parameters.get(CHANNEL), info);
        int t = TapasBatchUtils.analyseChannelFrameName(parameters.get(FRAME), info);
        // binning
        int binXY = Integer.parseInt(getParameter(BINXY));
        int binZ = Integer.parseInt(getParameter(BINZ));
        try {
            OmeroConnect connect = new OmeroConnect();
            connect.connect();
            ImageData imageData = connect.findOneImage(project2, dataset2, name2, true);
            if (imageData == null) {
                IJ.log("Cannot find " + project2 + " / " + dataset2 + " / " + name2);
                return null;
            }

            IJ.log("Loading from OMERO " + imageData.getName() + " c-" + c + " t-" + t);
            output = connect.getImageBin(imageData, t, c, binXY, binZ);
            connect.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.getImagePlus();
    }

    @Override
    public String getName() {
        return "Input from OMERO with binning";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, IMAGE, FRAME, CHANNEL, BINXY, BINZ};
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
