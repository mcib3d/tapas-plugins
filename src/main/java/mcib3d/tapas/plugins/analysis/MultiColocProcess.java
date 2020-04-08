package mcib3d.tapas.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.geom.Objects3DPopulationColocalisation;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.IOException;
import java.util.HashMap;

public class MultiColocProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String DIR_COLOC = "dirLabel";
    private static final String FILE_COLOC = "fileLabel";

    HashMap<String, String> parameters;
    ImageInfo info;

    public MultiColocProcess() {
        parameters = new HashMap<>();
        parameters.put(FILE, "results.csv");
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
            case DIR_COLOC:
                parameters.put(id, value);
                return true;
            case FILE_COLOC:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get images
        ImageHandler img = ImageHandler.wrap(input);
        ImagePlus plus = TapasBatchProcess.getImageFromFileParameters(parameters.get(DIR_COLOC), parameters.get(FILE_COLOC), info);
        ImageHandler coloc = ImageHandler.wrap(plus);

        // get population of objects
        Objects3DPopulation popA = new Objects3DPopulation(img);
        Objects3DPopulation popB = new Objects3DPopulation(coloc);

        // compute multi-colocalisation
        IJ.log("Computing multi-coloc");
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        resultsTable.reset();
        Objects3DPopulationColocalisation colocalisation = new Objects3DPopulationColocalisation(popA, popB);
        resultsTable = colocalisation.getResultsTableOnlyColoc(true);

        // save results
        IJ.log("Saving results");
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        try {
            resultsTable.saveAs(dir2 + name2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Multi-colocalisation between two images";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR_COLOC, FILE_COLOC, DIR, FILE};
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
