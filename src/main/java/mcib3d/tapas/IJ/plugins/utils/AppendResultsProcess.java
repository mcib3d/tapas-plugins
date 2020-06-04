package mcib3d.tapas.IJ.plugins.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.macro.Variable;
import ij.measure.ResultsTable;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.ImageInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AppendResultsProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE1 = "file1";
    private static final String FILE2 = "file2";


    HashMap<String, String> parameters;
    ImageInfo info;

    public AppendResultsProcess() {
        parameters = new HashMap<>();
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE1:
                parameters.put(id, value);
                return true;
            case FILE2:
                parameters.put(id, value);
                return true;

        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String dir1 = TapasBatchUtils.analyseDirName(parameters.get(DIR));
        String name1 = TapasBatchUtils.analyseFileName(parameters.get(FILE1), info);
        File file1 = new File(dir1 + name1);
        String dir2 = TapasBatchUtils.analyseDirName(parameters.get(DIR));
        String name2 = TapasBatchUtils.analyseFileName(parameters.get(FILE2), info);
        File file2 = new File(dir2 + name2);
        // no file2 do nothing
        if (!file2.exists()) {
            IJ.log("File " + file2.getAbsolutePath() + " does not exist, exiting");
            return input.duplicate();
        }
        // no file1, cp file2 into file1, add the label as the image
        if (!file1.exists()) {
            IJ.log("File " + file1.getAbsolutePath() + " does not exist, will copy file2 into file1");
        }
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        else resultsTable.reset();

        // reading file1
        HashMap<String, Variable[]> headers1 = new HashMap<>();
        String[] headers;
        int nResults1;
        if (!file1.exists()) {
            nResults1 = 0;
        } else {
            resultsTable = ResultsTable.open2(dir1 + name1);
            nResults1 = resultsTable.size();
            headers1 = extractValuesFromTable(resultsTable);
            resultsTable.reset();
        }

        // reading file2
        HashMap<String, Variable[]> headers2 = new HashMap<>();
        // file2
        resultsTable = ResultsTable.open2(dir2 + name2);
        int nResults2 = resultsTable.size();
        headers2 = extractValuesFromTable(resultsTable);

        // create new results table
        resultsTable.reset();
        for (int r = 0; r < nResults1; r++) {
            resultsTable.incrementCounter();
            for (String header : headers1.keySet()) {
                if (headers1 == null) IJ.log("headers1 null " + header);
                if (headers1.get(header) == null) IJ.log("headers1.get null " + header);
                if (headers1.get(header)[r].getString() != null)
                    resultsTable.setValue(header, r, headers1.get(header)[r].getString());
                else resultsTable.setValue(header, r, headers1.get(header)[r].getValue());
            }
        }
        for (int r = nResults1; r < nResults1 + nResults2; r++) {
            resultsTable.incrementCounter();
            for (String header : headers2.keySet()) {
                if (headers2.get(header)[r - nResults1].getString() != null)
                    resultsTable.setValue(header, r, headers2.get(header)[r - nResults1].getString());
                else resultsTable.setValue(header, r, headers2.get(header)[r - nResults1].getValue());
            }
        }
        // save results table
        try {
            resultsTable.saveAs(dir1 + name1);
        } catch (IOException e) {
            IJ.log("Cannot saved appended table " + dir1 + name1);
            e.printStackTrace();
        }

        return input.duplicate();
    }

    private HashMap<String, Variable[]> extractValuesFromTable(ResultsTable resultsTable) {
        HashMap<String, Variable[]> results = new HashMap<>();
        String[] headers = resultsTable.getHeadings();
        int nResults2 = resultsTable.size();
        for (String h : headers) {
            int c = resultsTable.getColumnIndex(h);
            if (c != -1)
                results.put(h, resultsTable.getColumnAsVariables(h));
        }
        // check if column "image" exists
        if (!resultsTable.columnExists("Image")) {
            Variable[] images = new Variable[resultsTable.size()];
            for (int r = 0; r < resultsTable.size(); r++) {
                images[r] = new Variable(info.getImage());
            }
            results.put("Image", images);
        }
        // check special column label
        if (resultsTable.getLabel(0) != null) {
            Variable[] labels = new Variable[resultsTable.size()];
            for (int r = 0; r < resultsTable.size(); r++) {
                labels[r] = new Variable(resultsTable.getLabel(r));
            }
            results.put("Label", labels);
        }

        return results;
    }

    @Override
    public String getName() {
        return "Append a results table";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE1, FILE2};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
};