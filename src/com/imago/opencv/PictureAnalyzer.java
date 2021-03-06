package com.imago.opencv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class PictureAnalyzer {
	static final int max_images=52;
	public static JSONArray searchImage(String real_path) throws JSONException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		HashMap<String,Integer>similarity=new HashMap();
		String query_image = real_path;
		//String query_image ="/home/joey/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/imago/Fruits//img_1.jpg";
		MatOfKeyPoint
				objectDescriptors = feature_extract(query_image);
		
		for (int i = 1; i <= max_images; i++) {
	
			String test_image = "/home/joey/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/imago/Fruits//img_"+i+".jpg";
			String filename="Fruits//img_"+i+".jpg";
//			String query_image = "SampleImages//bookobject.jpg";
//			String test_image = "SampleImages//bookscene.jpg";
			
			MatOfKeyPoint sceneDescriptors = feature_extract(test_image);
			ArrayList<KeyPoint>keypoints=test(objectDescriptors,sceneDescriptors);
			int similarity_score=0;

			//System.out.println(test_image+"=>"+simlarity_score);
			similarity.put(filename, similarity_score);

			

		}
		Set<Entry<String, Integer>> set = similarity.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
		{
			public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
			{
				if(o1.getValue()<o2.getValue() )
					return 0;
				else return -1;//Ascending order

			}
		} );
		JSONArray results=new JSONArray();
		for(Map.Entry<String, Integer> entry:list){
			System.out.println(entry.getKey()+" ==== "+entry.getValue());
			if(entry.getValue()>0){
		        
				JSONObject jobject=new JSONObject();
				jobject.put("file", entry.getKey());
			jobject.put("similarity", entry.getValue());
				
		        results.put(jobject);
			}
	
	        
	    }
		return results;
		
	}
	public static void main(String[] args) {
	try {
		searchImage("Fruits/img.jpg");
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	public static MatOfKeyPoint feature_extract(String query_image){

		
		Mat queryImage = Highgui.imread(query_image, Highgui.CV_LOAD_IMAGE_COLOR);
	

		MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
		FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
		//		System.out.println("Detecting key points...");
		featureDetector.detect(queryImage, objectKeyPoints);
		
		KeyPoint[] keypoints = objectKeyPoints.toArray();
//				System.out.println(keypoints.length);
//				for (KeyPoint point:keypoints){
//					System.out.println(point.angle);
//				}

		MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
		DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
		//		System.out.println("Computing descriptors...");
		descriptorExtractor.compute(queryImage, objectKeyPoints, objectDescriptors);	
		return objectDescriptors;
	}
	
	public static ArrayList<KeyPoint> test(MatOfKeyPoint objectDescriptors,MatOfKeyPoint sceneDescriptors) {

	




		List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
		DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
		//		System.out.println("Matching object and scene images...");
	//	descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);
		KeyPoint[] keys=objectDescriptors.toArray();
		ArrayList<KeyPoint>keypoints=new ArrayList<KeyPoint>();
		
		int feature=0;
		for(int i=0;i<keys.length;i++){
	//		System.out.println(keys[i].angle+" "+keys[i].octave+" "+keys[i].response+" "+keys[i].size);
			if(keys[i].angle!=0){
				feature++;
				keypoints.add(keys[i]);
			}
			
		}
		System.out.println(keys.length+" "+feature);
		return keypoints;
		
//
//		//		System.out.println("Calculating good match list...");
//		LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
//
//		float nndrRatio = 0.7f;
//
//		for (int i = 0; i < matches.size(); i++) {
//			MatOfDMatch matofDMatch = matches.get(i);
//			DMatch[] dmatcharray = matofDMatch.toArray();
//			DMatch m1 = dmatcharray[0];
//			DMatch m2 = dmatcharray[1];
//
//			if (m1.distance <= m2.distance * nndrRatio) {
//				goodMatchesList.addLast(m1);
//
//			}
//		}
//		return goodMatchesList.size();

		//		if (goodMatchesList.size() >= 5) {
		//			System.out.println("Object Found!!!");
		//
		//			List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
		//			List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();
		//
		//			LinkedList<Point> objectPoints = new LinkedList<>();
		//			LinkedList<Point> scenePoints = new LinkedList<>();
		//
		//			for (int i = 0; i < goodMatchesList.size(); i++) {
		//				objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
		//				scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
		//			}
		//
		//			MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
		//			objMatOfPoint2f.fromList(objectPoints);
		//			MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
		//			scnMatOfPoint2f.fromList(scenePoints);
		//
		//			Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);
		//
		//			Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
		//			Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);
		//
		//			obj_corners.put(0, 0, new double[]{0, 0});
		//			obj_corners.put(1, 0, new double[]{queryImage.cols(), 0});
		//			obj_corners.put(2, 0, new double[]{queryImage.cols(), queryImage.rows()});
		//			obj_corners.put(3, 0, new double[]{0, queryImage.rows()});
		//
		//			System.out.println("Transforming object corners to scene corners...");
		//			Core.perspectiveTransform(obj_corners, scene_corners, homography);
		//
		//			Mat img = Highgui.imread(test_image, Highgui.CV_LOAD_IMAGE_COLOR);
		//
		//			Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
		//			Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
		//			Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
		//			Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
		//
		//			System.out.println("Drawing matches image...");
		//			MatOfDMatch goodMatches = new MatOfDMatch();
		//			goodMatches.fromList(goodMatchesList);
		//
		//			Features2d.drawMatches(queryImage, objectKeyPoints, testImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);
		//
		//			Highgui.imwrite("Fruits//outputImage.jpg", outputImage);
		//			Highgui.imwrite("Fruits//matchoutput.jpg", matchoutput);
		//			Highgui.imwrite("Fruits//img.jpg", img);
		//		} else {
		//			System.out.println("Object Not Found");
		//		}

		//		System.out.println("Ended....");
	}
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

}
