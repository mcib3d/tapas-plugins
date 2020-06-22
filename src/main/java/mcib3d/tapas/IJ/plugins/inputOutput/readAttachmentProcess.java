package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchUtils;
import omero.gateway.model.FileAnnotationData;
import omero.gateway.model.ImageData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class readAttachmentProcess implements TapasProcessingIJ {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String IMAGE = "image";
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
        setParameter(IMAGE, "?image?");
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
            case "name": // deprecated
            case IMAGE:
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
        String project = TapasBatchUtils.analyseFileName(getParameter(PROJECT), info);
        String dataset = TapasBatchUtils.analyseFileName(getParameter(DATASET), info);
        String name = TapasBatchUtils.analyseFileName(getParameter(IMAGE), info);
        String attach = TapasBatchUtils.analyseFileName(getParameter(ATTACH), info);
        String fileName = TapasBatchUtils.analyseFileName(getParameter(FILE), info);
        String dir = TapasBatchUtils.analyseDirName(getParameter(DIR));
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
        return new String[]{PROJECT, DATASET, IMAGE, ATTACH, DIR, FILE, USERS};
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
