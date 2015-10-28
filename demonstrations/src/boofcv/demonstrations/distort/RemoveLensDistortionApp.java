/*
 * Copyright (c) 2011-2015, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.demonstrations.distort;

import boofcv.alg.distort.*;
import boofcv.alg.interpolate.InterpolatePixelS;
import boofcv.core.image.border.BorderType;
import boofcv.factory.distort.FactoryDistort;
import boofcv.factory.interpolate.FactoryInterpolation;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.SelectAlgorithmAndInputPanel;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.PathLabel;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.distort.PointTransform_F32;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Displays undistorted images with different types of adjustments for visibility.
 *
 * @author Peter Abeles
 */
public class RemoveLensDistortionApp extends SelectAlgorithmAndInputPanel {

	ListDisplayPanel gui = new ListDisplayPanel();

	IntrinsicParameters param;

	// distorted input
	MultiSpectral<ImageFloat32> dist;

	// storage for undistorted image
	MultiSpectral<ImageFloat32> undist;

	boolean hasProcessed = false;

	public RemoveLensDistortionApp() {
		super(0);

		setMainGUI(gui);
	}

	public void configure( final BufferedImage orig , IntrinsicParameters param )
	{
		this.param = param;

		// distorted image
		dist = ConvertBufferedImage.convertFromMulti(orig, null,true, ImageFloat32.class);

		// storage for undistorted image
		undist = new MultiSpectral<ImageFloat32>(ImageFloat32.class,
				dist.getWidth(),dist.getHeight(),dist.getNumBands());

		// show results and draw a horizontal line where the user clicks to see rectification easier
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.reset();
				gui.addItem(new ImagePanel(orig), "Original");
			}
		});

		// add different types of adjustments
		PointTransform_F32 add_p_to_p = LensDistortionOps.transformPoint(param).distort_F32(true,true);
		addUndistorted("No Adjustment", add_p_to_p);
		PointTransform_F32 shrink = LensDistortionOps.transform_F32(AdjustmentType.EXPAND, param, null, true);
		addUndistorted("Shrink", shrink);
		PointTransform_F32 fullView = LensDistortionOps.transform_F32(AdjustmentType.FULL_VIEW,param, null,true);
		addUndistorted("Full View", fullView);

		hasProcessed = true;
	}

	private void addUndistorted(final String name, final PointTransform_F32 model) {
		// Set up image distort
		InterpolatePixelS<ImageFloat32> interp = FactoryInterpolation.bilinearPixelS(ImageFloat32.class, BorderType.VALUE);
		ImageDistort<ImageFloat32,ImageFloat32> undistorter =
				FactoryDistort.distortSB(false, interp, ImageFloat32.class);
		undistorter.setModel(new PointToPixelTransform_F32(model));

		DistortImageOps.distortMS(dist, undist, undistorter);

		final BufferedImage out = ConvertBufferedImage.convertTo(undist,null,true);

		// Add this rectified image
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.addItem(new ImagePanel(out), name);
			}});
	}

	@Override
	public void refreshAll(Object[] cookies) {}

	@Override
	public void setActiveAlgorithm(int indexFamily, String name, Object cookie) {}

	@Override
	public void changeInput(String name, int index) {
		PathLabel refs = inputRefs.get(index);

		IntrinsicParameters param = UtilIO.loadXML(media.openFile(refs.getPath(0)));
		BufferedImage orig = media.openImage(refs.getPath(1));

		configure(orig,param);
	}

	@Override
	public void loadConfigurationFile(String fileName) {}

	@Override
	public boolean getHasProcessedImage() {
		return hasProcessed;
	}

	public static void main( String args[] ) {
		RemoveLensDistortionApp app = new RemoveLensDistortionApp();

		// camera config, image left, image right
		String calibDir = UtilIO.pathExample("calibration/mono/Sony_DSC-HX5V_Chess/");
		String imageDir = UtilIO.pathExample("structure/");
		String bumbleDir = UtilIO.pathExample("calibration/stereo/Bumblebee2_Chess/");

		java.util.List<PathLabel> inputs = new ArrayList<PathLabel>();
		inputs.add(new PathLabel("Sony HX5V",calibDir + "intrinsic.xml",imageDir + "dist_cyto_01.jpg"));
		inputs.add(new PathLabel("BumbleBee2",bumbleDir+"intrinsicLeft.xml",bumbleDir + "left01.jpg"));

		app.setInputList(inputs);

		// wait for it to process one image so that the size isn't all screwed up
		while( !app.getHasProcessedImage() ) {
			Thread.yield();
		}
		ShowImages.showWindow(app, "Remove Lens Distortion",true);

		System.out.println("Done");
	}
}