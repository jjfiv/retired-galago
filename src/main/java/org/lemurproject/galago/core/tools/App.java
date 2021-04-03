// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.tools;

import org.lemurproject.galago.utility.Parameters;
import org.lemurproject.galago.utility.tools.AppFunction;
import org.lemurproject.galago.core.tools.apps.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sjh, irmarc, trevor
 */
public class App {

    /**
     * function selection and processing
     */
    public final static Logger log;
    public final static HashMap<String, AppFunction> appFunctions;

    // init function -- allows internal use of app function library
    static {
        log = Logger.getLogger("Galago-App");

        AppFunction[] available = new AppFunction[] { new org.lemurproject.galago.core.repair.RepairFn(),
                new TimedBatchSearch(),
                new DumpTermStatisticsFn(),
                new DumpCorpusFn(),
                new DebugQuery(),
                new BatchSearch(),
                new BuildWindowIndex(),
                new DumpIndexManifestFn(),
                new TransformQueryFn(),
                new StatsFn(),
                new DumpNamesLengths(),
                new BuildStemmerConflation(),
                new org.lemurproject.galago.core.tools.MakeCorpus(),
                new DumpLengthsFn(),
                new DumpKeysFn(),
                new GetDocsJSONL(),
                new DumpConnectionFn(),
                new ThreadedBatchSearch(),
                new GetRMTermsFn(),
                new BuildSpecialPart(),
                new QueryTransformFn(),
                new DumpDocNameFn(),
                new DumpTermStatisticsExtFn(),
                new org.lemurproject.galago.utility.tools.TarToZipConverter(),
                new org.lemurproject.galago.core.tools.apps.DumpIndexFn(),
                new org.lemurproject.galago.tupleflow.tools.ShowConfig(),
                new HelpFn(),
                new DumpKeyValueFn(),
                new org.lemurproject.galago.core.eval.Eval(), new org.lemurproject.galago.core.tools.apps.BuildIndex(),
                new HarvestLinksFn(),
                new ChainFns(),
                new DocCountFn(),
                new DumpDocFn(),
                new OverwriteManifestFn(),
                new DumpDocTermsFn(),
                new org.lemurproject.galago.core.index.merge.MergeIndex(),
                new XCountFn(),
                new PageRankFn(),
                new TokenizeAndGrabStats(),
                new OperatorHelpFn(),
                new DumpDocIdFn(),
                new BuildPartialIndex() };

        appFunctions = new HashMap<>();

        for (AppFunction f : available) {
            appFunctions.put(f.getName(), f);
        }
    }

    /*
     * Eval function
     */
    public static void main(String[] args) throws Exception {
        App.run(args);
    }

    public static void run(String[] args) throws Exception {
        run(args, System.out);
    }

    public static void run(String[] args, PrintStream out) throws Exception {
        String fn = "help";

        if (args.length > 0 && appFunctions.containsKey(args[0])) {
            fn = args[0];
        }
        appFunctions.get(fn).run(args, out);
    }

    public static void run(String fn, Parameters p, PrintStream out) throws Exception {
        if (appFunctions.containsKey(fn)) {
            appFunctions.get(fn).run(p, out);
        } else {
            log.log(Level.WARNING, "Could not find app: " + fn);
        }
    }
}
