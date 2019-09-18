package mcib3d.tapas.plugins.utils;


import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.ImageInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AppendResultsProcess implements TapasProcessing {
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
        String dir1 = TapasBatchProcess.analyseDirName(parameters.get(DIR));
        String name1 = TapasBatchProcess.analyseFileName(parameters.get(FILE1), info);
        File file1 = new File(dir1 + name1);
        String dir2 = TapasBatchProcess.analyseDirName(parameters.get(DIR));
        String name2 = TapasBatchProcess.analyseFileName(parameters.get(FILE2), info);
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
        HashMap<String, float[]> headers1 = new HashMap<>();
        String[] headers;
        String[] labels1 = null;
        int nResults1;
        if (!file1.exists()) {
            resultsTable = null;
            nResults1 = 0;
        } else {
            resultsTable = ResultsTable.open2(dir1 + name1);
            headers = resultsTable.getHeadings();
            //for (String h : headers) IJ.log("Found header " + h);
            nResults1 = resultsTable.size();
            labels1 = new String[nResults1];
            for (int r = 0; r < nResults1; r++) labels1[r] = ".";
            for (String h : headers) {
                int c = resultsTable.getColumnIndex(h);
                if (c != -1)
                    headers1.put(h, resultsTable.getColumn(c));
                else if (h.equalsIgnoreCase("label")) {
                    for (int r = 0; r < nResults1; r++) labels1[r] = resultsTable.getLabel(r);
                }
            }
            resultsTable.reset();
        }


        // reading file2
        HashMap<String, float[]> headers2 = new HashMap<>();
        String[] labels2 = null;
        // file2
        resultsTable = ResultsTable.open2(dir2 + name2);
        headers = resultsTable.getHeadings();
        int nResults2 = resultsTable.size();
        labels2 = new String[nResults2];
        for (int r = 0; r < nResults2; r++) labels2[r] = ".";
        for (String h : headers) {
            int c = resultsTable.getColumnIndex(h);
            if (c != -1)
                headers2.put(h, resultsTable.getColumn(c));
            else if (h.equalsIgnoreCase("label")) {
                for (int r = 0; r < nResults2; r++) labels2[r] = resultsTable.getLabel(r);
            }
        }
        for (int r = 0; r < nResults2; r++) if (labels2[r].equals(".")) labels2[r] = info.getName();
        // create new results table
        resultsTable.reset();
        for (int r = 0; r < nResults1; r++) {
            resultsTable.incrementCounter();
            // set label first
            if (labels1 != null) resultsTable.setLabel(labels1[r], r);
            for (String header : headers1.keySet()) {
                if (headers1 == null) IJ.log("headers1 null " + header);
                if (headers1.get(header) == null) IJ.log("headers1.get null " + header);
                float val = headers1.get(header)[r];
                if (Float.isNaN(val))
                    resultsTable.setValue(header, r, Double.NaN);
                else resultsTable.setValue(header, r, val);
            }
        }
        for (int r = nResults1; r < nResults1 + nResults2; r++) {
            resultsTable.incrementCounter();
            // set label first
            if (labels2 != null) resultsTable.setLabel(labels2[r - nResults1], r);
            else resultsTable.setLabel(info.getName(), r);
            for (String header : headers2.keySet()) {
                float val = headers2.get(header)[r - nResults1];
                if (Float.isNaN(val))
                    resultsTable.setValue(header, r, Double.NaN);
                else resultsTable.setValue(header, r, val);
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