package mcib3d.tapas.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.HashMap;

public class ShowProcess implements TapasProcessing {
    final static public String TITLE = "title";

    HashMap<String, String> parameters;
    ImageInfo info;

    public ShowProcess() {
        parameters = new HashMap<>();
        setParameter(TITLE, "?name?");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case TITLE:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        if (IJ.macroRunning()) return input.duplicate();
        // show image :)
        String title;
        title = TapasBatchProcess.analyseFileName(getParameter(TITLE), info);
        input.setTitle(title);
        input.show();

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Display current image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{TITLE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
