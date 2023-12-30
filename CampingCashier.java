package com.example.tubes;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class CampingCashier extends Application {

    private TextField itemQuantityField;
    private TextArea receiptArea;
    private double totalAmount = 0.0;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private ComboBox<Item> itemComboBox;
    private ObservableList<Item> orderedItems = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sewa Alat Camping");

        // Header
        Label titleLabel = new Label("SEWA ALAT CAMPING");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        Label groupLabel = new Label("KELOMPOK 10");

        VBox headerBox = new VBox(titleLabel, groupLabel);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER);
        headerBox.setSpacing(10);

        // UI Elements
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        itemComboBox = new ComboBox<>();
        itemComboBox.setItems(FXCollections.observableArrayList(
                new Item("Tenda", 200000.0),
                new Item("Sleeping Bag", 100000.0),
                new Item("Kompor", 15000.0),
                new Item("Alat Masak", 8000.0),
                new Item("Senter", 5000.0)
        ));
        itemComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + currencyFormat.format(item.getPrice()));
                }
            }
        });
        itemComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + currencyFormat.format(item.getPrice()));
                }
            }
        });
        itemComboBox.setPromptText("Pilih Item");
        grid.add(itemComboBox, 0, 0);

        itemQuantityField = new TextField();
        itemQuantityField.setPromptText("Jumlah");
        grid.add(itemQuantityField, 1, 0);

        Button addItemButton = new Button("Tambah Item");
        addItemButton.setOnAction(e -> addItemToOrder());
        grid.add(addItemButton, 2, 0);

        Button updateItemButton = new Button("Update Jumlah");
        updateItemButton.setOnAction(e -> updateItemQuantity());
        grid.add(updateItemButton, 3, 0);

        Button deleteItemButton = new Button("Hapus Item");
        deleteItemButton.setOnAction(e -> deleteItem());
        grid.add(deleteItemButton, 4, 0);

        receiptArea = new TextArea();
        receiptArea.setEditable(false);
        grid.add(receiptArea, 0, 2, 5, 1);

        Button printReceiptButton = new Button("Cetak Struk");
        printReceiptButton.setOnAction(e -> printReceipt());
        grid.add(printReceiptButton, 0, 3);

        Scene scene = new Scene(new VBox(headerBox, grid), 600, 400);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void addItemToOrder() {
        Item selectedItem = itemComboBox.getValue();

        if (selectedItem != null) {
            String itemName = selectedItem.getName();
            String quantityText = itemQuantityField.getText();

            if (!quantityText.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityText);
                    double itemPrice = selectedItem.getPrice();

                    // Check if the item is already in the order
                    boolean itemExists = false;
                    for (Item orderedItem : orderedItems) {
                        if (orderedItem.getName().equals(itemName)) {
                            orderedItem.setQuantity(orderedItem.getQuantity() + quantity);
                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        Item orderedItem = new Item(itemName, itemPrice);
                        orderedItem.setQuantity(quantity);
                        orderedItems.add(orderedItem);
                    }

                    updateReceiptArea();
                    itemComboBox.getSelectionModel().clearSelection();
                    itemQuantityField.clear();
                } catch (NumberFormatException e) {
                    showAlert("Error", "Jumlah harus berupa angka.");
                }
            } else {
                showAlert("Error", "Mohon isi jumlah.");
            }
        } else {
            showAlert("Error", "Mohon pilih nama item.");
        }
    }

    private void updateItemQuantity() {
        Item selectedItem = itemComboBox.getValue();

        if (selectedItem != null) {
            String quantityText = itemQuantityField.getText();

            if (!quantityText.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityText);

                    // Check if the item is in the order
                    for (Item orderedItem : orderedItems) {
                        if (orderedItem.getName().equals(selectedItem.getName())) {
                            orderedItem.setQuantity(quantity);
                            break;
                        }
                    }

                    updateReceiptArea();
                    itemComboBox.getSelectionModel().clearSelection();
                    itemQuantityField.clear();
                } catch (NumberFormatException e) {
                    showAlert("Error", "Jumlah harus berupa angka.");
                }
            } else {
                showAlert("Error", "Mohon isi jumlah.");
            }
        } else {
            showAlert("Error", "Mohon pilih nama item.");
        }
    }

    private void deleteItem() {
        Item selectedItem = itemComboBox.getValue();

        if (selectedItem != null) {
            orderedItems.removeIf(item -> item.getName().equals(selectedItem.getName()));
            updateReceiptArea();
            itemComboBox.getSelectionModel().clearSelection();
            itemQuantityField.clear();
        } else {
            showAlert("Error", "Mohon pilih item untuk dihapus.");
        }
    }

    private void printReceipt() {
        updateReceiptArea();
        if (totalAmount > 0) {
            receiptArea.appendText("-------------------------\n");
            receiptArea.appendText(String.format("Total: %s\n", currencyFormat.format(totalAmount)));
        } else {
            showAlert("Info", "Keranjang belanja kosong.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateReceiptArea() {
        receiptArea.clear();
        totalAmount = 0.0;

        for (Item orderedItem : orderedItems) {
            String receiptEntry = String.format(
                    "%s x %d = %s\n",
                    orderedItem.getName(),
                    orderedItem.getQuantity(),
                    currencyFormat.format(orderedItem.getPrice() * orderedItem.getQuantity())
            );
            receiptArea.appendText(receiptEntry);
            totalAmount += orderedItem.getPrice() * orderedItem.getQuantity();
        }
    }

    // Inner class for representing items
    private static class Item {
        private final String name;
        private final double price;
        private int quantity = 0; // Initialize quantity to 0

        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
