package mcib3d.tapas.plugins.misc;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;

import java.io.*;
import java.util.HashMap;

public class ExecutableProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String ARG = "arg";

    HashMap<String, String> parameters;
    ImageInfo info;

    public ExecutableProcess() {
        parameters = new HashMap<>();
        parameters.put(DIR, "");
        parameters.put(ARG, "");
        info = new ImageInfo();
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
            case ARG:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        // analyse DIR and ARG
        String dir = parameters.get(DIR);
        String dir2 = TapasBatchProcess.analyseDirName(dir);
        String exe = parameters.get(FILE);
        String arg = parameters.get(ARG);
        String arg2 = TapasBatchProcess.analyseStringKeywords(arg, info);
        try {
            // create script file
            String tmpExe = "";
            if (IJ.isWindows()) tmpExe = System.getProperty("user.home") + File.separator + "tmpExeTapas.bat";
            if (IJ.isLinux()) tmpExe = System.getProperty("user.home") + File.separator + "tmpExeTapas.sh";
            if (IJ.isMacOSX()) tmpExe = System.getProperty("user.home") + File.separator + "tmpExeTapas.command";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmpExe));
            bufferedWriter.write("\""+dir2 + exe + "\" " + arg2);
            bufferedWriter.close();

            // Process
            ProcessBuilder pb = null;
            if (IJ.isLinux()) {
                pb = new ProcessBuilder("sh", tmpExe);
            }
            if (IJ.isWindows()) {
                pb = new ProcessBuilder(tmpExe);
            }
            if (IJ.isMacOSX()) {
                pb = new ProcessBuilder(tmpExe);
            }

            // TEST
            final Process pTest = pb.start();
            BufferedReader brtest = new BufferedReader(new InputStreamReader(pTest.getInputStream()));
            String line;
            while ((line = brtest.readLine()) != null) {
                System.out.println(line);
            }
            if (pTest.waitFor() == 0)
                IJ.log("Process "+exe+" terminated correctly");
            else
                IJ.log("Process "+exe+" terminated in error");
            // TEST


            //if (p.waitFor() != 0) {
            //   IJ.log("Pb with exe (0) " + parameters.get(FILE));
            //}
        } catch (IOException e) {
            IJ.log("Pb with exe (1) " + parameters.get(FILE) + " " + e.getMessage());
        } catch (InterruptedException e) {
            IJ.log("Pb with exe (2) " + parameters.get(FILE));
        }

        return input.duplicate();
    }

    @Override
    public String getName() {
        return "Executable file";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, ARG};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
