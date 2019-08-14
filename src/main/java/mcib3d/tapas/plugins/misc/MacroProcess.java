package mcib3d.tapas.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class MacroProcess implements TapasProcessing {
    public static final String DIR = "dir";
    public static final String FILE = "file";
    HashMap<String, String> parameters;
    ImageInfo info;


    public MacroProcess() {
        parameters = new HashMap<>();
        info = new ImageInfo();
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
        String name = getParameter(FILE);
        String dir = getParameter(DIR);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        IJ.log("Running macro" + dir2 + name2);
        WindowManager.setTempCurrentImage(input);
        input.show();
        IJ.runMacroFile(dir2 + name2);
        ImagePlus macroPlus = WindowManager.getCurrentImage();
        macroPlus.deleteRoi();
        ImagePlus dupMacro = macroPlus.duplicate();
        macroPlus.changes = false;
        macroPlus.close();

        return dupMacro;
    }

    @Override
    public String getName() {
        return "Macro execution";
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
