package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class AttachProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String NAME = "name";

    HashMap<String, String> parameters;
    ImageInfo info;

    public AttachProcess() {
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
            case FILE:
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
        // file
        String name = getParameter(FILE);
        String dir = getParameter(DIR);
        String nameF = TapasBatchUtils.analyseFileName(name, info);
        String dirF = TapasBatchUtils.analyseDirName(dir);
        // core
        String nameO = TapasBatchUtils.analyseFileName(getParameter(NAME), info);
        String project = TapasBatchUtils.analyseFileName(getParameter(PROJECT), info);
        String dataset = TapasBatchUtils.analyseFileName(getParameter(DATASET), info);

        IJ.log("Attaching " + dirF + nameF);
        File file = new File(dirF + nameF);
        if (!file.exists()) {
            IJ.log(file.getAbsolutePath() + " does not exists. Ignoring.");
            return input.duplicate();
        }
        // check if core or files
        if (info.isFile()) { // if file copy in same dataset directory
            try {
                String path2 = info.getRootDir() + project + File.separator + dataset + File.separator + nameF;
                IJ.log("Attaching to FILE");
                File file2 = new File(path2);
                // delete if exist
                if (file2.exists()) {
                    IJ.log("File " + file2.getPath() + " exists. Overwriting");
                    file2.delete();
                }
                Files.copy(file.toPath(), file2.toPath());
            } catch (IOException e) {
                IJ.log("Could not copy " + file.getPath() + " to " + info.getRootDir() + project + File.separator + dataset + File.separator + name);
                e.printStackTrace();
            }
        } else {
            try {
                IJ.log("Attaching to OMERO");
                OmeroConnect connect = new OmeroConnect();
                connect.setLog(false);
                connect.connect();
                connect.addFileAnnotation(connect.findOneImage(project, dataset, nameO, true), new File(dirF + nameF));
                connect.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return input.duplicate();
    }


    @Override
    public String getName() {
        return "Attach";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, PROJECT, DATASET, NAME};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
