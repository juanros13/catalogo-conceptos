package saf.cgmaig.budgetclassification.dto;

import java.util.List;

/**
 * DTO especializado para respuestas jerárquicas completas
 * Útil para cargar árboles completos de clasificación presupuestaria
 */
public class HierarchyResponse {

    private String codigoRaiz;
    private String nombreRaiz;
    private List<BudgetClassificationResponse> jerarquia;
    private int totalNodos;
    private int profundidadMaxima;

    // Constructor vacío
    public HierarchyResponse() {}

    // Constructor con parámetros
    public HierarchyResponse(String codigoRaiz, String nombreRaiz, 
                           List<BudgetClassificationResponse> jerarquia) {
        this.codigoRaiz = codigoRaiz;
        this.nombreRaiz = nombreRaiz;
        this.jerarquia = jerarquia;
        this.totalNodos = contarNodos(jerarquia);
        this.profundidadMaxima = calcularProfundidad(jerarquia, 0);
    }

    private int contarNodos(List<BudgetClassificationResponse> nodos) {
        if (nodos == null || nodos.isEmpty()) {
            return 0;
        }
        
        int total = nodos.size();
        for (BudgetClassificationResponse nodo : nodos) {
            total += contarNodos(nodo.getHijos());
        }
        return total;
    }

    private int calcularProfundidad(List<BudgetClassificationResponse> nodos, int nivelActual) {
        if (nodos == null || nodos.isEmpty()) {
            return nivelActual;
        }
        
        int maxProfundidad = nivelActual + 1;
        for (BudgetClassificationResponse nodo : nodos) {
            int profundidadHijo = calcularProfundidad(nodo.getHijos(), nivelActual + 1);
            maxProfundidad = Math.max(maxProfundidad, profundidadHijo);
        }
        return maxProfundidad;
    }

    // Getters y Setters
    public String getCodigoRaiz() {
        return codigoRaiz;
    }

    public void setCodigoRaiz(String codigoRaiz) {
        this.codigoRaiz = codigoRaiz;
    }

    public String getNombreRaiz() {
        return nombreRaiz;
    }

    public void setNombreRaiz(String nombreRaiz) {
        this.nombreRaiz = nombreRaiz;
    }

    public List<BudgetClassificationResponse> getJerarquia() {
        return jerarquia;
    }

    public void setJerarquia(List<BudgetClassificationResponse> jerarquia) {
        this.jerarquia = jerarquia;
        this.totalNodos = contarNodos(jerarquia);
        this.profundidadMaxima = calcularProfundidad(jerarquia, 0);
    }

    public int getTotalNodos() {
        return totalNodos;
    }

    public void setTotalNodos(int totalNodos) {
        this.totalNodos = totalNodos;
    }

    public int getProfundidadMaxima() {
        return profundidadMaxima;
    }

    public void setProfundidadMaxima(int profundidadMaxima) {
        this.profundidadMaxima = profundidadMaxima;
    }
}