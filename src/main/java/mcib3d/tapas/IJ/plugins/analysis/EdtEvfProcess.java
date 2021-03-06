package mcib3d.tapas.IJ.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageFloat;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.distanceMap3d.EDT;

import java.util.HashMap;

public class EdtEvfProcess implements TapasProcessingIJ {
    private static final String EVF = "evf";
    private static final String INVERSE = "inverse";

    HashMap<String, String> parameters;

    public EdtEvfProcess() {
        parameters = new HashMap<>(1);
        parameters.put(EVF, "no");
        parameters.put(INVERSE, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case EVF:
                parameters.put(id, value);
                return true;
            case INVERSE:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImageInt labels = ImageInt.wrap(input);
        IJ.log("Processing EDT");
        boolean inverse = getParameter(INVERSE).equalsIgnoreCase("yes");
        ImageFloat edt = EDT.run(ImageHandler.wrap(input), 0, inverse, 0);
        IJ.log("EDT processed");
        if (getParameter(EVF).equalsIgnoreCase("no")) {
            return edt.getImagePlus();
        } else {
            IJ.log("Normalising EVF in each label");
            //EDT.normalizeDistanceMap(edt, labels, false);
            // normalize distance map per label
            ImageHandler evf = EDT.normaliseLabel(labels,edt);
            IJ.log("EVF processed");
            return evf.getImagePlus();
        }
    }

    @Override
    public String getName() {
        return "EDT and multi-label EVF";
    }

    @Override
    public String[] getParameters() {
        return new String[]{EVF, INVERSE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
    }
}
