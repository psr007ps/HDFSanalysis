/**
 * 
 */
package HDFSUpload.edu.ucr.cs.cs226.pshri002;

/**
 * @author pranshu shrivastava
 *
 */


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
public class HDFSUpload {
	public static void copy(FileSystem fileSystem, Path outputPath, String arg0)
			throws IOException {
		byte[] byte1 = new byte[512];
		FSDataOutputStream outputStream = fileSystem.create(outputPath);
		InputStream inputStream = new BufferedInputStream(new FileInputStream(arg0));
		try {

		while (inputStream.read(byte1) > 0) {
			outputStream.write(byte1);
		}
		
		}
		catch(IOException e){
			System.out.println("--------Error: The file was not created in HDFS due to some error.------------");
            e.printStackTrace();
        }
		finally
		{
			
			inputStream.close();
			outputStream.close();
		}
	}
	

	public static void read(FileSystem fileSystem, Path outputPath)
			throws IOException {

		byte[] byte2 = new byte[512];
		FSDataInputStream inputStream = fileSystem.open(outputPath);
		

		
			
			File file = new File("/home/pranshu/readMe");
			FileOutputStream outputStream = new FileOutputStream(file);
			try {
			while (inputStream.read(byte2) > 0) {
				
				outputStream.write(byte2);
			}
							
			}
			catch(IOException e){
				System.out.println("--------Error: The file was not created in local due to some error.------------");
				e.printStackTrace();
	        }
			finally
			{
				
				inputStream.close();
				outputStream.close();
			}
	}

	public static void random(FileSystem fileSystem, Path outputPath) throws IOException {
		FSDataInputStream inputStream = fileSystem.open(outputPath);
		Random randomGenerator = new Random();
		byte[] byte3 = new byte[2048];
		try {
		for (int i = 0; i < 2000; i++) {
			long randomNumber = (long) randomGenerator.nextInt(200000);
			inputStream.seek(randomNumber);
			inputStream.read(byte3, 0, 1024); 
		}
		

		
	}
		catch(IOException e){
			System.out.println("--------Error: Some error occured while randomly reading the file.------------");
            e.printStackTrace();
        }
		finally
		{
			inputStream.close();
			
		}	
	}
		


	public static FileSystem fileSystem = null;

     public static void main(String[] args) throws IOException, InterruptedException {
    	 	 try {   	
         String arg0 = args[0];
         String arg1 = args[1];
         Path outputPath = new Path(arg1);
         Configuration config = new Configuration();
         fileSystem = FileSystem.get(config);	
			

			// check if input file doesn't exist
			File file = new File(args[0]);
			if (!file.exists()) {
				System.out.println("-------ERROR : The input file doesn't exist-------");
				System.exit(0);
			}

			// Check if a file in HDFS already exists
			if (fileSystem.exists(outputPath)) {
				System.out.println("-------ERROR : The file already exists in HDFS------");
				System.exit(0);
			}
			
         //code to copy
         Long intialTimeToReadFromLocal = Calendar.getInstance().getTimeInMillis();
         copy(fileSystem, outputPath, arg0);											//function call to copy the file from local to HDFS
         Long finalTimeToCopyToHDFS = Calendar.getInstance().getTimeInMillis();
     	 Long timeTakenToMove = finalTimeToCopyToHDFS - intialTimeToReadFromLocal;
       	 System.out.println("Time taken to copy 2GB file from local to HDFS: " + timeTakenToMove + " milliseconds");
     	
       	 
       	 //code to read
     	 Long intialTimeToReadHDFS = Calendar.getInstance().getTimeInMillis();
      	 read(fileSystem, outputPath);													//function call to read the file in HDFS and create a README file in local FS
         Long finalTimeToReadHDFS = Calendar.getInstance().getTimeInMillis();
  		Long timeTakenToReadHDFS = finalTimeToReadHDFS - intialTimeToReadHDFS;
  		System.out.println("Time taken to read 2GB file sequentially from HDFS: " + timeTakenToReadHDFS + " milliseconds");
  		
  		
  		//code to randomly read
  		Long intialTimeForReadingRandomly = Calendar.getInstance().getTimeInMillis(); 
  		random(fileSystem, outputPath);													//function call to randomly read 1KB out of 2000 random access
         Long finalTimeForReadingRandomly = Calendar.getInstance().getTimeInMillis();
    		Long timeTakenForRandomRead = finalTimeForReadingRandomly - intialTimeForReadingRandomly;
    		System.out.println("Time taken to make 2,000 random accesses, each of size 1KB is " + timeTakenForRandomRead + " milliseconds");
    		System.out.println("--------------------------------Performance Summary Start-------------------------------------------");
    		System.out.println("Time taken to copy 2GB file from local to HDFS: " + timeTakenToMove + " milliseconds");
     		System.out.println("Time taken to read 2GB file sequentially from HDFS: " + timeTakenToReadHDFS + " milliseconds");
     		System.out.println("Time taken to make 2,000 random accesses, each of size 1KB is " + timeTakenForRandomRead + " milliseconds");
     		System.out.println("--------------------------------Performance Summary End-------------------------------------------");
     	    	 
}
     catch (Exception e) {
    	 System.out.println("--------Error: Some Error Occured.------------");
			e.printStackTrace();
		} finally {
			
				fileSystem.close();
			
		}
}

}
