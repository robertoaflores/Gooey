package edu.cnu.cs.gooey;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PaintBackgroundWithButtons extends JFrame {
	public PaintBackgroundWithButtons() {
		super("Paint Background");
		
		setLayout( new FlowLayout() );

		var  green = new JButton("Green");
		green.addActionListener( e -> getContentPane().setBackground( Color.GREEN ));
		add( green );
//		var  green = new JButton("Green");
//		green.addActionListener( 
//			new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					getContentPane().setBackground( Color.GREEN );
//				}
//			}
//		);
//		add( green );
		
		var  orange = new JButton("Orange");
		orange.addActionListener( e -> getContentPane().setBackground( Color.ORANGE ));
		add( orange );
		
		setSize( 300, 200 );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
	}
	public static void main(String[] args) {
		JFrame f = new PaintBackgroundWithButtons();
		f.setVisible( true );
	}
}
