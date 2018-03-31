package kurven;

import java.util.ArrayList;
import java.util.Date;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import database.Position;
import service.SunPos;

/**
 * Test for class {@link SimpleCurveFitter}.
 */
public class KurvenFormel {

	public void testPolynomialFit() {
		final Random randomizer = new Random(53882150042L);

		final double[] coeff = { 12.9, -3.4, 2.1 }; // 12.9 - 3.4 x + 2.1 x^2
		final PolynomialFunction f = new PolynomialFunction(coeff);

		// Collect data from a known polynomial.
		final WeightedObservedPoints obs = new WeightedObservedPoints();
		for (int i = 0; i < 10; i++) {
			final double x = randomizer.nextInt(100);
			obs.add(x, f.value(x) + 0.1 * randomizer.nextGaussian());
		}

		final ParametricUnivariateFunction function = new PolynomialFunction.Parametric();
		// Start fit from initial guesses that are far from the optimal values.
		// final SimpleCurveFitter fitter = SimpleCurveFitter.create(function,
		// new double[] { -1e20, 3e15, -5e25 });
		final SimpleCurveFitter fitter = SimpleCurveFitter.create(function, new double[] { -2e20, 1e15, -1e25 });
		// 2e2 ist 2*10^2 = 2*100
		final double[] best = fitter.fit(obs.toList());
		System.out.println(coeff.length);
		for (double d : coeff) {
			System.out.print(d + ", ");
		}
		System.out.println();
		System.out.println(best.length);
		for (double d : best) {
			System.out.print(d + ", ");
		}
		// funktion ausgeben
		final PolynomialFunction fp = new PolynomialFunction(best);
		List<WeightedObservedPoint> list = obs.toList();
		for (WeightedObservedPoint p : list) {
			// System.out.println("ERGEBNIS x: " + p.getX() + ", y: " + p.getY()
			// + ", y': " + fp.value(p.getX()) + ", delta: " + (p.getY() -
			// f.value(p.getX())));
		}
	}

	public static PolynomialFunction getPolynomialFit(List<Point> pList) {
		PolynomialFunction result = null;
		if (pList == null)
			return result;
		try {

			final WeightedObservedPoints obs = new WeightedObservedPoints();
			for (Point p : pList) {
				obs.add(p.getX(), p.getY());
			}

			final ParametricUnivariateFunction function = new PolynomialFunction.Parametric();
			// Start fit from initial guesses that are far from the optimal
			// values.
			// final SimpleCurveFitter fitter =
			// SimpleCurveFitter.create(function,
			// new double[] { -1e20, 3e15, -5e25 });
			final SimpleCurveFitter fitter = SimpleCurveFitter.create(function, new double[] { -2e20, 1e15, -1e25 });
			// 2e2 ist 2*10^2 = 2*100
			final double[] best = fitter.fit(obs.toList());
			System.out.println("Parameters: " + best.length);
			for (double d : best) {
				// System.out.print(d + ", ");
			}
			// funktion ausgeben
			result = new PolynomialFunction(best);
			List<WeightedObservedPoint> list = obs.toList();
			// for (WeightedObservedPoint p : list) {
			// System.out.println("ERGEBNIS x: " + p.getX() //
			// + ", y: " + p.getY() //
			// + ", y': " + result.value(p.getX()) //
			// + ", delta: " + (p.getY() - result.value(p.getX())));
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static PolynomialFunction getKurveAzimuth(List<Position> list, SunPos sp) {
		// Kurvenfunktion auf Basis der Messwerte ermitteln
		List<Point> pList = new ArrayList<Point>();
		for (Position pos : list) {
			// sonne zu spiegel
			Date d = pos.getDatum();
			pList.add(new Point(sp.getAzimuth(d), pos.getX180()));
		}
		PolynomialFunction f = KurvenFormel.getPolynomialFit(pList);
		return f;
	}

	public static PolynomialFunction getKurveZenith(List<Position> list, SunPos sp) {
		// Kurvenfunktion auf Basis der Messwerte ermitteln
		List<Point> pList = new ArrayList<Point>();
		double a;
		for (Position pos : list) {
			// sonne zu spiegel
			Date d = pos.getDatum();
			// Projektion
			a = Math.asin(Math.sin(Math.toRadians(-pos.getY())) * Math.cos(Math.toRadians(Math.PI * pos.getZ())));
			a = Math.toDegrees(a);
			a = Math.round(100.0 * a) / 100.0;
			pList.add(new Point(sp.getZenith(d), a));
		}
		PolynomialFunction f = KurvenFormel.getPolynomialFit(pList);
		return f;
	}

	public static void main(String[] args) {
		new KurvenFormel().testPolynomialFit();

	}
}
