package edu.cnu.cs.gooey;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NameExample extends JFrame {

	public NameExample() {
		super( "Example" );

		JLabel  jlbText = new JLabel( "<no name>" ) {
			@Override
			public Dimension getPreferredSize() {
				final Dimension dimension = super.getPreferredSize();
				dimension.width = 200;
				return dimension;
			}
		};
		Runnable editName = () -> {
			String  given = jlbText.getText();
			boolean exit  = false;
			while (!exit) {
				String typed = JOptionPane.showInputDialog( this, "Write a name", given );
				exit = true;
				if (typed != null) {
					if (typed.isEmpty()) {
						JOptionPane.showMessageDialog( this, "Name cannot be empty" );
						exit = false;
					} else {
						jlbText.setText( typed );
					}
				}
			}
		};
		
		// menu
		JMenuBar     jmbMain = new JMenuBar();
		setJMenuBar( jmbMain );

		JMenu        jmnProgram = new JMenu( "Program" );
		jmbMain.add( jmnProgram );

		JMenuItem jmiEdit = jmnProgram.add( "Edit Name..." );
		jmnProgram.addSeparator();
		JMenuItem jmiExit = jmnProgram.add( "Exit" );
		
		jmiEdit.addActionListener( e -> editName.run() );
		jmiExit.addActionListener( e -> dispose() );

		// labels & button
		JPanel jpnPanel = new JPanel();
		add(   jpnPanel );
		
		JLabel jlbLabel = new JLabel( "Name: " );
		jlbLabel.setName( "label" );
		jpnPanel.add( jlbLabel );
		
		jlbText.setName( "current" );
		jpnPanel.add( jlbText );
		
		JButton       jbtEdit = new JButton( "Edit Name" );
		jpnPanel.add( jbtEdit );
		jbtEdit.addActionListener( e -> editName.run() );
		
		pack();
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setLocationRelativeTo( null );
	}

	public static void main(final String[] args) {
		JFrame frame = new NameExample();
		frame.setVisible( true );
	}
}
