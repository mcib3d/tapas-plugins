package mcib3d.tapas.plugins.segmentation;

import ij.ImagePlus;
import ij.plugin.filter.EDM;
import ij.process.ImageProcessor;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;

public class WatershedIJProcess implements TapasProcessing {
    @Override
    public boolean setParameter(String id, String value) {
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImagePlus copy = input.duplicate();
        ImageProcessor processor = copy.getProcessor();
        EDM wat = new EDM();
        wat.setup("watershed", input);
        wat.run(processor);
        
        return copy;
    }

    @Override
    public String getName() {
        return "ImageJ Watershed 2D to separate touching objects";
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
