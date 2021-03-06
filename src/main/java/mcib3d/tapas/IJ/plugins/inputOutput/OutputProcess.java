package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;

import java.util.HashMap;

public class OutputProcess implements TapasProcessingIJ {
    public static final String PROJECT = "project";
    public static final String DATASET = "dataset";
    public static final String IMAGE = "image";

    HashMap<String, String> parameters;
    ImageInfo info;

    public OutputProcess() {
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
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
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // update final name
        String name = getParameter(IMAGE);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String project = getParameter(PROJECT);
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset = getParameter(DATASET);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        // check if image is OMERO or files
        TapasBatchProcess.outputImage(input, info, project2, dataset2, name2);

        return input.duplicate();
    }


    @Override
    public String getName() {
        return "Output image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, IMAGE};
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
