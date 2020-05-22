package mcib3d.tapas.plugins.inputOutput;

import ij.ImagePlus;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;

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
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        ImageHandler output = null;

        // core input
        int c = TapasBatchUtils.analyseChannelFrameName(parameters.get(CHANNEL), info);
        int t = TapasBatchUtils.analyseChannelFrameName(parameters.get(FRAME), info);

       ImagePlus plus = TapasBatchProcess.inputImage(info,project2,dataset2,name2,c,t);

       return plus;
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
