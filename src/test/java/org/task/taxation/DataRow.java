package org.task.taxation;

public class DataRow {
    String matchId;
    int marketId;
    String outcomeId;
    String specifiers;
    String time;

    public DataRow(String matchId, int marketId, String outcomeId, String specifiers) {
        this.matchId = matchId;
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.specifiers = specifiers;

    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInsertValueStatement(String time) {
        return String.format("(%s, %d, %s, %s, '%s')",
                matchId,
                marketId,
                outcomeId,
                specifiers == null || specifiers.isEmpty() ? "''" : specifiers,
                time
        );
    }

    public String formatLine() {
        return String.format("%s;%d;%s;%s;%s",
                matchId,
                marketId,
                outcomeId,
                specifiers == null || specifiers.isEmpty() ? "''" : specifiers,
                time
        );
    }
}
