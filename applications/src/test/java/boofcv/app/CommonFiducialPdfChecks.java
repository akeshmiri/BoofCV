/*
 * Copyright (c) 2011-2018, Peter Abeles. All Rights Reserved.
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

package boofcv.app;

import boofcv.io.image.UtilImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.AfterEach;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class CommonFiducialPdfChecks {
	String document_name = "target";

	List<File> opened = new ArrayList<>();

	public BufferedImage loadPDF() throws IOException {
		File f = new File(document_name+".pdf");
		opened.add(f);

		PDDocument document = PDDocument.load(f);
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		if( document.getNumberOfPages() != 1 )
			throw new RuntimeException("Egads");
		BufferedImage output = pdfRenderer.renderImageWithDPI(0, 150, ImageType.RGB);
		document.close();

		return output;
	}

	public BufferedImage loadImage( String name )  {
		File f = new File(document_name+name+".png");
		opened.add(f);
		return UtilImageIO.loadImage(f.getAbsolutePath());
	}

	@AfterEach
	public void cleanup() {
		for (int i = 0; i < opened.size(); i++) {
			opened.get(i).delete();
		}
		opened.clear();
	}
}
