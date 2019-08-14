package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.BioformatsReader;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.image3d.ImageHandler;
import omero.gateway.model.ImageData;

import java.io.File;
import java.util.HashMap;

public class InputProcess implements TapasProcessing {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String NAME = "name";
    private static final String CHANNEL = "channel";
    private static final String FRAME = "frame";

    ImageInfo info;
    HashMap<String, String> parameters;

    public InputProcess() {
        info = new ImageInfo();
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(NAME, "?name?");
        setParameter(CHANNEL, "?channel?");
        setParameter(FRAME, "?frame?");
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
            case NAME:
                parameters.put(id, value);
                return true;
            case CHANNEL:
                parameters.put(id, value);
                return true;
            case FRAME:
                parameters.put(id, value);
                return true;
        }
        return false;
    }


    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = getParameter(NAME);
        String project = getParameter(PROJECT);
        String dataset = getParameter(DATASET);
        String project2 = TapasBatchProcess.analyseFileName(project, info);
        String dataset2 = TapasBatchProcess.analyseFileName(dataset, info);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        ImageHandler output = null;

        // core input
        int c = TapasBatchProcess.analyseChannelFrameName(parameters.get(CHANNEL), info);
        int t = TapasBatchProcess.analyseChannelFrameName(parameters.get(FRAME), info);
        if (info.isOmero()) {
            try {
                OmeroConnect connect = new OmeroConnect();
                connect.connect();
                ImageData imageData = connect.findOneImage(project2, dataset2, name2, true);
                if (imageData == null) {
                    IJ.log("Cannot find " + project2 + " / " + dataset2 + " / " + name2);
                    return null;
                }

                IJ.log("Loading from OMERO : " + imageData.getName() + " c-" + c + " t-" + t);
                output = connect.getImage(imageData, t, c);
                connect.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.getImagePlus();
        } else { // use bioformats
            ImageInfo info2 = new ImageInfo(info.getRootDir(), project2, dataset2, name2, c, t);
            IJ.log("Loading : " + info2.getFilePath());
            ImagePlus plus = BioformatsReader.OpenImagePlus(info2.getFilePath(), info2.getC() - 1, info2.getT() - 1);
            if (plus == null) {
                IJ.log("Could not load " + info2.getFilePath());
                return null;
            }

            plus.setTitle(name2);
            return plus;
        }
    }

    @Override
    public String getName() {
        return "Input image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, NAME, CHANNEL, FRAME};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

}
