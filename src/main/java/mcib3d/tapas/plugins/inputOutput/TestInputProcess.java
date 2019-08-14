package mcib3d.tapas.plugins.inputOutput;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;

import java.util.HashMap;

public class TestInputProcess implements TapasProcessing {
    private static final String DIMENSION = "3D";

    HashMap<String, String> parameters;

    public TestInputProcess() {
        parameters = new HashMap<>();
        setParameter(DIMENSION, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIMENSION:
                parameters.put(id, value);
                return true;
        }

        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // 2D
        if (getParameter(DIMENSION).equalsIgnoreCase("no")) {
            ImageProcessor ip = new ByteProcessor(100, 100);
            ip.noise(50);
            return new ImagePlus("test", ip);
        }
        // 3D
        else {
            ImageStack stack = new ImageStack(100, 100);
            for (int i = 0; i < 10; i++) {
                ImageProcessor ip = new ByteProcessor(100, 100);
                ip.noise(50);
                stack.addSlice(ip);
            }
            return new ImagePlus("test", stack);
        }
    }

    @Override
    public String getName() {
        return "Test input";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIMENSION};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }
}
