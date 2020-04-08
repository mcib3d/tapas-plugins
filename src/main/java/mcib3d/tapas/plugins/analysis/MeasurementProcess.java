package mcib3d.tapas.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MeasurementProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String LIST_MEASUREMENTS = "list";

    HashMap<String, String> parameters;
    ImageInfo info;

    public MeasurementProcess() {
        parameters = new HashMap<>();
        parameters.put(FILE, "results.csv");
        setParameter(LIST_MEASUREMENTS, "volume");
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
            case LIST_MEASUREMENTS:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        // measurements
        Objects3DPopulation population = new Objects3DPopulation(ImageInt.wrap(input));
        ArrayList<String> list = new ArrayList<>();
        list.add("value");
        // case all
        String measList = parameters.get(LIST_MEASUREMENTS);
        if (measList.equalsIgnoreCase("all")) {
            measList = "volume,area,compactness,ellipsoid,dc,centroid";
        }
        String[] meas = measList.split(",");
        for (int i = 0; i < meas.length; i++) list.add(meas[i].trim().toLowerCase());
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        else resultsTable.reset();
        for (int i = 0; i < population.getNbObjects(); i++) {
            resultsTable.incrementCounter();
            HashMap<String, Double> m = doMeasurement(population.getObject(i), list);
            for (String val : m.keySet())
                resultsTable.setValue(val, i, m.get(val));
        }
        IJ.log("Saving measurements to " + dir2 + " " + name2);
        try {
            resultsTable.saveAs(dir2 + name2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }

    private HashMap<String, Double> doMeasurement(Object3D object3D, ArrayList<String> list) {
        HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i).trim()) {
                case "value":
                    map.put("Value", (double) object3D.getValue());
                    break;
                case "volume":
                    map.put("Volume_Pix", (double) object3D.getVolumePixels());
                    map.put("Volume_Unit", object3D.getVolumeUnit());
                    break;
                case "area":
                    map.put("Surface_Pix", object3D.getAreaPixels());
                    map.put("Surface_Unit", object3D.getAreaUnit());
                    break;
                case "centroid":
                    map.put("Cx_Pix", object3D.getCenterX());
                    map.put("Cy_Pix", object3D.getCenterY());
                    map.put("Cz_Pix", object3D.getCenterZ());
                    break;
                case "compactness":
                    map.put("Compactness_Pix", object3D.getCompactness());
                    map.put("Compactness_Unit", object3D.getCompactness(true));
                    map.put("Compactness_Pix", object3D.getSphericity());
                    map.put("Compactness_Unit", object3D.getSphericity(true));
                    map.put("CompactnessDiscrete", object3D.getObject3DVoxels().getDiscreteCompactness());
                    break;
                case "ellipsoid":
                    map.put("MainElongation", object3D.getMainElongation());
                    map.put("MedianElongation", object3D.getMedianElongation());
                    map.put("RatioVolEll", object3D.getRatioEllipsoid());
                    break;
                case "dc":
                    map.put("DCmean", object3D.getDistCenterMean());
                    map.put("DCsigma", object3D.getDistCenterSigma());
                    map.put("DCmin", object3D.getDistCenterMin());
                    map.put("DCmax", object3D.getDistCenterMax());
                    break;
            }
        }

        return map;
    }


    @Override
    public String getName() {
        return "Measurements";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, LIST_MEASUREMENTS};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
