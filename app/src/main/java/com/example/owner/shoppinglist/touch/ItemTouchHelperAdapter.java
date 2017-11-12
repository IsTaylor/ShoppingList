package com.example.owner.shoppinglist.touch;

/**
 * Created by owner on 11/10/17.
 */

public interface ItemTouchHelperAdapter {

    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);

}
