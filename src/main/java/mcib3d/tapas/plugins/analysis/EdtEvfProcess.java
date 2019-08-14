package mcib3d.tapas.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageFloat;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.distanceMap3d.EDT;

import java.util.HashMap;

public class EdtEvfProcess implements TapasProcessing {
    private static final String EVF = "evf";

    HashMap<String, String> parameters;

    public EdtEvfProcess() {
        parameters = new HashMap<>(1);
        parameters.put(EVF, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case EVF:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        IJ.log("Processing EDT");
        ImageFloat edt = EDT.run(ImageHandler.wrap(input), 0, false, 0);
        IJ.log("EDT processed");
        if (getParameter(EVF).equalsIgnoreCase("no")) {
            return edt.getImagePlus();
        } else {
            IJ.log("Normalising EVF");
            EDT.normalizeDistanceMap(edt, ImageInt.wrap(input), false);
            IJ.log("EVF processed");
            return edt.getImagePlus();
        }
    }

    @Override
    public String getName() {
        return "EDT and EVF";
    }

    @Override
    public String[] getParameters() {
        return new String[]{EVF};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }
}
