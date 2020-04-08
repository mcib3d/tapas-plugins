package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.File;
import java.util.HashMap;

public class DeleteListProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String LIST = "list";
    HashMap<String, String> parameters;
    ImageInfo info;

    public DeleteListProcess() {
        parameters = new HashMap<>();
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case LIST:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // dir
        String dir = getParameter(DIR);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        // get list of files
        String[] files = parameters.get(LIST).split(",");
        // delete
        for (int f = 0; f < files.length; f++) {
            String name = files[f];
            String name2 = TapasBatchUtils.analyseFileName(name.trim(), info);
            File file = new File(dir2 + name2);
            if (file.exists()) {
                IJ.log("Deleting " + file.getPath());
                file.delete();
            }
            else {
                IJ.log("File "+ file.getPath()+" not found");
            }
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Deleting files in directory";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, LIST};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }


}
