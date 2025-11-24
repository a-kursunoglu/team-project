package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.UserCases.UploadClothing.UploadClothingController;
import FuzeWardrobePlanner.UserCases.UploadClothing.UploadClothingView;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class UploadClothingPanel extends JPanel implements UploadClothingView {
    private final JTextField nameField;
    private final JComboBox<String> clothingTypeDropdown;
    private final JComboBox<String> weatherRatingDropdown;
    private final JCheckBox waterproofCheckbox;

    private final JLabel imagePreviewLabel;
    private final JLabel messageLabel;

    private String selectedImagePath = null;

    private final UploadClothingController controller;

    public UploadClothingPanel(UploadClothingController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Name:"), c);

        nameField = new JTextField(15);
        c.gridx = 1;
        formPanel.add(nameField, c);
        row++;

        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Clothing Type:"), c);

        String[] clothingTypes = {"Tops", "Bottoms", "Outerwear", "Accessories"};
        clothingTypeDropdown = new JComboBox<>(clothingTypes);

        c.gridx = 1;
        formPanel.add(clothingTypeDropdown, c);
        row++;

        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Weather Rating:"), c);

        String[] weatherRatings = {"0", "1", "2", "3", "4", "5"};
        weatherRatingDropdown = new JComboBox<>(weatherRatings);

        c.gridx = 1;
        formPanel.add(weatherRatingDropdown, c);
        row++;

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        JLabel ratingHint = new JLabel("0 = light/hot weather, 5 = heaviest/coldest");
        ratingHint.setFont(ratingHint.getFont().deriveFont(Font.ITALIC, 11f));
        formPanel.add(ratingHint, c);
        c.gridwidth = 1;
        row++;

        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Waterproof:"), c);

        waterproofCheckbox = new JCheckBox("Is Waterproof");
        c.gridx = 1;
        formPanel.add(waterproofCheckbox, c);
        row++;

        JButton uploadButton = new JButton("UPLOAD");
        uploadButton.addActionListener(e -> uploadImage());
        c.gridx = 0; c.gridy = row;
        formPanel.add(uploadButton, c);

        JButton saveButton = new JButton("SAVE");
        saveButton.addActionListener(e -> saveClothing());
        c.gridx = 1;
        formPanel.add(saveButton, c);
        row++;

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        formPanel.add(messageLabel, c);

        add(formPanel, BorderLayout.WEST);

        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(300, 250));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel imagePanel = new JPanel();
        imagePanel.add(imagePreviewLabel);

        add(imagePanel, BorderLayout.CENTER);
    }

    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            ImageIcon icon = new ImageIcon(selectedImagePath);

            Image scaledImage = icon.getImage().getScaledInstance(
                    280, 240, Image.SCALE_SMOOTH
            );
            imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void saveClothing() {
        String name = nameField.getText().trim();
        String category = clothingTypeDropdown.getSelectedItem().toString();
        int weatherRating = Integer.parseInt(weatherRatingDropdown.getSelectedItem().toString());
        boolean waterproof = waterproofCheckbox.isSelected();

        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        if (selectedImagePath == null) {
            showError("Please upload an image.");
            return;
        }

        controller.addClothing(
                name,
                category,
                weatherRating,
                waterproof,
                selectedImagePath
        );
    }

    @Override
    public void showSuccess(String message) {
        messageLabel.setForeground(new Color(0,128,0)); // green
        messageLabel.setText(message);

        nameField.setText("");
        waterproofCheckbox.setSelected(false);
        imagePreviewLabel.setIcon(null);
        selectedImagePath = null;
    }

    @Override
    public void showError(String errorMessage) {
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(errorMessage);
    }
}
