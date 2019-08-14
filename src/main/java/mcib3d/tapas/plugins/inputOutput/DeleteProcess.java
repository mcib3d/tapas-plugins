package mcib3d.tapas.plugins.inputOutput;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;

import java.io.File;
import java.util.HashMap;

public class DeleteProcess implements TapasProcessing {
    public static final String DIR = "dir";
    public static final String FILE = "file";
    HashMap<String, String> parameters;
    ImageInfo info;

    public DeleteProcess() {
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
        String name = getParameter(FILE);
        String dir = getParameter(DIR);
        String name2 = analyseFileName(name);
        String dir2 = analyseDirName(dir);
        File file = new File(dir2 + name2);
        if (file.exists()) {
            IJ.log("Deleting " + file.getPath());
            file.delete();
        }
        else {
            IJ.log("File "+ file.getPath()+" not found");
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Deleting file";
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

    private String analyseFileName(String s) {
        String file = new String(s);
        if (file.contains("?project?")) file = file.replace("?project?", info.getProject());
        if (file.contains("?dataset?")) file = file.replace("?dataset?", info.getDataset());
        if (file.contains("?name?")) file = file.replace("?name?", info.getImage());

        return file;
    }

    private String analyseDirName(String s) {
        String dir = new String(s);
        String home = System.getProperty("user.home");
        String ij = IJ.getDirectory("imagej");
        String tmp = System.getProperty("java.io.tmpdir");
        // we want these dir to NOT ends with /
        if (home.endsWith(File.separator)) home = home.substring(0, home.length() - 1);
        if (ij.endsWith(File.separator)) ij = ij.substring(0, ij.length() - 1);
        if (tmp.endsWith(File.separator)) tmp = tmp.substring(0, tmp.length() - 1);
        // but we want final dir name to ends with /
        if (dir.contains("?home?")) dir = dir.replace("?home?", home);
        if (dir.contains("?ij?")) dir = dir.replace("?ij?", ij);
        if (dir.contains("?tmp?")) dir = dir.replace("?tmp?", tmp);
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;

        return dir;
    }
}
