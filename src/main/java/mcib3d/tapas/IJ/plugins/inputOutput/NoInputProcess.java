package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class NoInputProcess implements TapasProcessingIJ {

    public NoInputProcess() {
    }

    @Override
    public boolean setParameter(String id, String value) {

        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImageProcessor ip = new ByteProcessor(1, 1);
        return new ImagePlus("noInput", ip);
    }

    @Override
    public String getName() {
        return "No input required";
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
