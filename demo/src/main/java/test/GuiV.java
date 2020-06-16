package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder; 
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.gui.TreeViewer;
import org.antlr4gwt.demo.client.Py2Ph;
import org.antlr4gwt.demo.shared.grammar.Python3Lexer;
import org.antlr4gwt.demo.shared.grammar.Python3Parser;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.File_inputContext;

public class GuiV extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiV frame = new GuiV();
					frame.setTitle("V!Code");
					frame.setVisible(true);
					frame.setIconImage(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GuiV() {
	  try {
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
	} catch(Exception e){

	}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width/6, d.height/6, d.width*2/3, d.height*2/3);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		panel.setDividerLocation(0.5);
		tabbedPane.addTab("Code", null, panel, null);
		//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JTextArea codein = new JTextArea();
		panel.add(new JScrollPane(codein),JSplitPane.TOP );
		codein.setRows(10);
		
		codein.setText(read("history"));
		
		JTextArea codeout = new JTextArea();
		codeout.setBackground(new Color(240, 255, 240));
		panel.add(new JScrollPane(codeout),JSplitPane.BOTTOM);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Tree", null, scrollPane, null);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		JButton btnConvert = new JButton("Convert");
		toolBar.add(btnConvert);
		toolBar.add(lblNewLabel);
		btnConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long t=System.currentTimeMillis();
				Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(codein.getText()) );
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				  
				Python3Parser parser = new Python3Parser(tokens);
			     
				// Actually launch the parsing
				parser.setBuildParseTree(true);
				
				  File_inputContext tree = parser.file_input();
				  t=System.currentTimeMillis()-t;
				  
				  write("history", codein.getText());
				  
				  lblNewLabel.setText(t+"");
				  
				  try {
					  
					 
				  Object rs =  new Py2Ph().visitFile_input(tree);
				  codeout.setText(String.valueOf(rs)
						  +"\nerror:"
						  +parser.getNumberOfSyntaxErrors()
						
						  );
				  
				  
				  TreeViewer viewr = new TreeViewer(Arrays.asList( parser.getRuleNames()),tree);
				  viewr.setPreferredSize(new Dimension(10024, 3600)); 
				  scrollPane.setViewportView(viewr);
				  }catch(Exception ex) {ex.printStackTrace();}
				  
				  
			}
		});
		 
		
		
	}
	static void write(String file,String str) {
		 
		try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"utf-8")){
		 out.write(str);
		}catch(Exception e) {}
		
	}
	static String read(String file) {
		StringBuilder bb=new StringBuilder();
		if(new java.io.File(file).exists())
		try(InputStreamReader in = new InputStreamReader(new FileInputStream(file),"utf-8")){
		char[] buf=new char[1024];
		int len=0;
		while((len=in.read(buf))!=-1) {
			bb.append(buf,0,len);
		}
		}catch(Exception e) {}
		
		return bb.toString();
	}

}
