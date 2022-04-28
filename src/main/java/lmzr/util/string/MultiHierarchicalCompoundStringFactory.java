package lmzr.util.string;

/**
 *
 */
public class MultiHierarchicalCompoundStringFactory {

    private final HierarchicalCompoundStringFactory a_partsFactory;

    /**
     *
     */
    public MultiHierarchicalCompoundStringFactory() {
        this.a_partsFactory = new HierarchicalCompoundStringFactory();
    }

    /**
     * @return
     */
    public HierarchicalCompoundStringFactory getHierarchicalCompoundStringFactory() {
        return this.a_partsFactory;
    }

    //TODO supprimer tous les appels aux méthodes ci-dessous et les remplacer par un appel à la méthode ci dessus

    /**
     * @return
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return this.a_partsFactory.getRoot();
    }

    /**
     * @return root
     */
    public HierarchicalCompoundString getRootAsHierarchicalCompoundString() {
        return this.a_partsFactory.getRootAsHierarchicalCompoundString();
    }

    /**
     * @param string
     * @return newly built MultiHierarchicalCoumpountString
     */
    public MultiHierarchicalCompoundString create(final String string) {
        final String s[] = string.split("\n");
        final HierarchicalCompoundString parts[] = new HierarchicalCompoundString[s.length];
        for (int i=0; i<s.length; i++) {
            parts[i] = this.a_partsFactory.create(s[i]);
        }
        return new MultiHierarchicalCompoundString(parts);
    }
}
