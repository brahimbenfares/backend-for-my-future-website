package Model;

import java.util.List;

public class UnreadOrdersResponse {
    private final int count;
    private final List<Integer> unreadOrderIds;

    public UnreadOrdersResponse(int count, List<Integer> unreadOrderIds) {
        this.count = count;
        this.unreadOrderIds = unreadOrderIds;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getUnreadOrderIds() {
        return unreadOrderIds;
    }


    
}
