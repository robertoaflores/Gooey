package edu.cnu.cs.gooey;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class BMI extends JFrame {
	private JTextField jtfWeight;
	private JTextField jtfHeight;
	private JButton    jbtGo;
	private JLabel     jlbIndex;
	
	public BMI() {
		super( "Body Mass Index" );
		
		setLayout( new GridLayout( 2, 1 ) );
		
		JPanel first = new JPanel( new FlowLayout() );
		first .add( new JLabel( "Weight (lb)" ));
		first .add( jtfWeight = new JTextField( 10 ));
		first .add( new JLabel( "Height (ft)" ));
		first .add( jtfHeight = new JTextField( 10 ));
		first .add( jbtGo     = new JButton( "Go" ));
		
		JPanel second = new JPanel( new FlowLayout() );
		second.add( new JLabel( "Index:" ));
		second.add( jlbIndex = new JLabel());
		
		add( first );
		add( second );
		
		pack();
		
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setLocationRelativeTo( null );
		
		jbtGo.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent anEvent) {
				double  weight = 0;
				double  height = 0;
				boolean ok     = false; 
				try {
					weight = Float.parseFloat( jtfWeight.getText() );
					height = Float.parseFloat( jtfHeight.getText() );
					ok     = weight > 0 && height > 0;
				}
				catch (NumberFormatException e) {
				}
				if (ok) {
					double index = (weight * 4.88) / (height * height);
					jlbIndex.setText( "" + Math.round( index * 100 ) / 100.0 );
				}
				else {
					jlbIndex.setText( "Enter positive numbers" );
				}
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame f = new BMI();
		f.setVisible( true );
	}
}
