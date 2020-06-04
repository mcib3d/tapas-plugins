package mcib3d.tapas.IJ.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class QuantificationProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String LIST_MEASUREMENTS = "list";
    private static final String DIR_RAW = "dirRaw";
    private static final String FILE_RAW = "fileRaw";
    private boolean debug = true;

    HashMap<String, String> parameters;
    ImageInfo info;

    public QuantificationProcess() {
        parameters = new HashMap<>();
        parameters.put(FILE, "results.csv");
        parameters.put(LIST_MEASUREMENTS,"mean");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;
            case DIR_RAW:
                parameters.put(id, value);
                return true;
            case FILE_RAW:
                parameters.put(id, value);
                return true;
            case LIST_MEASUREMENTS:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        //String name = parameters.get(FILE_RAW);
        //String dir = parameters.get(DIR_RAW);
        //String name2 = TapasBatchUtils.analyseFileName(name, info);
        //String dir2 = TapasBatchUtils.analyseDirName(dir);
        // open raw image to perform quantification
        if (debug) IJ.log("Opening raw image");
        //ImagePlus plus = IJ.openImage(dir2 + name2);
        ImagePlus plus = TapasBatchProcess.getImageFromFileParameters(parameters.get(DIR_RAW), parameters.get(FILE_RAW), info);
        ImageHandler raw = ImageHandler.wrap(plus);
        // measurements
        if (debug) IJ.log("Building population");
        Objects3DPopulation population = new Objects3DPopulation(ImageInt.wrap(input));
        ArrayList<String> list = new ArrayList<>();
        list.add("value");
        String[] meas = parameters.get(LIST_MEASUREMENTS).split(",");
        for (int i = 0; i < meas.length; i++) list.add(meas[i].trim().toLowerCase());
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        else resultsTable.reset();
        if (debug) IJ.log("Performing measurements");
        for (int i = 0; i < population.getNbObjects(); i++) {
            resultsTable.incrementCounter();
            HashMap<String, Double> m = doMeasurement(population.getObject(i), raw, list);
            for (String val : m.keySet())
                resultsTable.setValue(val, i, m.get(val));
        }
        // save results
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        if (debug) IJ.log("Saving results to " + dir2 + " " + name2);
        try {
            resultsTable.saveAs(dir2 + name2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }

    private HashMap<String, Double> doMeasurement(Object3D object3D, ImageHandler raw, ArrayList<String> list) {
        HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i).toLowerCase().trim()) {
                case "value":
                    map.put("Value", (double) object3D.getValue());
                    break;
                case "mean":
                    map.put("Mean", object3D.getPixMeanValue(raw));
                    break;
                case "min":
                    map.put("Min", object3D.getPixMinValue(raw));
                    break;
                case "max":
                    map.put("Max", object3D.getPixMaxValue(raw));
                    break;
                case "sd":
                    map.put("StdDev", object3D.getPixStdDevValue(raw));
                    break;
                case "sum":
                    map.put("Sum", object3D.getIntegratedDensity(raw));
                    break;
                case "centre":
                    map.put("Centre", object3D.getPixCenterValue(raw));
                    break;
                case "center":
                    map.put("Centre", object3D.getPixCenterValue(raw));
                    break;
                case "all":
                    map.put("Value", (double) object3D.getValue());
                    map.put("Mean", object3D.getPixMeanValue(raw));
                    map.put("Min", object3D.getPixMinValue(raw));
                    map.put("Max", object3D.getPixMaxValue(raw));
                    map.put("StdDev", object3D.getPixStdDevValue(raw));
                    map.put("Sum", object3D.getIntegratedDensity(raw));
                    map.put("Centre", object3D.getPixCenterValue(raw));
            }
        }

        return map;
    }


    @Override
    public String getName() {
        return "Signal quantification";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR_RAW, FILE_RAW, LIST_MEASUREMENTS, DIR, FILE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
