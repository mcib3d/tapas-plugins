package mcib3d.tapas.IJ.plugins.segmentation;

import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;

public class FillHolesProcess implements TapasProcessingIJ {
    @Override
    public boolean setParameter(String id, String value) {
        return true;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImagePlus filled = input.duplicate();
        FillHoles2D.fill(filled, 255, 0);

        return filled;
    }

    @Override
    public String getName() {
        return "2D Filling Holes";
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
