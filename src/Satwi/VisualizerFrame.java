package Satwi;
import java.text.MessageFormat;
import java.sql.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;

@SuppressWarnings("serial")
public class VisualizerFrame extends JFrame {

	private final int MAX_SPEED = 1000;
	private final int MIN_SPEED = 1;
	private final int MAX_SIZE = 500;
	private final int MIN_SIZE = 1;
	private final int DEFAULT_SPEED = 20;
	private final int DEFAULT_SIZE = 100;

	private final String[] Sorts = {"Bubble", "Selection", "Insertion", "Merge",  "Bubble(fast)", "Selection(fast)", "Insertion(fast)"};

	private int sizeModifier;

	private JPanel wrapper;
	private JPanel arrayWrapper;
	private JPanel buttonWrapper;
	private JPanel[] squarePanels;
	private JButton start;
	private JComboBox<String> selection;
	private JSlider speed;
	private JSlider size;
	private JLabel speedVal;
	private JLabel sizeVal;
	private GridBagConstraints c;
	private JCheckBox stepped;
	private ImageIcon icon;
	private Border blackline = BorderFactory.createLineBorder(Color.black,2);  
	private Border greenline = BorderFactory.createLineBorder(Color.green,7);  
	

	public VisualizerFrame(){

		super("Satwi's Sorting Visualizer");      super.setBackground(new Color(25,0,97));super.setForeground(Color.green); 
		
	     icon=new ImageIcon("background.png");
		//arrayWrapper.setImage(new ImageIcon("background.jpg"));
		start = new JButton("Start Visualising"); start.setBackground(new Color(53,0,211));start.setForeground(Color.green);
		buttonWrapper = new JPanel();             buttonWrapper.setBackground(new Color(12,0,50));buttonWrapper.setForeground(Color.green);
	 // tried adding background here, but failed
		arrayWrapper = new JPanel(){  
		
			
			protected void paintComponent(Graphics g)
			{
				g.drawImage(icon.getImage(), 0,0, null);
				super.paintComponent(g);
			}
		};
 
		arrayWrapper.setBackground(new Color(77, 77, 255));arrayWrapper.setForeground(Color.green);
		arrayWrapper.setBorder(blackline);
		
		wrapper = new JPanel();                   wrapper.setBackground(new Color(195,7,63));wrapper.setForeground(Color.green);
		wrapper.setBorder(greenline);
		selection = new JComboBox<String>();      selection.setBackground(new Color(53,0,211));selection.setForeground(Color.green);
		speed = new JSlider(MIN_SPEED, MAX_SPEED, DEFAULT_SPEED); 
		size = new JSlider(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
		speedVal = new JLabel("Speed: 20 ms");                      speedVal.setForeground(Color.green);
		sizeVal = new JLabel("Size: 100 values");                   sizeVal.setForeground(Color.green);
		stepped = new JCheckBox("Stepped Values");                  stepped.setForeground(Color.green); stepped.setBackground(new Color(53,0,211)); 
		c = new GridBagConstraints();

		for(String s : Sorts) selection.addItem(s);        //adding types of sortign algos

		arrayWrapper.setLayout(new GridBagLayout());
		wrapper.setLayout(new BorderLayout());

		c.insets = new Insets(0,2,0,2);
		c.anchor = GridBagConstraints.SOUTH;
		c.ipadx=10;

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SortingVisualizer.startSort((String) selection.getSelectedItem());
			}
		});

		stepped.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SortingVisualizer.stepped = stepped.isSelected();
			}
		});

		speed.setMinorTickSpacing(20);
		speed.setMajorTickSpacing(100);
		speed.setPaintTicks(true);

		speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				speedVal.setText(("Speed: " + Integer.toString(speed.getValue()) + "ms"));
				validate();
				SortingVisualizer.sleep = speed.getValue();
			}
		});

		size.setMinorTickSpacing(20);
		size.setMajorTickSpacing(100);
		size.setPaintTicks(true);

		size.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				sizeVal.setText(("Size: " + Integer.toString(size.getValue()) + " values"));
				validate();
				SortingVisualizer.sortDataCount = size.getValue();
			}
		});

		buttonWrapper.add(stepped);
		buttonWrapper.add(speedVal);
		buttonWrapper.add(speed);
		buttonWrapper.add(sizeVal);
		buttonWrapper.add(size);
		buttonWrapper.add(start);
		buttonWrapper.add(selection);

		wrapper.add(buttonWrapper, BorderLayout.SOUTH);
		wrapper.add(arrayWrapper);

		add(wrapper);

		setExtendedState(JFrame.MAXIMIZED_BOTH );

		addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				// Reset the sizeModifier
				// 90% of the windows height, divided by the size of the sorted array.
				sizeModifier = (int) ((getHeight()*0.9)/(squarePanels.length));
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// Do nothing
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// Do nothing
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// Do nothing
			}

		});

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	// preDrawArray reinitializes the array of panels that represent the values. They are set based on the size of the window.
	public void preDrawArray(Integer[] squares){
		squarePanels = new JPanel[SortingVisualizer.sortDataCount];
		arrayWrapper.removeAll();
		// 90% of the windows height, divided by the size of the sorted array.
		sizeModifier =  (int) ((getHeight()*0.9)/(squarePanels.length));
		for(int i = 0; i<SortingVisualizer.sortDataCount; i++){
			squarePanels[i] = new JPanel();
			squarePanels[i].setPreferredSize(new Dimension(SortingVisualizer.blockWidth, squares[i]*sizeModifier));
			
			squarePanels[i].setBorder(blackline);  //not working
			squarePanels[i].setBackground(new Color(199,36,177));
			arrayWrapper.add(squarePanels[i], c);
		}
		repaint();
		validate();
	}

	public void reDrawArray(Integer[] x){
		reDrawArray(x, -1);
	}

	public void reDrawArray(Integer[] x, int y){
		reDrawArray(x, y, -1);
	}

	public void reDrawArray(Integer[] x, int y, int z){
		reDrawArray(x, y, z, -1);
	}

	// reDrawArray does similar to preDrawArray except it does not reinitialize the panel array.
	public void reDrawArray(Integer[] squares, int working, int comparing, int reading){
		arrayWrapper.removeAll();
		for(int i = 0; i<squarePanels.length; i++){
			squarePanels[i] = new JPanel();
			squarePanels[i].setBorder(blackline);  //not working
			squarePanels[i].setPreferredSize(new Dimension(SortingVisualizer.blockWidth, squares[i]*sizeModifier));
			if (i == working){
				squarePanels[i].setBackground(new Color(224,231,34));
			}else if(i == comparing){
				squarePanels[i].setBackground(Color.green);
			}else if(i == reading){
				squarePanels[i].setBackground(Color.green);
			}else{
				squarePanels[i].setBackground(new Color(199,36,177));
			}
			arrayWrapper.add(squarePanels[i], c);
		}
		repaint();
		validate();
	}

}
