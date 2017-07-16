import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class UnbiasMe extends JFrame implements ActionListener{

	private JPanel contentPane;
	private FolderChooserButton button_importDir, button_exportDir;
	private JButton button_randomize;
	private JProgressBar progressBar;
	public static JLabel label_size_output,label_fileNumberOutput;
	
	private File importFolder, exportFolder;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UnbiasMe frame = new UnbiasMe();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UnbiasMe() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("UnbiasMe");
		setBounds(100, 100, 600, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label_importDir = new JLabel("Select import directory:");
		label_importDir.setBounds(10, 11, 150, 14);
		contentPane.add(label_importDir);
		
		button_importDir = new FolderChooserButton(true);
		button_importDir.setBounds(150, 7, 200, 23);
		button_importDir.choosertitle="Select import directory";
		contentPane.add(button_importDir);
		
		JLabel label_exportDir = new JLabel("Select export directory:");
		label_exportDir.setBounds(10, 36, 150, 14);
		contentPane.add(label_exportDir);
		
		button_exportDir = new FolderChooserButton(false);
		button_exportDir.setBounds(150, 32, 200, 23);
		button_exportDir.choosertitle="Select export directory";
		contentPane.add(button_exportDir);
		
		JLabel label_size = new JLabel("Size of selected files:");
		label_size.setBounds(10, 61, 122, 14);
		contentPane.add(label_size);
		
		label_size_output = new JLabel("0 byte");
		label_size_output.setBounds(136, 61, 46, 14);
		contentPane.add(label_size_output);
		
		JLabel label_numberOfFiles = new JLabel("Files in folder:");
		label_numberOfFiles.setBounds(10, 86, 122, 14);
		contentPane.add(label_numberOfFiles);
		
		label_fileNumberOutput= new JLabel("0");
		label_fileNumberOutput.setBounds(136, 86, 46, 14);
		contentPane.add(label_fileNumberOutput);
		
		button_randomize = new JButton("Randomize!");
		button_randomize.setBounds(10, 111, 120, 23);
		button_randomize.addActionListener(this);
		contentPane.add(button_randomize);
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10,146,350,23);
		contentPane.add(progressBar);
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.button_randomize){
			System.out.println("randomizing files");
			importFolder = button_importDir.selectedFolder;
			exportFolder = button_exportDir.selectedFolder;
			
			if(importFolder==null){
				JOptionPane.showMessageDialog(this, "No import folder selected.");
			}
			else if(exportFolder==null){
				JOptionPane.showMessageDialog(this, "No export folder selected.");
			}
			else{
				System.out.println("randomizing...");
				randomize();
			}
		}
	}
	
	private void randomize(){
		//1.: create new folder, create second folder if this one already exists
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		String folderName = "UnbiasMe-"+timeStamp;
		
		String newPath = exportFolder.getPath()+'\\'+folderName;
		System.out.println(newPath);
		File newFolder=new File(newPath);
		
		File workingDirectory=null;
		
		if(!newFolder.exists()){
			boolean created = newFolder.mkdir();
			workingDirectory=newFolder;
			if(created){
				System.out.println("created new folder");
			}
			else{
				System.out.println("Something went wrong with the folder creation");
			}
		}
		else{
			System.out.println("folder already exists: cycling through options");
			//cycle through names until an available folder name is found
			int max = 99;
			for(int i = 1;i<=max;i++){
				String pathname=newPath+"("+i+")";
				System.out.println(pathname);
				File newFolder2 = new File(pathname);
				if(!newFolder2.exists()){
					newFolder2.mkdir();
					workingDirectory=newFolder2;
					System.out.println("created Folder: "+pathname);
					break;
				}
			}
		}		
		
		//2.: create random order of files
		int[] shuffle = new int[importFolder.listFiles().length];
		for(int i=0;i<shuffle.length;i++){
			shuffle[i]=i;
		}
		shuffleArray(shuffle);
		
		progressBar.setMaximum(shuffle.length);
		
		//3.: copy the files with random names while keeping their filetype
		File[] importFiles = importFolder.listFiles();
		String[] extensions = new String[importFiles.length];
		
		if(workingDirectory!=null){
			System.out.println("copying files...");
			extensions = getFileExtensions(importFiles);
			
			for(int i=0;i<importFiles.length;i++){
				File target = new File(workingDirectory+"\\"+i+"."+extensions[shuffle[i]]);
				try {
					Files.copy(importFiles[shuffle[i]].toPath(), target.toPath());
					System.out.println("copied file "+importFiles[shuffle[i]].getName()+" as "+target.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("failed to copy "+importFiles[shuffle[i]].getName());
					e.printStackTrace();
				}
				
			}
		}
		
		//4.: create index .txt file:
		try{
			FileWriter fw = new FileWriter(workingDirectory+"\\index.txt");
			for (int i = 0; i < shuffle.length; i++) {
				fw.write(importFiles[shuffle[i]].getName()+","+i+"."+extensions[shuffle[i]]+"\n");
			}
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		
		JOptionPane.showMessageDialog(this, "Exported randomized files to: "+workingDirectory.toPath().toString());
	}
	
	//extract the extensions from a list of files
	private String[] getFileExtensions(File[] filelist){
		//1. create array of name strings
		String[] fileNames = new String[filelist.length];
		for(int i=0; i<fileNames.length;i++){
			fileNames[i] = filelist[i].getName();
		}
		
		//2. get extensions:
		String[] extensions = new String[fileNames.length];
		for(int i=0; i<fileNames.length;i++){
			String extension = "";

			int j = fileNames[i].lastIndexOf('.');
			int p = Math.max(fileNames[i].lastIndexOf('/'), fileNames[i].lastIndexOf('\\'));

			if (j > p) {
			    extension = fileNames[i].substring(j+1);
			}
			
			extensions[i] = extension;
		}
		
		return extensions;
	}
	
	// Fisher–Yates shuffle
	  static void shuffleArray(int[] ar)
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
}
