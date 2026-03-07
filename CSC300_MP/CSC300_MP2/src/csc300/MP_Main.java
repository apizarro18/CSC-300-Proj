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
			System.out.printf("min_x: %d, max_x: %d, min_y: %d, max_y: %d, ratio: %.3f%n", min_x, max_x, min_y, max_y, ratio);
		}
		return new Pair<LinkedList<ArrayList<Integer>>, LinkedList<ArrayList<Integer>>>(rect, tri);
        // the first list: list of indices (like the indices in the blue list in figure 3.b) of CCs that belongs to triangle
        // the second list: list of indices (like the indices in the blue list in figure 3.b) of CCs that belongs to rectangle
    }

	public static void main(String[] args) {
		// Given to you as the start point ..., but you can modify how to call these functions to your convenience
		boolean[][] img = loadImage("CSC300_MP/img_00.png");

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

		Pair<LinkedList<ArrayList<Integer>>, LinkedList<ArrayList<Integer>>> rect_tri = separateCC(img.length, img[0].length, cc_list, 0.75);

		System.out.println(rect_tri.first.size());
		System.out.println(rect_tri.second.size());
		// TODO (Part 5): Analyze One Image Mode

        // TODO (Part 5): Data Collection Mode
        File f = new File(".");
        File [] files = f.listFiles();
		
	}

}
