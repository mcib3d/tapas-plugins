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

public class DistancesCenterProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public DistancesCenterProcess() {
        parameters = new HashMap<>();
        parameters.put(FILE, "distancesCenter.csv");
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
        }
        return false;
    }

    private double[] computeDistancesAll(Objects3DPopulation population, Object3D ref) {
        double[] distances = new double[population.getNbObjects()];
        for (int i = 0; i < population.getNbObjects(); i++) {
            Object3D obj = population.getObject(i);
            distances[i] = ref.distCenterUnit(obj);
        }

        return distances;
    }


    @Override
    public ImagePlus execute(ImagePlus input) {
        // get population
        ImageHandler imageHandler = ImageHandler.wrap(input);
        Objects3DPopulation population = new Objects3DPopulation(imageHandler);
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
            // headers
            buf.write("Obj");
            for (int i = 0; i < population.getNbObjects(); i++) {
                buf.write(delimiter + "O" + population.getObject(i).getValue());
            }
            buf.write("\n");
            // objects lines
            for (int i = 0; i < population.getNbObjects(); i++) {
                buf.write("O" + population.getObject(i).getValue());
                double[] dist = computeDistancesAll(population, population.getObject(i));
                for (int d = 0; d < dist.length; d++) {
                    buf.write(delimiter + dist[d]);
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

    @Override
    public String getName() {
        return "Distances all objects center-center";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE};
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
