package mcib3d.tapas.IJ.plugins.processing;

import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import ij.plugin.ZProjector;

public class ProjectionProcess implements TapasProcessingIJ {
    @Override
    public boolean setParameter(String id, String value) {
        return true;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ZProjector zProjector = new ZProjector();
        zProjector.setImage(input);
        zProjector.setMethod(ZProjector.MAX_METHOD);
        zProjector.setStartSlice(1);
        zProjector.setStopSlice(input.getNSlices());
        zProjector.doProjection();

        return zProjector.getProjection();
    }

    @Override
    public String getName() {
        return "Maximum Projection Z";
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
