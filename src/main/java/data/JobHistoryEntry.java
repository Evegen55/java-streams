package data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JobHistoryEntry {
    private final int duration;
    private final String position;
    private final String employer;

    public JobHistoryEntry(int duration, String position, String employer) {
        this.duration = duration;
        this.position = position;
        this.employer = employer;
    }

    public int getDuration() {
        return duration;
    }

    public String getPosition() {
        return position;
    }

    public String getEmployer() {
        return employer;
    }

    /**
     * factory method
     * @param duration
     * @return new JobHistoryEntry
     */
    public JobHistoryEntry withDuration(int duration) {
        return new JobHistoryEntry(duration, position, employer);
    }

    /**
     * factory method
     * @param position
     * @return new JobHistoryEntry
     */
    public JobHistoryEntry withPosition(String position) {
        return new JobHistoryEntry(duration, position, employer);
    }

    /**
     * factory method
     * @param employer
     * @return new JobHistoryEntry
     */
    public JobHistoryEntry withEmployer(String employer) {
        return new JobHistoryEntry(duration, position, employer);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("duration", duration)
                .append("position", position)
                .append("employer", employer)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        JobHistoryEntry that = (JobHistoryEntry) o;

        return new EqualsBuilder()
                .append(duration, that.duration)
                .append(position, that.position)
                .append(employer, that.employer)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(duration)
                .append(position)
                .append(employer)
                .toHashCode();
    }
}
