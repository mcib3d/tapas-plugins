package mcib3d.tapas.plugins.processing;

import ij.ImagePlus;
import ij.plugin.filter.Filters;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;

public class InvertGrayProcess implements TapasProcessing {
    @Override
    public boolean setParameter(String id, String value) {
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImageHandler img = ImageHandler.wrap(input.duplicate());
        img.invert();

        return img.getImagePlus();
    }

    @Override
    public String getName() {
        return "Invert gray levels";
    }

    @Override
    public String[] getParameters() {
        return new String[0];
    }

    @Override
    public String getParameter(String id) {
        return null;
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
