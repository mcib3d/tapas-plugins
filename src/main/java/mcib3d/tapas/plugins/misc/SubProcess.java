package mcib3d.tapas.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SubProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public SubProcess() {
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
        // TODO move to a static class with all the information
        HashMap<String, String> map = TapasBatchProcess.readPluginsFile(IJ.getDirectory("imagej") + File.separator + "tapas.txt", false);
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        ArrayList<TapasProcessing> tapasProcessings = TapasBatchProcess.readProcessings(dir2 + name2, map);
        ImagePlus img = input.duplicate();
        for (TapasProcessing tapasProcessing : tapasProcessings) {
            IJ.log("  * " + tapasProcessing.getName());
            tapasProcessing.setCurrentImage(info);
            img = tapasProcessing.execute(img);
            if (img == null) return null;
        }

        return img;
    }

    @Override
    public String getName() {
        return "Execute sub-processes";
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
