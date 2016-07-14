package storm.starter;

import org.apache.storm.metric.api.CountMetric;
import org.apache.storm.metric.api.MeanReducer;
import org.apache.storm.metric.api.MultiCountMetric;
import org.apache.storm.metric.api.ReducedMetric;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Example Exclamation Bolt.
 */
public class ExclamationBolt extends BaseRichBolt {
    OutputCollector collector;

    // Metrics
    // Note: these must be declared as transient since they are not Serializable
    transient CountMetric countMetric;
    transient MultiCountMetric wordCountMetric;
    transient ReducedMetric wordLengthMeanMetric;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        // Metrics must be initialized and registered in the prepare() method for bolts,
        // or the open() method for spouts.  Otherwise, an Exception will be thrown
        initMetrics(context);
    }

    void initMetrics(TopologyContext context) {
        countMetric = new CountMetric();
        wordCountMetric = new MultiCountMetric();
        wordLengthMeanMetric = new ReducedMetric(new MeanReducer());

        context.registerMetric("execute_count", countMetric, 5);
        context.registerMetric("word_count", wordCountMetric, 60);
        context.registerMetric("word_length", wordLengthMeanMetric, 60);
    }

    @Override
    public void execute(Tuple tuple) {
        collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
        collector.ack(tuple);

        updateMetrics(tuple.getString(0));
    }

    void updateMetrics(String word) {
        countMetric.incr();
        wordCountMetric.scope(word).incr();
        wordLengthMeanMetric.update(word.length());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
