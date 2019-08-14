package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.OmeroConnect;

import java.io.File;
import java.util.HashMap;

public class OutputProcess implements TapasProcessing {
    public static final String PROJECT = "project";
    public static final String DATASET = "dataset";
    public static final String NAME = "name";

    HashMap<String, String> parameters;
    ImageInfo info;

    public OutputProcess() {
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
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
            case NAME:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // update final name
        String name = getParameter(NAME);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String project = getParameter(PROJECT);
        String project2 = TapasBatchProcess.analyseFileName(project, info);
        String dataset = getParameter(DATASET);
        String dataset2 = TapasBatchProcess.analyseFileName(dataset, info);
        // check if image is OMERO or files
        if (info.isFile()) {
            IJ.log("Saving to FILE");
            ImageInfo info2 = new ImageInfo(info.getRootDir(), project2, dataset2, name2, info.getC(), info.getT());
            String path2 = info2.getFilePath();
            // check if file exists
            File file = new File(path2);
            if (file.exists()) {
                IJ.log("File  " + path2 + " already exists, deleting");
                file.delete();
            }
            if (!saveFile(input, path2)) IJ.log("Pb saving " + path2);
            else IJ.log("Saved  " + path2);
        } else {
            // import into core
            try {
                IJ.log("Saving to OMERO : " + project2 + "/" + dataset2 + "/" + name2);
                // save temporary file
                String dirTmp = System.getProperty("java.io.tmpdir");
                String pathOmero = dirTmp + File.separator + name2;
                if (!saveFile(input, pathOmero)) IJ.log("Pb saving temp " + pathOmero);
                OmeroConnect connect = new OmeroConnect();
                connect.connect();
                connect.addImageToDataset(project2, dataset2, dirTmp + File.separator, name2);
                connect.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return input.duplicate();
    }

    private boolean saveFile(ImagePlus input, String path) {
        FileSaver saver = new FileSaver(input);
        boolean saveOk;
        if (input.getNSlices() > 1) {
            saveOk = saver.saveAsTiffStack(path);
        } else {
            saveOk = saver.saveAsTiff(path);
        }

        return saveOk;
    }

    @Override
    public String getName() {
        return "Output image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, NAME};
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
