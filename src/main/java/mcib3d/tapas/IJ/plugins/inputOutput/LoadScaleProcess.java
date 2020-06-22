package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchUtils;
import omero.gateway.model.ImageData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LoadScaleProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String IMAGE = "image";


    HashMap<String, String> parameters;
    ImageInfo info;

    public LoadScaleProcess() {
        parameters = new HashMap<>();
        setParameter(FILE, "scale.txt");
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(IMAGE, "?image?");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case PROJECT:
                parameters.put(id, value);
                return true;
            case DATASET:
                parameters.put(id, value);
                return true;
            case "name": // deprecated
            case IMAGE:
                parameters.put(id, value);
                return true;
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
        // get the calibration file
        String dir = TapasBatchUtils.analyseDirName(getParameter(DIR));
        String file = TapasBatchUtils.analyseFileName(getParameter(FILE), info);
        // get the image data
        String project2 = TapasBatchUtils.analyseFileName(getParameter(PROJECT), info);
        String dataset2 = TapasBatchUtils.analyseFileName(getParameter(DATASET), info);
        String name2 = TapasBatchUtils.analyseFileName(getParameter(IMAGE), info);
        // get calibration
        Calibration calibration = input.getCalibration();
        double sxy = 1;
        double sz = 1;
        String unit = "pix";
        try {
            // load file
            BufferedReader bw = new BufferedReader(new FileReader(dir + file));
            String[] data = bw.readLine().split(":");
            sxy = Double.parseDouble(data[0]);
            sz = Double.parseDouble(data[1]);
            unit = data[2];
            bw.close();
        } catch (FileNotFoundException e) {
            IJ.log("Pb with file " + dir + file + " : " + e.getMessage());
        } catch (IOException e) {
            IJ.log("Pb with file " + dir + file + " : " + e.getMessage());
        }
        // set calibration to omero
        if (info.isOmero()) {
            try {
                OmeroConnect connect = new OmeroConnect();
                connect.connect();
                ImageData imageData = connect.findOneImage(project2, dataset2, name2, true);
                if (imageData != null) {
                    if (connect.setResolutionImageUM(imageData, sxy, sz)) {
                        IJ.log("Set calibration to " + sxy + " " + sz);
                    } else {
                        IJ.log("Pb to set calibration");
                    }
                }
                connect.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // set calibration to result
        if (calibration == null) calibration = new Calibration();
        calibration.pixelWidth = sxy;
        calibration.pixelHeight = sxy;
        calibration.pixelDepth = sz;
        calibration.setUnit(unit);

        ImagePlus result = input.duplicate();
        result.setCalibration(calibration);
        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Load the scale calibration properties and apply it";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, PROJECT, DATASET, IMAGE};
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
