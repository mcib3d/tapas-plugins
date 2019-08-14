package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.ImageInfo;

import java.io.File;
import java.util.HashMap;

public class WaitForFileProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String TIME = "time";// milli-seconds

    HashMap<String, String> parameters;
    ImageInfo info;


    public WaitForFileProcess() {
        parameters = new HashMap<>();
        setParameter(TIME, "1000");
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
            case TIME:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        int time = Integer.parseInt(parameters.get(TIME));
        // check names
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        File file = new File(dir2 + name2);
        if (!file.exists()) IJ.log("Waiting for file " + name2 + " in directory " + dir2);
        try {
            while (!file.exists()) {
                Thread.sleep(time);
            }
        } catch (InterruptedException e) {
            IJ.log("Pb with wait for " + parameters.get(FILE));
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Waiting for file";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, TIME};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
