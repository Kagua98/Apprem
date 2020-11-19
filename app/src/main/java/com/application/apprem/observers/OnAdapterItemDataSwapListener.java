package com.application.apprem.observers;

/**
 * Interface that declares methods to be implemented by the
 * {@link com.application.apprem.activities.BookSearchActivity}
 * to receive event callbacks related to RecyclerView's Adapter data change
 *
 * @author Kaushik N Sanji
 */
public interface OnAdapterItemDataSwapListener {

    /**
     * Method invoked when the data on the RecyclerView's Adapter has been swapped successfully
     */
    void onItemDataSwapped();
}
