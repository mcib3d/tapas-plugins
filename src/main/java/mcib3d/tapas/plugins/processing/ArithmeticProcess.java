package mcib3d.tapas.plugins.processing;

import ij.ImagePlus;
import mcib3d.tapas.TapasProcessing;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.processing.FastArithmetic3D;
import mcib3d.utils.Logger.NoLog;

import java.util.HashMap;

public class ArithmeticProcess implements TapasProcessing {
    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String OP = "operation";
    private static final String COEF0 = "coef0";
    private static final String COEF1 = "coef1";

    HashMap<String, String> parameters;
    ImageInfo info;

    public ArithmeticProcess() {
        parameters = new HashMap<>();
        parameters.put(COEF0, "1.0");
        parameters.put(COEF1, "1.0");
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
            case COEF0:
                parameters.put(id, value);
                return true;
            case COEF1:
                parameters.put(id, value);
                return true;
            case OP:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ImagePlus execute(ImagePlus input) {
        ImagePlus plus = TapasBatchProcess.getImageFromFileParameters(parameters.get(DIR), parameters.get(FILE), info);
        if (plus == null) return null;
        ImageInt img = ImageInt.wrap(plus);
        // operation
        int op = 0;
        switch (parameters.get(OP)) {
            case "add":
                op = FastArithmetic3D.ADD;
                break;
            case "mult":
                op = FastArithmetic3D.MULT;
                break;
            case "max":
                op = FastArithmetic3D.MAX;
                break;
            case "min":
                op = FastArithmetic3D.MIN;
                break;
            case "diff":
                op = FastArithmetic3D.DIFF;
                break;
        }
        if (op == 0) return null;
        // coeffs
        float coef0 = Float.parseFloat(parameters.get(COEF0));
        float coef1 = Float.parseFloat(parameters.get(COEF1));
        ImageInt res = FastArithmetic3D.mathIntImage(ImageInt.wrap(input), img, op, coef0, coef1, 0, false, new NoLog());

        return res.getImagePlus();
    }


    @Override
    public String getName() {
        return "Arithmetic operation";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, OP, COEF0, COEF1};
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
