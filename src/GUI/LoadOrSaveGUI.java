package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import bot.Encrypter;
import bot.SetCentered;


public class LoadOrSaveGUI extends JFrame {
	private SettingsGUI settingsGUIObject;
	private JFileChooser chooser;
	private boolean loading;
	private JPanel contentPane;
	private Encrypter encrypterOrDecrypter;

	public LoadOrSaveGUI(String type, Encrypter encrypter) {
		this.encrypterOrDecrypter = encrypter;
		this.settingsGUIObject = encrypterOrDecrypter.getGUI();
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setContentPane(contentPane);
		setVisible(true);

		if ("save".equals(type)) {
			loading = false;
		} else {
			loading = true;
		}

		chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt");

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle("");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(filter);


		if (loading) {
			chooser.setDialogType(0);
		} else {
			chooser.setDialogType(1);
		}

		contentPane.add(chooser, BorderLayout.CENTER);
		pack();

		chooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) { //they cancelled
					dispose();
					return;
				}

				File selection = chooser.getSelectedFile();
				if (loading) {
					processLoad(selection);
				} else {
					System.out.println("process save");
					processSave(selection);
				}

			}
		});
		SetCentered centerer = new SetCentered(this);
	}

	private void processLoad(File selectedFile) {

		try {
			String content = new Scanner(selectedFile).useDelimiter("\\Z").next();
			System.out.println(content);
			settingsGUIObject.updateGUI(encrypterOrDecrypter.decrypt(content));
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
			dispose();
			JOptionPane.showMessageDialog(null, "Error reading file; it may have been encrypted by another license");
		}


	}

	private void processSave(File selectedFile) {

		try {

			if (!selectedFile.getName().contains(".txt")) {
				selectedFile = new File(selectedFile.getAbsolutePath()+".txt");
			}

			if (selectedFile.exists()) {
				int res = JOptionPane.showOptionDialog(null, "The selected file already exists. Overwrite?", "File Exists", 0, 0, null, null, null);

				if (res == 1) {
					dispose();
					return;
				}

			}
			
			//write the file, pull the values, encrypt them and then write
			String encryptedText = encrypterOrDecrypter.encrypt(settingsGUIObject.pullFields());
			writeToFile(encryptedText, selectedFile);
			dispose();
	
		} catch (Exception e) {
			e.printStackTrace();
			dispose();
			JOptionPane.showMessageDialog(null, "Error writing to file");
		}
	}

	private void writeToFile(String fileContent, File selectedFile) throws IOException {

		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selectedFile), "utf-8")); //create the writer, set its destination to the proper file
		writer.write(fileContent); //write the text
		writer.close(); //close the writer

	}
}
