/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.struct.image;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestImageInt16 extends StandardImageTests {


	@Override
	public ImageBase createImage(int width, int height) {
		return new ImageInt16(width, height);
	}

	@Override
	public Number randomNumber() {
		return (short) (rand.nextInt(Short.MAX_VALUE - Short.MIN_VALUE) - Short.MIN_VALUE);
	}

	@Test
	public void getU() {
		ImageInt16 a = new ImageInt16(2, 2);

		a.set(0, 1, 5);
		a.set(1, 1, Short.MAX_VALUE + 1);

		assertEquals(5, a.get(0, 1));
		assertEquals(5, a.getU(0, 1));
		assertEquals(Short.MAX_VALUE + 1, a.getU(1, 1));
		assertTrue(Short.MAX_VALUE + 1 != a.get(1, 1));
	}
}
