// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.tools;

import org.junit.Assert;
import org.junit.Test;
import org.lemurproject.galago.core.btree.format.BTreeFactory;
import org.lemurproject.galago.core.btree.format.SplitBTreeReader;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.tupleflow.FileUtility;
import org.lemurproject.galago.tupleflow.Utility;
import org.lemurproject.galago.utility.FSUtil;
import org.lemurproject.galago.utility.Parameters;
import org.lemurproject.galago.utility.StreamUtil;
import org.lemurproject.galago.utility.btree.disk.GalagoBTreeReader;
import org.lemurproject.galago.utility.btree.disk.VocabularyReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author trevor
 */
public class AppTest {

    final String newLine = System.getProperty("line.separator");

    public static String trecDocument(String docno, String text) {
        return "<DOC>\n<DOCNO>" + docno + "</DOCNO>\n"
                + "<TEXT>\n" + text + "</TEXT>\n</DOC>\n";
    }

    public static void verifyIndexStructures(File indexPath) throws Exception {
        // Check main path
        assertTrue(indexPath.isDirectory());
        // Time to check standard parts
        Retrieval ret = RetrievalFactory.instance(indexPath.getAbsolutePath(), Parameters.create());
        Parameters availableParts = ret.getAvailableParts();
        assertNotNull(availableParts);

        // ensure that we have (at least) the basic parts
        assertTrue(availableParts.containsKey("lengths"));
        assertTrue(availableParts.containsKey("names"));
        assertTrue(availableParts.containsKey("names.reverse"));
        assertTrue(availableParts.containsKey("postings"));

        for (String part : availableParts.getKeys()){
          File childPath = new File(indexPath, part);
          assertTrue(childPath.exists());
        }

    }

    @Test
    public void testSimplePipeline2() throws Exception {
        File queryFile = null;
        File trecCorpusFile = null;
        File indexFile = null;

        try {
            // create a simple doc file, trec format:
            String trecCorpus = trecDocument("55", "This is a sample document")
                    + trecDocument("59", "sample document two");
            trecCorpusFile = FileUtility.createTemporary();
            StreamUtil.copyStringToFile(trecCorpus, trecCorpusFile);

            // now, try to build an index from that
            indexFile = FileUtility.createTemporaryDirectory();
            App.main(new String[]{"build", "--indexPath=" + indexFile.getAbsolutePath(),
                "--inputPath=" + trecCorpusFile.getAbsolutePath(),
                "--corpus=true", "--server=false"});

            // Checks path and components
            verifyIndexStructures(indexFile);

            // try to batch search that index with a no-match string
            String queries
                    = "{\n"
                    + "\"queries\" : [\n"
                    + "{ \"number\" :\"5\", \"text\" : \"nothing\"},\n"
                    + "{ \"number\" :\"9\", \"text\" : \"sample\"},\n"
                    + "{ \"number\" :\"10\", \"text\" : \"nothing sample\"},\n"
                    + "{ \"number\" :\"14\", \"text\" : \"#combine(#ordered:1(this is) sample)\"},\n"
                    + "{ \"number\" :\"23\", \"text\" : \"#combine( sample sample document document )\"},\n"
                    + "{ \"number\" :\"24\", \"text\" : \"#combine( #combine(sample) two #combine(document document) )\"},\n"
                    + "{ \"number\" :\"25\", \"text\" : \"#combine( sample two document )\"}\n"
                    + "]}\n";
            queryFile = FileUtility.createTemporary();
            StreamUtil.copyStringToFile(queries, queryFile);

            //- batch search -- showNoResults=false
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayStream);

            App.run(new String[]{"batch-search",
                "--index=" + indexFile.getAbsolutePath(),
                "--showNoResults=true",
                queryFile.getAbsolutePath()}, printStream);

            // Now, verify that some stuff exists
            String output = byteArrayStream.toString();

            String expectedScores
		    = "5 Q0 no_results_found 1 -999 galago" + newLine
                    + "9 Q0 59 1 -1.38562925 galago" + newLine
                    + "9 Q0 55 2 -1.38695903 galago" + newLine
                    + "10 Q0 59 1 -2.08010799 galago" + newLine
                    + "10 Q0 55 2 -2.08143777 galago" + newLine
                    + "14 Q0 55 1 -1.73220460 galago" + newLine
                    + "14 Q0 59 2 -1.73353440 galago" + newLine
                    + "23 Q0 59 1 -1.38562925 galago" + newLine
                    + "23 Q0 55 2 -1.38695903 galago" + newLine
                    + "24 Q0 59 1 -1.61579296 galago" + newLine
                    + "24 Q0 55 2 -1.61889580 galago" + newLine
                    + "25 Q0 59 1 -1.61579296 galago" + newLine
                    + "25 Q0 55 2 -1.61889580 galago" + newLine;

            assertEquals(expectedScores, output);

            //- batch search -- showNoResults=true and --systemName=apptest (must have --trec=true)
            byteArrayStream = new ByteArrayOutputStream();
            printStream = new PrintStream(byteArrayStream);

            App.run(new String[]{"batch-search",
                "--index=" + indexFile.getAbsolutePath(),
                "--showNoResults=true",
                "--trec=true",
                "--systemName=apptest",
		queryFile.getAbsolutePath()}, printStream);

            // Now, verify that some stuff exists
            output = byteArrayStream.toString();

            expectedScores
                    = "5 Q0 no_results_found 1 -999 apptest" + newLine
                    + "9 Q0 59 1 -1.38562925 apptest" + newLine
                    + "9 Q0 55 2 -1.38695903 apptest" + newLine
                    + "10 Q0 59 1 -2.08010799 apptest" + newLine
                    + "10 Q0 55 2 -2.08143777 apptest" + newLine
                    + "14 Q0 55 1 -1.73220460 apptest" + newLine
                    + "14 Q0 59 2 -1.73353440 apptest" + newLine
                    + "23 Q0 59 1 -1.38562925 apptest" + newLine
                    + "23 Q0 55 2 -1.38695903 apptest" + newLine
                    + "24 Q0 59 1 -1.61579296 apptest" + newLine
                    + "24 Q0 55 2 -1.61889580 apptest" + newLine
                    + "25 Q0 59 1 -1.61579296 apptest" + newLine
                    + "25 Q0 55 2 -1.61889580 apptest" + newLine;

            assertEquals(expectedScores, output);

            // Smoke test with batch search - non normalizing
            byteArrayStream = new ByteArrayOutputStream();
            printStream = new PrintStream(byteArrayStream);

            App.run(new String[]{"batch-search",
                "--norm=false",
                "--index=" + indexFile.getAbsolutePath(),
                queryFile.getAbsolutePath()}, printStream);

            printStream.close();
            
            // Now, verify that some stuff exists
            output = byteArrayStream.toString();
            byteArrayStream.close();
            
            expectedScores
                    = "9 Q0 59 1 -1.38562925 galago" + newLine
                    + "9 Q0 55 2 -1.38695903 galago" + newLine
                    + "10 Q0 59 1 -4.16021597 galago" + newLine
                    + "10 Q0 55 2 -4.16287555 galago" + newLine
                    + "14 Q0 55 1 -3.46440920 galago" + newLine
                    + "14 Q0 59 2 -3.46706879 galago" + newLine
                    + "23 Q0 59 1 -5.54251699 galago" + newLine
                    + "23 Q0 55 2 -5.54783614 galago" + newLine
                    + "24 Q0 59 1 -4.84737888 galago" + newLine
                    + "24 Q0 55 2 -4.85668740 galago" + newLine
                    + "25 Q0 59 1 -4.84737888 galago" + newLine
                    + "25 Q0 55 2 -4.85668740 galago" + newLine;

            assertEquals(expectedScores, output);

        } finally {
            if (queryFile != null) {
                Assert.assertTrue(queryFile.delete());
            }
            if (trecCorpusFile != null) {
                Assert.assertTrue(trecCorpusFile.delete());
            }
            if (indexFile != null) {       
                FSUtil.deleteDirectory(indexFile);
            }

        }
    }
}
