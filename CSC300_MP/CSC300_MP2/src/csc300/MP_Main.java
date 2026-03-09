package csc300;

/* 
 * CSC 300 - Mini Project 2
 * 
 * Created by: Kenny Davila
 *
 * Modified by: Fei Xu
 * 
 * Completed by: [Paul Murphy & Alex Pizarro]
 */


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.imageio.ImageIO;


public class MP_Main {
	/*
	 * DO NOT MODIFY UNLESS YOU WANT 0 
	 * 
	 * This function loads a binary image from the specified file name, and 
	 * returns a boolean array representing the image 
	 * */
	public static boolean[][] loadImage(String filename){
		File f = new File(filename);
		try {
			BufferedImage img_buff = ImageIO.read(f);
			Raster raster = img_buff.getData();

			int h = img_buff.getHeight();
			int w = img_buff.getWidth();
			int[] pixel = new int[3];
			boolean[][] img_bool = new boolean[h][w];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					raster.getPixel(x, y, pixel);
					img_bool[y][x] = pixel[0] > 128;
				}
			}
			return img_bool;
		} catch (Exception e) {
			System.out.println("Invalid image file");
			return null;
		}
	}
	
	/*
	 * This function helps you "visualize" a given image as text
	 *
	 * You can modify this to your convenience
	 * But do not change the input and output's data type
	 * */
	public static String boolImgToString(boolean[][] img_bool) {
		StringBuffer buffer = new StringBuffer();
		for (int y = 0; y < img_bool.length; y++) {
			for (int x = 0; x < img_bool[y].length; x++) {
				buffer.append(img_bool[y][x] ? "#" : "-");
			}
			buffer.append("\n");
		}
		
		return buffer.toString();
	}

    public record Pair<T, U>(T first, U second) {

	}

    public static Pair<QuickUnion, LinkedList<ArrayList<Integer>>> img2UF(boolean [][] imgBoolean){
		// Todo (Part 2): takes the boolean array of the image and generates a Union Find with elements on the same CC being connected.
        QuickUnion result_uf = new QuickUnion(imgBoolean.length * imgBoolean[0].length);
		LinkedList<ArrayList<Integer>> result_list = new LinkedList<>();
		int count = 0;

		for (int row = 0; row < imgBoolean.length; row++) {
			for (int col = 0; col < imgBoolean[0].length; col++) {
				if (imgBoolean[row][col]) {
					if (row >= 1 && imgBoolean[row - 1][col]) result_uf.union(count, count - imgBoolean[0].length);
					if (col >= 1 && imgBoolean[row][col - 1]) {
						result_uf.union(count, count - 1);
						result_list.getLast().add(count);
					} else {
						result_list.add(new ArrayList<Integer>());
						result_list.getLast().add(count);
					}
					if (col < imgBoolean[0].length - 1 && imgBoolean[row][col + 1]) result_uf.union(count, count + 1);
					if (row < imgBoolean.length - 1 && imgBoolean[row + 1][col]) result_uf.union(count, count + imgBoolean[0].length);
				}
				count++;
			}
		}

        return new Pair<>(result_uf, result_list);
    }


    public static LinkedList<ArrayList<Integer>> genCC(QuickUnion imgUF,  LinkedList<ArrayList<Integer>> rawCC){
        // Todo (Part 2): take the generated ImgUF from function img2UF(), and output all CCs in the format of list of lists
		int row = 0;
		while (row < rawCC.size()) {
			for (int i = row + 1; i < rawCC.size(); i++) {
				if (imgUF.connected(rawCC.get(i).getFirst(), rawCC.get(row).getFirst())) {
					rawCC.get(row).addAll(rawCC.get(i));
					rawCC.remove(i);
					i--;
				}
			}
			row++;
		}
        return rawCC;
    }

    public static Pair<LinkedList<ArrayList<Integer>>, LinkedList<ArrayList<Integer>>> separateCC(int imgH, int imgW, LinkedList<ArrayList<Integer>> CCs, double threshold){
        // Todo (Part 3): take the CCs and img resolutions as input, and return two lists of integers
		LinkedList<ArrayList<Integer>> rect = new LinkedList<ArrayList<Integer>>();
		LinkedList<ArrayList<Integer>> tri = new LinkedList<ArrayList<Integer>>();
		for(int cur_cc = 0; cur_cc < CCs.size(); cur_cc++) {
			int max_x = 0;
			int min_x = imgW;
			int cur_x;
			int min_y = (int) (CCs.get(cur_cc).getFirst() / imgW);
			int max_y = (int) (CCs.get(cur_cc).getLast() / imgW);

			for (int i = 0; i < CCs.get(cur_cc).size(); i++) {
				cur_x = CCs.get(cur_cc).get(i) % imgW;
				max_x = Math.max(cur_x, max_x);
				min_x = Math.min(cur_x, min_x);
			}
			double ratio = CCs.get(cur_cc).size() / ((max_x - min_x + 1) * (max_y - min_y + 1) * 1.0);
			if (ratio > threshold) rect.add(CCs.get(cur_cc));
			else tri.add(CCs.get(cur_cc));
//			System.out.printf("min_x: %d, max_x: %d, min_y: %d, max_y: %d, ratio: %.3f%n", min_x, max_x, min_y, max_y, ratio);
		}
		return new Pair<LinkedList<ArrayList<Integer>>, LinkedList<ArrayList<Integer>>>(rect, tri);
        // the first list: list of indices (like the indices in the blue list in figure 3.b) of CCs that belongs to triangle
        // the second list: list of indices (like the indices in the blue list in figure 3.b) of CCs that belongs to rectangle
    }


	public static Pair<LinkedList<ArrayList<Integer>>, LinkedList<ArrayList<Integer>>> processSingleImage(boolean[][] img, boolean silent){
		if(!silent && img.length <= 100){
			System.out.println(boolImgToString(img));
		}

		//img2UF
		long startTime = System.nanoTime();
		Pair<QuickUnion, LinkedList<ArrayList<Integer>>> imgProcessPair = img2UF(img);
		long endTime = System.nanoTime();
		if(!silent){
			System.out.printf("Time img2UF: %.6f seconds \n", (endTime - startTime) / 1_000_000_000.0);
		}

		//genCC
		startTime = System.nanoTime();
		LinkedList<ArrayList<Integer>> ccList = genCC(imgProcessPair.first(), imgProcessPair.second());
		endTime = System.nanoTime();
		if(!silent){
			System.out.println("Total CCs: " + ccList.size());
			System.out.printf("Time genCC: %.6f seconds \n", (endTime - startTime) / 1_000_000_000.0);
		}

		//separateCC
		double threshold = 0.75;
		Pair<LinkedList<ArrayList<Integer>>, LinkedList<ArrayList<Integer>>> shapes = separateCC(img.length, img[0].length, ccList, threshold);
		if(!silent){
			System.out.println("Rectangles: " + shapes.first().size());
			System.out.printf("Rectangles list: %s%n", shapes.first);
			System.out.println("Triangles: " + shapes.second().size());
			System.out.printf("Triangles list: %s%n", shapes.second);
		}
		return shapes;
	}

	public static boolean[][] readImage(Scanner scanner){
		while(true){
			System.out.println("Please enter the path of the image: ");
			String userInput = scanner.nextLine();
			boolean[][] img = loadImage(userInput);

			if(img == null){
				System.out.println("Could not read image! Make sure it is a valid PNG");
				continue;
			}
			return img;
		}
	}

	public static Path readDirectory(Scanner scanner){
		while(true){
			System.out.println("Please enter the file directory path: ");
			String userInput = scanner.nextLine();
			Path path = Paths.get(userInput);

			if(Files.exists(path) && Files.isDirectory(path)){
				return path;
			}
			else{
				System.out.println("Error! Path is invalid or not a directory.");
			}
		}
	}

	public static void main(String[] args) {
		// Given to you as the start point ..., but you can modify how to call these functions to your convenience
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running){
			System.out.println("Welcome to Image Analyzer! Please choose an option from the menu:");
			System.out.println("1. Analyze One Image");
			System.out.println("2. Data Collection");
			System.out.println("3. Quit");
			if (!scanner.hasNextInt()){
				System.out.println("Please enter a number!");
				continue;
			}
			int userChoice = scanner.nextInt();
			scanner.nextLine();
			switch(userChoice){

				//Analyze 1 image
				case 1:
					boolean[][] singleImg = readImage(scanner);
					processSingleImage(singleImg, false);
					break;

				//Data Collection
				case 2:
					Path imgDirectory = readDirectory(scanner);
					File folder = imgDirectory.toFile();
					File[] files =  folder.listFiles();

					if(files == null || files.length == 0){
						System.out.println("No files found in the directory.");
						break;
					}

					HashMap<Integer, ArrayList<Double>> resolution_dict = new HashMap<>();
					HashMap<Integer, ArrayList<Double>> cc_dict = new HashMap<>();

					System.out.println("Filename | Avg CCA Time | Rectangles | Triangles");
					System.out.println("-------------------------------------------------");

					for(int i = 0; i< files.length; i++){
						if(files[i].isFile() && files[i].getName().endsWith(".png")){
							boolean[][] currentImg = loadImage(files[i].getAbsolutePath());
							if(currentImg == null){
								continue;
							}

							long totalNanoTime = 0;

							//Analysis runs 5x!
							LinkedList<ArrayList<Integer>> cc_list = new LinkedList<>();
							for(int j=0; j<5; j++){
								long startTime = System.nanoTime();

								Pair<QuickUnion, LinkedList<ArrayList<Integer>>> pair = img2UF(currentImg);
								cc_list = genCC(pair.first(), pair.second());
								long endTime = System.nanoTime();
								totalNanoTime += (endTime - startTime);
							}

							//Calc average for the current image. (I like to convert to seconds for ease of viewing)
							double avgTime = (totalNanoTime/5.0) / 1_000_000_000.0;

							if (resolution_dict.get(currentImg.length * currentImg[0].length) == null) resolution_dict.put(currentImg.length * currentImg[0].length, new ArrayList<Double>());
							resolution_dict.get(currentImg.length * currentImg[0].length).add(avgTime);
							if (cc_dict.get(cc_list.size()) == null) cc_dict.put(cc_list.size(), new ArrayList<Double>());
							cc_dict.get(cc_list.size()).add(avgTime);

							System.out.printf("%s | %.6f seconds | CCs: %d | Pixels in image: %d \n",
									files[i].getName(), avgTime, cc_list.size(), currentImg.length * currentImg[0].length);

						}
					}

					ArrayList<Integer> cc_keys = new ArrayList<Integer>(cc_dict.keySet());
					for (int i = 0; i < cc_keys.size(); i++) {
						ArrayList<Double> cur_list = cc_dict.get(cc_keys.get(i));
						if (cur_list == null || cur_list.isEmpty()) continue;

						// 1. Min, Max, Avg in one pass
						DoubleSummaryStatistics stats = cur_list.stream()
								.mapToDouble(Double::doubleValue)
								.summaryStatistics();

						// 2. Median (requires sorting)
						Collections.sort(cur_list);
						double median;
						int size = cur_list.size();
						if (size % 2 == 0) {
							median = (cur_list.get(size / 2 - 1) + cur_list.get(size / 2)) / 2.0;
						} else {
							median = cur_list.get(size / 2);
						}

						System.out.printf("CCs: %d | min: %f | max: %f | average: %f | median: %f %n",
								cc_keys.get(i), stats.getMin(), stats.getMax(), stats.getAverage(), median);
					}

					ArrayList<Integer> resolution_keys = new ArrayList<Integer>(resolution_dict.keySet());
					for (int i = 0; i < resolution_keys.size(); i++) {
						ArrayList<Double> cur_list = resolution_dict.get(resolution_keys.get(i));
						if (cur_list == null || cur_list.isEmpty()) continue;

						// 1. Min, Max, Avg in one pass
						DoubleSummaryStatistics stats = cur_list.stream()
								.mapToDouble(Double::doubleValue)
								.summaryStatistics();

						// 2. Median (requires sorting)
						Collections.sort(cur_list);
						double median;
						int size = cur_list.size();
						if (size % 2 == 0) {
							median = (cur_list.get(size / 2 - 1) + cur_list.get(size / 2)) / 2.0;
						} else {
							median = cur_list.get(size / 2);
						}

						System.out.printf("Resolution: %d | min: %f | max: %f | average: %f | median: %f %n",
								resolution_keys.get(i), stats.getMin(), stats.getMax(), stats.getAverage(), median);
					}

					System.out.println("Data Collection Complete!");
					break;

				//Quit
				case 3:
					System.out.println("Terminating program.");
					running = false;
					break;

				default:
					throw new RuntimeException("You must choose from one of the three options!");
			}
		}

		// TODO (Part 5): Analyze One Image Mode

        // TODO (Part 5): Data Collection Mode

	}

}
