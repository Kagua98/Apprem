package com.application.apprem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.apprem.activities.BookDetailActivity;
import com.application.apprem.R;
import com.application.apprem.adapters.RecyclerGridAdapter;
import com.application.apprem.adapters.RecyclerListAdapter;
import com.application.apprem.models.BookInfo;
import com.application.apprem.observers.BaseRecyclerViewScrollListener;
import com.application.apprem.observers.OnAdapterItemClickListener;
import com.application.apprem.observers.OnAdapterItemDataSwapListener;
import com.application.apprem.observers.OnPagerFragmentVerticalScrollListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Inflates recycler_layout_view
 * It manages both the List and Grid ItemView Layouts
 *
 */
public class RecyclerViewFragment extends Fragment
        implements OnAdapterItemClickListener {

    //Annotation constants that define the possible values for LayoutMode
    public static final int LIST_MODE = 0;
    public static final int GRID_MODE = 1;
    //Constant used for logs
    private static final String LOG_TAG = RecyclerViewFragment.class.getSimpleName();
    //Bundle Constant used for storing the argument passed during initialization
    private static final String LAYOUT_MODE_INT_KEY = "layoutMode.Value";
    //Stores the reference to the RecyclerView inflated
    private RecyclerView mRecyclerView;
    //Stores the reference to the Listener OnPagerFragmentVerticalScrollListener
    private OnPagerFragmentVerticalScrollListener mListener;

    /**
     * Static constructor of the Fragment {@link RecyclerViewFragment}
     *
     * @param layoutMode is the Integer Mode value that decides the Layout (List/Grid) and its Adapter
     *                   which will be one of the values of the annotation {@link LayoutMode}
     * @return Instance of this Fragment {@link RecyclerViewFragment}
     */
    public static RecyclerViewFragment newInstance(@LayoutMode int layoutMode) {
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(1);
        bundle.putInt(LAYOUT_MODE_INT_KEY, layoutMode);
        recyclerViewFragment.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END

        //Returning the instance
        return recyclerViewFragment;
    }


    public void setOnPagerFragmentVerticalScrollListener(OnPagerFragmentVerticalScrollListener listener) {
        mListener = listener;
    }

    /**
     * Method that unregisters the {@link OnPagerFragmentVerticalScrollListener}
     */
    public void clearOnPagerFragmentVerticalScrollListener() {
        mListener = null;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI ('R.layout.recycler_layout_view').
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the RecyclerView layout ('R.layout.recycler_layout_view')
        View rootView = inflater.inflate(R.layout.recycler_layout_view, container, false);

        //Retrieving the RecyclerView component
        mRecyclerView = rootView.findViewById(R.id.recycler_layout_id);

        if (getArguments() != null) {
            //Retrieving the Layout mode passed
            int layoutMode = getArguments().getInt(LAYOUT_MODE_INT_KEY);

            //Setting the Layout Manager, Adapter and Decoration based on the Layout Mode passed
            if (layoutMode == LIST_MODE) {
                //When the Layout mode passed is for Vertical List View
                setLayoutForListView();
            } else if (layoutMode == GRID_MODE) {
                //When the Layout mode passed is for Vertical Grid View
                setLayoutForGridView();
            }
        }

        //Registering the scroll listener on RecyclerView
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener());

        //Returning the inflated layout
        return rootView;
    }

    /**
     * Method that selects the Layout Manager, Adapter
     * and Decoration for Grid View Layout Mode
     */
    private void setLayoutForGridView() {
        //Initializing the StaggeredGridLayoutManager with Vertical Orientation and spanning two columns
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //Initializing an empty ArrayList of BookInfo Objects as the dataset for the Adapter
        ArrayList<BookInfo> bookInfoList = new ArrayList<>();

        //Initializing the Adapter for the Grid view
        RecyclerGridAdapter recyclerGridAdapter = new RecyclerGridAdapter(requireContext(), R.layout.books_grid_item, bookInfoList);

        //Registering the OnAdapterItemClickListener on the Adapter
        recyclerGridAdapter.setOnAdapterItemClickListener(this);

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(recyclerGridAdapter);

    }

    /**
     * Method that selects the Layout Manager, Adapter
     * and Decoration for List View Layout Mode
     */
    private void setLayoutForListView() {
        //Initializing the LinearLayoutManager with Vertical Orientation and start to end layout direction
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Initializing an empty ArrayList of BookInfo Objects as the dataset for the Adapter
        ArrayList<BookInfo> bookInfoList = new ArrayList<>();

        //Initializing the Adapter for the List view
        RecyclerListAdapter recyclerListAdapter = new RecyclerListAdapter(requireContext(), R.layout.library_child3, bookInfoList);

        //Registering the OnAdapterItemClickListener on the Adapter
        recyclerListAdapter.setOnAdapterItemClickListener(this);

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(recyclerListAdapter);

    }

    /**
     * Method that retrieves the item position of the first completely visible
     * or the partially visible item in the screen
     *
     * @return is the Integer value of the first item position that is currently visible in the screen
     */
    public int getFirstVisibleItemPosition() {
        //Retrieving the Layout Manager of the RecyclerView
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //When the Layout Manager is an instance of LinearLayoutManager

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //First, retrieving the top completely visible item position
            int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the top partially visible item position
                return linearLayoutManager.findFirstVisibleItemPosition();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //When the Layout Manager is an instance of StaggeredGridLayoutManager

            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            //First, retrieving the top completely visible item position
            int position = gridLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the top partially visible item position
                return gridLayoutManager.findFirstVisibleItemPositions(null)[0];
            }
        }

        //On all else, returning -1 (RecyclerView.NO_POSITION)
        return RecyclerView.NO_POSITION;
    }

    /**
     * Method that sets the Layout Manager's currently viewing position to the item position specified
     *
     * @param position        is the item position to which the Layout Manager needs to be set
     * @param scrollImmediate is a boolean which denotes the way in which the scroll to position
     *                        needs to be handled
     *                        <br/><b>TRUE</b> if the scroll to position needs to be set immediately
     *                        without any animation
     *                        <br/><b>FALSE</b> if the scroll to position needs to be done naturally
     *                        with the default animation
     */
    public void scrollToItemPosition(int position, boolean scrollImmediate) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (position > RecyclerView.NO_POSITION) {
            //Scrolling to the item position passed when valid
            if (scrollImmediate) {
                //Scrolling to the item position immediately
                layoutManager.scrollToPosition(position);
            } else {
                //Scrolling to the item position naturally with default animation
                layoutManager.smoothScrollToPosition(mRecyclerView, null, position);
            }
        }
    }

    /**
     * Method exposed for the {@link com.application.apprem.activities.BookSearchActivity}
     * to register the {@link OnAdapterItemDataSwapListener} on the Fragment position specified
     *
     * @param itemDataSwapListener is the instance of the Activity implementing the {@link OnAdapterItemDataSwapListener}
     * @param position             is the Fragment position on which this Listener is to be Registered
     */
    public void registerItemDataSwapListener(@Nullable OnAdapterItemDataSwapListener itemDataSwapListener, int position) {
        //Retrieving the RecyclerView's Adapter
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        //Registering the listener on the corresponding adapter based on the Fragment position passed
        if (position == LIST_MODE) {
            //When the Fragment position is of the LIST_MODE Fragment

            RecyclerListAdapter listAdapter = (RecyclerListAdapter) adapter;
            listAdapter.setOnAdapterItemDataSwapListener(itemDataSwapListener);
        } else if (position == GRID_MODE) {
            //When the Fragment position is of the GRID_MODE Fragment

            RecyclerGridAdapter gridAdapter = (RecyclerGridAdapter) adapter;
            gridAdapter.setOnAdapterItemDataSwapListener(itemDataSwapListener);
        }
    }

    /**
     * Method exposed for the {@link com.application.apprem.activities.BookSearchActivity}
     * to clear the {@link OnAdapterItemDataSwapListener} previously registered for the Fragment position specified
     *
     * @param position is the Fragment position on which this Listener is to be Unregistered
     */
    public void clearItemDataSwapListener(int position) {
        //Propagating the call to #registerItemDataSwapListener with null to unregister
        registerItemDataSwapListener(null, position);
    }

    /**
     * Method to load the data into the Adapter
     *
     * @param bookInfos List of {@link BookInfo} objects to be loaded into the Adapter
     */
    public void loadNewData(List<BookInfo> bookInfos) {
        Log.d(LOG_TAG, "loadNewData: Started");
        //Calling the Adapter's method to Load the new data
        swapAdapterData(bookInfos);
    }

    /**
     * Method that swaps the data in the Adapter
     *
     * @param bookInfos List of {@link BookInfo} objects to be loaded into the Adapter
     */
    private void swapAdapterData(@NonNull List<BookInfo> bookInfos) {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        //Calling the respective method from the Adapter to load & display the new data accordingly
        if (adapter instanceof RecyclerListAdapter) {
            Log.d(LOG_TAG, "swapAdapterData: Started for List");
            RecyclerListAdapter listAdapter = (RecyclerListAdapter) adapter;
            listAdapter.swapItemData(bookInfos);
        } else if (adapter instanceof RecyclerGridAdapter) {
            Log.d(LOG_TAG, "swapAdapterData: Started for Grid");
            RecyclerGridAdapter gridAdapter = (RecyclerGridAdapter) adapter;
            gridAdapter.swapItemData(bookInfos);
        }
    }

    /**
     * Method to clear the data from the Adapter
     */
    public void clearData() {
        //Creating an Empty List of BookInfo objects to clear the content in the Adapter
        List<BookInfo> bookInfos = new ArrayList<>();
        //Calling the Adapter's method to clear the data
        swapAdapterData(bookInfos);
    }

    /**
     * Method invoked when an Item on the Adapter is clicked
     *
     * @param itemBookInfo is the corresponding {@link BookInfo} object of the item view
     *                     clicked in the Adapter
     */
    @Override
    public void onItemClick(BookInfo itemBookInfo) {
        //Passing the selected Item's data as an Intent to the BookDetailActivity
        Intent itemIntent = new Intent(getActivity(), BookDetailActivity.class);
        itemIntent.putExtra(BookDetailActivity.BOOK_INFO_ITEM_STR_KEY, itemBookInfo);
        startActivity(itemIntent);
    }

    //Defining the LayoutMode IntDef annotation with Retention only at SOURCE
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LIST_MODE, GRID_MODE})
    @interface LayoutMode {
    }

    /**
     * Subclass of {@link BaseRecyclerViewScrollListener} that listens to the scroll event
     * received when the scroll reaches/leaves the last three items in the {@link RecyclerView}
     */
    private class RecyclerViewScrollListener extends BaseRecyclerViewScrollListener {

        /**
         * Callback Method to be implemented to receive events when the
         * scroll has reached/left the last three items in the {@link RecyclerView}
         *
         * @param verticalScrollAmount is the amount of vertical scroll.
         *                             <br/>If >0 then scroll is moving towards the bottom;
         *                             <br/>If <0 then scroll is moving towards the top
         */
        @Override
        public void onBottomReached(int verticalScrollAmount) {
            //Propagating the call to the listener OnPagerFragmentVerticalScrollListener
            mListener.onBottomReached(verticalScrollAmount);
        }
    }


}
