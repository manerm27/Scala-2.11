/* NSC -- new Scala compiler
 * Copyright 2005-2010 LAMP/EPFL
 * @author  Martin Odersky
 */

package scala.tools.nsc
package util

import Chars._

abstract class CharArrayReader { self =>

  val buf: Array[Char]

  def decodeUni: Boolean = true

  /** An error routine to call on bad unicode escapes \\uxxxx. */
  protected def error(offset: Int, msg: String)

  /** the last read character */
  var ch: Char = _

  /** The offset one past the last read character */
  var charOffset: Int = 0

  /** The start offset of the current line */
  var lineStartOffset: Int = 0

  /** The start offset of the line before the current one */
  var lastLineStartOffset: Int = 0

  private var lastUnicodeOffset = -1

  /** Is last character a unicode escape \\uxxxx? */
  def isUnicodeEscape = charOffset == lastUnicodeOffset

  /** Advance one character; reducing CR;LF pairs to just LF */
  final def nextChar() {
    if (charOffset >= buf.length) {
      ch = SU
    } else {
      val c = buf(charOffset)
      ch = c
      charOffset += 1
      if (c == '\\') potentialUnicode()
      else if (c < ' ') { skipCR(); potentialLineEnd() }
    }
  }

  /** Advance one character, leaving CR;LF pairs intact */
  final def nextRawChar() {
    if (charOffset >= buf.length) {
      ch = SU
    } else {
      val c = buf(charOffset)
      ch = c
      charOffset += 1
      if (c == '\\') potentialUnicode()
      else if (c < ' ') potentialLineEnd()
    }
  }

  /** Interpret \\uxxxx escapes */
  private def potentialUnicode() {
    def evenSlashPrefix: Boolean = {
      var p = charOffset - 2
      while (p >= 0 && buf(p) == '\\') p -= 1
      (charOffset - p) % 2 == 0
    }
    def udigit: Int = {
      val d = digit2int(buf(charOffset), 16)
      if (d >= 0) charOffset += 1
      else error(charOffset, "error in unicode escape")
      d
    }
    if (charOffset < buf.length && buf(charOffset) == 'u' && decodeUni && evenSlashPrefix) {
      do charOffset += 1
      while (charOffset < buf.length && buf(charOffset) == 'u')
      val code = udigit << 12 | udigit << 8 | udigit << 4 | udigit
      lastUnicodeOffset = charOffset
      ch = code.toChar
    }
  }

  /** replace CR;LF by LF */
  private def skipCR() {
    if (ch == CR)
      if (charOffset < buf.length && buf(charOffset) == LF) {
        charOffset += 1
        ch = LF
      }
  }

  /** Handle line ends */
  private def potentialLineEnd() {
    if (ch == LF || ch == FF) {
      lastLineStartOffset = lineStartOffset
      lineStartOffset = charOffset
    }
  }

  /** A new reader that takes off at the current character position */
  def lookaheadReader = new CharArrayReader {
    val buf = self.buf
    charOffset = self.charOffset
    ch = self.ch
    override def decodeUni = self.decodeUni
    def error(offset: Int, msg: String) = self.error(offset, msg)
    /** A mystery why CharArrayReader.nextChar() returns Unit */
    def getc() = { nextChar() ; ch }
  }
}
