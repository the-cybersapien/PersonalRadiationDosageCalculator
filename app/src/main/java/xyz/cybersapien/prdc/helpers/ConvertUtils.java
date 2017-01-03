package xyz.cybersapien.prdc.helpers;

/**
 * Created by ogcybersapien on 12/30/2016.
 * Class containing all the helper methods for the Radiation Units
 * Convertor
 */

public class ConvertUtils {

    /**
     * Public Constants for different powers of 10
     * for conversions
     */
    public static final Double GIGA = Math.pow(10, 9);
    public static final Double MEGA = Math.pow(10, 6);
    public static final Double KILO = Math.pow(10, 3);
    public static final Double CENTI = Math.pow(10, -2);
    public static final Double MILLI = Math.pow(10, -3);
    public static final Double MICRO = Math.pow(10, -6);
    public static final Double NANO = Math.pow(10, -9);

    public static final int RAD = 100;
    public static final int GRAY = 101;
    public static final int REM = 102;
    public static final int SIEVERT = 103;
    public static final int ROENTGEN = 104;
    public static final int COULOMB_PER_KILOGRAM = 105;

    public static Double convertRem(Double rem, int choice){
        switch (choice){
            case RAD:
            case REM:
                return rem;
            case SIEVERT:
            case GRAY:
                return rem / 100;
            case ROENTGEN:
                return rem * 1.14;
            case COULOMB_PER_KILOGRAM:
                return rem * 0.0002942;
            default:
                return Double.NaN;
        }
    }

    public static Double convertRad(Double rad, int choice){
        switch (choice){
            case REM:
            case RAD:
                return rad;
            case SIEVERT:
            case GRAY:
                return rad / 100;
            case ROENTGEN:
                return rad * 1.14;
            case COULOMB_PER_KILOGRAM:
                return rad * 0.0002942;
            default:
                return Double.NaN;
        }
    }

    public static Double convertGray(Double gray, int choice){
        switch (choice){
            case RAD:
            case REM:
                return gray * 100;
            case SIEVERT:
            case GRAY:
                return gray;
            case ROENTGEN:
                return gray * 114;
            case COULOMB_PER_KILOGRAM:
                return gray * 0.02942;
            default:
                return Double.NaN;
        }
    }

    public static Double convertSievert(Double sievert, int choice){
        switch (choice){
            case RAD:
            case REM:
                return sievert * 100;
            case GRAY:
            case SIEVERT:
                return sievert;
            case ROENTGEN:
                return sievert * 114;
            case COULOMB_PER_KILOGRAM:
                return sievert * 0.02942;
            default:
                return Double.NaN;
        }
    }

    public static Double convertRoentgen(Double roentgen, int choice){
        switch (choice){
            case RAD:
            case REM:
                return roentgen * 0.877;
            case GRAY:
            case SIEVERT:
                return roentgen * .00877;
            case ROENTGEN:
                return roentgen;
            case COULOMB_PER_KILOGRAM:
                return roentgen * 0.000258;
            default:
                return Double.NaN;
        }
    }

    public static Double convertCoulombPerKg(Double CpKg, int choice){
        switch (choice){
            case RAD:
            case REM:
                return CpKg * 3400;
            case GRAY:
            case SIEVERT:
                return CpKg * 34;
            case ROENTGEN:
                return CpKg * 3876;
            case COULOMB_PER_KILOGRAM:
                return CpKg;
            default:
                return Double.NaN;
        }
    }
}
