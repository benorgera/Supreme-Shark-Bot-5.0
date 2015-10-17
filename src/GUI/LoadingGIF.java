package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import bot.SetCentered;
import bot.main;

public class LoadingGIF extends JFrame {
	private JLabel messageHolder;

	public LoadingGIF(String message, String title) {

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0,0));
		setContentPane(contentPane);

		((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		setUndecorated(true); //maybe, maybe not
		
		
		contentPane.add(new JLabel(title+" "+main.getThisVersionNumber(), JLabel.CENTER), BorderLayout.NORTH);
		setResizable(false);
		ImageIcon loading = new ImageIcon(LoadingGIF.class.getResource("ajax-loader.gif"));
		messageHolder = new JLabel(message+"...", loading, JLabel.CENTER);
		contentPane.add(messageHolder, BorderLayout.SOUTH);

		ImageIcon logo = new ImageIcon(LoadingGIF.class.getResource("logo.png"));

		contentPane.add(new JLabel(logo, JLabel.CENTER), BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);
		pack();
		SetCentered centerer = new SetCentered(this);
		setVisible(true);
	}
	
	public void passUI(String message) {
		this.messageHolder.setText(message+"...");
	}

}
