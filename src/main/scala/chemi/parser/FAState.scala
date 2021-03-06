package chemi.parser

import cats.data.{NonEmptyList, Validated}
import cats.data.Validated.{Invalid, Valid}
import chemi.ValRes

/**
 * A state in a Finite State Automaton
 *
 */
sealed trait FAState[A] {
  /**
   * Calculates a new automaton state and product from
   * the actual product a and character c.
   */
  def next(a: A, c: Char): FARes[A]
}

object FAState {
  /**
   * A dummy automaton state that always returns itself and the
   * unmodified product.   // Validated[A, B]
   */
  def dummy[A]: FAState[A] = apply ((a, _) => Valid((dummy, a)))

  def apply[A](f: (A, Char) ⇒ FARes[A]): FAState[A] = new FAState[A] {
    def next(a: A, c: Char) = f(a, c)
  }

  /**
   * Parse string s starting with automaton state fas and product a.
   * If an error occurs, the actual position in the string is included
   * in the error message.
   */
  def parse[A] (s: String, fas: FAState[A], a: A): ValRes[A] = {
    def fail (pos: Int)(nel: NonEmptyList[String]) =
      Invalid(nel map ("Pos. %d in %s: %s" format (pos, s, _)))

    @scala.annotation.tailrec
    def fsa (s: String, fas: FAState[A], a: A, pos: Int): ValRes[A] = s match {
      case "" ⇒ fas next (a, EOT) fold (fail(pos), fa => Validated.Valid(fa._2))
      case cs ⇒ fas next (a, cs.head) match {
        case Validated.Invalid(ss)               ⇒ fail(pos)(ss)
        case Validated.Valid((newFas, newA))   ⇒ fsa(cs.tail, newFas, newA, pos + 1)
      }
    }

    fsa(s, fas, a, 1)
  }
}