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

        AppFunction[] available = new AppFunction[] { 
                new DumpTermStatisticsFn(),
                new DumpCorpusFn(),
                new DebugQuery(),
                new BatchSearch(),
                new DumpIndexManifestFn(),
                new TransformQueryFn(),
                new StatsFn(),
                new DumpNamesLengths(),
                new DumpLengthsFn(),
                new DumpKeysFn(),
                new GetDocsJSONL(),
                new GetRMTermsFn(),
                new QueryTransformFn(),
                new DumpDocNameFn(),
                new DumpTermStatisticsExtFn(),
                new org.lemurproject.galago.utility.tools.TarToZipConverter(),
                new DumpIndexFn(),
                new org.lemurproject.galago.tupleflow.tools.ShowConfig(),
                new HelpFn(),
                new DumpKeyValueFn(),
                new org.lemurproject.galago.core.eval.Eval(), 
                new BuildIndex(),
                new DocCountFn(),
                new DumpDocFn(),
                new DumpDocTermsFn(),
                new org.lemurproject.galago.core.index.merge.MergeIndex(),
                new XCountFn(),
                new TokenizeAndGrabStats(),
                new OperatorHelpFn(),
                new DumpDocIdFn(),
            };

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
