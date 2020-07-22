package mcib3d.tapas.IJ.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.OmeroConnect;
import mcib3d.tapas.core.TapasBatchUtils;
import omero.gateway.model.ImageData;

import java.util.HashMap;

public class InputProcessCropping implements TapasProcessingIJ {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String IMAGE = "image";
    private static final String CHANNEL = "channel";
    private static final String FRAME = "frame";
    // cropping
    private static final String STARTXYZ = "startXYZ"; // list startx, starty, startz
    private static final String SIZEXYZ = "sizeXYZ"; // list sizex, sizey, sizez

    ImageInfo info;
    HashMap<String, String> parameters;

    public InputProcessCropping() {
        info = new ImageInfo();
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(IMAGE, "?image?");
        setParameter(CHANNEL, "?channel?");
        setParameter(FRAME, "?frame?");
        setParameter(STARTXYZ,"0,0,0");
        setParameter(SIZEXYZ,"100,0,0");
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
            case CHANNEL:
                parameters.put(id, value);
                return true;
            case FRAME:
                parameters.put(id, value);
                return true;
            case STARTXYZ:
                parameters.put(id, value);
                return true;
            case SIZEXYZ:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = getParameter(IMAGE);
        String project = getParameter(PROJECT);
        String dataset = getParameter(DATASET);
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        ImageHandler output = null;

        // core input
        int c = TapasBatchUtils.analyseChannelFrameName(parameters.get(CHANNEL), info);
        int t = TapasBatchUtils.analyseChannelFrameName(parameters.get(FRAME), info);
        // cropping
        String start = getParameter(STARTXYZ);
        String[] startxyz = start.split(",");
        int startX = 0;
        int startY = 0;
        int startZ = 0;
        if(startxyz.length != 3) {
            IJ.log("Pb with cropping start parameters, should be startX,startY, startZ. Using default values 0,0,0.");
        }
        else {
            startX = Integer.parseInt(startxyz[0].trim());
            startY = Integer.parseInt(startxyz[1].trim());
            startZ = Integer.parseInt(startxyz[2].trim());
        }
        String size = getParameter(SIZEXYZ);
        String[] sizeXYZ = size.split(",");
        int sizeX = 100;
        int sizeY = 100;
        int sizeZ = 10;
        if(sizeXYZ.length != 3) {
            IJ.log("Pb with cropping sie parameters, should be sizeX,sizeY,sizeZ. Using default values 100,100,10.");
        }
        else {
            sizeX = Integer.parseInt(sizeXYZ[0].trim());
            sizeY = Integer.parseInt(sizeXYZ[1].trim());
            sizeZ = Integer.parseInt(sizeXYZ[2].trim());
        }
        int endX = startX + sizeX;
        int endY = startY + sizeY;
        int endZ = startZ + sizeZ;
        try {
            OmeroConnect connect = new OmeroConnect();
            connect.connect();
            ImageData imageData = connect.findOneImage(project2, dataset2, name2, true);
            if (imageData == null) {
                IJ.log("Cannot find " + project2 + " / " + dataset2 + " / " + name2);
                return null;
            }

            IJ.log("Loading from OMERO " + imageData.getName() + " c-" + c + " t-" + t);
            output = connect.getImageXYZ(imageData, t, c, 1, 1, startX, endX, startY, endY, startZ, endZ);
            connect.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.getImagePlus();


    }


    @Override
    public String getName() {
        return "Input from OMERO with cropping";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, IMAGE, FRAME, CHANNEL, STARTXYZ, SIZEXYZ};
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
