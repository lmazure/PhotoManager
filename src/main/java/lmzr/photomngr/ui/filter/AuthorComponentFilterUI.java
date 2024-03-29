package lmzr.photomngr.ui.filter;

import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import lmzr.photomngr.data.AuthorFactory;
import lmzr.photomngr.data.filter.FilterOnAuthor;

/**
 *
 */
public class AuthorComponentFilterUI extends ComponentFilterUI {

    final private JCheckBox a_check[];

    /**
     * @param label
     * @param authorFactory
     * @param filter
     */
    public AuthorComponentFilterUI(final String label,
                                   final AuthorFactory authorFactory,
                                   final FilterOnAuthor filter) {
        super(label, filter);

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        final String[] authors = authorFactory.getAuthors();
        final HashSet<String> filteredAuthors = filter.getFilteredAuthors();

        a_check = new JCheckBox[authors.length];
        for (int i=0; i<authors.length; i++) {
            a_check[i] = new JCheckBox(authors[i]);
            if (filter.isEnabled()) {
                a_check[i].setSelected(filteredAuthors.contains(authors[i]));
            } else {
                a_check[i].setSelected(true);
            }
            getPane().add(a_check[i]);
        }
    }

    /**
     * @return null if the field is not enabled, the values of the filter otherwise
     */
    public HashSet<String> getValues() {
        final HashSet<String> filteredAuthors = new HashSet<>();
        for (final JCheckBox element : a_check) {
            if ( element.isSelected() ) {
                filteredAuthors.add(element.getText());
            }
        }
        return filteredAuthors;
    }
}
