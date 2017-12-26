package chemi

import cats.implicits._
import scalax.collection.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._

trait Molecules {

  /**
   * Calculates the exact mass for a given molecule. Returns None,
   * if the exact mass for one or more isotopes was not defined.
   */
  def exactMass (molecule: Molecule): Option[Double] = {
    molecule.nodes.foldLeft(None)(_.exactMass)
  }

  /**
   * Calculates the exact mass for a given formula. Returns None,
   * if the exact mass for one or more isotopes was not defined.
   */
  def exactMass (f: Formula): Option[Double] =
    (f.toList map (p => p._1.exactMass map (_ * p._2))).partition(_.isEmpty) match {
      case (Nil, masses) => Some(masses.flatten.sum)
      case _ => None
    }

  /**
   * Calculates the total formula of a molecule
   */
  def formula (molecule: Molecule): Formula = molecule foldMap (v => v.formula)

  /**
   * Calculates the molar weight of a molecule. Returns None
   * if the mass for one or more isotopes was not defined.
   */
  def mass (molecule: Molecule): Option[Double] = molecule foldMap (_.mass)

}

object Molecules extends Molecules