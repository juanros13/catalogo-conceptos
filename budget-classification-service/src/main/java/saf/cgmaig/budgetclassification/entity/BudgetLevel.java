package saf.cgmaig.budgetclassification.entity;

public enum BudgetLevel {
    CAPITULO(1, "Capítulo", "X000"),
    PARTIDA_GENERICA(2, "Partida Genérica", "XX00"),
    PARTIDA_ESPECIFICA(3, "Partida Específica", "XXX0"),
    PARTIDA(4, "Partida", "XXXX");

    private final int nivel;
    private final String descripcion;
    private final String patron;

    BudgetLevel(int nivel, String descripcion, String patron) {
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.patron = patron;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPatron() {
        return patron;
    }

    /**
     * Determina el nivel basado en el código presupuestario
     */
    public static BudgetLevel fromCode(String code) {
        if (code == null || code.length() != 4) {
            throw new IllegalArgumentException("Código debe tener exactamente 4 dígitos");
        }

        if (code.endsWith("000")) {
            return CAPITULO;
        } else if (code.endsWith("00")) {
            return PARTIDA_GENERICA;
        } else if (code.endsWith("0")) {
            return PARTIDA_ESPECIFICA;
        } else {
            return PARTIDA;
        }
    }

    /**
     * Valida si un código corresponde a este nivel
     */
    public boolean isValidCode(String code) {
        if (code == null || code.length() != 4) {
            return false;
        }

        return switch (this) {
            case CAPITULO -> code.endsWith("000");
            case PARTIDA_GENERICA -> code.endsWith("00") && !code.endsWith("000");
            case PARTIDA_ESPECIFICA -> code.endsWith("0") && !code.endsWith("00");
            case PARTIDA -> !code.endsWith("0");
        };
    }

    /**
     * Obtiene el código padre para este nivel
     */
    public String getParentCode(String code) {
        if (!isValidCode(code)) {
            throw new IllegalArgumentException("Código inválido para nivel " + this);
        }

        return switch (this) {
            case CAPITULO -> null; // Capítulos no tienen padre
            case PARTIDA_GENERICA -> code.substring(0, 1) + "000"; // 2100 -> 2000
            case PARTIDA_ESPECIFICA -> code.substring(0, 2) + "00"; // 2110 -> 2100
            case PARTIDA -> code.substring(0, 3) + "0"; // 2111 -> 2110
        };
    }
}