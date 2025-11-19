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

        // NAME FIELD
        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Name:"), c);

        nameField = new JTextField(15);
        c.gridx = 1;
        formPanel.add(nameField, c);
        row++;

        // CLOTHING TYPE DROPDOWN
        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Clothing Type:"), c);

        String[] clothingTypes = {"Tops", "Bottoms", "Outerwear", "Accessories"};
        clothingTypeDropdown = new JComboBox<>(clothingTypes);

        c.gridx = 1;
        formPanel.add(clothingTypeDropdown, c);
        row++;

        // WEATHER RATING DROPDOWN
        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Weather Rating:"), c);

        String[] weatherRatings = {"0", "1", "2", "3", "4", "5"};
        weatherRatingDropdown = new JComboBox<>(weatherRatings);

        c.gridx = 1;
        formPanel.add(weatherRatingDropdown, c);
        row++;

        // WATERPROOF CHECKBOX
        c.gridx = 0; c.gridy = row;
        formPanel.add(new JLabel("Waterproof:"), c);

        waterproofCheckbox = new JCheckBox("Is Waterproof");
        c.gridx = 1;
        formPanel.add(waterproofCheckbox, c);
        row++;

        // UPLOAD BUTTON
        JButton uploadButton = new JButton("UPLOAD");
        uploadButton.addActionListener(e -> uploadImage());
        c.gridx = 0; c.gridy = row;
        formPanel.add(uploadButton, c);

        // SAVE BUTTON
        JButton saveButton = new JButton("SAVE");
        saveButton.addActionListener(e -> saveClothing());
        c.gridx = 1;
        formPanel.add(saveButton, c);
        row++;

        // MESSAGE LABEL
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        formPanel.add(messageLabel, c);

        add(formPanel, BorderLayout.WEST);

        // RIGHT SIDE IMAGE PREVIEW
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(300, 250));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel imagePanel = new JPanel();
        imagePanel.add(imagePreviewLabel);

        add(imagePanel, BorderLayout.CENTER);
    }

    // UPLOAD BUTTON ACTION
    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            ImageIcon icon = new ImageIcon(selectedImagePath);

            // Scale Image for Preview
            Image scaledImage = icon.getImage().getScaledInstance(
                    280, 240, Image.SCALE_SMOOTH
            );
            imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    // SAVE BUTTON ACTION
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

    // MESSAGES
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
