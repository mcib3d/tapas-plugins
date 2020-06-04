package mcib3d.tapas.IJ.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Plot;
import ij.io.FileSaver;
import mcib3d.geom.Point3D;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageHandler;

import java.io.IOException;
import java.util.HashMap;

public class EvfLayerProcess implements TapasProcessingIJ {
    public static final String DIR_EVF = "dirEvf";
    public static final String FILE_EVF = "fileEvf";
    public static final String NB_LAYERS = "nbLayers";
    public static final String DIR_RES = "dir";
    public static final String FILE_RES = "file";
    public static final String RANDOM = "random";

    ImageInfo info;
    HashMap<String, String> parameters;

    public EvfLayerProcess() {
        parameters = new HashMap<>();
        setParameter(NB_LAYERS,"100");
        setParameter(RANDOM, "no");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR_EVF:
                parameters.put(id, value);
                return true;
            case FILE_EVF:
                parameters.put(id, value);
                return true;
            case NB_LAYERS:
                parameters.put(id, value);
                return true;
            case DIR_RES:
                parameters.put(id, value);
                return true;
            case FILE_RES:
                parameters.put(id, value);
                return true;
            case RANDOM:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // random
        boolean rand = getParameter(RANDOM).trim().compareToIgnoreCase("yes") == 0;
        String name = getParameter(FILE_EVF);
        String dir = getParameter(DIR_EVF);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        IJ.log("Opening EVF image " + dir2 + name2);
        ImagePlus plus = IJ.openImage(dir2 + name2);
        if (plus == null) {
            IJ.log("Could not open EVF image " + dir2 + " " + name2);
            return null;
        }
        IJ.log("Computing EVF layers");
        name2 = TapasBatchUtils.analyseFileName(getParameter(FILE_RES), info);
        dir2 = TapasBatchUtils.analyseDirName(getParameter(DIR_RES));
        //IJ.log("PLOT "+plot.getImagePlus()+" "+plot.getResultsTable());
        Plot[] plots = computeEVFLayer(ImageHandler.wrap(plus), ImageHandler.wrap(input), Integer.parseInt(getParameter(NB_LAYERS)), rand);
        FileSaver saver = new FileSaver(plots[0].getImagePlus());
        saver.saveAsPng(dir2 + name2 + ".png");
        saver = new FileSaver(plots[1].getImagePlus());
        saver.saveAsPng(dir2 + name2 + "-all.png");
        if (rand) {
            saver = new FileSaver(plots[2].getImagePlus());
            saver.saveAsPng(dir2 + name2 + "-random.png");
        }
        try {
            plots[0].getResultsTable().saveAs(dir2 + name2 + ".csv");
            plots[1].getResultsTable().saveAs(dir2 + name2 + "-all.csv");
            if (rand)
                plots[2].getResultsTable().saveAs(dir2 + name2 + "-random.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }

    private Plot[] computeEVFLayer(ImageHandler evf, ImageHandler spots, int nbBin, boolean rand) {
        double step = 1.0 / (double) (nbBin);
        float[] countSpots = new float[nbBin];
        float[] countAll = new float[nbBin];
        float[] idx = new float[nbBin];
        double volTotalSpots = 0;
        double volTotal = 0;
        for (int z = 0; z < evf.sizeZ; z++) {
            for (int y = 0; y < evf.sizeY; y++) {
                for (int x = 0; x < evf.sizeX; x++) {
                    float evfPix = evf.getPixel(x, y, z);
                    if ((evfPix >= 0) && (evfPix <= 1)) {
                        int bin = (int) Math.floor(evfPix / step);
                        if (bin >= countAll.length) bin = countAll.length - 1;
                        float rawPix = spots.getPixel(x, y, z);
                        if (rawPix > 0) {
                            countSpots[bin]++;
                            volTotalSpots++;
                        }
                        countAll[bin]++;
                        volTotal++;
                    }
                }
            }
        }

        for (int i = 0; i < countAll.length; i++) {
            if (countAll[i] > 0)
                countAll[i] /= volTotal;
            else
                countAll[i] = 0;
            if (countSpots[i] > 0)
                countSpots[i] /= volTotalSpots;
            else
                countAll[i] = 0;
            idx[i] = (float) (i * step);
        }
        Plot plot1 = new Plot("Density spots", "evf", "density");
        plot1.addPoints(idx, countSpots, Plot.LINE);
        Plot plot2 = new Plot("Density all", "evf", "density");
        plot2.addPoints(idx, countAll, Plot.LINE);

        if (rand)
            return new Plot[]{plot1, plot2, randomSpots(evf, (int) volTotalSpots, nbBin)};
        else return new Plot[]{plot1, plot2};
    }

    private Plot randomSpots(ImageHandler evf, int nbSpots, int nbBin) {
        double step = 1.0 / (double) (nbBin);
        float[] count = new float[nbBin];
        float[] idx = new float[nbBin];

        int xmin = 0;
        int xmax = evf.sizeX - 1;
        int ymin = 0;
        int ymax = evf.sizeY;
        int zmin = 0;
        int zmax = evf.sizeZ - 1;

        for (int s = 0; s < nbSpots; s++) {
            int x = (int) Math.round(Math.random() * xmax);
            int y = (int) Math.round(Math.random() * ymax);
            int z = (int) Math.round(Math.random() * zmax);

            while ((!evf.contains(x, y, z)) || (evf.getPixel(x, y, z) < 0)) {
                x = (int) Math.round(Math.random() * xmax);
                y = (int) Math.round(Math.random() * ymax);
                z = (int) Math.round(Math.random() * zmax);
            }
            float evfPix = evf.getPixel(x, y, z);
            int bin = (int) Math.floor(evfPix / step);
            if (bin >= count.length) bin = count.length - 1;
            count[bin]++;
        }

        for (int i = 0; i < count.length; i++) {
            if (count[i] > 0)
                count[i] /= nbSpots;
            else
                count[i] = 0;
            idx[i] = (float) (i * step);
        }

        Plot plot = new Plot("Density random", "evf", "density");
        plot.addPoints(idx, count, Plot.LINE);

        return plot;
    }

    @Override
    public String getName() {
        return "EVF Layer Analysis";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR_EVF, FILE_EVF, NB_LAYERS, DIR_RES, FILE_RES, RANDOM};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
