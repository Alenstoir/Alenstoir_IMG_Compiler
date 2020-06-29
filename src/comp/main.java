package comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLWriter;

import ui.SimpleUI;


public class main {

	static String loader = "loader";
	static String code = "code";
	static String res = "res";
	static String layout = "main";
	static String output = "output";
	static String ws = "workspace";
	
	static File loader_file;
	static File code_file;
	static File res_file;
	static String output_file;
	
	static void compile(JTextArea log, JTextField fsize, JTextField csize, JTextField rsize) {
		Path myfly = Paths.get(output_file);
		Path loader = loader_file.toPath();		
		Path res = res_file.toPath();
		Path code = code_file.toPath();
		
		int size = Integer.parseInt(fsize.getText())*512*2;
		
		byte [] file = new byte[size];
		
		log.append("File "+loader_file.getName()+" exists::"+Files.exists(loader)+"\n\n");
		if(Files.exists(loader)) {
			byte[] coded;
			try {
				coded = Files.readAllBytes(loader);
				for(int i = 0; i < coded.length; i++) {
					file[i] = coded[i];
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
				log.append("----------------------------\nFILE "+loader+" READ ERROR"+"\n----------------------------\n\n");
			}
			file[510] = 0x55;
			file[511] = (byte) 0xAA;		
		}
		
		log.append("File "+code_file.getName()+" exists::"+Files.exists(code)+"\n\n");
		
		
		if(Files.exists(code)) {
			byte[] coded;
			try {
				coded = Files.readAllBytes(code);
				for(int i = 0; i < coded.length; i++) {
					file[i+Integer.parseInt(csize.getText())*512] = coded[i];
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
				log.append("----------------------------\nFILE "+code+" READ ERROR"+"\n----------------------------\n\n");
			}
		} else {
			log.append("----------------------------\nFILE "+code+" MISSING"+"\n----------------------------\n\n");
		}
		
		log.append("File "+res_file.getName()+" exists::"+Files.exists(res)+"\n\n");
		
		if(Files.exists(res)) {
			byte[] coded;
			try {
				coded = Files.readAllBytes(res);
				for(int i = 0; i < coded.length; i++) {
					file[i+Integer.parseInt(rsize.getText())*512] = coded[i];
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
				log.append("----------------------------\nFILE "+res+" READ ERROR"+"\n----------------------------\n\n");
			}
		}else {
			log.append("----------------------------\nFILE "+res+" MISSING"+"\n----------------------------\n\n");
		}
		
		log.append("File "+myfly+" exists::"+Files.exists(myfly)+"\n");
		
		if(Files.notExists(myfly)) {
			
			log.append("Creating file::"+myfly+"\n");
			
			try {
				Files.createFile(myfly);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
				log.append("----------------------------\nFILE "+myfly+" CANT CREATE"+"\n----------------------------\n\n");
			}
			
			log.append("File "+myfly+" sucsessfully created"+"\n");
		}
		try {
			Files.write(myfly, file, StandardOpenOption.WRITE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
			log.append("----------------------------\nFILE "+myfly+" WRITE ERROR"+"\n----------------------------\n\n");
		}

		log.append("----------------------------\nAssembly complete\n----------------------------\n\n");
	}
	
	
	static void uiInic(SimpleUI ui) {
		ActionListener variables = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loader_file = (File) ui.getSelectedInBox(loader);
				code_file = (File) ui.getSelectedInBox(code);
				res_file = (File) ui.getSelectedInBox(res);
				output_file = ui.getText(output, SimpleUI.TEXTBOX);
				
				ui.setPanel(ws);
				System.out.println("Loader: "+loader_file.getName()+"/n Code: "+code_file.getName()+"/n Resource: "+res_file.getName()+"/n Output file: "+output_file);
			}
		};
		
		//byte[] file = new byte[32768];
		File dir = new File(System.getProperty("user.dir"));
		try {
			System.out.println(dir.getCanonicalFile());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File[] contents = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				String filename = f.getPath();
				int i = filename.lastIndexOf(".");
				if((i > 0) && (i < filename.length()-1)) {
					String[] splitt = filename.split("\\.");
					if(splitt.length>0 && splitt[splitt.length-1].equalsIgnoreCase("bin")) {
					return true;
					}
				}
				return false;
			}			
		});
		for(int i = 0; i < contents.length; i++) {
			System.out.println(contents[i]);
		}
		
		JPanel inner = ui.createPanel(250, 500, ws);
		SpringLayout lay = new SpringLayout();
		inner.setLayout(lay);
		
		//ui.setMinimumSize(new Dimension(250,500));
		//ui.setMaximumSize(new Dimension(250,500));
		ui.setResizable(false);
		
		ui.addLabel("Select loader binary", layout);
		ui.addBox(loader, layout);
		ui.addItemInBox(loader, contents);
		
		ui.addLabel("Select code binary", layout);
		ui.addBox(code, layout);
		ui.addItemInBox(code, contents);
		
		ui.addLabel("Select res binary", layout);
		ui.addBox(res, layout);
		ui.addItemInBox(res, contents);

		ui.addLabel("Enter output file name", layout);
		ui.addText(output, layout, 23);
		
		ui.addButton("confirm", "Confirm", layout);
		ui.setOnClickListener("confirm", variables);
		
		JLabel fsizeLbl = ui.addLabel("Enter file size in kb", ws);
		JTextField fsize = ui.addText(ws, 5);
		
		JLabel csizeLbl = ui.addLabel("Enter code sectors", ws);
		JTextField csize = ui.addText(ws, 5);

		JLabel rsizeLbl = ui.addLabel("Enter res sectors", ws);
		JTextField rsize = ui.addText(ws, 5);		
		
		
		JTextArea textlog = new JTextArea(10, 23);
        textlog.setText("");
        
        // Параметры переноса слов
        textlog.setLineWrap(true);
        textlog.setWrapStyleWord(true);
        textlog.setEditable(false);
        
        //inner.add(textlog);
        
        JScrollPane pane = new JScrollPane(textlog);
        
        inner.add(pane);
		
		JButton compbtn = ui.addButton("Assemble", ws);
		
		compbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				compile(textlog, fsize, csize, rsize);
			}
			
		});
		
		lay.putConstraint(SpringLayout.WEST, fsizeLbl, 5, SpringLayout.WEST, inner);
		lay.putConstraint(SpringLayout.NORTH, fsizeLbl, 5, SpringLayout.NORTH, inner);
		lay.putConstraint(SpringLayout.EAST, fsize, -20, SpringLayout.EAST, inner);
		lay.putConstraint(SpringLayout.NORTH, fsize, 5, SpringLayout.NORTH, inner);
		lay.putConstraint(SpringLayout.WEST, csizeLbl, 5, SpringLayout.WEST, inner);
		lay.putConstraint(SpringLayout.NORTH, csizeLbl, 50, SpringLayout.NORTH, fsizeLbl);
		lay.putConstraint(SpringLayout.EAST, csize, -20, SpringLayout.EAST, inner);
		lay.putConstraint(SpringLayout.NORTH, csize, 50, SpringLayout.NORTH, fsize);
		lay.putConstraint(SpringLayout.WEST, rsizeLbl, 5, SpringLayout.WEST, inner);
		lay.putConstraint(SpringLayout.NORTH, rsizeLbl, 50, SpringLayout.NORTH, csizeLbl);
		lay.putConstraint(SpringLayout.EAST, rsize, -20, SpringLayout.EAST, inner);
		lay.putConstraint(SpringLayout.NORTH, rsize, 50, SpringLayout.NORTH, csize);
		

		lay.putConstraint(SpringLayout.WEST, pane, 5, SpringLayout.WEST, inner);
		lay.putConstraint(SpringLayout.SOUTH, pane, -100, SpringLayout.SOUTH, inner);

		//lay.putConstraint(SpringLayout.WEST, textlog, 5, SpringLayout.WEST, inner);
		//lay.putConstraint(SpringLayout.SOUTH, textlog, -50, SpringLayout.SOUTH, inner);
		

		lay.putConstraint(SpringLayout.WEST, compbtn, 70, SpringLayout.WEST, inner);
		lay.putConstraint(SpringLayout.NORTH, compbtn, 5, SpringLayout.SOUTH, pane);
		
		ui.setLayout(layout, SimpleUI.FLOW_LAYOUT);

		ui.pack();
		//ui.setLocationByPlatform(true);
		ui.setLocationRelativeTo(null);
		SimpleUI.setDefaultLookAndFeelDecorated(true);
		ui.setVisible(true);
	}
	
	
	
	public static void main(String[] args) throws IOException {
			
			SimpleUI ui = new SimpleUI(250, 500, "Окно");
			uiInic(ui);
						
	}

}
