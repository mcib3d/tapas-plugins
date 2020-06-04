package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.*;
import java.util.HashMap;

public class SaveScaleProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public SaveScaleProcess() {
        parameters = new HashMap<>();
        setParameter(FILE, "scale.txt");
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
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get the file
        String dir = TapasBatchUtils.analyseDirName(getParameter(DIR));
        String file = TapasBatchUtils.analyseFileName(getParameter(FILE), info);
        try {
            // get calibration
            Calibration calibration = input.getCalibration();
            double sxy = 1;
            double sz = 1;
            String unit = "um";
            if (calibration != null) {
                sxy = calibration.getX(1);
                sz = calibration.getZ(1);
                unit = calibration.getUnit();
            }
            // save file
            BufferedWriter bw = new BufferedWriter(new FileWriter(dir + file));
            bw.write(sxy + ":" + sz + ":" + unit);
            bw.close();
        } catch (FileNotFoundException e) {
            IJ.log("Pb with file " + dir + file + " : " + e.getMessage());
        } catch (IOException e) {
            IJ.log("Pb with file " + dir + file + " : " + e.getMessage());
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Save the scale calibration  properties";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE};
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
