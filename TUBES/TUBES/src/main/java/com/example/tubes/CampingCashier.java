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

// Kelas utama aplikasi JavaFX untuk kasir alat camping
public class CampingCashier extends Application {

    private TextField itemQuantityField;  // Input field untuk jumlah item
    private TextArea receiptArea;  // Area teks untuk menampilkan struk belanja
    private double totalAmount = 0.0;  // Jumlah total belanja
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private ComboBox<Item> itemComboBox;  // ComboBox untuk memilih item
    private ObservableList<Item> orderedItems = FXCollections.observableArrayList();  // Daftar item yang telah dipesan

    // Metode utama untuk menjalankan aplikasi
    public static void main(String[] args) {
        launch(args);
    }

    // Metode untuk mengatur antarmuka pengguna (UI)
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

        // Elemen UI
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // ComboBox untuk memilih item
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

        // Input field untuk jumlah item
        itemQuantityField = new TextField();
        itemQuantityField.setPromptText("Jumlah");
        grid.add(itemQuantityField, 1, 0);

        // Tombol untuk menambahkan item ke daftar pesanan
        Button addItemButton = new Button("Tambah Item");
        addItemButton.setOnAction(e -> addItemToOrder());
        grid.add(addItemButton, 2, 0);

        // Tombol untuk memperbarui jumlah item dalam daftar pesanan
        Button updateItemButton = new Button("Update Jumlah");
        updateItemButton.setOnAction(e -> updateItemQuantity());
        grid.add(updateItemButton, 3, 0);

        // Tombol untuk menghapus item dari daftar pesanan
        Button deleteItemButton = new Button("Hapus Item");
        deleteItemButton.setOnAction(e -> deleteItem());
        grid.add(deleteItemButton, 4, 0);

        // Area teks untuk menampilkan struk belanja
        receiptArea = new TextArea();
        receiptArea.setEditable(false);
        grid.add(receiptArea, 0, 2, 5, 1);

        // Tombol untuk mencetak struk belanja
        Button printReceiptButton = new Button("Cetak Struk");
        printReceiptButton.setOnAction(e -> printReceipt());
        grid.add(printReceiptButton, 0, 3);

        // Menyiapkan tata letak utama
        Scene scene = new Scene(new VBox(headerBox, grid), 600, 400);
        primaryStage.setScene(scene);

        // Menampilkan stage
        primaryStage.show();
    }

    // Metode untuk menambahkan item ke daftar pesanan
    private void addItemToOrder() {
        Item selectedItem = itemComboBox.getValue();

        if (selectedItem != null) {
            String itemName = selectedItem.getName();
            String quantityText = itemQuantityField.getText();

            if (!quantityText.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityText);
                    double itemPrice = selectedItem.getPrice();

                    // Periksa apakah item sudah ada dalam daftar pesanan
                    boolean itemExists = false;
                    for (Item orderedItem : orderedItems) {
                        if (orderedItem.getName().equals(itemName)) {
                            orderedItem.setQuantity(orderedItem.getQuantity() + quantity);
                            itemExists = true;
                            break;
                        }
                    }

                    // Jika item belum ada dalam daftar pesanan, tambahkan ke daftar
                    if (!itemExists) {
                        Item orderedItem = new Item(itemName, itemPrice);
                        orderedItem.setQuantity(quantity);
                        orderedItems.add(orderedItem);
                    }

                    // Perbarui area struk dan bersihkan input fields
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

    // Metode untuk memperbarui jumlah item dalam daftar pesanan
    private void updateItemQuantity() {
        Item selectedItem = itemComboBox.getValue();

        if (selectedItem != null) {
            String quantityText = itemQuantityField.getText();

            if (!quantityText.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityText);

                    // Periksa apakah item ada dalam daftar pesanan
                    for (Item orderedItem : orderedItems) {
                        if (orderedItem.getName().equals(selectedItem.getName())) {
                            orderedItem.setQuantity(quantity);
                            break;
                        }
                    }

                    // Perbarui area struk dan bersihkan input fields
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

    // Metode untuk menghapus item dari daftar pesanan
    private void deleteItem() {
        Item selectedItem = itemComboBox.getValue();

        if (selectedItem != null) {
            // Hapus item dari daftar pesanan berdasarkan nama item
            orderedItems.removeIf(item -> item.getName().equals(selectedItem.getName()));

            // Perbarui area struk dan bersihkan input fields
            updateReceiptArea();
            itemComboBox.getSelectionModel().clearSelection();
            itemQuantityField.clear();
        } else {
            showAlert("Error", "Mohon pilih item untuk dihapus.");
        }
    }

    // Metode untuk mencetak struk belanja
    private void printReceipt() {
        // Perbarui area struk
        updateReceiptArea();

        // Tambahkan total belanja ke area struk
        if (totalAmount > 0) {
            receiptArea.appendText("-------------------------\n");
            receiptArea.appendText(String.format("Total: %s\n", currencyFormat.format(totalAmount)));
        } else {
            showAlert("Info", "Keranjang belanja kosong.");
        }
    }

    // Metode untuk menampilkan dialog informasi
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Metode untuk memperbarui area struk
    private void updateReceiptArea() {
        // Bersihkan area struk dan setel total belanja ke 0
        receiptArea.clear();
        totalAmount = 0.0;

        // Iterasi melalui daftar pesanan dan tambahkan setiap item ke area struk
        for (Item orderedItem : orderedItems) {
            String receiptEntry = String.format(
                    "%s x %d = %s\n",
                    orderedItem.getName(),
                    orderedItem.getQuantity(),
                    currencyFormat.format(orderedItem.getPrice() * orderedItem.getQuantity())
            );
            receiptArea.appendText(receiptEntry);

            // Hitung total belanja
            totalAmount += orderedItem.getPrice() * orderedItem.getQuantity();
        }
    }

    // Kelas dalam untuk merepresentasikan item
    private static class Item {
        private final String name;  // Nama item
        private final double price;  // Harga item
        private int quantity = 0; // Inisialisasi jumlah ke 0

        // Konstruktor untuk membuat objek Item
        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }

        // Metode untuk mendapatkan nama item
        public String getName() {
            return name;
        }

        // Metode untuk mendapatkan harga item
        public double getPrice() {
            return price;
        }

        // Metode untuk mendapatkan jumlah item
        public int getQuantity() {
            return quantity;
        }

        // Metode untuk mengatur jumlah item
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
