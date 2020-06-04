package mcib3d.tapas.IJ.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.IOException;
import java.util.HashMap;

public class AnalyzeParticlesProcess implements TapasProcessingIJ {
    private static final String SIZEMIN = "minSize";
    private static final String SIZEMAX = "maxSize";
    private static final String CIRCMIN = "minCirc";
    private static final String CIRCMAX = "maxCirc";
    private static final String EXCLUDEEDGES = "excludeEdges";
    private static final String UNIT = "unit";
    private static final String LIST = "list";
    //private static final String DIR_RAW = "dirRaw";
    //private static final String FILE_RAW = "fileRaw";
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public AnalyzeParticlesProcess() {
        parameters = new HashMap<>();
        parameters.put(SIZEMIN, "0");
        parameters.put(SIZEMAX, "-1");
        parameters.put(UNIT, "no");
        parameters.put(CIRCMIN, "0");
        parameters.put(CIRCMAX, "1");
        parameters.put(LIST, "area,perimeter");
        parameters.put(FILE, "results.csv");
        parameters.put(EXCLUDEEDGES, "yes");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case SIZEMIN:
                parameters.put(id, value);
                return true;
            case SIZEMAX:
                parameters.put(id, value);
                return true;
            case CIRCMIN:
                parameters.put(id, value);
                return true;
            case CIRCMAX:
                parameters.put(id, value);
                return true;
            case EXCLUDEEDGES:
                parameters.put(id, value);
                return true;
            case UNIT:
                parameters.put(id, value);
                return true;
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;
            case LIST:
                parameters.put(id, value);
                return true;

        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get image calibration
        double resXY = 1;
        double resZ = 1;
        Calibration calibration = input.getCalibration();
        if (calibration != null) {
            resXY = calibration.getX(1);
            resZ = calibration.getZ(1);
        }
        // min-max size and circularity
        // get min max volumes
        double minS = getParameterDouble(SIZEMIN);
        double maxS = getParameterDouble(SIZEMAX);
        double minC = getParameterDouble(CIRCMIN);
        double maxC = getParameterDouble(CIRCMAX);
        // check if unit, convert in pixels
        if (getParameter(UNIT).trim().equalsIgnoreCase("yes")) {
            double volInv = 1.0 / (resXY * resXY * resZ);
            minS = (int) Math.round(minS * volInv);
            if (maxS < 0) maxS = Integer.MAX_VALUE;
            else maxS = (int) Math.round(maxS * volInv);
        }
        if (minS < 0) minS = 0;
        if (maxS < 0) maxS = Integer.MAX_VALUE;
        if (minC < 0) minC = 0;
        if (maxC > 1) maxC = 1;
        // options
        int options = ParticleAnalyzer.SHOW_ROI_MASKS;
        if (getParameter(EXCLUDEEDGES).equalsIgnoreCase("yes")) options += ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
        // measurements
        int measurements = Measurements.AREA + Measurements.PERIMETER + Measurements.LIMIT;// by default to compute circ
        String[] list = getParameter(LIST).split(",");
        for (String mes : list) {
            if (mes.trim().equalsIgnoreCase("centroid")) measurements += Measurements.CENTROID;
            else if (mes.trim().equalsIgnoreCase("ellipse")) measurements += Measurements.ELLIPSE;
            else if (mes.trim().equalsIgnoreCase("shape")) measurements += Measurements.SHAPE_DESCRIPTORS;
            else if (mes.trim().equalsIgnoreCase("feret")) measurements += Measurements.FERET;
        }
        // results table
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        // run particle analyzer
        IJ.log("Running particle analyzer with size " + minS + "-" + maxS + " and circ. " + minC + "-" + maxC);
        ParticleAnalyzer analyzer = new ParticleAnalyzer(options, measurements, resultsTable, minS, maxS, minC, maxC);
        analyzer.setHideOutputImage(true);
        analyzer.analyze(input);
        // save results table
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        try {
            resultsTable.saveAs(dir2 + name2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return analyzer.getOutputImage();
    }

    @Override
    public String getName() {
        return "ImageJ 2D Analyse Particles";
    }

    @Override
    public String[] getParameters() {
        return new String[]{SIZEMIN, SIZEMAX, UNIT, CIRCMIN, CIRCMAX, EXCLUDEEDGES, LIST, DIR, FILE};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

    private double getParameterDouble(String id) {
        return Double.parseDouble(getParameter(id));
    }
}
