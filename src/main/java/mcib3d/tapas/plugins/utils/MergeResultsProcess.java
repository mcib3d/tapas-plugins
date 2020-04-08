package mcib3d.tapas.plugins.utils;


import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.core.ImageInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MergeResultsProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String LIST = "list";
    private static final String FILE = "fileMerge";

    HashMap<String, String> parameters;
    ImageInfo info;

    public MergeResultsProcess() {
        parameters = new HashMap<>();
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case LIST:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;

        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // FIXME pb with labels
        ResultsTable resultsTable = ResultsTable.getResultsTable();
        if (resultsTable == null) resultsTable = new ResultsTable();
        else resultsTable.reset();
        // reading list
        int nResults = 0;
        HashMap<String, float[]> headers = new HashMap<>();
        ArrayList<String> headerString = new ArrayList<>();
        String dir2 = TapasBatchUtils.analyseDirName(parameters.get(DIR));
        String[] files = parameters.get(LIST).split(",");
        for (int f = 0; f < files.length; f++) {
            String name = files[f];
            String name2 = TapasBatchUtils.analyseFileName(name.trim(), info);
            File file = new File(dir2 + name2);
            if (file.exists()) {
                IJ.log("Reading results " + dir2 + name2);
                resultsTable = ResultsTable.open2(dir2 + name2);
                if (nResults == 0) nResults = resultsTable.size();
                if (nResults != resultsTable.size()) {
                    IJ.log("Number of values not consistent");
                }
                // read headers
                String[] headers1 = resultsTable.getHeadings();
                for (int i = 0; i < headers1.length; i++) {
                    String header = headers1[i].concat("_" + (f + 1));
                    headers.put(header, resultsTable.getColumn(i));
                    headerString.add(header);
                }
                resultsTable.reset();
            } else {
                IJ.log("No results table " + dir2 + name2);
            }
        }

        // save results
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name3 = TapasBatchUtils.analyseFileName(name, info);
        String dir3 = TapasBatchUtils.analyseDirName(dir);
        writeData(dir3 + name3, headers, headerString, ",");

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Merge a list of results tables";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, LIST};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

    public boolean writeData(String fileName, HashMap<String, float[]> values, ArrayList<String> headers, String delimiter) {
        BufferedWriter buf;
        String name = fileName;
        if (!name.contains(".")) {
            name = name.concat(".csv");
        }
        try {
            buf = new BufferedWriter(new FileWriter(name));
            int numRows = values.get(headers.get(0)).length;
            int numCols = headers.size();
            buf.write(headers.get(0));
            for (int i = 1; i < headers.size(); i++) {
                buf.write(delimiter + headers.get(i));
            }
            buf.write("\n");
            for (int i = 0; i < numRows; i++) {
                buf.write("" + values.get(headers.get(0))[i]);
                for (int j = 1; j < numCols; j++) {
                    buf.write(delimiter + values.get(headers.get(j))[i]);
                }
                buf.write("\n");
            }
            buf.close();
        } catch (FileNotFoundException ex) {
            IJ.log("No file " + fileName);
            return false;
        } catch (IOException ex) {
            IJ.log("Pb file " + fileName);
            return false;
        }

        return true;
    }
}
