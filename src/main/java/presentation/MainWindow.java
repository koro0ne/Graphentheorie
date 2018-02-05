package presentation;

import domain.Matrix;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import service.Logic;


public class MainWindow extends Application {

    private Stage theStage;

    private Scene scene1;
    private Scene scene2;

    private BorderPane firstPane;

    private GridPane gridMatrix;
    private GridPane gridWegMatrix;
    private GridPane gridDistanzMatrix;
    private GridPane mainGrid;
    private GridPane gridOtherResultsC1;
    private GridPane gridOtherResultsC2;
    private GridPane gridOtherResultsC3;

    private static int size;

    private TextField tfSize;

    private Button go;
    private Button back;
    private AdjazenzButton bt;
    private AdjazenzButton[][] buttons;

    private Label lb;
    private Label[][] wegLabels;
    private Label[][] distanzLabels;
    private Label[][] otherResultsLabels;

    private Group adjazenzButtonGroup;
    private Group wegLabelGroup;
    private Group distanzLabelGroup;

    private Logic logic;

    private Matrix matrix;
    private Matrix wegMatrix;
    private Matrix distanzMatrix;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Use Stage in other Methods
        theStage = primaryStage;
        generateItemsForFirstScene();
        theStage.resizableProperty().setValue(false);
        theStage.show();
    }

    private void generateItemsForFirstScene() {
        lb = new Label("Anzahl der Knoten");
        lb.setTextFill(Color.web("#000000"));

        tfSize = new TextField();
        tfSize.setAlignment(Pos.CENTER);
        tfSize.setMaxWidth(50);
        tfSize.setText("3");
        tfSize.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                generateItemsAndSwitchScene();
            }
        });

        go = new Button();
        go.setText("Go!");
        go.setOnAction(event -> generateItemsAndSwitchScene());

        firstPane = new BorderPane();
        firstPane.setAlignment(lb, Pos.BOTTOM_CENTER);
        firstPane.setTop(lb);
        firstPane.setAlignment(tfSize, Pos.CENTER);
        firstPane.setCenter(tfSize);
        firstPane.setAlignment(go, Pos.CENTER);
        firstPane.setBottom(go);
        firstPane.setStyle("-fx-padding: 10;");

        scene1 = new Scene(firstPane, 150, 100);

        theStage.setTitle("Berger Programm - Graphentheorie");
        theStage.setScene(scene1);
    }

    private void switchToFirstScene() {
        generateItemsForFirstScene();
        theStage.setScene(scene1);
        theStage.centerOnScreen();
    }

    private void generateItemsAndSwitchScene() {
        mainGrid = new GridPane();
        gridMatrix = new GridPane();
        gridMatrix.setStyle("-fx-padding: 10;-fx-spacing: 10;");

        setColumnConstraints(33.3, mainGrid);
        setColumnConstraints(33.3, mainGrid);
        setColumnConstraints(33.3, mainGrid);

        generateButtons();

        Label adjLb = new Label("Adjazenzmatrix");
        Label wegLb = new Label("Wegmatrix");
        Label disLb = new Label("Distanzmatrix");

        wegLabelGroup = new Group();
        wegLabels = new Label[size][size];
        gridWegMatrix = new GridPane();
        gridWegMatrix.setStyle("-fx-padding: 10;-fx-spacing: 10;");

        distanzLabelGroup = new Group();
        distanzLabels = new Label[size][size];
        gridDistanzMatrix = new GridPane();
        gridDistanzMatrix.setStyle("-fx-padding: 10;-fx-spacing: 10;");

        otherResultsLabels = new Label[5][6];

        gridOtherResultsC1 = new GridPane();
        gridOtherResultsC2 = new GridPane();
        gridOtherResultsC3 = new GridPane();
        gridOtherResultsC1.setStyle("-fx-padding: 15;");
        gridOtherResultsC2.setStyle("-fx-padding: 15;");
        gridOtherResultsC3.setStyle("-fx-padding: 15;");

        mainGrid.setStyle("-fx-padding: 15;");

        addItemsToGrid(mainGrid, adjLb, 0, 0);
        addItemsToGrid(mainGrid, wegLb, 0, 1);
        addItemsToGrid(mainGrid, disLb, 0, 2);
        mainGrid.setHalignment(adjLb, HPos.CENTER);
        mainGrid.setHalignment(wegLb, HPos.CENTER);
        mainGrid.setHalignment(disLb, HPos.CENTER);

        logic = new Logic();
        logic.wegDistanzBerechnung();

        matrix = logic.getAdjazenz();
        wegMatrix = logic.getWeg();
        distanzMatrix = logic.getDistanz();

        generateLabels(wegLabelGroup, wegLabels, gridWegMatrix, 1, 1, wegMatrix);
        generateLabels(distanzLabelGroup, distanzLabels, gridDistanzMatrix, 1, 2, distanzMatrix);
        generateOtherResults();

        scene2 = new Scene(mainGrid);
        theStage.centerOnScreen();
        theStage.setScene(scene2);
        theStage.sizeToScene();
        theStage.centerOnScreen();
    }


    private void generateButtons() {
        adjazenzButtonGroup = new Group();
        getSizeFromTextfield();
        buttons = new AdjazenzButton[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // new Button
                bt = new AdjazenzButton("0", row, col);
                bt.setAlignment(Pos.CENTER);
                bt.setPrefHeight(28);
                bt.setPrefWidth(28);
                buttons[bt.row][bt.col] = bt;
                bt.setOnAction(event -> clickAdjazenz(event));
                if (row == col) {
                    bt.setDisable(true);
                }
                addItemsToGrid(gridMatrix, bt, row, col);
            }
        }
        mainGrid.setRowIndex(adjazenzButtonGroup, 1);
        mainGrid.setColumnIndex(adjazenzButtonGroup, 0);
        mainGrid.setHalignment(adjazenzButtonGroup, HPos.CENTER);
        addGroupToMainGrid(adjazenzButtonGroup, gridMatrix);
    }

    private void generateLabels(Group group, Label[][] labels, GridPane grid, int rowIndex, int colIndex, Matrix matrix) {

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                lb = new Label(String.valueOf(matrix.getValue(row, col)));
                lb.setAlignment(Pos.CENTER);
                lb.setPrefWidth(28);
                lb.setPrefHeight(28);
                lb.setStyle("-fx-border-color: darkgrey;");
                labels[row][col] = lb;
                if (row == col) {
                    lb.setStyle("-fx-background-color: burlywood;-fx-border-color: darkgrey;");
                }
                addItemsToGrid(grid, lb, row, col);
            }
        }
        mainGrid.setRowIndex(group, rowIndex);
        mainGrid.setColumnIndex(group, colIndex);
        mainGrid.setHalignment(group, HPos.CENTER);
        addGroupToMainGrid(group, grid);
        updateLabels(labels, matrix);
    }

    private void generateOtherResults() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 6; col++) {
                lb = new Label();

                lb.setStyle("-fx-border-color: darkgrey;");

                if (row == 0 && col == 0) {
                    lb.setText(" Komponenten ");
                    //lb.setPrefHeight(300);
                    addItemsToGrid(gridOtherResultsC1, lb, row, 0);
                }
                if (row == 0 && col == 1) {
                    lb.setText(logic.getKomponentenToString());
                    //lb.setPrefHeight(300);
                    lb.setAlignment(Pos.TOP_CENTER);
                    addItemsToGrid(gridOtherResultsC1, lb, 0, 1);
                }
                if (row == 0 && col == 2) {
                    lb.setText(" Exzentrizitäten ");
                    addItemsToGrid(gridOtherResultsC2, lb, 0, 0);
                }
                if (row == 0 && col == 3) {
                    lb.setText(logic.getExzentrizitaeten());
                    addItemsToGrid(gridOtherResultsC2, lb, 0, 1);
                }
                if (row == 0 && col == 4) {
                    lb.setText(" Zentrum ");
                    addItemsToGrid(gridOtherResultsC3, lb, 0, 0);
                }
                if (row == 0 && col == 5) {
                    lb.setText("kein Zentrum!");
                    addItemsToGrid(gridOtherResultsC3, lb, 0, 1);
                }


                if (row == 1 && col == 4) {
                    lb.setText(" Radius ");
                    addItemsToGrid(gridOtherResultsC3, lb, row, 0);
                }
                if (row == 1 && col == 5) {
                    lb.setText("kein Radius!");
                    addItemsToGrid(gridOtherResultsC3, lb, row, 1);
                }


                if (row == 2 && col == 4) {
                    lb.setText(" Durchmesser ");
                    addItemsToGrid(gridOtherResultsC3, lb, row, 0);
                }
                if (row == 2 && col == 5) {
                    lb.setText("kein Durchmesser!");
                    addItemsToGrid(gridOtherResultsC3, lb, row, 1);
                }


                if (row == 4 && col == 4) {
                    lb.setText(" Brücken ");
                    addItemsToGrid(gridOtherResultsC3, lb, row, 0);
                }
                if (row == 4 && col == 5) {
                    lb.setText(logic.getBruecken());
                    addItemsToGrid(gridOtherResultsC3, lb, row, 1);
                }

                if (row == 3 && col == 4) {
                    lb.setText(" Artikulationen ");
                    addItemsToGrid(gridOtherResultsC3, lb, row, 0);
                }
                if (row == 3 && col == 5) {
                    lb.setText(logic.getArtikulationen());
                    addItemsToGrid(gridOtherResultsC3, lb, row, 1);
                }
                lb.setAlignment(Pos.CENTER);
                lb.setWrapText(true);

                otherResultsLabels[row][col] = lb;

                gridOtherResultsC1.setHalignment(lb, HPos.CENTER);
                gridOtherResultsC1.setFillWidth(lb, true);
                gridOtherResultsC1.setFillHeight(lb, true);
                gridOtherResultsC1.setPrefHeight(350);
                gridOtherResultsC2.setHalignment(lb, HPos.CENTER);
                gridOtherResultsC2.setFillWidth(lb, true);
                gridOtherResultsC2.setFillHeight(lb, true);
                gridOtherResultsC2.setPrefHeight(350);
                gridOtherResultsC3.setHalignment(lb, HPos.CENTER);
                gridOtherResultsC3.setFillWidth(lb, true);
                gridOtherResultsC3.setFillHeight(lb, true);
                gridOtherResultsC3.setPrefHeight(350);
                lb.setMaxWidth(Double.MAX_VALUE);
                lb.setMaxHeight(Double.MAX_VALUE);
            }
        }

        back = new Button("Neue Matrix");
        setColumnConstraints(28, gridOtherResultsC1);
        setColumnConstraints(72, gridOtherResultsC1);
        setColumnConstraints(40, gridOtherResultsC2);
        setColumnConstraints(60, gridOtherResultsC2);
        setColumnConstraints(40, gridOtherResultsC3);
        setColumnConstraints(60, gridOtherResultsC3);
        addItemsToGrid(mainGrid, gridOtherResultsC1, 2, 0);
        addItemsToGrid(mainGrid, gridOtherResultsC2, 2, 1);
        addItemsToGrid(mainGrid, gridOtherResultsC3, 2, 2);
        addItemsToGrid(mainGrid, back, 3, 0);

        back.setOnAction(event -> switchToFirstScene());
    }

    private void updateOtherResults() {
        otherResultsLabels[0][3].setText(logic.getExzentrizitaeten());
        otherResultsLabels[0][3].setMaxHeight(Double.MAX_VALUE);

        otherResultsLabels[0][2].setMaxHeight(Double.MAX_VALUE);
        otherResultsLabels[0][2].prefHeightProperty().bind(otherResultsLabels[0][3].heightProperty());

        if (logic.getRadius() == -1)
            otherResultsLabels[1][5].setText("kein Radius!");
        else
            otherResultsLabels[1][5].setText(String.valueOf(logic.getRadius()));

        otherResultsLabels[0][5].setText(logic.getZentren());

        if (logic.getDurchmesser() == -1)
            otherResultsLabels[2][5].setText("kein Durchmesser!");
        else
            otherResultsLabels[2][5].setText(String.valueOf(logic.getDurchmesser()));
        otherResultsLabels[0][1].setText(logic.getKomponentenToString());
        otherResultsLabels[4][5].setText(logic.getBruecken());
        otherResultsLabels[3][5].setText(logic.getArtikulationen());
    }

    private void addItemsToGrid(GridPane grid, Node n, int row, int col) {
        grid.setRowIndex(n, row);
        grid.setColumnIndex(n, col);
        grid.getChildren().add(n);
    }

    private void addGroupToMainGrid(Group group, GridPane grid) {
        group.getChildren().add(grid);
        mainGrid.getChildren().add(group);
    }

    public static int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size >= 3 && size <= 15)
            this.size = size;
    }

    private void setColumnConstraints(double setPercentWidth, GridPane grid) {
        ColumnConstraints constraint = new ColumnConstraints();
        constraint.setPercentWidth(setPercentWidth);
        grid.getColumnConstraints().add(constraint);
    }

    private void clickAdjazenz(ActionEvent event) {
        int row, col;
        bt = (AdjazenzButton) event.getSource();
        row = bt.row;
        col = bt.col;
        if (matrix.getValue(row, col) == 0 && matrix.getValue(col, row) == 0) {
            matrix.addKante(row, col);
            logic.wegDistanzBerechnung();
            updateButtonText(row, col);
            colorNodes(row, col, buttons);
            updateLabels(wegLabels, wegMatrix);
            updateLabels(distanzLabels, distanzMatrix);
            updateOtherResults();
        } else {
            if (matrix.getValue(row, col) == 1 && matrix.getValue(col, row) == 1) {
                matrix.removeKante(row, col);
                logic.wegDistanzBerechnung();
                updateButtonText(row, col);
                noColorNodes(row, col, buttons);
                updateLabels(wegLabels, wegMatrix);
                updateLabels(distanzLabels, distanzMatrix);
                updateOtherResults();
            }
        }
    }

    private void updateButtonText(int row, int col) {
        buttons[row][col].setText(String.valueOf(matrix.getValue(row, col)));
        buttons[col][row].setText(String.valueOf(matrix.getValue(col, row)));
    }

    private void updateLabels(Label[][] labels, Matrix matrix) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                labels[row][col].setText(String.valueOf(matrix.getValue(row, col)));
                labels[col][row].setText(String.valueOf(matrix.getValue(col, row)));
                if (matrix == wegMatrix) {
                    if (matrix.getValue(row, col) != 0 && row != col)
                        colorNodes(row, col, labels);
                    if (matrix.getValue(row, col) == 0 && row != col)
                        noColorNodes(row, col, labels);
                }
                if (matrix == distanzMatrix) {
                    if (matrix.getValue(row, col) != -1 && row != col)
                        colorNodes(row, col, labels);
                    if (matrix.getValue(row, col) == -1 && row != col) {
                        noColorNodes(row, col, labels);
                        labels[row][col].setText("\u221E");
                        labels[col][row].setText("\u221E");
                    }
                }
            }
        }
    }

    private void colorNodes(int row, int col, Node n[][]) {
        n[row][col].setStyle("-fx-background-color: lightblue;" +
                "-fx-border-color: darkgrey;-fx-padding: 0;");
        n[col][row].setStyle("-fx-background-color: lightblue;" +
                "-fx-border-color: darkgrey;-fx-padding: 0;");
    }

    private void noColorNodes(int row, int col, Node n[][]) {
        if (n == wegLabels || n == distanzLabels) {
            n[row][col].setStyle("-fx-border-color: darkgrey;");
            n[col][row].setStyle("-fx-border-color: darkgrey;");
        } else {
            n[col][row].setStyle("");
            n[row][col].setStyle("");
        }
    }

    private void getSizeFromTextfield() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler!");
        if (!tfSize.getText().isEmpty()) {
            if (tfSize.getText() != null) {
                if (Integer.parseInt(tfSize.getText()) >= 3 && Integer.parseInt(tfSize.getText()) <= 15) {
                    setSize(Integer.parseInt(tfSize.getText()));
                } else {
                    alert.setHeaderText("Falscher Wert für Knotenanzahl!");
                    alert.setContentText("Achtung! Falscher Wert wurde eingegeben! Erlaubte Werte: 3-15");
                    alert.showAndWait();
                    throw new IllegalArgumentException("Anzahl der Knoten darf mindestens 3 und maximal 15 betragen");
                }
            } else {
                alert.setHeaderText("Eingabefeld war null!");
                alert.setContentText("Achtung! Eingabefeld darf nicht null sein! Erlaubte Werte: 3-15");
                alert.showAndWait();
                throw new IllegalArgumentException("Eingabe darf nicht null sein!");
            }
        } else {
            alert.setHeaderText("Eingabefeld war leer!");
            alert.setContentText("Achtung! Eingabefeld darf nicht leer sein! Erlaubte Werte: 3-15");
            alert.showAndWait();
            throw new IllegalArgumentException("Eingabefeld darf nicht leer sein!");
        }
    }


    class AdjazenzButton extends Button {
        int row, col;

        AdjazenzButton(String text, int row, int col) {
            super(text);
            this.row = row;
            this.col = col;
        }
    }
}
