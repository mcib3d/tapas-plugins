package mcib3d.tapas.plugins.analysis;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageInt;

import java.io.IOException;
import java.util.HashMap;

public class BinaryClassificationProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String DESC = "descriptor";
    private static final String THRESHOLD = "threshold";
    private boolean debug = false;

    HashMap<String, String> parameters;
    ImageInfo info;

    public BinaryClassificationProcess() {
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
            case DESC:
                parameters.put(id, value);
                return true;
            case THRESHOLD:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        else resultsTable.reset();
        // reading results
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        resultsTable = ResultsTable.open2(dir2 + name2);
        // population
        ImageInt img = ImageInt.wrap(input);
        Objects3DPopulation population = new Objects3DPopulation(img);
        // results in new Image
        ImagePlus classify = NewImage.createRGBImage("classify", input.getWidth(), input.getHeight(), input.getNSlices(), NewImage.FILL_BLACK);
        ImageStack stack=classify.getImageStack();
        // analyse results
        String desc = parameters.get(DESC);
        double threshold = Double.parseDouble(parameters.get(THRESHOLD));
        int c1 = 0;
        int c2 = 0;
        for (int res = 0; res < resultsTable.size(); res++) {
            int value = (int) resultsTable.getValue("Value", res);
            Object3D object3D = population.getObjectByValue(value);
            double descValue = resultsTable.getValue(desc, res);
            if (descValue > threshold) {
                object3D.draw(stack, 0, 255, 0);
                c2++;
            } else {
                object3D.draw(stack, 255, 0, 0);
                c1++;
            }
        }
        // results table
        resultsTable.reset();
        resultsTable.incrementCounter();
        resultsTable.setValue("Positive", 0, c2);
        resultsTable.setValue("Negative", 0, c1);
        resultsTable.setValue("Total", 0, c1 + c2);
        try {
            resultsTable.saveAs(dir2 + name2 + "-classification.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classify;
    }

    @Override
    public String getName() {
        return "Binary Classification";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, DESC, THRESHOLD};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
