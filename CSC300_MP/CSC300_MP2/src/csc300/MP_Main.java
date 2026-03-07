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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public static Pair<List<Integer>, List<Integer>> separateCC(int imgH, int imgW, List<List> CCs, double threshold){
        // Todo (Part 3): take the CCs and img resolutions as input, and return two lists of integers
        // the first list: list of indices (like the indices in the blue list in figure 3.b) of CCs that belongs to triangle
        // the second list: list of indices (like the indices in the blue list in figure 3.b) of CCs that belongs to rectangle
        return null;
    }

	public static void main(String[] args) {
		// Given to you as the start point ..., but you can modify how to call these functions to your convenience
		boolean[][] img = loadImage("CSC300_MP/check1.png");

		if (img == null){
			System.out.println("Could not load the input image");
			return;
		}
		if (img.length <= 100) {
			System.out.println(boolImgToString(img));
		}

		Pair<QuickUnion, LinkedList<ArrayList<Integer>>> uf_list = img2UF(img);

		LinkedList<ArrayList<Integer>> cc_list = genCC(uf_list.first, uf_list.second);
		System.out.println(cc_list);
		// TODO (Part 5): Analyze One Image Mode

        // TODO (Part 5): Data Collection Mode
        File f = new File(".");
        File [] files = f.listFiles();
		
	}

}
