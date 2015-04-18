package com.hipchalytics.compute.storm.bolt;

import com.hipchalytics.compute.config.ConfigurationConstants;
import com.hipchalytics.core.model.FatMessage;
import com.hipchalytics.core.model.HipchatEntity;
import com.hipchalytics.core.model.Message;
import com.hipchalytics.core.model.Room;
import com.hipchalytics.core.model.User;

import org.apache.storm.guava.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link HipChatMessageSpout}.
 *
 * @author giannis
 *
 */
public class HipChatMessageSpout {

    private EntityExtractionBolt underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new EntityExtractionBolt();
        Map<String, String> confMap = Maps.newHashMapWithExpectedSize(1);
        confMap.put(ConfigurationConstants.HIPCHALYTICS_CONFIG.txt, "apiRetries: 0");
        underTest.prepare(confMap, mock(TopologyContext.class), mock(OutputCollector.class));
    }

    /**
     * Ensures that entities are properly extracted and returned from a {@link Message}.
     */
    @Test
    public void testExtractEntities() {
        DateTime date = DateTime.now().withZone(DateTimeZone.UTC);
        String mentionName = "jane";
        int userId = 1;
        String ent1 = "Jane Doe";
        String ent2 = "Mount Everest";
        Message msg = new Message(date, mentionName, userId,
                                  String.format("Today, %s is going to climb %s", ent1, ent2));
        User mockUser = mock(User.class);
        when(mockUser.getMentionName()).thenReturn("jane");
        Room mockRoom = mock(Room.class);
        when(mockRoom.getName()).thenReturn("theroom");
        FatMessage fatMessage = new FatMessage(msg, mockUser, mockRoom);
        List<HipchatEntity> entities = underTest.extractEntities(fatMessage);
        assertEquals(2, entities.size());

        HipchatEntity entity = entities.get(1);
        assertEquals(ent1, entity.getEntityValue());
        assertEquals(1, entity.getOccurrences());
        assertEquals(date, entity.getMentionTime());

        entity = entities.get(0);
        assertEquals(ent2, entity.getEntityValue());
        assertEquals(1, entity.getOccurrences());
        assertEquals(date, entity.getMentionTime());
    }

}