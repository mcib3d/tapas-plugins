package mcib3d.tapas.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.Resizer;
import ij.plugin.RoiReader;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.IOException;
import java.util.HashMap;

public class CropProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public CropProcess() {
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
        // load ROI
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        IJ.log("opening roi " + dir2 + name2);
        WindowManager.setTempCurrentImage(input);
        try {
            ij.plugin.RoiReader roiReader = new RoiReader();
            roiReader.openRoi(dir2, name2);
        } catch (IOException e) {
            IJ.log(" Cannot read ROI ");
            e.printStackTrace();
        }
        ij.plugin.Resizer resizer = new Resizer();
        resizer.run("crop");

        return WindowManager.getCurrentImage();
    }

    @Override
    public String getName() {
        return "Cropping with ROI";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE};
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
