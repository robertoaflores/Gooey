package edu.cnu.cs.gooey;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Fahrenheit extends JFrame {
	
	public Fahrenheit() {
		super( "Fahrenheit Conversion" );

		JTextField jtfFahrenheit = new JTextField( 10 );
		JButton    jbtGo         = new JButton( "To" );
		JLabel     jlbResult     = new JLabel();

		setLayout( new BorderLayout() );
		
		JPanel first  = new JPanel( new FlowLayout() );
		JPanel second = new JPanel( new FlowLayout( FlowLayout.LEFT ));
		
		first .add( new JLabel( "Fahrenheit" ));
		first .add( jtfFahrenheit );
		first .add( jbtGo );
				
		ButtonGroup  rGroup   = new ButtonGroup();
		JRadioButton rCelsius = new JRadioButton("Celsius");
		JRadioButton rKelvin  = new JRadioButton("Kelvin");
		rGroup.add(rCelsius);
		rGroup.add(rKelvin);
		JPanel       rPanel   = new JPanel();
		rPanel.setLayout(new GridLayout(2, 1));
		rPanel.add(rCelsius);
		rPanel.add(rKelvin);
		rPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		rCelsius.setSelected(true);
		
		second.add(rPanel);
		second.add( new JLabel( "Result:" ));
		second.add( jlbResult );
		
		add( first,  BorderLayout.NORTH );
		add( second, BorderLayout.CENTER );
		
		pack();
		
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setLocationRelativeTo( null );
		
		jbtGo.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent anEvent) {
				try {
					double value  = Float.parseFloat( jtfFahrenheit.getText() );
					String result;
					if (rCelsius.isSelected()) {
						double c = (value - 32) * 5f/9;
						result   = String.format("%.2f", c );
					} else {
						if (rKelvin.isSelected()) {
							double k = (value + 459.67) * 5f/9;
							result   = String.format("%.2f", k );
						} else {
							result   = "Select a temperature";
						}
					}
					jlbResult.setText( result );
				}
				catch (NumberFormatException e) {
					jlbResult.setText( "Enter a number" );
				}
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame f = new Fahrenheit();
		f.setVisible( true );
	}
}
