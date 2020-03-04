package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchProcess;
import omero.gateway.model.FileAnnotationData;
import omero.gateway.model.ImageData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class readAttachmentProcess implements TapasProcessing {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String NAME = "name";
    private static final String ATTACH = "attachment";
    private static final String DIR = "dir";
    private static final String FILE = "file";
    final static private String USERS = "user";

    ImageInfo info;
    HashMap<String, String> parameters;

    public readAttachmentProcess() {
        info = new ImageInfo();
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(NAME, "?name?");
        setParameter(USERS, "-");
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
            case ATTACH:
                parameters.put(id, value);
                return true;
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;
            case USERS:
                parameters.put(id, value);
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get parameters
        String project = TapasBatchProcess.analyseFileName(getParameter(PROJECT), info);
        String dataset = TapasBatchProcess.analyseFileName(getParameter(DATASET), info);
        String name = TapasBatchProcess.analyseFileName(getParameter(NAME), info);
        String attach = TapasBatchProcess.analyseFileName(getParameter(ATTACH), info);
        String fileName = TapasBatchProcess.analyseFileName(getParameter(FILE), info);
        String dir = TapasBatchProcess.analyseDirName(getParameter(DIR));
        // users
        ArrayList<String> addUsers = null;
        String users = getParameter(USERS);
        if ((!users.equalsIgnoreCase("-")) && (!users.isEmpty())) {
            addUsers = new ArrayList<>();
            String[] all = users.split(",");
            for (String S : all) {
                addUsers.add(S.trim());
            }
        }
        // if not on omero mode exit
        if (info.isFile()) {
            IJ.log("Not using TAPAS OMERO, nothing to do");
            return input.duplicate();
        }
        // connect
        OmeroConnect omero = new OmeroConnect();
        try {
            omero.connect();
            ImageData image = omero.findOneImage(project, dataset, name, true);
            if (image == null) {
                IJ.log("Cannot find image " + project + " / " + dataset + " / " + name);
                return input.duplicate();
            }
            FileAnnotationData annotation = omero.getFileAnnotation(image, attach, addUsers);
            if (annotation == null) {
                IJ.log("Cannot find attachment " + attach);
                return input.duplicate();
            }
            IJ.log("Reading attachment " + attach);
            File file = omero.readAttachment(new ImageInfo(project, dataset, name), attach, null);
            omero.disconnect();
        } catch (Exception e) {
            IJ.log("Pb reading attachment");
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Read an OMERO attachment and save it locally.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, NAME, ATTACH, DIR, FILE, USERS};
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
