package com.chatalytics.compute.storm;

import com.chatalytics.compute.storm.bolt.EmojiCounterBolt;
import com.chatalytics.compute.storm.bolt.EntityExtractionBolt;
import com.chatalytics.compute.storm.bolt.MessageSummaryBolt;
import com.chatalytics.compute.storm.bolt.RealtimeBolt;
import com.chatalytics.compute.storm.spout.HipChatMessageSpout;
import com.chatalytics.compute.storm.spout.LocalTestSpout;
import com.chatalytics.compute.storm.spout.SlackBackfillSpout;
import com.chatalytics.compute.storm.spout.SlackMessageSpout;
import com.chatalytics.core.InputSourceType;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;


/**
 * Declares and sets up the Storm topology.
 *
 * @author giannis
 */
public class ChatAlyticsStormTopology {

    private final InputSourceType type;

    public ChatAlyticsStormTopology(InputSourceType type) {
        this.type = type;
    }

    public StormTopology get() {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        String inputSpoutId;
        if (type == InputSourceType.HIPCHAT) {
            inputSpoutId = HipChatMessageSpout.SPOUT_ID;
            topologyBuilder.setSpout(inputSpoutId, new HipChatMessageSpout());
        } else if (type == InputSourceType.SLACK) {
            inputSpoutId = SlackMessageSpout.SPOUT_ID;
            topologyBuilder.setSpout(inputSpoutId, new SlackMessageSpout());
        } else if (type == InputSourceType.SLACK_BACKFILL) {
            inputSpoutId = SlackBackfillSpout.SPOUT_ID;
            topologyBuilder.setSpout(inputSpoutId, new SlackBackfillSpout());
        } else if (type == InputSourceType.LOCAL_TEST) {
            inputSpoutId = LocalTestSpout.SPOUT_ID;
            topologyBuilder.setSpout(inputSpoutId, new LocalTestSpout());
        } else {
            throw new RuntimeException("Can't determine input source type from " + type);
        }

        // entity extraction bolt
        topologyBuilder.setBolt(EntityExtractionBolt.BOLT_ID, new EntityExtractionBolt())
                       .shuffleGrouping(inputSpoutId);

        // emoji bolt
        topologyBuilder.setBolt(EmojiCounterBolt.BOLT_ID, new EmojiCounterBolt())
                       .shuffleGrouping(inputSpoutId);

        // message summary bolt
        topologyBuilder.setBolt(MessageSummaryBolt.BOLT_ID, new MessageSummaryBolt())
                       .shuffleGrouping(inputSpoutId);

        // realtime bolt
        topologyBuilder.setBolt(RealtimeBolt.BOLT_ID, new RealtimeBolt())
                       .shuffleGrouping(EmojiCounterBolt.BOLT_ID)
                       .shuffleGrouping(EntityExtractionBolt.BOLT_ID)
                       .shuffleGrouping(MessageSummaryBolt.BOLT_ID);

        return topologyBuilder.createTopology();
    }
}
