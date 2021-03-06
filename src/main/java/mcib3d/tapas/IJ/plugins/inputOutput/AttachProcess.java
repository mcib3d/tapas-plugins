 package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class AttachProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String IMAGE = "image";

    HashMap<String, String> parameters;
    ImageInfo info;

    public AttachProcess() {
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(IMAGE, "?image?");
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
            case "name": // deprecated
            case IMAGE:
                parameters.put(id, value);
                return true;

        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // file
        String fileName = getParameter(FILE);
        String dir = getParameter(DIR);
        String nameF = TapasBatchUtils.analyseFileName(fileName, info);
        String dirF = TapasBatchUtils.analyseDirName(dir);
        // core
        String nameI = TapasBatchUtils.analyseFileName(getParameter(IMAGE), info);
        String project = TapasBatchUtils.analyseFileName(getParameter(PROJECT), info);
        String dataset = TapasBatchUtils.analyseFileName(getParameter(DATASET), info);

        IJ.log("Attaching " + dirF + nameF);
        File file = new File(dirF + nameF);
        if (!file.exists()) {
            IJ.log(file.getAbsolutePath() + " does not exists. Ignoring.");
            return input.duplicate();
        }

        // TODO replace with TapasBatchProcess attach function in next release
        if (!attach(info, file, project, dataset, nameI)) {
            IJ.log("Could not attach " + file.getName());
        }

        return input.duplicate();
    }

    private boolean attach(ImageInfo info, File file, String project, String dataset, String name) {
        boolean ok = false;
        if (info.isFile()) { // if file copy in same dataset directory
            ok = attachFiles(info, file, project, dataset);
        } else {
            ok = attachOMERO(file, project, dataset, name);
        }

        return ok;
    }

    private boolean attachFiles(ImageInfo info, File file, String project, String dataset) {
        String name = file.getName();
        String path = info.getRootDir() + project + File.separator + dataset + File.separator + name;
        // new 0.6.3, put in a folder "attachments"
        File attachFolder = new File(info.getRootDir() + project + File.separator + dataset + File.separator + "attachments" + File.separator);
        if (!attachFolder.exists()) {
            IJ.log("Creating folder " + attachFolder + " to store attachments");
            attachFolder.mkdir();
        }
        path = attachFolder.getPath() + File.separator + name;
        try {
            IJ.log("Attaching to FILES");
            File file2 = new File(path);
            // delete if exist
            if (file2.exists()) {
                IJ.log("File " + file2.getPath() + " exists. Overwriting");
                file2.delete();
            }
            Files.copy(file.toPath(), file2.toPath());
        } catch (IOException e) {
            IJ.log("Could not copy " + file.getPath() + " to " + path);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean attachOMERO(File file, String project, String dataset, String name) {
        try {
            IJ.log("Attaching to OMERO");
            OmeroConnect connect = new OmeroConnect();
            connect.setLog(false);
            connect.connect();
            connect.addFileAnnotation(connect.findOneImage(project, dataset, name, true), file);
            connect.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getName() {
        return "Attach results file to an image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, PROJECT, DATASET, IMAGE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
