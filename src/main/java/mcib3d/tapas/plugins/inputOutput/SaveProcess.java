package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.HashMap;

public class SaveProcess implements TapasProcessing {
    public static final String DIR = "dir";
    public static final String FILE = "file";
    public static final String FORMAT = "format";
    HashMap<String, String> parameters;
    ImageInfo info;

    public SaveProcess() {
        parameters = new HashMap<>();
        setParameter(FORMAT, "tif");
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
            case FORMAT:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = getParameter(FILE);
        String dir = getParameter(DIR);
        // check names
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        // save
        FileSaver saver = new FileSaver(input);
        boolean ok = false;
        if (input.getNSlices() > 1) {
            if (parameters.get(FORMAT).equalsIgnoreCase("zip"))
                ok = saver.saveAsZip(dir2 + name2);
            else
                ok = saver.saveAsTiffStack(dir2 + name2);
        } else {
            if (parameters.get(FORMAT).equalsIgnoreCase("zip"))
                ok = saver.saveAsZip(dir2 + name2);
            else
                ok = saver.saveAsTiff(dir2 + name2);
        }
        if (ok) {
            IJ.log(dir2 + name2 + " saved");
            return input.duplicate();
        } else {
            IJ.log("Pb with saving " + dir2 + name2);
            return null;
        }
    }

    @Override
    public String getName() {
        return "Saving file";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, FORMAT};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
