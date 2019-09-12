package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Concatenator;
import ij.plugin.HyperStackConverter;
import ij.plugin.RGBStackMerge;
import ij.process.ImageConverter;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.util.ArrayList;
import java.util.HashMap;

public class MergeChannelsProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String LIST = "list";
    private static final String RGB = "rgb";


    HashMap<String, String> parameters;
    ImageInfo info;

    public MergeChannelsProcess() {
        parameters = new HashMap<>();
        setParameter(RGB, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case LIST:
                parameters.put(id, value);
                return true;
            case RGB:
                parameters.put(id, value);
                return true;

        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // directory
        String dir = parameters.get(DIR);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        // list of files
        String[] files = parameters.get(LIST).split(",");
        // rgb or composite
        if (getParameter(RGB).equalsIgnoreCase("no"))
            return composite(files, dir2);
        else
            return rgb(files, dir2);
    }

    private ImagePlus rgb(String[] files, String dir2) {
        ArrayList<ImagePlus> pluses = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String name = files[i];
            String name2 = TapasBatchProcess.analyseFileName(name, info);
            IJ.log("Opening " + dir2 + name2);
            ImagePlus tmp = IJ.openImage(dir2 + name2);
            if (tmp == null) continue;
            pluses.add(tmp);
        }
        // merge channels RGB
        ImagePlus[] imagePluses = new ImagePlus[pluses.size()];
        for (int i = 0; i < pluses.size(); i++) imagePluses[i] = pluses.get(i);
        ImagePlus merge = RGBStackMerge.mergeChannels(imagePluses, false);
        ImageConverter converter = new ImageConverter(merge);
        converter.convertToRGB();

        return merge;
    }

    private ImagePlus composite(String[] files, String dir2) {
        // load first image
        String name = files[0];
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        IJ.log("Opening " + dir2 + name2);
        ImagePlus plus = IJ.openImage(dir2 + name2);
        int slices = plus.getNSlices();
        if (plus == null) return null;
        // load images
        int channels = 1;
        for (int i = 1; i < files.length; i++) {
            name = files[i];
            name2 = TapasBatchProcess.analyseFileName(name, info);
            IJ.log("Opening " + dir2 + name2);
            ImagePlus tmp = IJ.openImage(dir2 + name2);
            if (tmp == null) continue;
            plus = Concatenator.run(plus, tmp);
            channels++;
        }

        // convert to hyperstack composite
        ImagePlus merge = HyperStackConverter.toHyperStack(plus, channels, slices, 1, "xyzct", "color");

        return merge;
    }

    @Override
    public String getName() {
        return "Merge channels";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, LIST, RGB};
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
