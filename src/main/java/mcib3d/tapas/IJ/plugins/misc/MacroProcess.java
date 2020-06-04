package mcib3d.tapas.IJ.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class MacroProcess implements TapasProcessingIJ {
    public static final String DIR = "dir";
    public static final String FILE = "file";
    public static final String CLOSEALL = "closeImages";
    HashMap<String, String> parameters;
    ImageInfo info;


    public MacroProcess() {
        parameters = new HashMap<>();
        info = new ImageInfo();
        setParameter(CLOSEALL, "yes");
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
            case CLOSEALL:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = getParameter(FILE);
        String dir = getParameter(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        IJ.log("Running macro" + dir2 + name2);
        // set the right title to the image so macro can use it
        input.setTitle(info.getImage());
        WindowManager.setTempCurrentImage(input);
        input.show();
        IJ.runMacroFile(dir2 + name2);
        ImagePlus macroPlus = WindowManager.getCurrentImage();
        macroPlus.deleteRoi();
        ImagePlus dupMacro = macroPlus.duplicate();
        macroPlus.changes = false;
        macroPlus.close();
        // close all images
        if (getParameter(CLOSEALL).equalsIgnoreCase("yes")) {
            while (WindowManager.getImageCount() > 0) {
                ImagePlus tmp = WindowManager.getCurrentImage();
                tmp.changes = false;
                tmp.close();
            }
        }

        return dupMacro;
    }

    @Override
    public String getName() {
        return "Macro execution";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, CLOSEALL};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

}
