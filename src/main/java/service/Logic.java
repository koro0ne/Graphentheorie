package service;

import domain.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Logic {
    private Matrix adjazenz;
    private Matrix weg;
    private Matrix distanz;

    private boolean keinNeuerWeg;
    private boolean zusammenhaengend;
    private int[] exzentrizitaetenVektor;
    private List<int[]> komponenten;
    private int komponentenZaehler;

    public Logic() {
        adjazenz = new Matrix();
        weg = new Matrix();
        weg.setDiagonale(1);
        distanz = new Matrix();
        distanz.setValues(-1);
        distanz.setDiagonale(0);
    }

    public void wegDistanzBerechnung() {
        Matrix multiplyResult;
        int knoten = adjazenz.getKnoten();
        adjazenz.matrixUebertragen(weg, true);
        adjazenz.matrixUebertragen(distanz, false);
        weg.setDiagonale(1);
        distanz.setDiagonale(0);
        multiplyResult = adjazenz.multiplyMatrix(adjazenz);
        setValuesForWegmatrixAndDistanzmatrix(multiplyResult, 2);
        if (!keinNeuerWeg) {
            for (int n = 3; n < knoten; n++) {
                multiplyResult = multiplyResult.multiplyMatrix(adjazenz);
                setValuesForWegmatrixAndDistanzmatrix(multiplyResult, n);
            }
        }
    }

    public void setValuesForWegmatrixAndDistanzmatrix(Matrix multiplyResult, int n) {
        int knoten = adjazenz.getKnoten();
        keinNeuerWeg = true;
        for (int row = 0; row < knoten; row++) {
            for (int col = 0; col < knoten; col++) {
                if (weg.getValue(row, col) == 0) {
                    if (multiplyResult.getValue(row, col) > 0) {
                        weg.setValue(row, col, 1);
                        distanz.setValue(row, col, n);
                        keinNeuerWeg = false;
                    }
                }
            }
        }
    }

    public String getExzentrizitaeten() {
        int knoten = distanz.getKnoten();
        exzentrizitaetenVektor = new int[knoten];
        String result = "";
        zusammenhaengend = true;
        for (int row = 0; row < knoten; row++) {
            int max = -1;
            int min = 1000;
            int knotenAnzeigen = row + 1;
            for (int col = 0; col < knoten; col++) {
                if (min != -1) {
                    if (max <= distanz.getValue(row, col) && row != col) {
                        max = distanz.getValue(row, col);
                    }
                    if (min >= distanz.getValue(row, col) && row != col) {
                        min = distanz.getValue(row, col);
                    }
                }
            }
            if (min != -1) {
                result = result + "Knoten[" + knotenAnzeigen + "]:  " + max + "\n";
                exzentrizitaetenVektor[row] = max;
            } else {
                zusammenhaengend = false;
                result = result + "Knoten[" + knotenAnzeigen + "]:  " + "\u221E" + "\n";
            }
        }
        return result;
    }

    public int getRadius() {
        int knoten = distanz.getKnoten();
        int min = 1000;
        if (zusammenhaengend) {
            for (int i = 0; i < knoten; i++) {
                if (min >= exzentrizitaetenVektor[i])
                    min = exzentrizitaetenVektor[i];
            }
            return min;
        } else
            return -1;
    }

    public int getDurchmesser() {
        int knoten = distanz.getKnoten();
        int max = -1000;
        if (zusammenhaengend) {
            for (int i = 0; i < knoten; i++) {
                if (max <= exzentrizitaetenVektor[i])
                    max = exzentrizitaetenVektor[i];
            }
            return max;
        } else
            return -1;
    }

    public String getZentren() {
        int knoten = distanz.getKnoten();
        String zentren = "";
        int radius = getRadius();
        if (zusammenhaengend) {
            for (int i = 0; i < knoten; i++) {
                if (radius == exzentrizitaetenVektor[i]) {
                    int knotenzahl = i + 1;
                    zentren = zentren + knotenzahl + ", ";
                }
            }
            zentren = "[ " + zentren + "]";
            return zentren.replace(", ]", " ]");
        } else
            return "kein Zentrum!";
    }

    public List<int[]> getKomponenten() {
        komponenten = new ArrayList<>();
        komponentenZaehler = 0;
        int knoten = weg.getKnoten();
        for (int row = 0; row < knoten; row++) {
            int[] newRow = weg.getRow(row);
            for (int i = 0; i < knoten; i++) {
                if (newRow[i] == 1) {
                    newRow[i] = i + 1;
                }
            }
            if (!containsInList(komponenten, newRow)) {
                komponenten.add(newRow);
                komponentenZaehler++;
            }
        }
        return komponenten;
    }

    public String getBruecken() {
        int knoten = adjazenz.getKnoten();
        boolean brueckeFound = false;
        String result = "";
        int i = 0;
        for (int row = 0; row < knoten; row++) {
            for (int col = row + 1; col < knoten; col++) {
                if (adjazenz.getValue(row, col) == 1) {
                    wegDistanzBerechnung();
                    getKomponenten();
                    int before = komponentenZaehler;
                    adjazenz.setValue(row, col, 0);
                    wegDistanzBerechnung();
                    getKomponenten();
                    int after = komponentenZaehler;
                    if (before < after) {
                        int brueckeRow = row + 1;
                        int brueckeCol = col + 1;
                        if (i % 2 == 0) {
                            result = result + "[" + brueckeRow + ", " + brueckeCol + "] ";
                        }
                        if (i % 2 == 1) {
                            result = result + "[" + brueckeRow + ", " + brueckeCol + "]\n";
                        }
                        brueckeFound = true;
                        i++;
                    }
                    adjazenz.setValue(row, col, 1);
                }


            }
        }
        if (brueckeFound) {
            return result;
        } else
            return "keine Brücken!";
    }

    public String getArtikulationen() {
        int knoten = adjazenz.getKnoten();
        int temp = 0;
        boolean artikulationFound = false;
        String result = "";
        for (int row = 0; row < knoten; row++) {
            int[] newRow = adjazenz.getRow(row);
            for (int col = 0; col < knoten; col++) {
                if (adjazenz.getValue(row, col) == 1) {
                    wegDistanzBerechnung();
                    getKomponenten();
                    int before = komponentenZaehler;
                    for (int i = 0; i < knoten; i++) {
                        adjazenz.setValue(row, i, 0);
                        temp = i;
                    }
                    wegDistanzBerechnung();
                    getKomponenten();
                    int after = komponentenZaehler;
                    if (temp == knoten - 1 && before < after - 1) {
                        int artikulation = row + 1;
                        result = result + artikulation + ", ";
                        artikulationFound = true;
                    }
                }
            }
            for (int i = 0; i < newRow.length; i++) {
                if (newRow[i] == 1) {
                    adjazenz.setValue(row, i, 1);
                }

            }
        }
        if (artikulationFound) {
            result = "[ " + result + "]";
            return result.replace(", ]", " ]");
        } else
            return "keine Artikulationen!";
    }

    public boolean containsInList(List<int[]> list, int[] rowParam) {
        for (int[] row : list) {
            if (Arrays.equals(row, rowParam)) {
                return true;
            }
        }
        return false;
    }


    public String komponentenToString() {
        List<int[]> list = getKomponenten();
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            int komponente = i + 1;
            result = result + komponente + " " + getRowToString(list.get(i)).replace(", ]", " ]") + "\n";
        }
        return result;
    }

    public String getRowToString(int[] row) {
        String result = "[ ";
        for (int i = 0; i < row.length; i++) {
            if (row[i] != 0)
                result = result + row[i] + ", ";
        }
        result = result + "]";
        return result;
    }

    public Matrix getAdjazenz() {
        return adjazenz;
    }

    public Matrix getWeg() {
        return weg;
    }

    public Matrix getDistanz() {
        return distanz;
    }
}