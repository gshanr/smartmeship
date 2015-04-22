/**
 * LooCI Copyright (C) 2013 KU Leuven.
 * All rights reserved.
 *
 * LooCI is an open-source software development kit for developing and 
 * maintaining networked embedded applications;
 * it is distributed under a dual-use software license model:
 *
 * 1. Non-commercial use:
 * Non-Profits, Academic Institutions, and Private Individuals can redistribute 
 * and/or modify LooCI code under the terms of the GNU General Public License 
 * version 3, as published by the Free Software Foundation
 * (http://www.gnu.org/licenses/gpl.html).
 *
 * 2. Commercial use:
 * In order to apply LooCI in commercial code, a dedicated software license must 
 * be negotiated with KU Leuven Research & Development.
 *
 * Contact information:
 *  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
 *  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
 * Address:
 *  iMinds-DistriNet, KU Leuven
 *  Celestijnenlaan 200A - PB 2402,
 *  B-3001 Leuven,
 *  BELGIUM. 
 **/
package looci.osgi.mgtTerminal;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * This Filter makes sure that only the last line in the Terminal is editable.
 * 
 * @author klaas
 */
public class Filter extends DocumentFilter {

    private JTextArea area;
    
    public Filter(JTextArea area) {
        this.area = area;
    }
    
    public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
            throws BadLocationException {
        super.insertString(fb, offset, string, attr);
    }

    public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
        if (offset >= area.getLineStartOffset(area.getLineCount() - 1)) {
            super.remove(fb, offset, length);
        } else {
            // remove is not allowed.
        }
    }

    public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)
            throws BadLocationException {
        if (offset >= area.getLineStartOffset(area.getLineCount() - 1)) {
            super.replace(fb, offset, length, text, attrs);
        } else {
            // replace is not allowed.
        }
    }
}