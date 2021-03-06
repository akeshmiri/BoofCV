/*
 * Copyright (c) 2011-2019, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.distort.spherical;

import boofcv.alg.distort.brown.LensDistortionBrown;
import boofcv.alg.distort.pinhole.LensDistortionPinhole;
import boofcv.alg.distort.universal.LensDistortionUniversalOmni;
import boofcv.struct.calib.CameraPinhole;
import boofcv.struct.calib.CameraPinholeBrown;
import boofcv.struct.calib.CameraUniversalOmni;
import boofcv.struct.distort.PixelTransform;
import boofcv.struct.distort.Point2Transform2_F64;
import boofcv.struct.distort.Point2Transform3_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point3D_F64;

/**
 * Given a transform from a pixel to normalized image coordinates or spherical it will define an equirectangular
 * transform.
 *
 * @author Peter Abeles
 */
public class CameraToEquirectangular_F64
		extends EquirectangularDistortBase_F64
{
	public void setCameraModel( CameraPinholeBrown camera ) {
		Point2Transform2_F64 pixelToNormalized =
				new LensDistortionBrown(camera).undistort_F64(true,false);
		setCameraModel(camera.width,camera.height,pixelToNormalized);
	}

	public void setCameraModel( CameraPinhole camera ) {
		Point2Transform2_F64 pixelToNormalized =
				new LensDistortionPinhole(camera).undistort_F64(true,false);
		setCameraModel(camera.width,camera.height,pixelToNormalized);
	}

	public void setCameraModel( CameraUniversalOmni camera ) {
		Point2Transform3_F64 pixelToSpherical =
				new LensDistortionUniversalOmni(camera).undistortPtoS_F64();
		setCameraModel(camera.width,camera.height,pixelToSpherical);
	}


	public void setCameraModel( int width , int height , Point2Transform2_F64 pixelToNormalized )
	{
		declareVectors( width, height );

		// computing the 3D ray through each pixel in the pinhole camera at it's canonical
		// location

		Point2D_F64 norm = new Point2D_F64();
		for (int pixelY = 0; pixelY < height; pixelY++) {
			int index = pixelY*width;
			for (int pixelX = 0; pixelX < width; pixelX++) {
				pixelToNormalized.compute(pixelX, pixelY, norm);
				Point3D_F64 v = vectors[index++];

				v.set(norm.x,norm.y,1);
			}
		}
	}

	public void setCameraModel( int width , int height , Point2Transform3_F64 pixelToNSpherical )
	{
		declareVectors( width, height );

		for (int pixelY = 0; pixelY < height; pixelY++) {
			int index = pixelY*width;
			for (int pixelX = 0; pixelX < width; pixelX++) {
				pixelToNSpherical.compute(pixelX, pixelY, vectors[index++]);
			}
		}
	}

	@Override
	public CameraToEquirectangular_F64 copy() {
		throw new RuntimeException("Implement");
	}
}
