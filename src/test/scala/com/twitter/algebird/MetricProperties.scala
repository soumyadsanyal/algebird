/*
Copyright 2012 Twitter, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.twitter.algebird

import org.scalacheck._
import org.scalacheck.Prop._

object BaseMetricProperties extends Properties("Metric") with MetricProperties {
  property("double metric") = metricLaws[Double]
  property("int metric") = metricLaws[Int]
  property("float metric") = metricLaws[Float]
  property("long metric") = metricLaws[Long]
  property("short metric") = metricLaws[Short]

  implicit val mapMetric = Metric.minkowskiMapMetric[Int, Double](1)
  property("int double map metric") = metricLaws[Map[Int, Double]]
}

trait MetricProperties {
  def isNonNegative[T : Metric : Arbitrary] = forAll { (a: T, b: T) =>
    beGreaterThan(Metric(a, b), 0.0)
  }
  def isEqualIfZero[T : Metric : Arbitrary] = forAll { (a: T) =>
    beCloseTo(Metric(a, a), 0.0)
  }
  def isSymmetric[T : Metric : Arbitrary] = forAll { (a: T, b: T) =>
    beCloseTo(Metric(a, b), Metric(b, a))
  }
  def satisfiesTriangleInequality[T : Metric : Arbitrary] = forAll { (a: T, b:T, c: T) =>
    beGreaterThan(Metric(a, b) + Metric(b, c), Metric(a, c))
  }

  def metricLaws[T : Metric : Arbitrary] =
    isNonNegative[T] && isEqualIfZero[T] && isSymmetric[T] && satisfiesTriangleInequality[T]

  // TODO: are there methods in scalacheck that can do these?
  def beCloseTo(a: Double, b: Double, eps: Double = 1e-6) =  a == b || (math.abs(a - b) / math.abs(a)) < eps || (a.isInfinite && b.isInfinite)
  def beGreaterThan(a: Double, b: Double, eps: Double = 1e-10) = a > b || (math.abs(a - b) > -1 * eps) || (a.isInfinite && b.isInfinite)
}
