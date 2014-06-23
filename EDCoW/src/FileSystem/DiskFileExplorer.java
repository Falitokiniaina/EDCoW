package FileSystem;

import java.io.*;

/**
 * This class allows the exploration of the physical memory, management of the files
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */

public class DiskFileExplorer {

    private String initialpath = "";
    private boolean recursivePath = false;
    public int filecount = 0;
    public int dircount = 0;    

/*
 * Constructor
 * @param path path of the folder
 * @param subFolder analysis of the subfolders
 */
    public DiskFileExplorer(String path, boolean subFolder) {
        super();
        this.initialpath = path;
        this.recursivePath = subFolder;
    }
    
    
    public String getInitialpath() {
		return initialpath;
	}

	public void setInitialpath(String initialpath) {
		this.initialpath = initialpath;
	}

    /**
     * List the nwt files of a folder. 
     * @param dir the directory containing the nwt files
     */
	public void listDirectory(String dir) {//list in System.out.println the nwt files of a folder
        File file = new File(dir);
        File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".nwt");
			}
		});
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() == true) {
                    System.out.println("Dossier" + files[i].getAbsolutePath());
                    this.dircount++;
                } else {
                    System.out.println("Fichier " + files[i].getName());
                    this.filecount++;
                }
                if (files[i].isDirectory() == true && this.recursivePath == true) {
                    this.listDirectory(files[i].getAbsolutePath());
                }
            }
        }        
    }
    
    /**
     * list the nwt files of a folder
     * important : this.recursivePath == false so that it just takes care of the current folder only
     * @return List of files ending with .nwt of the folder
     */
	public File[] FilesNWT () { // return in a File table the nwt files of a folder
        File file = new File(initialpath);
        return file.listFiles(new FilenameFilter() { //! important : this.recursivePath == false so that it just takes care of the current folder only
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".nwt");
			}
        });        
    }

	/**
	 * Return in a File table the nt files of a folder
	 * @return list of the nt files of a folder
	 */
	public File[] FilesNT() {
		// TODO Auto-generated method stub
		 // return in a File table the nt files of a folder
        File file = new File(initialpath);
        return file.listFiles(new FilenameFilter() { //! important : this.recursivePath == false so that it just takes care of the current folder only
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".nt");
			}
        });        
    
	}    
    
}

