package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.util.StringSorter;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.io.File;
import java.util.HashMap;

public class SequenceProcess implements TapasProcessing {
    private static String[] excludedTypes = {".txt", ".lut", ".roi", ".pty", ".hdr", ".java", ".ijm", ".py", ".js", ".bsh", ".xml"};// from FolderOpener
    private static final String DIR = "dir";
    private static final String NAME = "filename";
    private static final String DIMENSION = "dimension"; // dimension Z or T

    HashMap<String, String> parameters;
    ImageInfo info;

    public SequenceProcess() {
        parameters = new HashMap<>();
        setParameter(NAME, "*");
        setParameter(DIMENSION, "Z");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case NAME:
                parameters.put(id, value);
                return true;
            case DIMENSION:
                parameters.put(id, value);
                return true;
        }

        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        String name = parameters.get(NAME);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchProcess.analyseFileName(name, info);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        // Directory list
        String[] files;
        File dirf = new File(dir);
        dirf.list();
        if ((name2.equalsIgnoreCase("*")) || (name2.isEmpty())) {
            files = dirf.list();
        } else {
            files = dirf.list((directory, nameFile) -> nameFile.contains(name2));
        }
        if (files.length == 0) IJ.log("Cannot files with name matching " + name2);
        files = trimFileList(files);
        // sort files
        files = StringSorter.sortNumerically(files);
        // Open images and create a stack
        IJ.log("Opening " + files.length + " files");
        // read first image
        ImagePlus tmp = IJ.openImage(dir2 + files[0]);
        ImageStack stack = new ImageStack(tmp.getWidth(), tmp.getHeight());
        stack.addSlice(tmp.getProcessor());
        for (int i = 1; i < files.length; i++) {
            stack.addSlice(IJ.openImage(dir2 + files[i]).getProcessor()); // FIXME pb with multi-channels images
        }
        ImagePlus plus = new ImagePlus("Sequence", stack);
        if (plus == null) return null;
        // check dimension, by default in Z
        if (getParameter(DIMENSION).equalsIgnoreCase("T")) {
            int n = plus.getNSlices();
            plus.setDimensions(1, 1, n);
        }

        return plus;
    }


    /**
     * Removes names that start with "." or end with ".db", ".txt", ".lut", "roi", ".pty", ".hdr", ".py", etc.
     */
    // from FolderOpener
    public String[] trimFileList(String[] rawlist) {
        int count = 0;
        for (int i = 0; i < rawlist.length; i++) {
            String name = rawlist[i];
            if (name.startsWith(".") || name.equals("Thumbs.db") || excludedFileType(name))
                rawlist[i] = null;
            else
                count++;
        }
        if (count == 0) return null;
        String[] list = rawlist;
        if (count < rawlist.length) {
            list = new String[count];
            int index = 0;
            for (int i = 0; i < rawlist.length; i++) {
                if (rawlist[i] != null)
                    list[index++] = rawlist[i];
            }
        }
        return list;
    }

    // from FolderOpener
    /* Returns true if 'name' ends with ".txt", ".lut", ".roi", ".pty", ".hdr", ".java", ".ijm", ".py", ".js" or ".bsh. */
    public static boolean excludedFileType(String name) {
        if (name == null) return true;
        for (int i = 0; i < excludedTypes.length; i++) {
            if (name.endsWith(excludedTypes[i]))
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Loading sequence of images from a directory";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, NAME};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

}
