package com.atkiwisolution.fatjarcreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

public class FatJarCreator extends JFrame {
    private JTextField inputDistField;
    private JTextField outputDirField;
    private JTextField jarNameField;
    private JButton createButton;
    private JTextArea logArea;
    private JProgressBar progressBar;

    public FatJarCreator() {
        initUI();
    }

    private void initUI() {
        setTitle("Fat JAR Creator");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.CENTER);

        // Bottom panel with progress and log
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 30, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Fat JAR Creator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel description = new JLabel("Create a single executable JAR from your distribution directory");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(new Color(180, 180, 220));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(description, BorderLayout.SOUTH);
        
        return headerPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Configuration"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        inputPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Input dist directory
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Input dist Directory:"), gbc);
        
        gbc.gridy = 1;
        JPanel inputPathPanel = new JPanel(new BorderLayout(5, 0));
        inputDistField = new JTextField();
        inputDistField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton inputBrowseButton = createBrowseButton();
        inputBrowseButton.addActionListener(e -> browseForDirectory(inputDistField));
        inputPathPanel.add(inputDistField, BorderLayout.CENTER);
        inputPathPanel.add(inputBrowseButton, BorderLayout.EAST);
        inputPanel.add(inputPathPanel, gbc);
        
        // Output directory
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Output Directory:"), gbc);
        
        gbc.gridy = 3;
        JPanel outputPathPanel = new JPanel(new BorderLayout(5, 0));
        outputDirField = new JTextField();
        outputDirField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton outputBrowseButton = createBrowseButton();
        outputBrowseButton.addActionListener(e -> browseForDirectory(outputDirField));
        outputPathPanel.add(outputDirField, BorderLayout.CENTER);
        outputPathPanel.add(outputBrowseButton, BorderLayout.EAST);
        inputPanel.add(outputPathPanel, gbc);
        
        // JAR name
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Output JAR Name (without .jar):"), gbc);
        
        gbc.gridy = 5;
        jarNameField = new JTextField("MyFatJar");
        jarNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputPanel.add(jarNameField, gbc);
        
        return inputPanel;
    }

    private JButton createBrowseButton() {
        JButton button = new JButton("Browse...");
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(13, 110, 253));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private JPanel createBottomPanel() {
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setForeground(new Color(13, 110, 253));
        
        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(245, 245, 245));
        JScrollPane logScroll = new JScrollPane(logArea);
        
        // Create button
        createButton = createMainButton("Create Fat JAR");
        createButton.addActionListener(e -> createFatJar());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(progressBar, BorderLayout.NORTH);
        bottomPanel.add(logScroll, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(13, 110, 253));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        return button;
    }

    private void browseForDirectory(JTextField field) {
        // Store current look and feel
        LookAndFeel currentLF = UIManager.getLookAndFeel();
        
        try {
            // Set system look and feel temporarily
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create file chooser
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            // Show dialog
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                field.setText(fc.getSelectedFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                // Restore original look and feel
                UIManager.setLookAndFeel(currentLF);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void createFatJar() {
        String inputDist = inputDistField.getText().trim();
        String outputDir = outputDirField.getText().trim();
        String jarName = jarNameField.getText().trim();
        
        if (inputDist.isEmpty() || outputDir.isEmpty() || jarName.isEmpty()) {
            log("Error: All fields are required!", true);
            return;
        }

        File inputDir = new File(inputDist);
        File outputFolder = new File(outputDir);
        
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            log("Invalid input directory: " + inputDist, true);
            return;
        }
        
        if (!outputFolder.exists() && !outputFolder.mkdirs()) {
            log("Failed to create output directory: " + outputDir, true);
            return;
        }

        File mainJar = findMainJar(inputDir);
        if (mainJar == null) {
            log("No main JAR found in input directory!", true);
            return;
        }

        File libDir = new File(inputDir, "lib");
        if (!libDir.exists() || !libDir.isDirectory()) {
            log("No 'lib' directory found in input folder!", true);
            return;
        }

        File outputJar = new File(outputFolder, jarName + ".jar");
        log("Creating fat JAR: " + outputJar.getAbsolutePath(), false);
        
        // Count total dependency files for progress
        File[] depFiles = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
        int totalFiles = depFiles != null ? depFiles.length : 0;
        
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                createFatJar(mainJar, libDir, outputJar, totalFiles);
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    log(message, false);
                }
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    log("\nSuccess! Fat JAR created.", false);
                    progressBar.setString("Completed successfully");
                } catch (Exception ex) {
                    log("\nError: " + ex.getMessage(), true);
                    ex.printStackTrace();
                    progressBar.setString("Failed: " + ex.getMessage());
                } finally {
                    createButton.setEnabled(true);
                }
            }
        }.execute();
        
        createButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("Processing...");
    }

    private File findMainJar(File distDir) {
        File[] files = distDir.listFiles((dir, name) -> 
            name.endsWith(".jar") && !name.toLowerCase().startsWith("setup")
        );
        return (files != null && files.length > 0) ? files[0] : null;
    }

    private void createFatJar(File mainJar, File libDir, File outputJar, int totalDependencies) throws Exception {
        // Get main class from original manifest
        String mainClass = getMainClassFromJar(mainJar);
        if (mainClass == null) {
            throw new Exception("Main-Class not found in manifest of " + mainJar.getName());
        }
        
        log("Main-Class: " + mainClass, false);
        log("Processing dependencies...", false);

        // Create new manifest
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.MAIN_CLASS, mainClass);

        // Track added entries to handle duplicates
        Set<String> addedEntries = new HashSet<>();
        int processedFiles = 0;

        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar), manifest)) {
            // Copy main JAR contents
            log("Processing main JAR: " + mainJar.getName(), false);
            addJarContents(mainJar, jos, addedEntries);
            
            // Copy all dependency JARs
            File[] depFiles = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (depFiles != null) {
                for (File depJar : depFiles) {
                    log("Adding dependency: " + depJar.getName(), false);
                    addJarContents(depJar, jos, addedEntries);
                    processedFiles++;
                    int progress = (int) ((processedFiles / (double) totalDependencies) * 100);
                    progressBar.setValue(progress);
                    progressBar.setString("Processing: " + depJar.getName());
                }
            }
        }
    }

    private String getMainClassFromJar(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                return manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
            }
        }
        return null;
    }

    private void addJarContents(File jarFile, JarOutputStream target, Set<String> addedEntries) throws IOException {
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFile))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String entryName = entry.getName();

                // Skip problematic META-INF files
                if (entryName.startsWith("META-INF/")) {
                    // Skip signature files
                    if (entryName.endsWith(".SF") || 
                        entryName.endsWith(".DSA") || 
                        entryName.endsWith(".RSA")) {
                        continue;
                    }
                    // Skip Spring AOT and factories files
                    if (entryName.contains("aot.factories") ||
                        entryName.contains("spring.factories")) {
                        continue;
                    }
                    // Skip duplicate manifests
                    if (entryName.equals("META-INF/MANIFEST.MF")) {
                        continue;
                    }
                }

                // Skip duplicate entries
                if (addedEntries.contains(entryName)) {
                    log("  Skipping duplicate: " + entryName, false);
                    continue;
                }
                addedEntries.add(entryName);

                // Add entry
                if (!entry.isDirectory()) {
                    target.putNextEntry(new JarEntry(entryName));
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = jis.read(buffer)) != -1) {
                        target.write(buffer, 0, bytesRead);
                    }
                    target.closeEntry();
                }
            }
        }
    }

    private void log(String message, boolean isError) {
        SwingUtilities.invokeLater(() -> {
            if (isError) {
                logArea.append("[ERROR] ");
            }
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FatJarCreator app = new FatJarCreator();
            app.setVisible(true);
        });
    }
}