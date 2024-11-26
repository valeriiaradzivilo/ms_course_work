package additional;

public enum Distribution {
    EXP, NORM, UNIF, UNKNOWN;

    public static Distribution getDistribution(String distribution) {
        return switch (distribution) {
            case "exp" -> EXP;
            case "norm" -> NORM;
            case "unif" -> UNIF;
            default -> UNKNOWN;
        };
    }

    public String getName() {
        return switch (this) {
            case EXP -> "exp";
            case NORM -> "norm";
            case UNIF -> "unif";
            default -> "unknown";
        };
    }


}