package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.OmeroConnect;

import java.io.File;
import java.util.HashMap;

public class AttachListProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String LIST = "list";
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String NAME = "name";

    HashMap<String, String> parameters;
    ImageInfo info;

    public AttachListProcess() {
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(NAME, "?name?");
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
        // dir
        String dir = getParameter(DIR);
        String dirF = TapasBatchProcess.analyseDirName(dir);
        // core
        String nameO = TapasBatchProcess.analyseFileName(getParameter(NAME), info);
        String project = TapasBatchProcess.analyseFileName(getParameter(PROJECT), info);
        String dataset = TapasBatchProcess.analyseFileName(getParameter(DATASET), info);
        try {
            // get list of files
            String[] files = parameters.get(LIST).split(",");
            OmeroConnect connect = new OmeroConnect();
            connect.setLog(false);
            connect.connect();
            for (String attachFile : files) {
                String nameF = TapasBatchProcess.analyseFileName(attachFile, info);
                IJ.log("Attaching " + dirF + nameF+" to OMERO");
                File file = new File(dirF + nameF);
                if (!file.exists()) IJ.log(file.getAbsolutePath() + " does not exists");
                connect.addFileAnnotation(connect.findOneImage(project, dataset, nameO, true), new File(dirF + nameF));
            }
            connect.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }


    @Override
    public String getName() {
        return "Attach list of files";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, LIST, PROJECT, DATASET, NAME};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
