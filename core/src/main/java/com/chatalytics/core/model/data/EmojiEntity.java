package com.chatalytics.core.model.data;

import com.google.common.base.MoreObjects;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Represents a mention of an emoji. This is also persisted in the database
 *
 * @author giannis
 *
 */
@Entity
@Table(name = EmojiEntity.EMOJI_TABLE_NAME,
       indexes = {@Index(name = "ee_idx_username", columnList = "username"),
                  @Index(name = "ee_idx_roomName", columnList = "roomName"),
                  @Index(name = "ee_idx_value", columnList = "value"),
                  @Index(name = "ee_idx_bot", columnList = "bot")})
@EqualsAndHashCode
@AllArgsConstructor
@Setter(value = AccessLevel.PROTECTED) // for hibernate
public class EmojiEntity implements IMentionable<String> {

    private static final long serialVersionUID = 7180644692083145769L;

    public static final String EMOJI_TABLE_NAME = "EMOJI";
    public static final String EMOJI_COLUMN = "VALUE";
    public static final String OCCURENCES_COLUMN = "OCCURRENCES";
    public static final String MENTION_TIME_COLUMN = "MENTION_TIME";
    public static final String ROOM_NAME_COLUMN = "ROOM_NAME";
    public static final String USER_NAME_COLUMN = "USER_NAME";
    public static final String BOT_COLUMN = "BOT";

    /**
     * Emoji alias without ':'
     */
    private String username;
    private String roomName;
    private DateTime mentionTime;
    private String value;
    private int occurrences;
    private boolean bot;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    public EmojiEntity(String username, String roomName, DateTime mentionTime,
                       String value, int occurrences, boolean bot) {
        this.username = username;
        this.roomName = roomName;
        this.mentionTime = mentionTime;
        this.value = value;
        this.occurrences = occurrences;
        this.bot = bot;
    }

    protected EmojiEntity() {} // for jackson

    @Override
    @Column(name = EMOJI_COLUMN)
    public String getValue() {
        return value;
    }

    @Override
    @Column(name = OCCURENCES_COLUMN)
    public int getOccurrences() {
        return occurrences;
    }

    @Override
    @Column(name = MENTION_TIME_COLUMN)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getMentionTime() {
        return mentionTime;
    }

    @Override
    @Column(name = USER_NAME_COLUMN)
    public String getUsername() {
        return username;
    }

    @Override
    @Column(name = ROOM_NAME_COLUMN)
    public String getRoomName() {
        return roomName;
    }

    @Override
    @Column(name = BOT_COLUMN)
    public boolean isBot() {
        return bot;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                          .add("value", value)
                          .add("occurrences", occurrences)
                          .add("mentionTime", mentionTime)
                          .add("username", username)
                          .add("roomName", roomName)
                          .add("bot", bot)
                          .toString();
    }
}
