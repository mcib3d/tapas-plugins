package mcib3d.tapas.plugins.analysis;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.plugins.analysis.LT.Clean_Up_Local_Thickness;
import mcib3d.tapas.plugins.analysis.LT.Distance_Ridge;
import mcib3d.tapas.plugins.analysis.LT.EDT_S1D;
import mcib3d.tapas.plugins.analysis.LT.Local_Thickness_Parallel;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;

public class LocalThicknessProcess implements TapasProcessing {

    @Override
    public boolean setParameter(String id, String value) {
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get Calibration
        ImageHandler handler = ImageInt.wrap(input);

        Calibration calibration = input.getCalibration();
        float scalex = (float) calibration.getX(1);
        float scalez = (float) calibration.getZ(1);
        float ratio = scalez / scalex;
        // first make isotropic
        handler = handler.resample(handler.sizeX, handler.sizeY, Math.round(handler.sizeZ * ratio), ImageProcessor.NONE);
        WindowManager.setTempCurrentImage(handler.getImagePlus());
        // then run Local thickness, LT will assume a 1 1 1 calibration
        //IJ.run("Geometry to Distance Map", "threshold=" + 128);
        EDT_S1D edt_s1D = new EDT_S1D();
        edt_s1D.setup("128", handler.getImagePlus());
        ImagePlus impDM = edt_s1D.run(null);
        //IJ.run("Distance Map to Distance Ridge");
        Distance_Ridge distanceRidge = new Distance_Ridge();
        distanceRidge.setup("", impDM);
        ImagePlus impDR = distanceRidge.run(null);
        //IJ.run("Distance Ridge to Local Thickness");
        Local_Thickness_Parallel thickness_parallel = new Local_Thickness_Parallel();
        thickness_parallel.setup("", impDR);
        thickness_parallel.run(null);
        //IJ.run("Local Thickness to Cleaned-Up Local Thickness");
        Clean_Up_Local_Thickness clean = new Clean_Up_Local_Thickness();
        clean.setup("", impDR);
        ImagePlus impLTC = clean.run(null);
        // then divide
        ImageStack stack = impLTC.getStack();
        for (int s = 1; s <= impLTC.getNSlices(); s++) {
            ImageProcessor processor = stack.getProcessor(s);
            processor.multiply(scalex);
        }
        // then go back to original calibration
        ImageHandler LTimg = ImageHandler.wrap(impLTC);
        LTimg = LTimg.resample(handler.sizeX, handler.sizeY, Math.round(handler.sizeZ / ratio), ImageProcessor.BICUBIC);
        // should be ok now
        LTimg.setScale(handler);

        return LTimg.getImagePlus();
    }

    @Override
    public String getName() {
        return "Local Thickness";
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
