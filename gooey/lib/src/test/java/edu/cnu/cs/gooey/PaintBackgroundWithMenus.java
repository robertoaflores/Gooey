package edu.cnu.cs.gooey;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class PaintBackgroundWithMenus extends JFrame {
	public PaintBackgroundWithMenus() {
		super("Paint Background");

		// Main menu
		JMenuBar     bar = new JMenuBar();
		setJMenuBar( bar );

		// Program menu
		JMenu     program = new JMenu  ( "Program"  );
		JMenuItem exit    = program.add( "Exit" );
		bar.add( program );

		// Colors menu
		JMenu     colors  = new JMenu ( "Colors" );
		JMenuItem green   = colors.add( "Green" );
		JMenuItem orange  = colors.add( "Orange" );
		bar.add( colors );

		exit  .addActionListener( e -> dispose() );
		green .addActionListener( e -> getContentPane().setBackground( Color.GREEN ));
		orange.addActionListener( e -> getContentPane().setBackground( Color.ORANGE ));

		setSize( 300, 200 );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
	}
	public static void main(String[] args) {
		JFrame f = new PaintBackgroundWithMenus();
		f.setVisible( true );
	}
}
