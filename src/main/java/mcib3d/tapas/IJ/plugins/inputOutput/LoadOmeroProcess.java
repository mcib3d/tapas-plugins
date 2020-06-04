 package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Concatenator;
import ij.plugin.HyperStackConverter;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;
import omero.gateway.model.ImageData;

import java.util.HashMap;

public class LoadOmeroProcess implements TapasProcessingIJ {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String NAME = "name";
    private static final String CHANNELS = "channels";
    private static final String FRAMES = "frames";
    HashMap<String, String> parameters;
    ImageInfo info;

    public LoadOmeroProcess() {
        parameters = new HashMap<>();
        info = new ImageInfo();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(NAME, "?name?");
        setParameter(CHANNELS, "1");
        setParameter(FRAMES, "1");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case PROJECT:
                parameters.put(id, value);
                return true;
            case DATASET:
                parameters.put(id, value);
                return true;
            case NAME:
                parameters.put(id, value);
                return true;
            case CHANNELS:
                parameters.put(id, value);
                return true;
            case FRAMES:
                parameters.put(id, value);
                return true;
        }
        return false;
    }


    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = getParameter(NAME);
        String project = getParameter(PROJECT);
        String dataset = getParameter(DATASET);
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        ImageHandler output = null;
        ImagePlus stack = null;
        try {
            OmeroConnect connect = new OmeroConnect();
            connect.connect();
            ImageData imageData = connect.findOneImage(project2, dataset2, name2, true);
            int nbFrames = imageData.getDefaultPixels().getSizeT();
            int nbChannels = imageData.getDefaultPixels().getSizeC();
            IJ.log("Image dimension : C"+nbChannels+" T"+nbFrames);
            int[] channels = processTextForTimeChannel(getParameter(CHANNELS), nbChannels);
            int[] frames = processTextForTimeChannel(getParameter(FRAMES), nbFrames);
            //IJ.log("Dimensions : "+channels[0]+" "+channels[1]+" "+frames[0]+" "+frames[1]);
            int c0 = Math.max(1, channels[0]);
            int c1 = Math.min(nbChannels, channels[1]);
            int t0 = Math.max(1, frames[0]);
            int t1 = Math.min(nbFrames, frames[1]);
            // read multiple frames
            // first frame
            IJ.log("Reading first image t" + t0 + " c" + c0);
            ImageHandler handler = connect.getImage(imageData, t0, c0);
            stack = handler.getImagePlus();
            int sizeZ = stack.getNSlices();
            // next channels
            for (int c = c0 + 1; c <= c1; c++) {
                IJ.log("Reading image t" + t0 + " c" + c);
                handler = connect.getImage(imageData, t0, c);
                stack = Concatenator.run(stack, handler.getImagePlus());
            }

            for (int t = t0 + 1; t <= t1; t++) {
                for (int c = c0; c <= c1; c++) {
                    IJ.log("Reading image t" + t + " c" + c);
                    handler = connect.getImage(imageData, t, c);
                    stack = Concatenator.run(stack, handler.getImagePlus());
                }
            }
            connect.disconnect();
            int nbStacks = (t1 - t0 + 1) * (c1 - c0 + 1);
            if (nbStacks > 1)
                stack = HyperStackConverter.toHyperStack(stack, c1 - c0 + 1, sizeZ, t1 - t0 + 1, "xyzct", "composite");
            stack.setTitle(name2 + "_C" + c0 + "-" + c1 + "T" + t0 + "-" + t1);
            IJ.log("Done");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return stack;
    }

    @Override
    public String getName() {
        return "Load hyperStack from Omero";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, NAME, CHANNELS, FRAMES};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

    private int[] processTextForTimeChannel(String nextString, int nb) {
        int[] vals = new int[2];
        if (nextString.equalsIgnoreCase("all")) {
            return new int[]{1, nb };
        }
        if (nextString.contains("-")) {
            String[] cs = nextString.split("-");
            vals[0] = Integer.parseInt(cs[0]);
            vals[1] = Integer.parseInt(cs[1]);
        } else {
            vals[0] = Integer.parseInt(nextString);
            vals[1] = vals[0];
        }

        return vals;
    }

}
