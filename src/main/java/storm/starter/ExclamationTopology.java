package storm.starter;

import com.accelerate_experience.storm.metrics.statsd.StatsdMetricConsumer;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
//import org.apache.storm.metric.LoggingMetricsConsumer;
import org.apache.storm.testing.TestWordSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a basic example of a Storm topology.
 */
public class ExclamationTopology {

    /**
     * Example Exclamation Topology with metrics forwarded to Statsd (which forwards to Datadog).
     *
     * @param args topology arguments
     * @throws Exception barf.
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("word", new TestWordSpout(), 10);
        builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("word");
        builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");

        Config conf = new Config();
        conf.setDebug(true);
        Map statsdConfig = new HashMap();
        statsdConfig.put(StatsdMetricConsumer.STATSD_HOST, "127.0.0.1");
        statsdConfig.put(StatsdMetricConsumer.STATSD_PORT, 8125);
        statsdConfig.put(StatsdMetricConsumer.STATSD_PREFIX, "storm.metrics.");
        // Log all metrics to statsd
        conf.registerMetricsConsumer(StatsdMetricConsumer.class, statsdConfig, 2);
        // This will simply log all Metrics received into $STORM_HOME/logs/metrics.log on one or more worker nodes.
        //conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 2);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);

            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(15 * 60 * 1000L);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }
}