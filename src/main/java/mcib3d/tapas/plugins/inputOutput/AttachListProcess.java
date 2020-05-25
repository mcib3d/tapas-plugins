package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        String dirF = TapasBatchUtils.analyseDirName(dir);
        // core
        String name = TapasBatchUtils.analyseFileName(getParameter(NAME), info);
        String project = TapasBatchUtils.analyseFileName(getParameter(PROJECT), info);
        String dataset = TapasBatchUtils.analyseFileName(getParameter(DATASET), info);
        try {
            // get list of files
            String[] files = parameters.get(LIST).split(",");
            for (String attachFile : files) {
                String nameF = TapasBatchUtils.analyseFileName(attachFile, info);
                File file = new File(dirF + nameF);
                IJ.log("Attaching " + dirF + nameF);
                if (!file.exists()) {
                    IJ.log(file.getAbsolutePath() + " does not exists. Ignoring.");
                    continue;
                }
                // check if omero or files34pp000002
                if (info.isFile()) { // if file copy in same dataset directory
                    boolean ok = attachFiles(file, info.getRootDir() + project + File.separator + dataset + File.separator + nameF);
                } else {
                    boolean ok = attachOMERO(file, project, dataset, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input.duplicate();
    }

    private boolean attachFiles(File file, String path) {
        try {
            IJ.log("Attaching to FILES : "+path);
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
