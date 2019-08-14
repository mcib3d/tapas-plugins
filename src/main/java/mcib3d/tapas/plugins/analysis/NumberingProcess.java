package mcib3d.tapas.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;

import java.io.IOException;
import java.util.HashMap;

public class NumberingProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String DIR_LABEL = "dirLabel";
    private static final String FILE_LABEL = "fileLabel";
    private boolean debug = true;

    HashMap<String, String> parameters;
    ImageInfo info;

    public NumberingProcess() {
        parameters = new HashMap<>();
        parameters.put(FILE, "results.csv");
    }

    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;
            case DIR_LABEL:
                parameters.put(id, value);
                return true;
            case FILE_LABEL:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    public ImagePlus execute(ImagePlus input) {
        if (debug) IJ.log("Opening labelled image");
        ImagePlus plus = TapasBatchProcess.getImageFromFileParameters(parameters.get(DIR_LABEL), parameters.get(FILE_LABEL), info);
        ImageHandler label = ImageHandler.wrap(plus);
        // measurements
        if (debug) IJ.log("Building population");
        Objects3DPopulation population = new Objects3DPopulation(ImageInt.wrap(input));
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        else resultsTable.reset();
        if (debug) IJ.log("Doing measurements");
        for (int i = 0; i < population.getNbObjects(); i++) {
            Object3D object3D = population.getObject(i);
            int[] res = object3D.getNumbering(label);
            resultsTable.incrementCounter();
            resultsTable.setValue("Value", i, object3D.getValue());
            resultsTable.setValue("NbObjects", i, res[0]);
            resultsTable.setValue("VolObjects", i, res[1]);

        }
        // save results
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        if (debug) IJ.log("Saving results to " + dir2 + " " + name2);
        try {
            resultsTable.saveAs(dir2 + name2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }


    public String getName() {
        return "Numbering inside objects";
    }

    public String[] getParameters() {
        return new String[]{DIR_LABEL, FILE_LABEL, DIR, FILE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
