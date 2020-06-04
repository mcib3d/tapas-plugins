package mcib3d.tapas.IJ.plugins.analysis;

import ij.ImagePlus;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.ImageLabeller;
import mcib3d.tapas.IJ.TapasProcessingIJ;
import mcib3d.tapas.core.ImageInfo;

public class KeepBiggestProcess implements TapasProcessingIJ {

    @Override
    public boolean setParameter(String id, String value) {
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // label
        ImageInt imageInt = ImageInt.wrap(input);
        ImageLabeller labeller = new ImageLabeller();
        ImageInt labels = labeller.getLabels(imageInt);
        // if no objects return
        Objects3DPopulation population = new Objects3DPopulation(labels);
        if (population.getNbObjects() == 0) return input.duplicate();
        // get biggest object
        Object3D object3DMax = population.getObject(0);
        int maxVol = object3DMax.getVolumePixels();
        for (int o = 1; o < population.getNbObjects(); o++) {
            Object3D object3D = population.getObject(o);
            int vol = object3D.getVolumePixels();
            if (vol > maxVol) {
                maxVol = vol;
                object3DMax = object3D;
            }
        }
        // draw biggest object in result
        ImageInt draw = new ImageByte("binary", imageInt.sizeX, imageInt.sizeY, imageInt.sizeZ);
        draw.setScale(imageInt);
        object3DMax.draw(draw, 255);

        return draw.getImagePlus();
    }

    @Override
    public String getName() {
        return "Keep biggest object";
    }

    @Override
    public String[] getParameters() {
        return new String[0];
    }

    public String getParameter(String id) {
        return null;
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
