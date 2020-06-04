package mcib3d.tapas.IJ.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import mcib3d.tapas.IJ.TapasProcessingIJ;

import mcib3d.tapas.core.*;

import java.io.File;
import java.util.HashMap;

public class SubProcess implements TapasProcessingIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public SubProcess() {
        parameters = new HashMap<>();
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

    @Override
    public ImagePlus execute(ImagePlus input) {
        // get information
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);

        File tapasFile = new File(IJ.getDirectory("imagej") + File.separator + "tapas.txt");
        String processFile=dir2+name2;

        // get processor
        TapasProcessorAbstract processor = TapasBatchProcess.getProcessor(processFile);
        IJ.log("Processing with "+processor.getNameProcessor());
        processor.init(TapasBatchProcess.readProcessings(processFile,TapasBatchProcess.readPluginsFile(tapasFile.getAbsolutePath(),false)));
        processor.processOneImage(info);

        ImageProcessor ip = new ByteProcessor(100, 100);
        ip.noise(50);
        return new ImagePlus("dummy", ip);
    }

    @Override
    public String getName() {
        return "Execute sub-process";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
