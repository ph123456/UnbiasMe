import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class FolderChooserButton extends JButton
   implements ActionListener {
   
   JFileChooser chooser;
   String choosertitle;
   File selectedFolder=null;
   
   private boolean gotDimensions;
   private int startWidth, startHeight;
   private static int letterwidth = 8;//for updating size upon selection of directory
   
   private boolean returnFolderSize= false;//if true, read how big the files within the folder are and return them to the Main Class (UnbiasMe.Java)
   
   
  public FolderChooserButton(boolean returnFolderSize) {
	  setText("selectDirectory");
	  addActionListener(this);
	  this.returnFolderSize=returnFolderSize;
   }

  public void actionPerformed(ActionEvent e) {
	  
	  //register size of the button for correct updating
	  if(!gotDimensions){
		  gotDimensions=true;
		  startWidth = getWidth();
		  startHeight = getHeight();
		  System.out.println(startWidth + "," + startHeight);
	  }
	  
        
    chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle(choosertitle);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //
    // disable the "All files" option.
    //
    chooser.setAcceptAllFileFilterUsed(false);
  
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
      System.out.println("getCurrentDirectory(): " 
         +  chooser.getCurrentDirectory());
      System.out.println("getSelectedFile() : " 
         +  chooser.getSelectedFile());
      setText(chooser.getSelectedFile().toString());
      selectedFolder=chooser.getSelectedFile();
      
      //update button size:
      setSize(selectedFolder.toString().length()*letterwidth, startHeight);
      }
    else {
      System.out.println("No Selection ");
      setText("Select directory");
      selectedFolder=null;
      
      //reset size:
      setSize(startWidth, startHeight);
      }
    
    //update size display on UnbiasMe.java:
    if(returnFolderSize){
        getFolderSize(selectedFolder);
    }
     }
   
  //for getting the size of the directory:
  private void getFolderSize(File file){
	  long size = 0;
	  int filenumber=0;
	  
	  if(file!=null){
		  File[] files = file.listFiles();
		  filenumber=files.length;
		  for(int i=0;i<files.length;i++){
			  size+=files[i].length();
		  }
	  }	
			
	String readableBytes = humanReadableByteCount(size,true);
	System.out.println(readableBytes);
	UnbiasMe.label_size_output.setText(readableBytes);
	UnbiasMe.label_fileNumberOutput.setText(filenumber+"");
	}
  
  public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	
  
  
  public Dimension getPreferredSize(){
    return new Dimension(200, 23);
    }
    
  public static void main(String s[]) {
    JFrame frame = new JFrame("");
    FolderChooserButton button = new FolderChooserButton(false);
    frame.addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
          }
        }
      );
    frame.getContentPane().add(button,"Center");
    frame.setSize(400,200);
    frame.setVisible(true);
    }
}