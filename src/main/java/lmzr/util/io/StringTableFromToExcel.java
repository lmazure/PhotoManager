package lmzr.util.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.util.LinkedList;

/**
 * This class enables reading/writing of Excel files in tab-separated format
 * @author Laurent Mazuré
 */
public class StringTableFromToExcel {

    /**
     * @param filename
     * @param data
     * @throws IOException (maybe a FileNotFoundException)
     */
    static public void save(final String filename,
                            final String[][] data) throws IOException {

        try (final BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(filename))) {
            for (final String[] element : data) {
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final OutputStreamWriter stream = new OutputStreamWriter(buffer,"Cp1252");
                for (int i = 0; i < element.length; i++) {
                    if (i > 0) {
                        stream.write("\t");
                    }
                    stream.write("\"" + element[i].replaceAll("\"", "\"\"") + "\"");
                }
                stream.flush();
                final byte buf[] = buffer.toByteArray();
                for (final byte element2 : buf) {
                    if (element2!='\r') {
                        file.write(element2);
                    }
                }
                file.write('\r');
                file.write('\n');
            }
        }
    }

    /**
     * @param filename
     * @return the data contained in the Excel sheet as an arrays of arrays of strings
     * @throws IOException (maybe a FileNotFoundException)
     * @throws ProtocolException
     */
    static public String[][] read(final String filename) throws IOException,
                                                                ProtocolException {

        final LinkedList<String[]> theList = new LinkedList<>();

        try (final FileInputStream stream = new FileInputStream(filename);
             final InputStreamReader freader = new InputStreamReader(stream,"Cp1252");
             final BufferedReader reader = new BufferedReader(freader)) {
            String str;
            while ( ( str=reader.readLine()) != null ) {
                final LinkedList<String> list = new LinkedList<>();
                boolean inDoubleQuote = false;
                String currentString = "";
                boolean cont = true;
                while (cont) {
                    for (int i=0; i<str.length(); i++) {
                        if ( str.charAt(i) == '\t' ) {
                            if ( inDoubleQuote) {
                                // tab inside a cell
                                currentString += '\t';
                            } else {
                                // end of a cell
                                list.add(currentString);
                                currentString = "";
                            }
                        } else if (str.charAt(i) == '"') {
                            if ( (currentString.length() == 0)  && !inDoubleQuote ) {
                                // beginning of a double-quotes cell
                                inDoubleQuote = true;
                            } else if ( (str.length()>(i+1)) && (str.charAt(i+1)=='\t') && inDoubleQuote ) {
                                // end of a double-quoted cell
                                inDoubleQuote = false;
                            } else if ( (str.length()==(i+1)) && inDoubleQuote ) {
                                // end of a double-quoted cell which is the last of the row
                                inDoubleQuote = false;
                            } else if ( (str.length()>(i+1)) && (str.charAt(i+1)=='"') && inDoubleQuote ) {
                                // double-quote inside a cell (so this one must be double-quoted)
                                currentString += '"';
                                i++;
                            } else {
                                throw new ProtocolException("invalid line \"" + str + "\" in file " + filename + " (issue with double quote)");
                            }
                        } else {
                            currentString += str.charAt(i);
                        }
                    }
                    if (inDoubleQuote) {
                        // new line inside a cell (so this one must be double-quoted)
                        currentString += '\n';
                        final String s = reader.readLine();
                        if (s == null) {
                            throw new ProtocolException("invalid line \"" + str + "\" in file " + filename + " (issue with new line)");
                        }
                        str = s;
                    } else {
                        // end of the last cell of the row
                        list.add(currentString);
                        theList.add(list.toArray(new String[1]));
                        cont = false;
                    }
                }
            }
        }

        return theList.toArray(new String[0][]);
    }

}
