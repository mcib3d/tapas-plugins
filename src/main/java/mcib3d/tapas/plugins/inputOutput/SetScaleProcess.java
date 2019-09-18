package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchProcess;
import omero.gateway.model.ImageData;

import java.util.HashMap;

public class SetScaleProcess implements TapasProcessing {
    final static private String SCALEXY = "scaleXY";
    final static private String SCALEZ = "scaleZ";

    ImageInfo info;
    HashMap<String, String> parameters;

    public SetScaleProcess() {
        parameters = new HashMap<>();
        setParameter(SCALEXY, "1");
        setParameter(SCALEZ, "1");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case SCALEXY: // test value
                parameters.put(id, value);
                return true;
            case SCALEZ:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        double sxy = Double.parseDouble(getParameter(SCALEXY).trim());
        double sz = Double.parseDouble(getParameter(SCALEZ).trim());
        if (info.isOmero()) {
            try {
                OmeroConnect connect = new OmeroConnect();
                connect.connect();
                ImageData imageData = connect.findOneImage(info);
                if (imageData != null) {
                    if (connect.setResolutionImageUM(imageData, sxy, sz)) {
                        IJ.log("Set resolution to " + sxy + " " + sz);
                    } else {
                        IJ.log("Pb to set resolution");
                    }
                }
                connect.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // change resolution for inout
        Calibration calibration = input.getCalibration();
        if (calibration == null) {
            calibration = new Calibration();
        }
        calibration.setUnit("um");
        calibration.pixelWidth = sxy;
        calibration.pixelHeight = sxy;
        calibration.pixelDepth = sz;

        input.setCalibration(calibration);

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Set the image scale, update scale if OMERO, in um";
    }

    @Override
    public String[] getParameters() {
        return new String[]{SCALEXY, SCALEZ};
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
