package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.HashMap;

public class LoadProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public LoadProcess() {
        parameters = new HashMap<>();
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        IJ.log("Loading " + dir2 + name2);
        ImagePlus plus = IJ.openImage(dir2 + name2);
        if (plus == null) {
            IJ.log("Could not load image.");
            return null;
        }

        return plus;
    }

    @Override
    public String getName() {
        return "Loading from file using ImageJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

}
