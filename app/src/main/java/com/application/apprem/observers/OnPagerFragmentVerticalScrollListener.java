package com.application.apprem.observers;

import com.application.apprem.fragments.RecyclerViewFragment;

/**
 * Interface that declares methods to be implemented by the
 * {@link com.application.apprem.activities.BookSearchActivity}
 * to receive event callbacks related to RecyclerView's scrolling action
 */
public interface OnPagerFragmentVerticalScrollListener {

    /**
     * Method invoked when the ViewPager's scroll has reached/left
     * the last three items in its Fragment
     * {@link RecyclerViewFragment}
     *
     * @param verticalScrollAmount is the amount of vertical scroll.
     *                             <br/>If >0 then scroll is moving towards the bottom;
     *                             <br/>If <0 then scroll is moving towards the top
     */
    void onBottomReached(int verticalScrollAmount);
}
