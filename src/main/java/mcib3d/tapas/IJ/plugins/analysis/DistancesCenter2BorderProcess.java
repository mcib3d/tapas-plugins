package mcib3d.tapas.IJ.plugins.analysis;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DistancesCenter2BorderProcess implements TapasProcessingIJ {
    private static final String DIRLABELS = "dirLabel";
    private static final String FILELABELS = "fileLabel";
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public DistancesCenter2BorderProcess() {
        parameters = new HashMap<>();
        parameters.put(FILE, "distancesCenter2Border.csv");
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
            case DIRLABELS:
                parameters.put(id, value);
                return true;
            case FILELABELS:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // read label image
        String dir = TapasBatchUtils.analyseDirName(getParameter(DIRLABELS));
        String file = TapasBatchUtils.analyseFileName(getParameter(FILELABELS), info);
        ImageHandler image = ImageHandler.wrap(IJ.openImage(dir + file));
        Objects3DPopulation population2 = new Objects3DPopulation(image);

        // get population 1
        ImageHandler imageHandler = ImageHandler.wrap(input);
        Objects3DPopulation population1 = new Objects3DPopulation(imageHandler);
        // get parameter
        String fileName = TapasBatchUtils.analyseDirName(getParameter(DIR)) + TapasBatchUtils.analyseFileName(getParameter(FILE), info);
        // create file a
        String delimiter = ",";
        BufferedWriter buf;
        if (!fileName.contains(".")) {
            fileName = fileName.concat(".csv");
        }
        try {
            buf = new BufferedWriter(new FileWriter(fileName));
            double[][] dist = computeDistancesAll(population1,population2);
            // headers
            buf.write("Obj");
            for (int i = 0; i < population2.getNbObjects(); i++) {
                buf.write(delimiter + "O" + population2.getObject(i).getValue());
            }
            buf.write("\n");
            // objects lines
            for (int i = 0; i < population1.getNbObjects(); i++) {
                buf.write("O" + population1.getObject(i).getValue());
                for (int j = 0; j < population2.getNbObjects(); j++) {
                    buf.write(delimiter + dist[i][j]);
                }
                buf.write("\n");
            }
            buf.close();
        } catch (FileNotFoundException ex) {
            IJ.log("No file " + fileName);
            return null;
        } catch (IOException ex) {
            IJ.log("Pb file " + fileName);
            return null;
        }

        return input.duplicate();
    }

    private double[][] computeDistancesAll(Objects3DPopulation population1, Objects3DPopulation population2) {
        double[][] distances = new double[population1.getNbObjects()][population2.getNbObjects()];
        for (int i = 0; i < population1.getNbObjects(); i++) {
            Object3D obj = population1.getObject(i);
            for (int j = 0; j < population2.getNbObjects(); j++) {
                double dist = obj.distCenterBorderUnit(population2.getObject(j));
                distances[i][j] = dist;
                //IJ.log("dist "+obj.getValue()+"-"+population2.getObject(j).getValue()+" "+dist);
            }
        }

        return distances;
    }

    @Override
    public String getName() {
        return "Distances all objects center-border for two images";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIRLABELS, FILELABELS, DIR, FILE};
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
