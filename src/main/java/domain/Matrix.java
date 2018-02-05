package domain;

import presentation.MainWindow;

import java.util.Arrays;
import java.util.List;

public class Matrix {

    private int[][] matrix;

    private int knoten;

    public Matrix() {
        knoten = MainWindow.getSize();
        if (knoten >= 3 && knoten <= 15) {
            this.matrix = new int[knoten][knoten];
        } else
            throw new IllegalArgumentException("Der Wert für das Attribut knoten befindet sich nicht im Bereich 3-15");

    }

    public void setValues(int value) {
        for (int row = 0; row < knoten; row++) {
            for (int col = 0; col < knoten; col++) {
                matrix[row][col] = value;
            }
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getKnoten() {
        return knoten;
    }

    public int getValue(int row, int col) {
        return matrix[row][col];
    }

    public int[] getRow(int row) {
        int[] extractRow = new int[knoten];
        for (int col = 0; col < knoten; col++) {
            extractRow[col] = getValue(row, col);
        }
        return extractRow;
    }

    public void setValue(int row, int col, int value) {
        matrix[row][col] = value;
        matrix[col][row] = value;
    }

    public void setDiagonale(int diagonal) {
        for (int row = 0; row < knoten; row++) {
            for (int col = 0; col < knoten; col++) {
                if (row == col)
                    setValue(row, col, diagonal);
            }
        }
    }

    public void addKante(int row, int col) {
        setValue(row, col, 1);
    }

    public void removeKante(int row, int col) {
        setValue(row, col, 0);
    }

    public Matrix multiplyMatrix(Matrix multiplyWith) {
        int[][] matrix = multiplyWith.getMatrix();
        Matrix result = new Matrix();
        for (int row = 0; row < knoten; row++) {
            for (int col = 0; col < knoten; col++) {
                int sum = 0;
                for (int i = 0; i < knoten; i++) {
                    sum += this.matrix[row][i] * matrix[i][col];
                }
                result.matrix[row][col] = sum;
            }
        }
        return result;
    }

    public void matrixUebertragen(Matrix matrix, boolean wegMatrix) {
        for (int row = 0; row < knoten; row++) {
            for (int col = 0; col < knoten; col++) {
                if (wegMatrix) {
                    matrix.setValue(row, col, getValue(row, col));
                } else {
                    if (getValue(row, col) != 0) {
                        matrix.setValue(row, col, getValue(row, col));
                    } else {
                        matrix.setValue(row, col, -1);
                    }
                }
            }
        }
    }
}