package mcib3d.tapas.plugins.processing;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.HashMap;

public class NormaliseValuesProcess implements TapasProcessing {
    final static String MEAN = "mean";
    final static String SD = "sd";

    ImageInfo info;
    HashMap<String, String> parameters;

    public NormaliseValuesProcess() {
        parameters = new HashMap<>();
        setParameter(MEAN, "128.0");
        setParameter(SD, "32.0");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case MEAN:
                parameters.put(id, value);
                return true;
            case SD:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // mean and sd
        float mean = Float.parseFloat(TapasBatchProcess.getKey(getParameter(MEAN), info, "-"));
        float sd = Float.parseFloat(TapasBatchProcess.getKey(getParameter(SD), info, "-"));
        ImageHandler img = ImageHandler.wrap(input);
        // convert to 16-bits if 8-bits and mean > 255
        if ((input.getBitDepth() < 16) && (mean > 255)) {
            img = ((ImageByte) img).convertToShort(false);
        }
        ImageHandler normalised = img.normaliseValue(mean, sd);

        return normalised.getImagePlus();
    }

    @Override
    public String getName() {
        return "Normalise values within the image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{MEAN, SD};
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
