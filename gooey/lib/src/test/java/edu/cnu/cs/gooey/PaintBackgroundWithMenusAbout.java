package edu.cnu.cs.gooey;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class PaintBackgroundWithMenusAbout extends JFrame {
	public PaintBackgroundWithMenusAbout() {
		super("Paint Background");

		// Main menu
		JMenuBar     bar = new JMenuBar();
		setJMenuBar( bar );

		// Program menu
		JMenu     program = new JMenu  ( "Program"  );
		JMenuItem about   = program.add( "About..."  );
		program.addSeparator();
		JMenuItem exit    = program.add( "Exit" );
		bar.add( program );

		// Colors menu
		JMenu     colors  = new JMenu ( "Colors" );
		JMenuItem green   = colors.add( "Green" );
		JMenuItem orange  = colors.add( "Orange" );
		bar.add( colors );

		about .addActionListener( e -> JOptionPane.showMessageDialog( 
				PaintBackgroundWithMenusAbout.this,
//                "Paint Background \n by You",
				new JLabel("<html><hr><i>Paint Background</i><br>by You<hr></html>"),
                "About",
                JOptionPane.INFORMATION_MESSAGE ));
		exit  .addActionListener( e -> dispose() );
		green .addActionListener( e -> getContentPane().setBackground( Color.GREEN ));
		orange.addActionListener( e -> getContentPane().setBackground( Color.ORANGE ));

		setSize( 300, 200 );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
	}
	public static void main(String[] args) {
		JFrame f = new PaintBackgroundWithMenusAbout();
		f.setVisible( true );
	}
}
