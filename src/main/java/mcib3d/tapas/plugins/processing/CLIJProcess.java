package mcib3d.tapas.plugins.processing;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

import java.util.HashMap;

public class CLIJProcess implements TapasProcessing {
    public final static String RADIUSXY = "radxy";
    public final static String RADIUSZ = "radz";
    public final static String FILTER = "filter";
    HashMap<String, String> parameters;

    public CLIJProcess() {
        // check install
        ClassLoader loader = IJ.getClassLoader();
        try {
            loader.loadClass("net.haesleinhuepf.clij.CLIJ");
        } catch (Exception e) {
            IJ.log("CLIJ not installed, please install from update site");
            return;
        }
        parameters = new HashMap<>();
        setParameter(RADIUSXY, "2");
        setParameter(RADIUSZ, "0");
        setParameter(FILTER, "median");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case RADIUSXY:
                parameters.put(id, value);
                return true;
            case RADIUSZ:
                parameters.put(id, value);
                return true;
            case FILTER:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get parameters
        int rx = getParameterInt(RADIUSXY);
        int ry = rx;
        int rz = getParameterInt(RADIUSZ);
        String filterS = getParameter(FILTER).toLowerCase();

        IJ.log("GPU Filtering with filter " + filterS + " and radii " + rx + "-" + ry + "-" + rz);
        // init CLIJ and create images
        CLIJ clij = CLIJ.getInstance();
        ClearCLBuffer inputCLBuffer = clij.push(input);

        // get result
        ClearCLBuffer outputCLBuffer;
        if (rz > 0) outputCLBuffer = filter3D(clij, inputCLBuffer, filterS, rx, ry, rz);
        else outputCLBuffer = filter2D(clij, inputCLBuffer, filterS, rx, ry);
        ImagePlus dstImagePlus = clij.pull(outputCLBuffer);
        dstImagePlus.setCalibration(input.getCalibration());
        // cleanup memory on GPU
        inputCLBuffer.close();
        outputCLBuffer.close();

        return dstImagePlus;
    }

    private ClearCLBuffer filter3D(CLIJ clij, ClearCLBuffer inputCLBuffer, String filter, int rx, int ry, int rz) {
        ClearCLBuffer outputCLBuffer = clij.create(inputCLBuffer);
        ClearCLBuffer tmpCLBuffer;

        switch (filter) {
            case "median":
                clij.op().medianSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "mean":
                clij.op().meanSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "erode":
            case "min":
                clij.op().minimumSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "dilate":
            case "max":
                clij.op().maximumSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "open":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.op().minimumSphere(inputCLBuffer, tmpCLBuffer, rx, ry, rz);
                clij.op().maximumSphere(tmpCLBuffer, outputCLBuffer, rx, ry, rz);
                tmpCLBuffer.close();
                break;
            case "close":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.op().maximumSphere(inputCLBuffer, tmpCLBuffer, rx, ry, rz);
                clij.op().minimumSphere(tmpCLBuffer, outputCLBuffer, rx, ry, rz);
                tmpCLBuffer.close();
                break;
            case "tophat":
                tmpCLBuffer = clij.create(inputCLBuffer);
                ClearCLBuffer openCLBuffer = clij.create(inputCLBuffer);
                clij.op().minimumSphere(inputCLBuffer, tmpCLBuffer, rx, ry, rz);
                clij.op().maximumSphere(tmpCLBuffer, openCLBuffer, rx, ry, rz);
                clij.op().subtractImages(inputCLBuffer, openCLBuffer, outputCLBuffer);
                tmpCLBuffer.close();
                openCLBuffer.close();
                break;
        }

        return outputCLBuffer;
    }

    private ClearCLBuffer filter2D(CLIJ clij, ClearCLBuffer inputCLBuffer, String filter, int rx, int ry) {
        ClearCLBuffer outputCLBuffer = clij.create(inputCLBuffer);
        ClearCLBuffer tmpCLBuffer;

        switch (filter) {
            case "median":
                clij.op().medianSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "mean":
                clij.op().meanSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "erode":
            case "min":
                clij.op().minimumSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "dilate":
            case "max":
                clij.op().maximumSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "open":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.op().minimumSphere(inputCLBuffer, tmpCLBuffer, rx, ry);
                clij.op().maximumSphere(tmpCLBuffer, outputCLBuffer, rx, ry);
                tmpCLBuffer.close();
                break;
            case "close":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.op().maximumSphere(inputCLBuffer, tmpCLBuffer, rx, ry);
                clij.op().minimumSphere(tmpCLBuffer, outputCLBuffer, rx, ry);
                tmpCLBuffer.close();
                break;
            case "tophat":
                tmpCLBuffer = clij.create(inputCLBuffer);
                ClearCLBuffer openCLBuffer = clij.create(inputCLBuffer);
                clij.op().minimumSphere(inputCLBuffer, tmpCLBuffer, rx, ry);
                clij.op().maximumSphere(tmpCLBuffer, openCLBuffer, rx, ry);
                clij.op().subtractImages(inputCLBuffer, openCLBuffer, outputCLBuffer);
                tmpCLBuffer.close();
                openCLBuffer.close();
                break;
        }

        return outputCLBuffer;
    }

    @Override
    public String getName() {
        return "2D/3D filters with CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{RADIUSXY, RADIUSZ, FILTER};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    private int getParameterInt(String id) {
        return Integer.parseInt(getParameter(id));
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }
}
