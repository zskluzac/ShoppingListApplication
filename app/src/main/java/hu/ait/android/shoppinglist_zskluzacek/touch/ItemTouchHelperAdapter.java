package hu.ait.android.shoppinglist_zskluzacek.touch;

/**
 * Created by Zachary on 11/7/2017.
 */

public interface ItemTouchHelperAdapter {

    void onItemDismiss(int Position);

    void onItemMove(int fromPosition, int toPosition);
}
